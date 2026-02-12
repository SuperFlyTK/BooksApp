package com.example.booksapp.data.remote.api

import com.example.booksapp.data.remote.dto.OpenLibraryResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenLibraryApi {
    @GET("search.json")
    suspend fun searchBooks(
        @Query("q") query: String,
        @Query("page") page: Int,
        @Query("limit") limit: Int,
    ): OpenLibraryResponseDto
}
