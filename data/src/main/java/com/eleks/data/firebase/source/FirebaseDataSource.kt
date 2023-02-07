package com.eleks.data.firebase.source

import com.eleks.data.model.GroupDataModel
import com.eleks.data.model.QuoteDataModel
import com.eleks.data.model.ResultDataModel
import com.eleks.data.model.SelectedGroupDataModel
import kotlinx.coroutines.flow.Flow

interface FirebaseDataSource {

     val groupsFlow: Flow<ResultDataModel<List<GroupDataModel?>>>

     val userGroupsFlow: Flow<ResultDataModel<List<GroupDataModel?>>>

     val selectedGroupsFlow: Flow<ResultDataModel<List<SelectedGroupDataModel?>>>

    suspend fun saveNewGroup(group: GroupDataModel): ResultDataModel<GroupDataModel>

    suspend fun saveNewQuote(groupId: String, quote: QuoteDataModel): ResultDataModel<QuoteDataModel>

    suspend fun saveSelection(selectedGroup: SelectedGroupDataModel): ResultDataModel<SelectedGroupDataModel>
}
