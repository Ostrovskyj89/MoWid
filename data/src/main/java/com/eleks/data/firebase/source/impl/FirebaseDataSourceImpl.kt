package com.eleks.data.firebase.source.impl

import com.eleks.data.firebase.source.FirebaseDataSource
import com.eleks.data.model.GroupDataModel
import com.eleks.data.model.QuoteDataModel
import com.eleks.data.model.ResultDataModel
import com.eleks.data.model.SelectedGroupDataModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseDataSourceImpl @Inject constructor(
    private val dbInstance: FirebaseFirestore,
    private val authInstance: FirebaseAuth
) : FirebaseDataSource {

    private val _groupsFlow = MutableStateFlow<ResultDataModel<List<GroupDataModel?>>>(
        ResultDataModel.success(null)
    )

    private val _userGroupsFlow = MutableStateFlow<ResultDataModel<List<GroupDataModel?>>>(
        ResultDataModel.success(null)
    )

    private val _selectedGroupsFlow = MutableStateFlow<ResultDataModel<List<SelectedGroupDataModel?>>>(
        ResultDataModel.success(null)
    )

    override val groupsFlow = _groupsFlow

    override val userGroupsFlow = _userGroupsFlow

    override val selectedGroupsFlow = _selectedGroupsFlow

    override fun subscribeGroups() {
        dbInstance.collection("groups")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    _groupsFlow.value = ResultDataModel.error(error.message ?: "")
                    return@addSnapshotListener
                }
                if (value != null && !value.isEmpty) {
                    val groups = mutableListOf<GroupDataModel>()
                    for (doc in value) {
                        groups.add(doc.toObject())
                    }
                    _groupsFlow.value = ResultDataModel.success(groups)
                }
            }
    }

    override fun subscribeUserGroups() {
        if (authInstance.currentUser != null) {
            dbInstance.collection("group_${authInstance.currentUser?.uid}")
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        _userGroupsFlow.value = ResultDataModel.error(error.message ?: "")
                        return@addSnapshotListener
                    }

                    if (value != null && !value.isEmpty) {
                        val groups = mutableListOf<GroupDataModel>()
                        for (doc in value) {
                            groups.add(doc.toObject())
                        }
                        _userGroupsFlow.value = ResultDataModel.success(groups)
                    }
                }

        }
    }

    override fun subscribeSelectedGroups() {
        if (authInstance.currentUser != null) {
            dbInstance.collection("group_${authInstance.currentUser?.uid}")
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        _selectedGroupsFlow.value = ResultDataModel.error(error.message ?: "")
                        return@addSnapshotListener
                    }

                    if (value != null && !value.isEmpty) {
                        val selections = mutableListOf<SelectedGroupDataModel>()
                        for (doc in value) {
                            selections.add(doc.toObject())
                        }
                        _selectedGroupsFlow.value = ResultDataModel.success(selections)
                    }
                }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun saveNewGroup(group: GroupDataModel): ResultDataModel<GroupDataModel> =
        suspendCancellableCoroutine { continuation ->
            val uuid = UUID.randomUUID().toString()
            authInstance.currentUser?.let {
                val listener = dbInstance.collection("group_${it.uid}").document(uuid)
                    .addSnapshotListener { _, error ->
                        if (error != null) {
                            continuation.resume(ResultDataModel.error(error.message ?: "")) {}
                            return@addSnapshotListener
                        }
                        continuation.resume(ResultDataModel.success(group)) {}
                    }
                continuation.invokeOnCancellation { listener.remove() }
            }
        }

    override suspend fun saveNewQuote(
        groupId: String,
        quote: QuoteDataModel
    ): ResultDataModel<QuoteDataModel> {
        TODO("Not yet implemented")
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun saveSelection(selectedGroup: SelectedGroupDataModel): ResultDataModel<SelectedGroupDataModel> =
        suspendCancellableCoroutine { continuation ->
            val uuid = UUID.randomUUID().toString()
            authInstance.currentUser?.let {
                val listener = dbInstance.collection("selection_${it.uid}").document(uuid)
                    .addSnapshotListener { _, error ->
                        if (error != null) {
                            continuation.resume(ResultDataModel.error(error.message ?: "")) {}
                            return@addSnapshotListener
                        }
                        continuation.resume(ResultDataModel.success(selectedGroup)) {}
                    }
                continuation.invokeOnCancellation { listener.remove() }
            }
        }
}
