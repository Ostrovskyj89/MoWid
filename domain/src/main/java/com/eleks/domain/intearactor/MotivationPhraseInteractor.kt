package com.eleks.domain.intearactor

import com.eleks.domain.model.GroupPhraseModel
import com.eleks.domain.model.QuoteModel
import com.eleks.domain.model.FrequenciesModel
import com.eleks.domain.repository.MotivationPhraseRepository
import kotlinx.coroutines.flow.Flow

class MotivationPhraseInteractor(private val motivationPhraseRepository: MotivationPhraseRepository) {

    fun getGroupPhraseListFlow(): Flow<List<GroupPhraseModel>> =
        motivationPhraseRepository.getGroupsFlow()

    fun getQuotesListFlow(groupId: String): Flow<List<QuoteModel>> =
        motivationPhraseRepository.getQuotes(groupId)

    fun getFrequencySettingsFlow(): Flow<FrequenciesModel> =
        motivationPhraseRepository.getFrequencySettingsFlow()

    suspend fun addGroup(name: String, description: String) {
        motivationPhraseRepository.addGroup(name, description)
    }

    suspend fun addQuote(groupId: String, quote: String, author: String) {
        motivationPhraseRepository.addQuote(groupId, quote, author)
    }

    suspend fun updateUserFrequency(id: Long) {
        motivationPhraseRepository.updateUserFrequency(id)
    }

    suspend fun deleteQuote(groupId: String, quoteId: String, isSelected: Boolean) {
        motivationPhraseRepository.deleteQuote(groupId, quoteId, isSelected)
    }

    suspend fun deleteGroup(id: String) {
        motivationPhraseRepository.deleteGroup(id)
    }

    suspend fun saveSelection(
        groupId: String,
        quoteId: String,
        quote: String,
        author: String?,
        isSelected: Boolean
    ) {
        motivationPhraseRepository.saveSelection(
            groupId = groupId,
            quoteId = quoteId,
            quote = quote,
            author = author,
            isSelected = isSelected
        )
    }
}
