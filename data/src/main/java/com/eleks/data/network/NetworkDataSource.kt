package com.eleks.data.network

import com.eleks.data.model.MemeDataModel
import kotlinx.coroutines.flow.Flow

interface NetworkDataSource {

    fun getRandomMeme() : Flow<MemeDataModel>
}
