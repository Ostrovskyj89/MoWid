package com.eleks.data.firebase.source.impl

import com.eleks.data.firebase.source.FirebaseDataSource
import com.eleks.data.model.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

@Singleton
class FirebaseDataSourceImpl @Inject constructor(
    private val dbInstance: FirebaseFirestore,
    private val authInstance: FirebaseAuth
) : FirebaseDataSource, CoroutineScope {

    override val coroutineContext: CoroutineContext = Dispatchers.Default

    private val _groupsFlow = MutableSharedFlow<ResultDataModel<List<GroupDataModel>>>(
        replay = 1
    )

    private val _userGroupsFlow = MutableSharedFlow<ResultDataModel<List<GroupDataModel>>>(
        replay = 1
    )

    private val _selectedGroupsFlow =
        MutableSharedFlow<ResultDataModel<List<SelectedGroupDataModel>>>(
            replay = 1
        )

    private val _selectedQuotesFlow =
        MutableSharedFlow<ResultDataModel<List<SelectedQuoteDataModel>>>(
            replay = 1
        )

    private val _quotesFlow = MutableSharedFlow<ResultDataModel<List<QuoteDataModel>>>(
        replay = 1
    )

    private val _userQuotesFlow = MutableSharedFlow<ResultDataModel<List<QuoteDataModel>>>(
        replay = 1
    )

    private var currentGroupId: String? = null

    override val groupsFlow = _groupsFlow.asSharedFlow()

    override val userGroupsFlow = _userGroupsFlow.asSharedFlow()

    override val selectedGroupsFlow = _selectedGroupsFlow.asSharedFlow()

    override val selectedQuotesFlow = _selectedQuotesFlow.asSharedFlow()

    override val quotesFlow = _quotesFlow.asSharedFlow()

    override val userQuotesFlow = _userQuotesFlow.asSharedFlow()

    override val currentUser
        get() = authInstance.currentUser

    init {
        launch { subscribeGroups() }
        subscribeUserGroups()
        subscribeSelectedGroups()
    }

    override fun signInSuccess() {
        subscribeUserGroups()
        subscribeSelectedGroups()
        currentGroupId?.let { subscribeAllGroupsQuotes(it) }
    }

    override fun signOutSuccess() {
        _userGroupsFlow.tryEmit(ResultDataModel.success(emptyList()))
        _userQuotesFlow.tryEmit(ResultDataModel.success(emptyList()))
        _selectedGroupsFlow.tryEmit(ResultDataModel.success(emptyList()))
        _selectedQuotesFlow.tryEmit(ResultDataModel.success(emptyList()))
    }

    override fun subscribeAllGroupsQuotes(groupId: String) {
        currentGroupId = groupId
        subscribeQuotes(groupId)
        subscribeUserQuotes(groupId)
        subscribeSelectedQuotes(groupId)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun saveNewGroup(group: GroupDataModel): ResultDataModel<GroupDataModel> =
        suspendCancellableCoroutine { continuation ->
            val uuid = UUID.randomUUID().toString()
            authInstance.currentUser?.let {
                dbInstance.collection(COLLECTION_PERSONAL)
                    .document(it.uid)
                    .collection(COLLECTION_GROUPS)
                    .document(uuid)
                    .set(group.apply {
                        id = uuid
                        quotesCount = 0
                    })
                    .addOnSuccessListener {
                        continuation.resume(ResultDataModel.success(group)) {}
                    }
                    .addOnFailureListener { exception ->
                        continuation.resume(ResultDataModel.error(exception)) {}
                    }
            }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun saveNewQuote(
        groupId: String, quote: QuoteDataModel
    ): ResultDataModel<QuoteDataModel> =
        suspendCancellableCoroutine { continuation ->
            val uuid = UUID.randomUUID().toString()
            authInstance.currentUser?.let {
                val currentDocument = dbInstance.collection(COLLECTION_PERSONAL)
                    .document(it.uid)
                    .collection(COLLECTION_GROUPS)
                    .document(groupId)
                currentDocument.collection(COLLECTION_QUOTES)
                    .document(uuid)
                    .set(quote.apply { id = uuid })
                    .addOnSuccessListener {
                        continuation.resume(ResultDataModel.success(quote)) {}
                    }
                    .addOnFailureListener { exception ->
                        continuation.resume(ResultDataModel.error(exception)) {}
                    }
                currentDocument.get().addOnCompleteListener {
                    if (!it.result.exists()) {
                        dbInstance.collection(COLLECTION_GROUPS).document(groupId).get()
                            .addOnCompleteListener { snapShot ->
                                val group = snapShot.result.toObject<GroupDataModel>()
                                group?.let { model ->
                                    currentDocument.set(model.apply {
                                        quotesCount = quotesCount?.inc()
                                    })
                                }
                            }
                    } else {
                        currentDocument.update("quotesCount", FieldValue.increment(1))
                    }

                }
            }
        }


    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun saveSelection(
        groupId: String,
        quote: SelectedQuoteDataModel,
        isSelected: Boolean
    ): ResultDataModel<SelectedQuoteDataModel> =
        suspendCancellableCoroutine { continuation ->
            authInstance.currentUser?.let {
                val currentDocument = dbInstance.collection(COLLECTION_PERSONAL)
                    .document(it.uid)
                    .collection(COLLECTION_SELECTION)
                    .document(groupId)
                currentDocument.get().addOnCompleteListener {
                    if (it.result.exists()) {
                        if (isSelected) {
                            currentDocument
                                .collection(COLLECTION_QUOTES)
                                .document(quote.id ?: "").set(quote)
                                .addOnSuccessListener {
                                    continuation.resume(ResultDataModel.success(quote)) {}
                                }
                                .addOnFailureListener { exception ->
                                    continuation.resume(ResultDataModel.error(exception)) {}
                                }
                            currentDocument.update("selectedQuotesCount", FieldValue.increment(1))
                        } else {
                            currentDocument
                                .collection(COLLECTION_QUOTES)
                                .document(quote.id ?: "")
                                .delete()
                            currentDocument.update("selectedQuotesCount", FieldValue.increment(-1))
                        }
                    } else {
                        currentDocument.set(
                            SelectedGroupDataModel(
                                groupId = groupId,
                                selectedQuotesCount = 1
                            )
                        ).addOnCompleteListener {
                            currentDocument
                                .collection(COLLECTION_QUOTES)
                                .document(quote.id ?: "").set(quote)
                        }
                    }
                }
            }
        }

    private suspend fun subscribeGroups() {
        var subscription: ListenerRegistration? = null
        _groupsFlow.subscriptionCount.collect {
            if (it > 0) {
                subscription = dbInstance.collection(COLLECTION_GROUPS)
                    .addSnapshotListener { value, error ->
                        if (error != null) {
                            _groupsFlow.tryEmit(ResultDataModel.error(error))
                            return@addSnapshotListener
                        }
                        value?.let { snapShot ->
                            val groups = mutableListOf<GroupDataModel>()
                            for (doc in snapShot) {
                                groups.add(doc.toObject())
                            }
                            _groupsFlow.tryEmit(ResultDataModel.success(groups))
                        }
                    }
            } else {
                subscription?.remove()
            }
        }
    }

    private fun subscribeUserGroups() {
        launch {
            var subscription: ListenerRegistration? = null
            _userGroupsFlow.subscriptionCount.collect { subscribers ->

                val userId = authInstance.currentUser?.uid
                if (userId == null) {
                    _userGroupsFlow.tryEmit(ResultDataModel.success(mutableListOf()))
                    cancel()
                } else {
                    if (subscribers > 0) {
                        subscription = dbInstance.collection(COLLECTION_PERSONAL)
                            .document(userId).collection(COLLECTION_GROUPS)
                            .addSnapshotListener { value, error ->
                                if (error != null) {
                                    // TODO empty string shouldn't be returned. In debug it should just throw ex
                                    _userGroupsFlow.tryEmit(ResultDataModel.error(error))
                                    return@addSnapshotListener
                                }

                                value?.let { snapShot ->
                                    val groups = mutableListOf<GroupDataModel>()
                                    for (doc in snapShot) {
                                        groups.add(doc.toObject())
                                    }
                                    _userGroupsFlow.tryEmit(ResultDataModel.success(groups))
                                }
                            }
                    } else {
                        subscription?.remove()
                    }
                }
            }
        }
    }

    private fun subscribeQuotes(groupId: String) {
        launch {
            var subscription: ListenerRegistration? = null
            _quotesFlow.subscriptionCount.collect { subscribers ->
                if (subscribers > 0) {
                    subscription = dbInstance.collection(COLLECTION_GROUPS).document(groupId)
                        .collection(COLLECTION_QUOTES)
                        .addSnapshotListener { value, error ->
                            if (error != null) {
                                _quotesFlow.tryEmit(ResultDataModel.error(error))
                                return@addSnapshotListener
                            }
                            value?.let { snapShot ->
                                val groups = mutableListOf<QuoteDataModel>()
                                for (doc in snapShot) {
                                    groups.add(doc.toObject())
                                }
                                _quotesFlow.tryEmit(ResultDataModel.success(groups))
                            }
                        }
                } else {
                    subscription?.let {
                        it.remove()
                        currentGroupId = null
                        cancel()
                    }
                }
            }
        }
    }

    private fun subscribeUserQuotes(groupId: String) {
        launch {
            var subscription: ListenerRegistration? = null
            _userQuotesFlow.subscriptionCount.collect { subscribers ->

                val userId = authInstance.currentUser?.uid
                if (userId == null) {
                    _userQuotesFlow.tryEmit(ResultDataModel.success(mutableListOf()))
                    return@collect
                }
                if (subscribers > 0) {
                    subscription = dbInstance.collection(COLLECTION_PERSONAL)
                        .document(userId).collection(COLLECTION_GROUPS)
                        .document(groupId).collection(COLLECTION_QUOTES)
                        .addSnapshotListener { value, error ->
                            if (error != null) {
                                _userQuotesFlow.tryEmit(ResultDataModel.error(error))
                                return@addSnapshotListener
                            }

                            value?.let { snapShot ->
                                val groups = mutableListOf<QuoteDataModel>()
                                for (doc in snapShot) {
                                    groups.add(doc.toObject())
                                }
                                _userQuotesFlow.tryEmit(ResultDataModel.success(groups))
                            }
                        }
                } else {
                    subscription?.let {
                        it.remove()
                        currentGroupId = null
                        cancel()
                    }
                }
            }
        }
    }

    private fun subscribeSelectedGroups() {
        launch {
            var subscription: ListenerRegistration? = null
            _selectedGroupsFlow.subscriptionCount.collect { subscribers ->
                val userId = authInstance.currentUser?.uid
                if (userId == null) {
                    _selectedGroupsFlow.tryEmit(ResultDataModel.success(mutableListOf()))
                    cancel()
                } else {
                    if (subscribers > 0) {
                        subscription =
                            dbInstance.collection(COLLECTION_PERSONAL)
                                .document(userId)
                                .collection(COLLECTION_SELECTION)
                                .addSnapshotListener { value, error ->
                                    if (error != null) {
                                        _selectedGroupsFlow.tryEmit(
                                            ResultDataModel.error(error)
                                        )
                                        return@addSnapshotListener
                                    }

                                    value?.let {
                                        val selections = mutableListOf<SelectedGroupDataModel>()
                                        for (doc in value) {
                                            selections.add(doc.toObject())
                                        }
                                        _selectedGroupsFlow.tryEmit(
                                            ResultDataModel.success(
                                                selections
                                            )
                                        )
                                    }
                                }
                    } else {
                        subscription?.remove()
                    }
                }
            }
        }
    }

    private fun subscribeSelectedQuotes(groupId: String) {
        launch {
            var subscription: ListenerRegistration? = null
            _selectedQuotesFlow.subscriptionCount.collect { subscribers ->
                val userId = authInstance.currentUser?.uid
                if (userId == null) {
                    _selectedQuotesFlow.tryEmit(ResultDataModel.success(mutableListOf()))
                    return@collect
                }
                if (subscribers > 0) {
                    subscription =
                        dbInstance.collection(COLLECTION_PERSONAL)
                            .document(userId)
                            .collection(COLLECTION_SELECTION)
                            .document(groupId)
                            .collection(COLLECTION_QUOTES)
                            .addSnapshotListener { value, error ->
                                if (error != null) {
                                    _selectedQuotesFlow.tryEmit(ResultDataModel.error(error))
                                    return@addSnapshotListener
                                }
                                value?.let {
                                    val selections = mutableListOf<SelectedQuoteDataModel>()
                                    for (doc in value) {
                                        selections.add(doc.toObject())
                                    }
                                    _selectedQuotesFlow.tryEmit(ResultDataModel.success(selections))
                                }
                            }
                } else {
                    subscription?.let {
                        it.remove()
                        currentGroupId = null
                        cancel()
                    }
                }
            }
        }
    }

    companion object {
        const val COLLECTION_PERSONAL = "personal"
        const val COLLECTION_SELECTION = "selection"
        const val COLLECTION_GROUPS = "groups"
        const val COLLECTION_QUOTES = "quotes"
    }
}
