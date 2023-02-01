package com.eleks.data.firebase.source.impl

import com.eleks.data.firebase.source.FirebaseDataSource
import com.eleks.data.model.GroupDataModel
import com.eleks.data.model.QuoteDataModel
import com.eleks.data.model.ResultDataModel
import com.eleks.data.model.SelectedGroupDataModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseDataSourceImpl @Inject constructor() : FirebaseDataSource {

    private val dbInstance = Firebase.firestore
    private val userInstance = FirebaseAuth.getInstance().currentUser

    private val _groupsFlow = MutableStateFlow<ResultDataModel<List<GroupDataModel?>>>(
        ResultDataModel.success(null)
    )

    private val _selectedGroups = MutableStateFlow<ResultDataModel<List<SelectedGroupDataModel?>>>(
        ResultDataModel.success(null)
    )

    val groupsFlow = _groupsFlow.filterNotNull()

    val selectedGroupsFlow = _selectedGroups.filterNotNull()

    override fun getGroups() {
        dbInstance.collection("groups")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    _groupsFlow.value = ResultDataModel.error(error.message ?: "")
                    return@addSnapshotListener
                }
                if (value != null && !value.isEmpty) {
                    val groups = value.documents.map { it.toObject<GroupDataModel>() }
                    _groupsFlow.value = ResultDataModel.success(groups)
                }
            }
    }

    override fun getUserGroups() {
        if (userInstance != null) {
            dbInstance.collection("group_${userInstance.uid}")
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        _groupsFlow.value = ResultDataModel.error(error.message ?: "")
                        return@addSnapshotListener
                    }

                    if (value != null && !value.isEmpty) {
                        val groups = value.documents.map { it.toObject<GroupDataModel>() }
                        _groupsFlow.value = ResultDataModel.success(groups)
                    }
                }
        }
    }

    override fun getUserSelectionGroups() {
        if (userInstance != null) {
            dbInstance.collection("group_${userInstance.uid}")
                .addSnapshotListener { value, error ->
                    if (error != null) {
                        _groupsFlow.value = ResultDataModel.error(error.message ?: "")
                        return@addSnapshotListener
                    }

                    if (value != null && !value.isEmpty) {
                        val groups = value.documents.map { it.toObject<SelectedGroupDataModel>() }
                        _selectedGroups.value = ResultDataModel.success(groups)
                    }
                }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun saveNewGroup(group: GroupDataModel): ResultDataModel<GroupDataModel> =
        suspendCancellableCoroutine { continuation ->
            val uuid = UUID.randomUUID().toString()
            userInstance?.let {
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
            userInstance?.let {
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
