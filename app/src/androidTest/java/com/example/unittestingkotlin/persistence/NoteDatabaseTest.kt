package com.example.unittestingkotlin.persistence

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import org.junit.After
import org.junit.Before
import java.lang.Thread.sleep

internal open class NoteDatabaseTest {

    private lateinit var noteDatabase: NoteDatabase

    protected fun getNoteDao(): NoteDao {
        return noteDatabase.getNoteDao()
    }

    @Before
    fun setUp() {
        /**
         * Room.inMemoryDatabaseBuilder creates a 'mock' database that only exists as long as the application is alive.
         * ApplicationProvider is a way to get access to android frameworks in tests.
         */
        val context = ApplicationProvider.getApplicationContext<Context>()
        noteDatabase = Room.inMemoryDatabaseBuilder(
            context,
            NoteDatabase::class.java
        ).build()
    }

    @After
    fun tearDown() {
        noteDatabase.close()
    }

}