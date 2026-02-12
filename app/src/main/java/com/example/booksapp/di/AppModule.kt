package com.example.booksapp.di

import com.example.booksapp.core.common.CachePolicy
import com.example.booksapp.core.common.DefaultDispatchersProvider
import com.example.booksapp.core.common.DispatchersProvider
import com.example.booksapp.core.common.RetryPolicy
import com.example.booksapp.domain.usecase.BookMergePolicy
import com.example.booksapp.domain.usecase.CredentialsValidator
import com.example.booksapp.domain.usecase.QuerySanitizer
import com.example.booksapp.domain.usecase.RecommendationScorer
import com.example.booksapp.domain.usecase.ReviewValidator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideDispatchersProvider(): DispatchersProvider = DefaultDispatchersProvider()

    @Provides
    @Singleton
    fun provideCachePolicy(): CachePolicy = CachePolicy()

    @Provides
    @Singleton
    fun provideRetryPolicy(): RetryPolicy = RetryPolicy()

    @Provides
    @Singleton
    fun provideQuerySanitizer(): QuerySanitizer = QuerySanitizer()

    @Provides
    @Singleton
    fun provideCredentialsValidator(): CredentialsValidator = CredentialsValidator()

    @Provides
    @Singleton
    fun provideReviewValidator(): ReviewValidator = ReviewValidator()

    @Provides
    @Singleton
    fun provideRecommendationScorer(): RecommendationScorer = RecommendationScorer()

    @Provides
    @Singleton
    fun provideBookMergePolicy(): BookMergePolicy = BookMergePolicy()
}
