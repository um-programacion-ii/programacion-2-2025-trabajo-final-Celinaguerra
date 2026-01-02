package com.celi.mobile.shared

/**
 * Interfaz para almacenamiento seguro de tokens multiplataforma.
 * En Android se usa TokenStorageAndroid, en iOS se usa TokenStorage (expect/actual).
 */
interface TokenStorage {
    suspend fun saveToken(token: String)
    suspend fun getToken(): String?
    suspend fun clearToken()
}

