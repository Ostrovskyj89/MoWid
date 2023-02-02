package com.eleks.data.firebase.source

import com.eleks.data.model.GroupDataModel
import com.eleks.data.model.QuoteDataModel
import com.eleks.data.model.ResultDataModel
import com.eleks.data.model.SelectedGroupDataModel
import kotlinx.coroutines.flow.MutableStateFlow

interface FirebaseDataSource {

     val groupsFlow: MutableStateFlow<ResultDataModel<List<GroupDataModel?>>>

     val userGroupsFlow: MutableStateFlow<ResultDataModel<List<GroupDataModel?>>>

     val selectedGroupsFlow: MutableStateFlow<ResultDataModel<List<SelectedGroupDataModel?>>>

    fun subscribeGroups()

    fun subscribeUserGroups()

    fun subscribeSelectedGroups()

    suspend fun saveNewGroup(group: GroupDataModel): ResultDataModel<GroupDataModel>

    suspend fun saveNewQuote(groupId: String, quote: QuoteDataModel): ResultDataModel<QuoteDataModel>

    suspend fun saveSelection(selectedGroup: SelectedGroupDataModel): ResultDataModel<SelectedGroupDataModel>
}
