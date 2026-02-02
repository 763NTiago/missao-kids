package com.sttalis.missaokids.model

data class TarefaRequest(
    val paiId: Long,
    val titulo: String,
    val valorEstrelas: Int,
    val imagemBase64: String? = null,
    val atribuidaParaId: Long? = null
)