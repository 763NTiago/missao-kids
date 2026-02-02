package com.sttalis.missaokids.model

data class FilhoRequest(
    val paiId: Long,
    val nome: String,
    val login: String,
    val senha: String,
    val fotoBase64: String? = null
)