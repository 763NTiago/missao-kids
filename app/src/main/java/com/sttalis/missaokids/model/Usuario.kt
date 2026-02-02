package com.sttalis.missaokids.model

data class Usuario(
    val id: Long,
    val login: String,
    val perfil: String,
    val nomeExibicao: String? = null,
    val fotoBase64: String? = null
)