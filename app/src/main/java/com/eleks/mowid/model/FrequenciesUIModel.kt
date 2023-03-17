package com.eleks.mowid.model

import com.eleks.domain.model.FrequencyModel
import com.eleks.domain.model.FrequenciesModel

data class FrequenciesUIModel(
    val selectedSetting: FrequencyUIModel?,
    val settings: List<FrequencyUIModel>
)

data class FrequencyUIModel(
    val frequencyId: Long,
    val value: String
)

fun FrequenciesUIModel.toDomainModel() = FrequenciesModel(
    selectedFrequency = selectedSetting?.toDomainModel(),
    settings = settings.toDomainModel()
)

fun FrequenciesModel.toUIModel() = FrequenciesUIModel(
    selectedSetting = selectedFrequency?.toUIModel(),
    settings = settings.toUIModel()
)

fun FrequencyUIModel.toDomainModel() = FrequencyModel(
    frequencyId = frequencyId,
    value = value
)

fun FrequencyModel.toUIModel() = FrequencyUIModel(
    frequencyId = frequencyId,
    value = value
)

fun List<FrequencyUIModel>.toDomainModel() = map { it.toDomainModel() }

fun List<FrequencyModel>.toUIModel() = map { it.toUIModel() }
