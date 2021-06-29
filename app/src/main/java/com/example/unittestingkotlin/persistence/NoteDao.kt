package com.example.unittestingkotlin.persistence

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.unittestingkotlin.models.Note
import io.reactivex.Single
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Singleton

@Singleton
@Dao
interface NoteDao {

    @Insert
    fun insertNote(note: Note): Single<Long>

    @Query("SELECT * FROM notes")
    fun getNotes(): LiveData<List<Note>>

    @Delete
    fun deleteNote(note: Note): Single<Int>

    @Update
    fun updateNote(note: Note): Single<Int>
}