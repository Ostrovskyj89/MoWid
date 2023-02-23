package com.eleks.data.firebase.source

import com.eleks.data.model.GroupDataModel
import com.eleks.data.model.QuoteDataModel
import com.eleks.data.model.ResultDataModel
import com.eleks.data.model.SelectedGroupDataModel
import com.google.firebase.auth.FirebaseUser
import com.eleks.data.model.*
import kotlinx.coroutines.flow.Flow

interface FirebaseDataSource {

    val groupsFlow: Flow<ResultDataModel<List<GroupDataModel>>>

     val userGroupsFlow: Flow<ResultDataModel<List<GroupDataModel>>>

     val selectedGroupsFlow: Flow<ResultDataModel<List<SelectedGroupDataModel>>>

     val selectedQuotesFlow: Flow<ResultDataModel<List<SelectedQuoteDataModel>>>

     val quotesFlow: Flow<ResultDataModel<List<QuoteDataModel>>>

    val userQuotesFlow: Flow<ResultDataModel<List<QuoteDataModel>>>

    val currentUser: FirebaseUser?

    fun subscribeAllGroupsQuotes(groupId: String)

    suspend fun saveNewGroup(group: GroupDataModel): ResultDataModel<GroupDataModel>

    suspend fun saveNewQuote(groupId: String, quote: QuoteDataModel): ResultDataModel<QuoteDataModel>

    suspend fun saveSelection(groupId: String, quote: SelectedQuoteDataModel, isSelected: Boolean): ResultDataModel<SelectedQuoteDataModel>
}
