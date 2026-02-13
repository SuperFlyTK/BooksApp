package com.example.booksapp.data.remote.dto

import com.squareup.moshi.Json

data class OpenLibraryResponseDto(
    @param:Json(name = "docs")
    @field:Json(name = "docs")
    val docs: List<BookDocDto> = emptyList(),
)

data class BookDocDto(
    @param:Json(name = "key")
    @field:Json(name = "key")
    val key: String?,
    @param:Json(name = "title")
    @field:Json(name = "title")
    val title: String?,
    @param:Json(name = "author_name")
    @field:Json(name = "author_name")
    val authorNames: List<String>?,
    @param:Json(name = "first_publish_year")
    @field:Json(name = "first_publish_year")
    val firstPublishYear: Int?,
    @param:Json(name = "cover_i")
    @field:Json(name = "cover_i")
    val coverId: Int?,
    @param:Json(name = "cover_edition_key")
    @field:Json(name = "cover_edition_key")
    val coverEditionKey: String?,
    @param:Json(name = "isbn")
    @field:Json(name = "isbn")
    val isbn: List<String>?,
    @param:Json(name = "subject")
    @field:Json(name = "subject")
    val subjects: List<String>?,
    @param:Json(name = "ratings_average")
    @field:Json(name = "ratings_average")
    val ratingsAverage: Double?,
)
