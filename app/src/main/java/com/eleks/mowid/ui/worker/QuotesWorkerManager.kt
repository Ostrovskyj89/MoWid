package com.eleks.mowid.ui.worker

interface QuotesWorkerManager {

    fun execute(option: ExecutionOption)
}

enum class ExecutionOption {
    REGULAR,
    PREVIOUS,
    NEXT
}
