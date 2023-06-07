package com.eleks.data.network.impl

import com.eleks.data.model.MemeDataModel
import com.eleks.data.network.MemesApiService
import com.eleks.data.network.NetworkDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class NetworkDataSourceImpl @Inject constructor(
    private val memesApiService: MemesApiService
) : NetworkDataSource {

    override fun getRandomMeme(): Flow<MemeDataModel> =
        flow {
            val meme = memesApiService.getRandomMeme()
            emit(meme)
        }.flowOn(Dispatchers.IO)
}
