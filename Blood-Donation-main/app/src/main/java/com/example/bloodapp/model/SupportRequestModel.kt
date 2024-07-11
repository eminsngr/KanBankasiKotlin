package com.example.bloodapp.model

data class SupportRequestModel(
    var id: String = "",
    var email: String = "",
    var message: String = "",
    var timestamp: Long = 0,
    var response: String? = null,
    var responseTimestamp: Long? = null,
    var userId: String = "",
    var userToken: String = ""
)
