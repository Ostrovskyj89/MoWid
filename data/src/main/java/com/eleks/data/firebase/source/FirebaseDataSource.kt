package com.eleks.data.firebase.source

import com.eleks.data.model.GroupDataModel
import com.eleks.data.model.QuoteDataModel
import com.eleks.data.model.ResultDataModel
import com.eleks.data.model.SelectedGroupDataModel

interface FirebaseDataSource {

    fun getGroups()

    fun getUserGroups()

    fun getUserSelectionGroups()

    suspend fun saveNewGroup(group: GroupDataModel): ResultDataModel<GroupDataModel>

    suspend fun saveNewQuote(groupId: String, quote: QuoteDataModel): ResultDataModel<QuoteDataModel>

    suspend fun saveSelection(selectedGroup: SelectedGroupDataModel): ResultDataModel<SelectedGroupDataModel>
}
