package com.eleks.mowid.model

import androidx.annotation.StringRes
import com.eleks.data.model.*
import com.eleks.domain.model.FrequencyModel
import com.eleks.domain.model.FrequenciesModel
import com.eleks.mowid.R

data class FrequenciesUIModel(
    val selectedFrequency: FrequencyUIModel?,
    val frequencies: List<FrequencyUIModel>
)

data class FrequencyUIModel(
    val frequencyId: Long,
    @StringRes val value: Int
)

fun FrequenciesUIModel.toDomainModel() = FrequenciesModel(
    selectedFrequency = selectedFrequency?.toDomainModel(),
    frequencies = frequencies.toDomainModel()
)

fun FrequenciesModel.toUIModel() = FrequenciesUIModel(
    selectedFrequency = selectedFrequency?.toUIModel(),
    frequencies = frequencies.toUIModel()
)

fun FrequencyUIModel.toDomainModel() = FrequencyModel(
    frequencyId = frequencyId,
)

fun FrequencyModel.toUIModel() = FrequencyUIModel(
    frequencyId = frequencyId,
    value = when(frequencyId) {
        FREQUENCY_ID_168 -> R.string.frequency_id_168
        FREQUENCY_ID_120 -> R.string.frequency_id_120
        FREQUENCY_ID_48 -> R.string.frequency_id_48
        FREQUENCY_ID_24 -> R.string.frequency_id_24
        FREQUENCY_ID_12 -> R.string.frequency_id_12
        FREQUENCY_ID_6 -> R.string.frequency_id_6
        else -> R.string.frequency_id_24
    }
)

fun List<FrequencyUIModel>.toDomainModel() = map { it.toDomainModel() }

fun List<FrequencyModel>.toUIModel() = map { it.toUIModel() }
