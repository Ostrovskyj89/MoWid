package com.eleks.data.model

data class ResultDataModel<out T>(
    val status: Status,
    val data: T? = null,
    val error: Throwable? = null
) {

    companion object {
        fun <T> success(data: T?): ResultDataModel<T> {
            return ResultDataModel(status = Status.SUCCESS, data = data)
        }

        fun <T> error(error: Throwable): ResultDataModel<T> {
            return ResultDataModel(status = Status.ERROR, error = error)
        }
    }
}

fun ResultDataModel<List<GroupDataModel>>.merge(model: ResultDataModel<List<GroupDataModel>>): ResultDataModel<List<GroupDataModel>> =
    if (this.status == Status.SUCCESS && model.status == Status.SUCCESS) {
        ResultDataModel.success(this.data.orEmpty() + model.data.orEmpty())
    } else {
        ResultDataModel.error(
            if (this.status == Status.ERROR) {
                error
            } else {
                model.error
            } ?: Exception("Unknown exception")
        )
    }


enum class Status {
    SUCCESS,
    ERROR
}