package com.sttalis.missaokids.api // Confirme seu pacote

import com.sttalis.missaokids.model.LoginRequest
import com.sttalis.missaokids.model.Usuario
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface TarefasService {
    @POST("api/login")
    fun fazerLogin(@Body request: LoginRequest): Call<Usuario>

    @GET("api/admin/filhos/{paiId}")
    fun listarFilhos(@Path("paiId") paiId: Long): Call<List<Usuario>>
}