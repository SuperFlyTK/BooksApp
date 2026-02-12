package com.example.booksapp.ui.navigation

object AppDestination {
    const val AUTH = "auth"
    const val FEED = "feed"
    const val SEARCH = "search"
    const val PROFILE = "profile"
    const val DETAILS = "details/{bookId}"
    const val REVIEW_EDITOR =
        "reviewEditor/{bookId}?commentId={commentId}&initialRating={initialRating}&initialText={initialText}"

    fun details(bookId: String): String = "details/$bookId"

    fun reviewEditor(
        bookId: String,
        commentId: String = "",
        initialRating: Int = 5,
        initialText: String = "",
    ): String {
        return "reviewEditor/$bookId?commentId=$commentId&initialRating=$initialRating&initialText=$initialText"
    }
}
