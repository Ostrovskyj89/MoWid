package com.eleks.data.preferences

interface LocalDataSource {

    var testValue: Boolean

    var quoteChangeOption: String?

    var token: String

    var frequency: Long
}
