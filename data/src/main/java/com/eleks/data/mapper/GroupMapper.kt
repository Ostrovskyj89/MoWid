package com.eleks.data.mapper

import com.eleks.data.model.GroupDataModel
import com.eleks.data.model.SelectedGroupDataModel
import com.eleks.domain.model.GroupPhraseModel

fun GroupDataModel.mapToDomain(selectedGroups: List<SelectedGroupDataModel>) = GroupPhraseModel(
    id = id.orEmpty(),
    name = name.orEmpty(),
    description = description.orEmpty(),
    count = quotesCount ?: 0,
    selectedCount = calculateSelectedCount(this, selectedGroups),
    canBeDeleted = canBeDeleted ?: false
)

fun calculateSelectedCount(
    groupDataModel: GroupDataModel,
    selectedGroups: List<SelectedGroupDataModel>
): Int {
    selectedGroups.firstOrNull { groupDataModel.id == it.groupId }?.let { group ->
        return group.selectedQuotesCount ?: 0
    }
    return 0
}
