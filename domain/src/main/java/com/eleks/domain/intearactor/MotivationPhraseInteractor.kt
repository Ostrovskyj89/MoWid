package com.eleks.domain.intearactor

import com.eleks.domain.model.GroupPhraseModel
import com.eleks.domain.repository.MotivationPhraseRepository
import kotlinx.coroutines.flow.Flow

class MotivationPhraseInteractor(private val motivationPhraseRepository: MotivationPhraseRepository) {

    fun getGroupPhraseListFlow(): Flow<List<GroupPhraseModel>> =
        motivationPhraseRepository.getGroupsFlow()

    suspend fun addGroup(name: String, description: String) {
        motivationPhraseRepository.addGroup(name, description)
    }
}
