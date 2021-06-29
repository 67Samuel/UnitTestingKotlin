package com.example.unittestingkotlin.repository

import androidx.lifecycle.MutableLiveData
import com.example.unittestingkotlin.models.Note
import com.example.unittestingkotlin.persistence.NoteDao
import com.example.unittestingkotlin.util.Constants.Companion.DELETE_FAILURE
import com.example.unittestingkotlin.util.Constants.Companion.DELETE_SUCCESS
import com.example.unittestingkotlin.util.Response
import com.example.unittestingkotlin.util.Constants.Companion.INSERT_FAILURE
import com.example.unittestingkotlin.util.Constants.Companion.INSERT_SUCCESS
import com.example.unittestingkotlin.util.Constants.Companion.INVALID_NOTE_ID
import com.example.unittestingkotlin.util.Constants.Companion.UPDATE_FAILURE
import com.example.unittestingkotlin.util.Constants.Companion.UPDATE_SUCCESS
import com.example.unittestingkotlin.util.InstantExecutorExtension
import com.example.unittestingkotlin.util.LiveDataTestUtil
import com.example.unittestingkotlin.util.TestUtil.Companion.TEST_NOTES_LIST
import com.example.unittestingkotlin.util.TestUtil.Companion.getTEST_NOTE_1
import io.reactivex.Single
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.*

@ExtendWith(InstantExecutorExtension::class)
internal class NoteRepositoryImplTest {

    lateinit var noteRepository: NoteRepository
    lateinit var noteDao: NoteDao

    @BeforeEach
    fun setUp() {
        noteDao = mock(NoteDao::class.java)
        noteRepository = NoteRepositoryImpl(noteDao)
    }

    /**
     * insert note
     * verify the correct method is called
     * confirm observer is triggered
     * confirm new rows inserted
     */
    @Test
    internal fun insertNote_returnRow() {
        // define the behaviour of the mocked object
        val insertedRow = 1L
        val returnedData = Single.just(insertedRow)
        `when`(noteDao.insertNote(any(Note::class.java)?: getTEST_NOTE_1())).thenReturn(returnedData)

        val returnedValue = noteRepository
            .insertNote(getTEST_NOTE_1())
            .blockingSingle() // Flowable method for waiting until note is inserted

        // verify that the insertNote method was called (regardless of input)
        verify(noteDao).insertNote(any(Note::class.java)?: getTEST_NOTE_1())
        // verify that nothing else happened after that
        verifyNoMoreInteractions(noteDao)

        println("Returned value = $returnedValue")
        assertEquals(Response.Success(1, INSERT_SUCCESS), returnedValue)
    }

    @Test
    internal fun insertNote_testWithRxJava_returnRow() {
        // define the behaviour of the mocked object
        val insertedRow = 1L
        val returnedData = Single.just(insertedRow)
        `when`(noteDao.insertNote(any(Note::class.java)?: getTEST_NOTE_1())).thenReturn(returnedData)

        noteRepository.insertNote(getTEST_NOTE_1())
            .test()
            // wait for note to be inserted
            .await()
            .assertValue(Response.Success(1, INSERT_SUCCESS))
    }

    @Test
    internal fun insertNote_returnGenericError() {
        // define the behaviour of the mocked object
        val failedInsert = -1L
        val returnedData = Single.just(failedInsert)
        `when`(noteDao.insertNote(any(Note::class.java)?: getTEST_NOTE_1())).thenReturn(returnedData)

        val returnedValue = noteRepository
            .insertNote(getTEST_NOTE_1())
            .blockingFirst() // Flowable method for waiting until note is inserted

        // verify that the insertNote method was called (regardless of input)
        verify(noteDao).insertNote(any(Note::class.java)?: getTEST_NOTE_1())
        // verify that nothing else happened after that
        verifyNoMoreInteractions(noteDao)

        println("Returned value = $returnedValue")
        assertEquals(Response.GenericError(null, INSERT_FAILURE), returnedValue)
    }

    @Test
    internal fun insertNote_testWithRxJava_returnGenericError() {
        // define the behaviour of the mocked object
        val insertedRow = -1L
        val returnedData = Single.just(insertedRow)
        `when`(noteDao.insertNote(any(Note::class.java)?: getTEST_NOTE_1())).thenReturn(returnedData)

        noteRepository.insertNote(getTEST_NOTE_1())
            .test()
            // wait for note to be inserted
            .await()
            .assertValue(Response.GenericError(null, INSERT_FAILURE))
    }

    @Test
    internal fun updateNote_testWithRxJava_returnRow() {
        // define the behaviour of the mocked object
        val updatedRow = 1
        val returnedData = Single.just(updatedRow)
        `when`(noteDao.updateNote(any(Note::class.java)?: getTEST_NOTE_1())).thenReturn(returnedData)

        noteRepository.updateNote(getTEST_NOTE_1())
            .test()
            // wait for note to be inserted
            .await()
            .assertValue(Response.Success(1, UPDATE_SUCCESS))
    }

    @Test
    internal fun updateNote_testWithRxJava_returnGenericError() {
        // define the behaviour of the mocked object
        val updatedRow = -1
        val returnedData = Single.just(updatedRow)
        `when`(noteDao.updateNote(any(Note::class.java)?: getTEST_NOTE_1())).thenReturn(returnedData)

        noteRepository.updateNote(getTEST_NOTE_1())
            .test()
            // wait for note to be inserted
            .await()
            .assertValue(Response.GenericError(null, UPDATE_FAILURE))
    }

    /**
     * deleteNote:
     * - success case
     * - check data, check message
     */
    @Test
    internal fun deleteNote_deleteSuccess_returnResourceSuccess() {
    	// Arrange
        val numDeletedRows = 1
        val returnedData = Response.Success(numDeletedRows, DELETE_SUCCESS)
        val liveDataTestUtil = LiveDataTestUtil<Response<Int>>()
        val note = getTEST_NOTE_1()
        `when`(noteDao.deleteNote(any(Note::class.java)?: getTEST_NOTE_1())).thenReturn(Single.just(numDeletedRows))
    	
    	// Act
        val observedResponse = liveDataTestUtil.getValue(noteRepository.deleteNote(note))
        println(noteRepository.deleteNote(note).value)
    	
    	// Assert
        assertEquals(returnedData, observedResponse)
    }
    

    /**
     * deleteNote:
     * - fail case
     * - check data, check message
     */
    @Test
    internal fun deleteNote_deleteFailure_returnResourceGenericError() {
        // Arrange
        val numDeletedRows = -1
        val returnedData = Response.GenericError(null, DELETE_FAILURE)
        val liveDataTestUtil = LiveDataTestUtil<Response<Int>>()
        val note = getTEST_NOTE_1()
        `when`(noteDao.deleteNote(any(Note::class.java)?: getTEST_NOTE_1())).thenReturn(Single.just(numDeletedRows))

        // Act
        val observedResponse = liveDataTestUtil.getValue(noteRepository.deleteNote(note))

        // Assert
        assertEquals(returnedData, observedResponse)
    }

    /**
     * getNotes:
     * - get notes
     * - check that notes retrieved == notes inserted
     */
    @Test
    internal fun getNotes_returnListWithNotes() {
    	// Arrange
        val listOfNotes = TEST_NOTES_LIST
        val returnedData = MutableLiveData<List<Note>>()
        returnedData.value = listOfNotes
        val liveDataTestUtil = LiveDataTestUtil<List<Note>>()
        `when`(noteDao.getNotes()).thenReturn(returnedData)

    	// Act
        val observedList = liveDataTestUtil.getValue(noteRepository.getNotes())

    	// Assert
        assertEquals(listOfNotes, observedList)
    }

    /**
     * getNotes:
     * - get notes
     * - return empty list
     */
    @Test
    internal fun getNotes_returnEmptyList() {
        // Arrange
        val listOfNotes = listOf<Note>()
        val returnedData = MutableLiveData<List<Note>>()
        returnedData.value = listOfNotes
        val liveDataTestUtil = LiveDataTestUtil<List<Note>>()
        `when`(noteDao.getNotes()).thenReturn(returnedData)

        // Act
        val observedList = liveDataTestUtil.getValue(noteRepository.getNotes())

        // Assert
        assertEquals(listOfNotes, observedList)
    }


    /**
     * checkId:
     * - check exception
     */
    @Test
    internal fun deleteNote_negativeNoteId_throwException() {
        val note = getTEST_NOTE_1()
        note.id = -1

    	val exception = assertThrows(Exception::class.java) {
    	    noteRepository.deleteNote(note)
        }
        assertEquals(INVALID_NOTE_ID, exception.message)
    }

}