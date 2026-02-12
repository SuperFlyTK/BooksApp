package com.example.booksapp.di

import com.example.booksapp.data.repository.AuthRepositoryImpl
import com.example.booksapp.data.repository.BooksRepositoryImpl
import com.example.booksapp.data.repository.CommentsRepositoryImpl
import com.example.booksapp.data.repository.FavoritesRepositoryImpl
import com.example.booksapp.domain.repository.AuthRepository
import com.example.booksapp.domain.repository.BooksRepository
import com.example.booksapp.domain.repository.CommentsRepository
import com.example.booksapp.domain.repository.FavoritesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindBooksRepository(impl: BooksRepositoryImpl): BooksRepository

    @Binds
    @Singleton
    abstract fun bindFavoritesRepository(impl: FavoritesRepositoryImpl): FavoritesRepository

    @Binds
    @Singleton
    abstract fun bindCommentsRepository(impl: CommentsRepositoryImpl): CommentsRepository
}
