package com.eleks.data.model

data class ResultDataModel<out T>(val status: Status, val data: T?, val message: String?) {

    companion object {
        fun <T> success(data: T?): ResultDataModel<T> {
            return ResultDataModel(Status.SUCCESS, data, null)
        }

        fun <T> error(msg: String): ResultDataModel<T> {
            return ResultDataModel(Status.ERROR, null, msg)
        }
    }
}

enum class Status {
    SUCCESS,
    ERROR
}