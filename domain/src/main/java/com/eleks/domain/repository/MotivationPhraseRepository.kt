package com.eleks.domain.repository

import com.eleks.domain.model.GroupPhraseModel
import com.eleks.domain.model.UserModel
import kotlinx.coroutines.flow.Flow

interface MotivationPhraseRepository {

    fun getCurrentUser(): UserModel?

    fun getGroupsFlow(): Flow<List<GroupPhraseModel>>

    fun getQuotes(groupId: String): Flow<List<QuoteModel>>

    suspend fun addGroup(name: String, description: String)

    suspend fun addQuote(groupId: String, quote: String, author: String)

    suspend fun saveSelection(groupId: String, quoteId: String, shownAt: String, isSelected: Boolean)