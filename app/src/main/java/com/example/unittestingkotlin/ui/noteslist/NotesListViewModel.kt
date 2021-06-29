package com.example.unittestingkotlin.ui.noteslist

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import com.example.unittestingkotlin.models.Note
import com.example.unittestingkotlin.repository.NoteRepository
import com.example.unittestingkotlin.util.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NotesListViewModel
@Inject
constructor(
    val noteRepository: NoteRepository
): ViewModel() {

    private val TAG: String = "NotesListViewModelDebug"

    private val notes = MediatorLiveData<List<Note>>()

    fun deleteNote(note: Note): LiveData<Response<Int>> {
        return noteRepository.deleteNote(note)
    }

    fun observeNotes(): LiveData<List<Note>> {
        return notes
    }

    fun getNotes() {
        val source = noteRepository.getNotes()
        notes.addSource(source) { newList ->
            notes.removeSource(source)
            notes.value = newList
        }
    }
}