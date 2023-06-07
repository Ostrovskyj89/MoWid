package com.eleks.domain.repository

import com.eleks.domain.model.GroupPhraseModel
import com.eleks.domain.model.QuoteModel
import com.eleks.domain.model.FrequenciesModel
import kotlinx.coroutines.flow.Flow

interface MotivationPhraseRepository {

    fun getGroupsFlow(): Flow<List<GroupPhraseModel>>

    fun getQuotes(groupId: String): Flow<List<QuoteModel>>

    fun getFrequencySettingsFlow() : Flow<FrequenciesModel>

    fun getRandomMeme() : Flow<String>

    suspend fun addGroup(name: String, description: String)

    suspend fun addQuote(groupId: String, quote: String, author: String)

    suspend fun updateUserFrequency(id: Long)

    suspend fun deleteQuote(groupId: String, quoteId: String, isSelected: Boolean)

    suspend fun deleteGroup(id: String)

    suspend fun saveSelection(
        groupId: String,
        quoteId: String,
        quote: String?,
        author: String?,
        memeUrl: String?,
        isSelected: Boolean
    )

    suspend fun editQuote(
        groupId: String,
        quoteId: String,
        editedQuote: String,
        editedAuthor: String
    )

    suspend fun editGroup(
        groupId: String,
        editedName: String,
        editedDescription: String
    )
}
