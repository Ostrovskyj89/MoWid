package com.eleks.data.repository

import android.util.Log
import com.eleks.data.firebase.source.FirebaseDataSource
import com.eleks.data.model.GroupDataModel
import com.eleks.data.model.QuoteDataModel
import com.eleks.domain.repository.MotivationPhraseRepository
import kotlinx.serialization.StringFormat
import kotlinx.serialization.decodeFromString
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MotivationPhraseRepositoryImpl @Inject constructor(
    private val firebaseDataSource: FirebaseDataSource,
    private val json: StringFormat,
) : MotivationPhraseRepository {
    override suspend fun saveGeneralGroups() {
        try {
            print("Start decoding")
            val result = json.decodeFromString<GroupDataModel>(quotesForHealth)
            firebaseDataSource.saveGeneralGroup(result.normalizeModel())
        } catch (ex: Exception) {
            print("Exception during decoding: $ex")
            Log.e("QQQQ", ex.message.orEmpty())
        }
    }

    private fun GroupDataModel.normalizeModel(): GroupDataModel {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US)
        var result = this
        val items = mutableListOf<QuoteDataModel>()
        quotes!!.forEach {
            var resultItem = it
            if (it.id.isNullOrEmpty()) {
                val uuid = UUID.randomUUID().toString()
                resultItem = it.copy(id = uuid)
            }
            if (it.created.isNullOrEmpty()) {
                resultItem = resultItem.copy(created = format.format(Date()))
            }
            items.add(resultItem)
        }

        if (id.isNullOrEmpty()) {
            val uuid = UUID.randomUUID().toString()
            result = this.copy(id = uuid)
        }
        result = result.copy(quotes = items)

        return result
    }

}