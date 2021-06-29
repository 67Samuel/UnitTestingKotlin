package com.example.unittestingkotlin.persistence

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.unittestingkotlin.models.Note

@Database(entities = [Note::class], version = 1)
abstract class NoteDatabase : RoomDatabase() {

    abstract fun getNoteDao(): NoteDao

    companion object {
        val DATABASE_NAME = "note_db"
    }
}