package com.eleks.data.mapper

import com.eleks.data.model.GroupDataModel
import com.eleks.data.model.SelectedGroupDataModel
import com.eleks.domain.model.GroupPhraseModel

fun GroupDataModel.mapToDomain(selectedGroups: List<SelectedGroupDataModel>?) = GroupPhraseModel(
    name = name ?: "",
    description = description ?: "",
    count = quotes?.size ?: 0,
    selectedCount = calculateSelectedCount(this, selectedGroups)
)

fun calculateSelectedCount(
    groupDataModel: GroupDataModel,
    selectedGroups: List<SelectedGroupDataModel>?
): Int {
    selectedGroups?.firstOrNull { groupDataModel.id == it.groupId }?.let { group ->
        return groupDataModel.quotes?.mapNotNull { model ->
            group.selectedQuotes?.firstOrNull { it.id == model.id }
        }?.size ?: groupDataModel.quotes?.size ?: 0
    }
    return groupDataModel.quotes?.size ?: 0
}
