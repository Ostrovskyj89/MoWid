package com.eleks.data.repository

import com.eleks.data.firebase.source.FirebaseDataSource
import com.eleks.data.mapper.mapToDomain
import com.eleks.data.model.GroupDataModel
import com.eleks.data.model.Status
import com.eleks.data.model.merge
import com.eleks.domain.model.GroupPhraseModel
import com.eleks.domain.repository.MotivationPhraseRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MotivationPhraseRepositoryImpl @Inject constructor(
    private val firebaseDataSource: FirebaseDataSource
) : MotivationPhraseRepository {

    override fun getGroupsFlow(): Flow<List<GroupPhraseModel>> =
        combine(
            firebaseDataSource.userGroupsFlow,
            firebaseDataSource.groupsFlow,
            firebaseDataSource.selectedGroupsFlow
        ) { groups, userGroups, selectedGroups ->
            val allGroups = groups.merge(userGroups)
            if (selectedGroups.status == Status.ERROR) {
                throw selectedGroups.error ?: Exception("Unknown exception")
            }
            when (allGroups.status) {
                Status.SUCCESS -> allGroups.data?.map { model -> model.mapToDomain(selectedGroups.data.orEmpty()) }
                    ?: emptyList()
                Status.ERROR -> throw allGroups.error ?: Exception("Unknown exception")
            }
        }

    override suspend fun addGroup(name: String, description: String) {
        firebaseDataSource.saveNewGroup(GroupDataModel(
            name = name,
            description = description
        ))
    }
}

