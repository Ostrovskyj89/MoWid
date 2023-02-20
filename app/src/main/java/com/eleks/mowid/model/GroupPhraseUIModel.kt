package com.eleks.mowid.model

import com.eleks.domain.model.GroupPhraseModel

data class GroupPhraseUIModel(
    val id: String,
    val name: String,
    val description: String,
    val count: Int,
    val selectedCount: Int
)

fun GroupPhraseUIModel.toDomainModel() = GroupPhraseModel(
    id = id,
    name = name,
    description = description,
    count = count,
    selectedCount = selectedCount
)

fun GroupPhraseModel.toUIModel() = GroupPhraseUIModel(
    id = id,
    name = name,
    description = description,
    count = count,
    selectedCount = selectedCount
)

fun List<GroupPhraseUIModel>.toDomainModel() = map { it.toDomainModel() }

fun List<GroupPhraseModel>.toUIModel() = map { it.toUIModel() }
