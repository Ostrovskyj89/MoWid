package com.eleks.data.preferences.impl

import android.annotation.SuppressLint
import android.content.SharedPreferences
import com.eleks.data.preferences.LocalDataSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalDataSourceImpl @Inject constructor(val sharedPreferences: SharedPreferences):
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

    }
}