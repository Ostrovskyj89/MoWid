package com.eleks.data.mapper

import com.eleks.domain.model.UserModel
import com.google.firebase.auth.FirebaseUser

fun FirebaseUser.toUserModel() = UserModel(
    uid = uid,
    fullName = displayName ?: ""
)
