package com.eleks.domain.model

data class FrequenciesModel(
    val selectedFrequency: FrequencyModel?,
    val settings: List<FrequencyModel>
)

data class FrequencyModel(
    val frequencyId: Long,
    val value: String,
)
