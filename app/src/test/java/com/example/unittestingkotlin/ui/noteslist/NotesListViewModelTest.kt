package com.example.unittestingkotlin.ui.noteslist

import androidx.lifecycle.MutableLiveData
import com.example.unittestingkotlin.models.Note
import com.example.unittestingkotlin.repository.NoteRepository
import com.example.unittestingkotlin.ui.note.NoteViewModel
import com.example.unittestingkotlin.util.Constants.Companion.DELETE_FAILURE
import com.example.unittestingkotlin.util.Constants.Companion.DELETE_SUCCESS
import com.example.unittestingkotlin.util.InstantExecutorExtension
import com.example.unittestingkotlin.util.LiveDataTestUtil
import com.example.unittestingkotlin.util.Response
import com.example.unittestingkotlin.util.TestUtil.Companion.TEST_NOTES_LIST
import com.example.unittestingkotlin.util.TestUtil.Companion.getTEST_NOTE_1
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito
import org.mockito.Mockito.`when`

@ExtendWith(InstantExecutorExtension::class)
internal class NotesListViewModelTest {
    // system under test
    lateinit var viewModel: NotesListViewModel

    lateinit var noteRepository: NoteRepository

    @BeforeEach
    fun setUp() {
        noteRepository = Mockito.mock(NoteRepository::class.java)
        viewModel = NotesListViewModel(noteRepository)
    }

    /**
     * get notes
     * observe notes
     * return list
     */
    @Test
    internal fun retrieveNotes_returnNotesList() {
    	// Arrange
    	val returnedData = TEST_NOTES_LIST
        val liveDataTestUtil = LiveDataTestUtil<List<Note>>()
        val returnedValue = MutableLiveData<List<Note>>()
        returnedValue.value = returnedData
        `when`(noteRepository.getNotes()).thenReturn(returnedValue)
    	
    	// Act
        viewModel.getNotes()
        val observedList = liveDataTestUtil.getValue(viewModel.observeNotes())
    	
    	// Assert
        assertEquals(returnedData, observedList)
    }
    

    /**
     * get notes
     * observe notes
     * return empty list
     */
    @Test
    internal fun retrieveNotes_returnEmptyNotesList() {
        // Arrange
        val returnedData = listOf<Note>()
        val liveDataTestUtil = LiveDataTestUtil<List<Note>>()
        val returnedValue = MutableLiveData<List<Note>>()
        returnedValue.value = returnedData
        `when`(noteRepository.getNotes()).thenReturn(returnedValue)

        // Act
        viewModel.getNotes()
        val observedList = liveDataTestUtil.getValue(viewModel.observeNotes())

        // Assert
        assertEquals(returnedData, observedList)
    }

    /**
     * delete note
     * observe success response
     * return success response
     */
    @Test
    internal fun deleteNote_returnSuccess() {
    	// Arrange
        val deletedRow = 1
    	val returnedData = Response.Success(deletedRow, DELETE_SUCCESS)
        val liveDataTestUtil = LiveDataTestUtil<Response<Int>>()
        val returnedValue = MutableLiveData<Response<Int>>()
        returnedValue.value = returnedData
        `when`(noteRepository.deleteNote(any(Note::class.java)?: getTEST_NOTE_1())).thenReturn(returnedValue)

    	// Act
        val observedResponse = liveDataTestUtil.getValue(viewModel.deleteNote(getTEST_NOTE_1()))

    	// Assert
        assertEquals(returnedData, observedResponse)
    }

    /**
     * delete note
     * observe error response
     * return error response
     */
    @Test
    internal fun deleteNote_returnGenericError() {
        // Arrange
        val returnedData = Response.GenericError(null, DELETE_FAILURE)
        val liveDataTestUtil = LiveDataTestUtil<Response<Int>>()
        val returnedValue = MutableLiveData<Response<Int>>()
        returnedValue.value = returnedData
        `when`(noteRepository.deleteNote(any(Note::class.java)?: getTEST_NOTE_1())).thenReturn(returnedValue)

        // Act
        val observedResponse = liveDataTestUtil.getValue(viewModel.deleteNote(getTEST_NOTE_1()))

        // Assert
        assertEquals(returnedData, observedResponse)
    }
}