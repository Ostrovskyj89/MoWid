package com.eleks.data.mapper

import com.eleks.data.model.UserDataModel
import com.eleks.domain.model.UserModel

fun UserDataModel.toDomain() = UserModel(
    token = token.orEmpty(),
    fullName = fullName.orEmpty(),
    email = email.orEmpty()
)
