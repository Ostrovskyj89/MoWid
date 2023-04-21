package com.eleks.data.firebase.source

import com.eleks.data.model.GroupDataModel
import com.eleks.data.model.QuoteDataModel
import com.eleks.data.model.ResultDataModel
import com.eleks.data.model.SelectedGroupDataModel
import com.eleks.data.model.*
import kotlinx.coroutines.flow.Flow

interface FirebaseDataSource {

    val groupsFlow: Flow<ResultDataModel<List<GroupDataModel>>>

    val userGroupsFlow: Flow<ResultDataModel<List<GroupDataModel>>>

    val selectedGroupsFlow: Flow<ResultDataModel<List<SelectedGroupDataModel>>>

    val selectedQuotesFlow: Flow<ResultDataModel<List<SelectedQuoteDataModel>>>

    val quotesFlow: Flow<ResultDataModel<List<QuoteDataModel>>>

    val userQuotesFlow: Flow<ResultDataModel<List<QuoteDataModel>>>

    val frequenciesFlow: Flow<ResultDataModel<List<FrequencyDataModel>>>

    val userFrequencyFlow: Flow<ResultDataModel<Long>>

    val userFlow: Flow<ResultDataModel<UserDataModel>>

    fun signInSuccess()

    fun signOutSuccess()

    fun subscribeAllGroupsQuotes(groupId: String)

    fun subscribeFrequencySettings()

    fun subscribeUser()

    suspend fun getSelectedQuotes(): ResultDataModel<List<SelectedQuoteDataModel>>

    suspend fun updateSelectedQuote(groupId: String, quoteId: String, shownTime: Long)

    suspend fun saveNewGroup(group: GroupDataModel): ResultDataModel<GroupDataModel>

    suspend fun updateUserFrequency(settingId: Long): ResultDataModel<Long>

    suspend fun saveNewQuote(
        groupId: String,
        quote: QuoteDataModel
    ): ResultDataModel<QuoteDataModel>

    suspend fun deleteQuote(
        groupId: String,
        quoteId: String,
        isSelected: Boolean
    ): ResultDataModel<String>

    suspend fun deleteGroup(groupId: String): ResultDataModel<String>

    suspend fun saveSelection(
        quote: SelectedQuoteDataModel,
        isSelected: Boolean
    ): ResultDataModel<SelectedQuoteDataModel>

    suspend fun editQuote(
        groupId: String,
        quoteId: String,
        editedQuote: String,
        editedAuthor: String
    ): ResultDataModel<String>

    suspend fun editGroup(
        groupId: String,
        editedName: String,
        editedDescription: String
    ): ResultDataModel<String>
}
