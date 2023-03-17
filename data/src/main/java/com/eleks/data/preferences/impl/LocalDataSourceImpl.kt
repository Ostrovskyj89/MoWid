package com.eleks.data.preferences.impl

import android.annotation.SuppressLint
import android.content.SharedPreferences
import com.eleks.data.firebase.source.impl.FirebaseDataSourceImpl
import com.eleks.data.preferences.LocalDataSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalDataSourceImpl @Inject constructor(val sharedPreferences: SharedPreferences) :
    LocalDataSource {

    override var testValue: Boolean
        get() = sharedPreferences.getBoolean(TEST_VALUE, false)
        set(value) = commit {
            putBoolean(TEST_VALUE, value)
        }
    override var quoteChangeOption: String?
        get() = sharedPreferences.getString(QUOTE_CHANGE_OPTION, "REGULAR")
        set(value) = commit {
            putString(QUOTE_CHANGE_OPTION, value)
        }
    override var token: String
        get() = sharedPreferences.getString(TOKEN, "") ?: ""
        set(value) = commit {
            putString(TOKEN, value)
        }
    override var frequency: Long
        get() = sharedPreferences.getLong(FREQUENCY, FirebaseDataSourceImpl.DEFAULT_FREQUENCY_VALUE)
        set(value) = commit {
            putLong(FREQUENCY, value)
        }


    @SuppressLint("ApplySharedPref")
    private inline fun commit(block: SharedPreferences.Editor.() -> Unit) {
        sharedPreferences
            .edit()
            .apply(block)
            .commit()
    }

    companion object {
        private const val TEST_VALUE = "TEST_VALUE"
        private const val QUOTE_CHANGE_OPTION = "QUOTE_CHANGE_OPTION"
        private const val TOKEN = "TOKEN"
        private const val FREQUENCY = "FREQUENCY"

    }
}