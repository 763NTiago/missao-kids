package com.sttalis.tarefas.data.api

import com.sttalis.tarefas.data.model.LoginRequest
import com.sttalis.tarefas.data.model.Usuario
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface TarefasService {
    @POST("/api/login")
    fun fazerLogin(@Body request: LoginRequest): Call<Usuario>
}