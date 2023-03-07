package com.eleks.mowid.ui.worker

interface QuotesWorkerManager {

    fun execute(options: Options)
}

enum class Options {
    REGULAR,
    PREVIOUS,
    NEXT
}
