package com.example.booksapp.data.remote.dto

import com.google.common.truth.Truth.assertThat
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import org.junit.Test

class OpenLibraryDtoParsingTest {

    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    @Test
    fun `parses cover fields from docs`() {
        val json = """
            {
              "docs": [
                {
                  "key": "/works/OL82563W",
                  "title": "Harry Potter and the Philosopher's Stone",
                  "author_name": ["J. K. Rowling"],
                  "first_publish_year": 1997,
                  "cover_i": 15155833,
                  "cover_edition_key": "OL61027601M",
                  "isbn": ["9780747532699"],
                  "subject": ["fiction"],
                  "ratings_average": 4.5
                }
              ]
            }
        """.trimIndent()

        val adapter = moshi.adapter(OpenLibraryResponseDto::class.java)
        val dto = adapter.fromJson(json)

        val first = dto?.docs?.firstOrNull()
        assertThat(first).isNotNull()
        assertThat(first?.coverId).isEqualTo(15155833)
        assertThat(first?.coverEditionKey).isEqualTo("OL61027601M")
        assertThat(first?.isbn).containsExactly("9780747532699")
    }
}
