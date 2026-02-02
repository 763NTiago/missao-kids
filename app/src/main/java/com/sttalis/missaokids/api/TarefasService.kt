package com.sttalis.missaokids.api

import com.sttalis.missaokids.model.*
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

    @POST("api/admin/filhos")
    fun adicionarFilho(@Body request: FilhoRequest): Call<String>

    @POST("api/admin/tarefas")
    fun adicionarTarefa(@Body request: TarefaRequest): Call<String>

    @POST("api/admin/recompensas")
    fun adicionarRecompensa(@Body request: RecompensaRequest): Call<String>

    @GET("api/admin/tarefas/{filhoId}")
    fun listarTarefasPorFamilia(@Path("filhoId") filhoId: Long): Call<List<TarefaResponse>>

    @GET("api/admin/recompensas/{filhoId}")
    fun listarRecompensasPorFamilia(@Path("filhoId") filhoId: Long): Call<List<RecompensaResponse>>
}