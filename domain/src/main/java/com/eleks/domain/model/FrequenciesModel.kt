package com.eleks.domain.model

data class FrequenciesModel(
    val selectedFrequency: FrequencyModel?,
    val frequencies: List<FrequencyModel>
)

data class FrequencyModel(
    val frequencyId: Long,
)
