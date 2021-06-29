package com.example.unittestingkotlin.di

import com.example.unittestingkotlin.persistence.NoteDao
import com.example.unittestingkotlin.repository.NoteRepository
import com.example.unittestingkotlin.repository.NoteRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Singleton
    @Provides
    fun provideNoteRepository(noteDao: NoteDao): NoteRepository {
        return NoteRepositoryImpl(noteDao)
    }
}