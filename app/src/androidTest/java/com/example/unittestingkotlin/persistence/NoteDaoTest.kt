package com.example.unittestingkotlin.persistence

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.unittestingkotlin.models.Note
import com.example.unittestingkotlin.util.LiveDataTestUtil
import com.example.unittestingkotlin.util.TestUtil.Companion.getTEST_NOTE_1
import junit.framework.TestCase.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

internal class NoteDaoTest: NoteDatabaseTest() {

    private val TEST_TITLE = "test title"
    private val TEST_CONTENT = "test content"
    private val TEST_TIMESTAMP = "08-2018"

    // we need this in junit4 to run tasks on background threads
    @Rule
    @JvmField
    var rule = InstantTaskExecutorRule()

    @Test
    fun insertReadDelete() {
        val note = getTEST_NOTE_1()
        // insert
        getNoteDao().insertNote(note).blockingGet() // Single methos for waiting until note is inserted before getting value

        // read
        val liveDataTestUtil = LiveDataTestUtil<List<Note>>()
        var insertedNotes = liveDataTestUtil.getValue(getNoteDao().getNotes())

        // assert that something was retrieved from the db
        assertNotNull(insertedNotes)
        insertedNotes!! // from here on out, we know insertedNotes is not null

        // assert correct content except for id which is auto generated
        assertEquals(note.content, insertedNotes[0].content)
        assertEquals(note.title, insertedNotes[0].title)
        assertEquals(note.timestamp, insertedNotes[0].timestamp)

        note.id = (insertedNotes[0].id)
        assertEquals(note, insertedNotes[0])

        // delete
        getNoteDao().deleteNote(note).blockingGet()

        // confirm db is empty
        insertedNotes = liveDataTestUtil.getValue(getNoteDao().getNotes())
        insertedNotes?.let {
            assertEquals(0, insertedNotes.size)
        }?: fail("insertedNotes returned null")
    }

    @Test
    fun insertReadUpdateReadDelete() {
        val note = getTEST_NOTE_1()
        // insert
        getNoteDao().insertNote(note).blockingGet() // waits until note is inserted before getting value

        // read
        val liveDataTestUtil = LiveDataTestUtil<List<Note>>()
        var insertedNotes = liveDataTestUtil.getValue(getNoteDao().getNotes())

        // assert that something was retrieved from the db
        assertNotNull(insertedNotes)
        insertedNotes!! // from here on out, we know insertedNotes is not null

        // assert correct content except for id which is auto generated
        assertEquals(note.content, insertedNotes[0].content)
        assertEquals(note.title, insertedNotes[0].title)
        assertEquals(note.timestamp, insertedNotes[0].timestamp)

        note.id = (insertedNotes[0].id)
        assertEquals(note, insertedNotes[0])

        // update
        note.title = TEST_TITLE
        note.content = TEST_CONTENT
        note.timestamp = TEST_TIMESTAMP
        getNoteDao().updateNote(note).blockingGet()

        // read
        insertedNotes = liveDataTestUtil.getValue(getNoteDao().getNotes())
        assertNotNull(insertedNotes)
        insertedNotes!!

        // assert correct content except for id which is auto generated
        assertEquals(TEST_CONTENT, insertedNotes[0].content)
        assertEquals(TEST_TITLE, insertedNotes[0].title)
        assertEquals(TEST_TIMESTAMP, insertedNotes[0].timestamp)

        note.id = (insertedNotes[0].id)
        assertEquals(note, insertedNotes[0])

        // delete
        getNoteDao().deleteNote(note).blockingGet()

        // confirm db is empty
        insertedNotes = liveDataTestUtil.getValue(getNoteDao().getNotes())
        insertedNotes?.let {
            assertEquals(0, insertedNotes.size)
        }?: fail("insertedNotes returned null")
    }
}