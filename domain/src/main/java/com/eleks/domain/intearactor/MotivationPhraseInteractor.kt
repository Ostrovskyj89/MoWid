package com.eleks.domain.intearactor

import com.eleks.domain.model.GroupPhraseModel
import com.eleks.domain.model.QuoteModel
import com.eleks.domain.repository.MotivationPhraseRepository
import kotlinx.coroutines.flow.Flow

class MotivationPhraseInteractor(private val motivationPhraseRepository: MotivationPhraseRepository) {

    fun getGroupPhraseListFlow(): Flow<List<GroupPhraseModel>> =
        motivationPhraseRepository.getGroupsFlow()

    fun getQuotesListFlow(groupId: String): Flow<List<QuoteModel>> =
        motivationPhraseRepository.getQuotes(groupId)

    suspend fun addGroup(name: String, description: String) {
        motivationPhraseRepository.addGroup(name, description)
    }

    suspend fun addQuote(groupId: String, quote: String, author: String) {
        motivationPhraseRepository.addQuote(groupId, quote, author)
    }

    suspend fun saveSelection(
        groupId: String,
        quoteId: String,
        shownAt: String,
        isSelected: Boolean
    ) {
        motivationPhraseRepository.saveSelection(groupId, quoteId, shownAt, isSelected)
    }

    fun getCurrentUser() = motivationPhraseRepository.getCurrentUser()
}
