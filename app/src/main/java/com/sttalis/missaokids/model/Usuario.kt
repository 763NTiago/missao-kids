package com.sttalis.missaokids.model

import com.google.gson.annotations.SerializedName

data class Usuario(
    val id: Long,
    val login: String,

    @SerializedName("nomeExibicao")
    val nome: String?,

    @SerializedName("fotoBase64")
    val fotoBase64: String?,

    val perfil: String?,
    val familiaId: String?
)