package com.eleks.domain.repository

import com.eleks.domain.model.GroupPhraseModel
import kotlinx.coroutines.flow.Flow

interface MotivationPhraseRepository {

    fun getGroupsFlow(): Flow<List<GroupPhraseModel>>

    suspend fun addGroup(name: String, description: String)
}
