package com.example.booksapp.data.remote.dto

import com.squareup.moshi.Json

data class OpenLibraryResponseDto(
    @field:Json(name = "docs") val docs: List<BookDocDto> = emptyList(),
)

data class BookDocDto(
    @field:Json(name = "key") val key: String?,
    @field:Json(name = "title") val title: String?,
    @field:Json(name = "author_name") val authorNames: List<String>?,
    @field:Json(name = "first_publish_year") val firstPublishYear: Int?,
    @field:Json(name = "cover_i") val coverId: Int?,
    @field:Json(name = "subject") val subjects: List<String>?,
    @field:Json(name = "ratings_average") val ratingsAverage: Double?,
)
