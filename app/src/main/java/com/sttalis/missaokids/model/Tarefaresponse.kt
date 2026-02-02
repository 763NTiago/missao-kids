package com.sttalis.missaokids.model

data class TarefaResponse(
    val id: Long,
    val titulo: String,
    val valorEstrelas: Int,
    val concluida: Boolean
)