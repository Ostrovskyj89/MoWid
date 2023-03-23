package com.eleks.data.mapper

import com.eleks.data.firebase.source.impl.FirebaseDataSourceImpl
import com.eleks.data.model.FrequencyDataModel
import com.eleks.domain.model.FrequencyModel
import com.eleks.domain.model.FrequenciesModel

fun List<FrequencyDataModel>.toDomain(userSetting: Long) = FrequenciesModel(
    selectedFrequency = this.firstOrNull { it.frequencyId == userSetting }?.toDomain(),
    frequencies = this.map { it.toDomain() }
)

fun FrequencyDataModel.toDomain() = FrequencyModel(
    frequencyId = frequencyId ?:FirebaseDataSourceImpl.DEFAULT_FREQUENCY_VALUE,
)
