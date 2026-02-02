package com.sttalis.missaokids.utils

import android.content.Context
import android.content.SharedPreferences

object GerenciadorDados {
    private const val PREFS_NAME = "MissaoKidsPrefs"
    private const val KEY_NOME_CRIANCA = "nome_crianca"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun salvarNomeCrianca(context: Context, nome: String) {
        val editor = getPrefs(context).edit()
        editor.putString(KEY_NOME_CRIANCA, nome)
        editor.apply()
    }

    fun lerNomeCrianca(context: Context): String {
        return getPrefs(context).getString(KEY_NOME_CRIANCA, "Campeão") ?: "Campeão"
    }
}