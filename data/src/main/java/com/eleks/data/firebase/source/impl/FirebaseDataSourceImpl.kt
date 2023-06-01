package com.eleks.data.firebase.source.impl

import com.eleks.data.firebase.source.FirebaseDataSource
import com.eleks.data.model.*
import com.eleks.data.preferences.LocalDataSource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

@Singleton
class FirebaseDataSourceImpl @Inject constructor(
    private val dbInstance: FirebaseFirestore,
    private val authInstance: FirebaseAuth,
    private val localDataSource: LocalDataSource
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

    private val _frequenciesFlow = MutableSharedFlow<ResultDataModel<List<FrequencyDataModel>>>(
        replay = 1
    )

    private val _userFrequencyFlow = MutableSharedFlow<ResultDataModel<Long>>(
        replay = 1
    )

    private val _userFlow =
        MutableSharedFlow<ResultDataModel<UserDataModel>>(
            replay = 1
        )

    private var currentGroupId: String? = null

    override val groupsFlow = _groupsFlow.asSharedFlow()

    override val userGroupsFlow = _userGroupsFlow.asSharedFlow()

    override val selectedGroupsFlow = _selectedGroupsFlow.asSharedFlow()

    override val selectedQuotesFlow = _selectedQuotesFlow.asSharedFlow()

    override val quotesFlow = _quotesFlow.asSharedFlow()

    override val userQuotesFlow = _userQuotesFlow.asSharedFlow()

    override val frequenciesFlow = _frequenciesFlow.asSharedFlow()

    override val userFrequencyFlow = _userFrequencyFlow.asSharedFlow()

    override val userFlow = _userFlow.asSharedFlow()

    private val mutex = Mutex()

    init {
        launch {
            val userJob = launch {
                saveCurrentToken()
            }
            userJob.join()
            subscribeGroups()
            subscribeUserGroups()
            subscribeSelectedGroups()
        }
    }

    override fun signInSuccess() {
        launch {
            val userJob = launch {
                saveUser()
            }
            userJob.join()
            subscribeUser()
            subscribeUserGroups()
            subscribeSelectedGroups()
            currentGroupId?.let { subscribeAllGroupsQuotes(it) }
        }
    }

    override fun signOutSuccess() {
        localDataSource.token = UUID.randomUUID().toString()
        subscribeUser()
        subscribeUserGroups()
        subscribeSelectedGroups()
        currentGroupId?.let { subscribeAllGroupsQuotes(it) }
    }

    override fun subscribeAllGroupsQuotes(groupId: String) {
        currentGroupId = groupId
        subscribeQuotes(groupId)
        subscribeUserQuotes(groupId)
        subscribeSelectedQuotes(groupId)
    }

    override fun subscribeFrequencySettings() {
        subscribeFrequencies()
        subscribeUserFrequency()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun subscribeUser() {
        launch {
            var subscription: ListenerRegistration? = null
            _userFlow.subscriptionCount.collect { subscribers ->
                if (subscribers > 0) {
                    subscription = dbInstance
                        .collection(COLLECTION_USER)
                        .document(authInstance.currentUser?.uid ?: "_")
                        .addSnapshotListener { value, error ->
                            if (error != null) {
                                _userFlow.tryEmit(ResultDataModel.error(error))
                                return@addSnapshotListener
                            }
                            val userModel = value?.toObject<UserDataModel>()
                            _userFlow.tryEmit(ResultDataModel.success(userModel))
                        }
                } else {
                    subscription?.let {
                        it.remove()
                        _userFlow.resetReplayCache()
                        cancel()
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun saveNewGroup(group: GroupDataModel): ResultDataModel<GroupDataModel> =
        suspendCancellableCoroutine { continuation ->
            val uuid = UUID.randomUUID().toString()
            dbInstance.collection(COLLECTION_PERSONAL)
                .document(localDataSource.token)
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

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun updateUserFrequency(settingId: Long): ResultDataModel<Long> =
        suspendCancellableCoroutine { continuation ->
            dbInstance.collection(COLLECTION_PERSONAL)
                .document(localDataSource.token)
                .set(hashMapOf(FREQUENCY_FIELD to settingId))
                .addOnSuccessListener {
                    localDataSource.frequency = settingId
                    continuation.resume(ResultDataModel.success(settingId)) {}
                }
                .addOnFailureListener { exception ->
                    continuation.resume(ResultDataModel.error(exception)) {}
                }
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun saveNewQuote(
        groupId: String, quote: QuoteDataModel
    ): ResultDataModel<QuoteDataModel> {
        mutex.withLock {
            return suspendCancellableCoroutine { continuation ->
                val uuid = UUID.randomUUID().toString()
                val currentDocument = dbInstance.collection(COLLECTION_PERSONAL)
                    .document(localDataSource.token)
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
                        currentDocument.update(QUOTES_COUNT_FIELD, FieldValue.increment(1))
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun deleteQuote(
        groupId: String,
        quoteId: String,
        isSelected: Boolean
    ): ResultDataModel<String> {
        mutex.withLock {
            return suspendCancellableCoroutine { continuation ->
                deleteQuote(
                    groupId = groupId,
                    quoteId = quoteId,
                    isSelected = isSelected,
                    onSuccess = {
                        continuation.resume(ResultDataModel.success(quoteId)) {}
                    },
                    onFailure = {
                        continuation.resume(ResultDataModel.error(it)) {}
                    }
                )
            }
        }
    }

    private fun deleteQuote(
        groupId: String,
        quoteId: String,
        isSelected: Boolean,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit,
    ) {
        val currentDocument = dbInstance.collection(COLLECTION_PERSONAL)
            .document(localDataSource.token)
            .collection(COLLECTION_GROUPS)
            .document(groupId)
        currentDocument
            .collection(COLLECTION_QUOTES)
            .document(quoteId)
            .delete()
            .addOnSuccessListener {
                currentDocument.update(QUOTES_COUNT_FIELD, FieldValue.increment(-1))
                if (isSelected) removeQuoteFromSelected(groupId, quoteId)
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun deleteGroup(groupId: String): ResultDataModel<String> {
        mutex.withLock {
            return suspendCancellableCoroutine { continuation ->
                val currentDocument = dbInstance.collection(COLLECTION_PERSONAL)
                    .document(localDataSource.token)
                    .collection(COLLECTION_GROUPS)
                    .document(groupId)
                currentDocument.collection(COLLECTION_QUOTES).get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            deleteQuote(
                                groupId = groupId,
                                quoteId = document.data["id"] as String,
                                isSelected = true,
                                onSuccess = {},
                                onFailure = {}
                            )
                        }
                        dbInstance.collection(COLLECTION_PERSONAL)
                            .document(localDataSource.token)
                            .collection(COLLECTION_SELECTION)
                            .document(groupId)
                            .delete()
                        currentDocument.delete()
                            .addOnSuccessListener {
                                continuation.resume(ResultDataModel.success(groupId)) {}
                            }
                            .addOnFailureListener { exception ->
                                continuation.resume(ResultDataModel.error(exception)) {}
                            }
                    }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun saveSelection(
        quote: SelectedQuoteDataModel,
        isSelected: Boolean
    ): ResultDataModel<SelectedQuoteDataModel> {
        mutex.withLock {
            return suspendCancellableCoroutine { continuation ->
                val currentDocument = dbInstance.collection(COLLECTION_PERSONAL)
                    .document(localDataSource.token)
                    .collection(COLLECTION_SELECTION)
                    .document(quote.groupId ?: "")
                currentDocument.get().addOnCompleteListener {
                    if (it.result.exists()) {
                        if (isSelected) {
                            currentDocument
                                .collection(COLLECTION_SELECTED_QUOTES)
                                .document(quote.id ?: "")
                                .set(quote.apply { selectedBy = localDataSource.token })
                                .addOnSuccessListener {
                                    continuation.resume(ResultDataModel.success(quote)) {}
                                }
                                .addOnFailureListener { exception ->
                                    continuation.resume(ResultDataModel.error(exception)) {}
                                }
                            currentDocument.update(
                                SELECTED_QUOTES_COUNT_FIELD,
                                FieldValue.increment(1)
                            )
                        } else {
                            currentDocument
                                .collection(COLLECTION_SELECTED_QUOTES)
                                .document(quote.id ?: "")
                                .delete()
                            currentDocument.update(
                                SELECTED_QUOTES_COUNT_FIELD,
                                FieldValue.increment(-1)
                            )
                        }
                    } else {
                        currentDocument.set(
                            SelectedGroupDataModel(
                                groupId = quote.groupId,
                                selectedQuotesCount = 1
                            )
                        ).addOnCompleteListener {
                            currentDocument
                                .collection(COLLECTION_SELECTED_QUOTES)
                                .document(quote.id ?: "")
                                .set(quote.apply { selectedBy = localDataSource.token })
                        }
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun getSelectedQuotes(): ResultDataModel<List<SelectedQuoteDataModel>> =
        suspendCancellableCoroutine { continuation ->
            dbInstance.collectionGroup(COLLECTION_SELECTED_QUOTES)
                .whereEqualTo("selectedBy", localDataSource.token)
                .get()
                .addOnSuccessListener { task ->
                    val quotes = mutableListOf<SelectedQuoteDataModel>()
                    for (doc in task.documents) {
                        doc.toObject<SelectedQuoteDataModel>()?.let {
                            quotes.add(it)
                        }
                    }
                    continuation.resume(ResultDataModel.success(quotes)) {}

                }
                .addOnFailureListener { exception ->
                    continuation.resume(ResultDataModel.error(exception)) {}
                }
        }

    override suspend fun updateSelectedQuote(
        groupId: String,
        quoteId: String,
        shownTime: Long
    ) {
        dbInstance.collection(COLLECTION_PERSONAL)
            .document(localDataSource.token)
            .collection(COLLECTION_SELECTION)
            .document(groupId)
            .collection(COLLECTION_SELECTED_QUOTES)
            .document(quoteId)
            .update(SHOWN_AT_FIELD, shownTime)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun editQuote(
        groupId: String,
        quoteId: String,
        editedQuote: String,
        editedAuthor: String
    ): ResultDataModel<String> {
        mutex.withLock {
            return suspendCancellableCoroutine { continuation ->
                val updateMap = hashMapOf<String, String>()
                updateMap[QUOTE_QUOTE_FIELD] = editedQuote
                updateMap[QUOTE_AUTHOR_FIELD] = editedAuthor
                dbInstance.collection(COLLECTION_PERSONAL)
                    .document(localDataSource.token)
                    .collection(COLLECTION_GROUPS)
                    .document(groupId)
                    .collection(COLLECTION_QUOTES)
                    .document(quoteId)
                    .update(updateMap as Map<String, String>)
                    .addOnSuccessListener {
                        continuation.resume(ResultDataModel.success(quoteId)) {}
                    }
                    .addOnFailureListener { exception ->
                        continuation.resume(ResultDataModel.error(exception)) {}
                    }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun editGroup(
        groupId: String,
        editedName: String,
        editedDescription: String
    ): ResultDataModel<String> {
        mutex.withLock {
            return suspendCancellableCoroutine { continuation ->
                val updateMap = hashMapOf<String, String>()
                updateMap[GROUP_NAME_FIELD] = editedName
                updateMap[GROUP_DESCRIPTION_FIELD] = editedDescription
                dbInstance.collection(COLLECTION_PERSONAL)
                    .document(localDataSource.token)
                    .collection(COLLECTION_GROUPS)
                    .document(groupId)
                    .update(updateMap as Map<String, String>)
                    .addOnSuccessListener {
                        continuation.resume(ResultDataModel.success(groupId)) {}
                    }
                    .addOnFailureListener { exception ->
                        continuation.resume(ResultDataModel.error(exception)) {}
                    }
            }
        }
    }

    private fun removeQuoteFromSelected(groupId: String, quoteId: String) {
        val currentDocument = dbInstance.collection(COLLECTION_PERSONAL)
            .document(localDataSource.token)
            .collection(COLLECTION_SELECTION)
            .document(groupId)
        currentDocument.collection(COLLECTION_SELECTED_QUOTES)
            .document(quoteId)
            .delete()
            .addOnSuccessListener {
                currentDocument.update(
                    SELECTED_QUOTES_COUNT_FIELD,
                    FieldValue.increment(-1)
                )
            }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun saveUser(): String {
        val token = getTokenFromUser() ?: ""
        return suspendCancellableCoroutine { continuation ->
            authInstance.currentUser?.let {
                if (token.isEmpty()) {
                    dbInstance.collection(COLLECTION_USER).document(it.uid)
                        .set(
                            UserDataModel(
                                token = localDataSource.token,
                                fullName = it.displayName,
                                email = it.email
                            )
                        )
                        .addOnCompleteListener { continuation.resume(token, {}) }
                } else {
                    localDataSource.token = token
                    continuation.resume(token, {})
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun getTokenFromUser(): String? =
        suspendCancellableCoroutine { continuation ->
            dbInstance.collection(COLLECTION_USER)
                .document(authInstance.currentUser?.uid ?: "_")
                .get()
                .addOnCompleteListener { snapShot ->
                    continuation.resume(snapShot.result.data?.get(TOKEN_FIELD) as? String, {})
                }
                .addOnFailureListener {
                    continuation.resume(null, {})
                }
        }

    private suspend fun saveCurrentToken(): String {
        var token: String = getTokenFromUser() ?: ""
        if (token.isEmpty()) {
            if (localDataSource.token.isEmpty()) {
                localDataSource.token = UUID.randomUUID().toString()
            }
            token = localDataSource.token
        } else {
            localDataSource.token = token
        }
        return token
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun subscribeGroups() {
        launch {
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
                    subscription?.let { listener ->
                        listener.remove()
                        _groupsFlow.resetReplayCache()
                        cancel()
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun subscribeUserGroups() {
        launch {
            var subscription: ListenerRegistration? = null
            _userGroupsFlow.subscriptionCount.collect { subscribers ->
                if (subscribers > 0) {
                    subscription = dbInstance.collection(COLLECTION_PERSONAL)
                        .document(localDataSource.token).collection(COLLECTION_GROUPS)
                        .addSnapshotListener { value, error ->
                            if (error != null) {
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
                    subscription?.let { listener ->
                        listener.remove()
                        _userGroupsFlow.resetReplayCache()
                        cancel()
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
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
                        _quotesFlow.resetReplayCache()
                        currentGroupId = null
                        cancel()
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun subscribeUserQuotes(groupId: String) {
        launch {
            var subscription: ListenerRegistration? = null
            _userQuotesFlow.subscriptionCount.collect { subscribers ->
                if (subscribers > 0) {
                    subscription = dbInstance.collection(COLLECTION_PERSONAL)
                        .document(localDataSource.token).collection(COLLECTION_GROUPS)
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
                        _userQuotesFlow.resetReplayCache()
                        currentGroupId = null
                        cancel()
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun subscribeFrequencies() {
        launch {
            var subscription: ListenerRegistration? = null
            _frequenciesFlow.subscriptionCount.collect { subscribers ->
                if (subscribers > 0) {
                    subscription = dbInstance.collection(COLLECTION_FREQUENCY)
                        .addSnapshotListener { value, error ->
                            if (error != null) {
                                _frequenciesFlow.tryEmit(ResultDataModel.error(error))
                                return@addSnapshotListener
                            }
                            value?.let { snapShot ->
                                val setting = mutableListOf<FrequencyDataModel>()
                                for (doc in snapShot) {
                                    setting.add(doc.toObject())
                                }
                                _frequenciesFlow.tryEmit(ResultDataModel.success(setting))
                            }
                        }
                } else {
                    subscription?.let {
                        it.remove()
                        _frequenciesFlow.resetReplayCache()
                        cancel()
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun subscribeUserFrequency() {
        launch {
            var subscription: ListenerRegistration? = null
            _userFrequencyFlow.subscriptionCount.collect { subscribers ->
                if (subscribers > 0) {
                    subscription = dbInstance.collection(COLLECTION_PERSONAL)
                        .document(localDataSource.token)
                        .addSnapshotListener { value, error ->
                            if (error != null) {
                                _userFrequencyFlow.tryEmit(ResultDataModel.error(error))
                                return@addSnapshotListener
                            }
                            val frequency = value?.data?.get(FREQUENCY_FIELD) as? Long
                            localDataSource.frequency = frequency ?: DEFAULT_FREQUENCY_VALUE
                            _userFrequencyFlow.tryEmit(ResultDataModel.success(frequency))
                        }
                } else {
                    subscription?.let {
                        it.remove()
                        _userFrequencyFlow.resetReplayCache()
                        cancel()
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun subscribeSelectedGroups() {
        launch {
            var subscription: ListenerRegistration? = null
            _selectedGroupsFlow.subscriptionCount.collect { subscribers ->
                if (subscribers > 0) {
                    subscription =
                        dbInstance.collection(COLLECTION_PERSONAL)
                            .document(localDataSource.token)
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
                                        ResultDataModel.success(selections)
                                    )
                                }
                            }
                } else {
                    subscription?.let { listener ->
                        listener.remove()
                        _selectedGroupsFlow.resetReplayCache()
                        cancel()
                    }
                }
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun subscribeSelectedQuotes(groupId: String) {
        launch {
            var subscription: ListenerRegistration? = null
            _selectedQuotesFlow.subscriptionCount.collect { subscribers ->
                if (subscribers > 0) {
                    subscription =
                        dbInstance.collection(COLLECTION_PERSONAL)
                            .document(localDataSource.token)
                            .collection(COLLECTION_SELECTION)
                            .document(groupId)
                            .collection(COLLECTION_SELECTED_QUOTES)
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
                                    _selectedQuotesFlow.tryEmit(
                                        ResultDataModel.success(selections)
                                    )
                                }
                            }
                } else {
                    subscription?.let {
                        it.remove()
                        _selectedQuotesFlow.resetReplayCache()
                        currentGroupId = null
                        cancel()
                    }
                }
            }
        }
    }

    companion object {
        const val DEFAULT_FREQUENCY_VALUE = 24L
        private const val COLLECTION_PERSONAL = "personal"
        private const val COLLECTION_USER = "users"
        private const val COLLECTION_SELECTION = "selection"
        private const val COLLECTION_FREQUENCY = "frequency"
        private const val COLLECTION_GROUPS = "groups"
        private const val COLLECTION_QUOTES = "quotes"
        private const val COLLECTION_SELECTED_QUOTES = "selectedquotes"
        private const val TOKEN_FIELD = "token"
        private const val FREQUENCY_FIELD = "frequency"
        private const val SHOWN_AT_FIELD = "shownAt"
        private const val SELECTED_QUOTES_COUNT_FIELD = "selectedQuotesCount"
        private const val QUOTES_COUNT_FIELD = "quotesCount"
        private const val GROUP_NAME_FIELD = "name"
        private const val GROUP_DESCRIPTION_FIELD = "description"
        private const val QUOTE_QUOTE_FIELD = "quote"
        private const val QUOTE_AUTHOR_FIELD = "author"
    }
}
