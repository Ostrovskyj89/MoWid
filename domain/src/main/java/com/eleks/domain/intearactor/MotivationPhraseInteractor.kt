package com.eleks.domain.intearactor

import com.eleks.domain.model.GroupPhraseModel
import com.eleks.domain.repository.MotivationPhraseRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MotivationPhraseInteractor(private val motivationPhraseRepository: MotivationPhraseRepository) {

    fun getGroupPhraseListFlow(): Flow<List<GroupPhraseModel>> {
        return flow {
            delay(1500)
            val tempResult = listOf(
                GroupPhraseModel(
                    name = "Name 1",
                    description = "Description 1",
                    count = 10,
                    selectedCount = 15
                ),
                GroupPhraseModel(
                    name = "Name 2",
                    description = "Description 2",
                    count = 10,
                    selectedCount = 15
                ),
                GroupPhraseModel(
                    name = "Name 3",
                    description = "Description 3",
                    count = 10,
                    selectedCount = 15
                ),
            )
            emit(tempResult)
        }
    }

    suspend fun addGroup(name: String, description: String) {

    }
}