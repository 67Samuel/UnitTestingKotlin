package com.example.unittestingkotlin.repository

import androidx.lifecycle.LiveData
import com.example.unittestingkotlin.models.Note
import com.example.unittestingkotlin.util.Response
import io.reactivex.Flowable

interface NoteRepository {

    fun insertNote(note: Note): Flowable<Response<Int>>

    fun updateNote(note: Note): Flowable<Response<Int>>

    fun deleteNote(note: Note): LiveData<Response<Int>>

    fun getNotes(): LiveData<List<Note>>

}