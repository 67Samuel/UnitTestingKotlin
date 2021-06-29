package com.example.unittestingkotlin.ui.note

import com.example.unittestingkotlin.models.Note
import com.example.unittestingkotlin.repository.NoteRepository
import com.example.unittestingkotlin.util.Response
import com.example.unittestingkotlin.util.Constants.Companion.INSERT_SUCCESS
import com.example.unittestingkotlin.util.Constants.Companion.NO_CONTENT_ERROR
import com.example.unittestingkotlin.util.Constants.Companion.NULL_NOTE_ERROR
import com.example.unittestingkotlin.util.InstantExecutorExtension
import com.example.unittestingkotlin.util.LiveDataTestUtil
import com.example.unittestingkotlin.util.TestUtil.Companion.getTEST_NOTE_1
import io.reactivex.Flowable
import io.reactivex.internal.operators.single.SingleToFlowable
import org.junit.internal.runners.statements.Fail
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.*

@ExtendWith(InstantExecutorExtension::class)
internal class NoteViewModelTest {

    /**
     * How to solve getMainLooper not mocked error.
     * This appears when we are doing unit tests that require background threads.
     * - Create InstantExecutorExtension (just copy paste)
     * - Use @ExtendWith
     */

    lateinit var noteViewModel: NoteViewModel
    lateinit var noteRepository: NoteRepository

    @BeforeEach
    fun setUp() {
        noteRepository = mock(NoteRepository::class.java)
        noteViewModel = NoteViewModel(noteRepository)
    }

    /**
     * Can't observe a note that hasn't been set
     */
    @Test
    internal fun observeNote_noteNotSet_returnEmptyNote() {
        val liveDataTestUtil = LiveDataTestUtil<Note>()
        val note = liveDataTestUtil.getValue(noteViewModel.observeNote())

        assertNull(note)
    }

    /**
     * Observe a note that has been set and that onChanged triggers in activity
     */
    @Test
    internal fun observeNote_noteSet() {
        val liveDataTestUtil = LiveDataTestUtil<Note>()
        val insertedNote = getTEST_NOTE_1()
        noteViewModel.setNote(insertedNote)
        val note = liveDataTestUtil.getValue(noteViewModel.observeNote())

        assertEquals(insertedNote, note)
    }

    /**
     * Insert a new note and observe row returned
     */
    @Test
    internal fun insertNote_returnRow() {
        // Arrange
        val liveDataTestUtil = LiveDataTestUtil<Response<Int>>()
        val insertedRow = 1
        val insertedNote = getTEST_NOTE_1()
        val returnedData: Flowable<Response<Int>> = SingleToFlowable.just(Response.Success(insertedRow, INSERT_SUCCESS))
        `when`(noteRepository.insertNote(any(Note::class.java) ?: insertedNote)).thenReturn(returnedData)

        // Act
        noteViewModel.setNote(insertedNote)
        noteViewModel.setIsNewNote(true)
        val cacheResult = liveDataTestUtil.getValue(noteViewModel.saveNote())

        // Assert
        cacheResult?.let {
            assertEquals(Response.Success(insertedRow, INSERT_SUCCESS), cacheResult)
        }?: Fail(Exception("cacheResult is null"))
    }

    /**
     * Insert: Don't return a new row if observer is not attached
     */
    @Test
    internal fun insertNote_noObserver_returnNothing() {
        // Arrange
        val note = getTEST_NOTE_1()

        // Act
        noteViewModel.setNote(note)

        // Assert
        // that noteRepository's insertNote method (using any parameters) was never used
        verify(noteRepository, never()).insertNote(any(Note::class.java)?:note)

    }

    /**
     * Update a new note and observe row returned
     */
    @Test
    internal fun updateNote_returnRow() {
        // Arrange
        val liveDataTestUtil = LiveDataTestUtil<Response<Int>>()
        val updatedRow = 1
        val updatedNote = getTEST_NOTE_1()
        val returnedData: Flowable<Response<Int>> = SingleToFlowable.just(Response.Success(updatedRow, INSERT_SUCCESS))
        `when`(noteRepository.updateNote(any(Note::class.java) ?: updatedNote)).thenReturn(returnedData)

        // Act
        noteViewModel.setNote(updatedNote)
        noteViewModel.setIsNewNote(false)
        val cacheResult = liveDataTestUtil.getValue(noteViewModel.saveNote())

        // Assert
        cacheResult?.let {
            assertEquals(Response.Success(updatedRow, INSERT_SUCCESS), cacheResult)
        }?: Fail(Exception("cacheResult is null"))
    }

    /**
     * Update: Don't return a new row if observer is not attached
     */
    @Test
    internal fun updateNote_noObserver_returnNothing() {
        // Arrange
        val note = getTEST_NOTE_1()

        // Act
        noteViewModel.setNote(note)

        // Assert
        // that noteRepository's insertNote method (using any parameters) was never used
        verify(noteRepository, never()).updateNote(any(Note::class.java)?:note)

    }

    /**
     * Save: Note is null, return error
     */
    @Test
    internal fun saveNote_noteNull_returnError() {
        // Arrange

        // Assert
        val exception = assertThrows(NullPointerException::class.java) {
            // Act
            noteViewModel.saveNote()
        }
        assertEquals(NULL_NOTE_ERROR, exception.message)
    }

    /**
     * Set note will empty title, throw exception
     */
    @Test
    internal fun setNote_emptyTitle_throwException() {
        // Arrange
        val note = getTEST_NOTE_1()
        note.title = ""

        // Assert
        assertThrows(Exception::class.java) {
            // Act
            noteViewModel.setNote(note)
        }

    }

    @Test
    internal fun saveNote_shouldAllowSave_returnFalse() {
        // Arrange
        val note = getTEST_NOTE_1()
        note.content = "      "

        // Act
        noteViewModel.setNote(note)
        noteViewModel.setIsNewNote(true)

        // Assert
        val exception = assertThrows(Exception::class.java) {
            noteViewModel.saveNote()
        }
        assertEquals(NO_CONTENT_ERROR, exception.message)
    }
    
}