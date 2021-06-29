package com.example.unittestingkotlin.models

import com.example.unittestingkotlin.util.TestUtil.Companion.TIMESTAMP_1
import com.example.unittestingkotlin.util.TestUtil.Companion.TIMESTAMP_2
import com.example.unittestingkotlin.util.TestUtil.Companion.getTEST_NOTE_1
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class NoteTest {

    @Test
    internal fun isNotesEqual_identicalProperties_returnTrue() {
        val note1 = getTEST_NOTE_1()
        val note2 = getTEST_NOTE_1()
        println("note1: $note1")
        println("note2: $note2")

        assertEquals(note1, note2)
        println("The notes are equal!")
    }

    @Test
    internal fun isNotesEqual_differentIds_returnFalse() {
        val note1 = Note(1,"title", "content", TIMESTAMP_1)
        val note2 = Note(2,"title", "content", TIMESTAMP_1)

        assertNotEquals(note1, note2)
        println("The notes are not equal!")
    }

    @Test
    internal fun isNotesEqual_differentTimestamps_returnTrue() {
        val note1 = Note(1,"title", "content", TIMESTAMP_1)
        val note2 = Note(1,"title", "content", TIMESTAMP_2)

        assertEquals(note1, note2)
        println("The notes are equal!")
    }

    @Test
    internal fun isNotesEqual_differentTitles_returnFalse() {
        val note1 = Note(1,"title", "content", TIMESTAMP_1)
        val note2 = Note(1,"title2", "content", TIMESTAMP_1)

        assertNotEquals(note1, note2)
        println("The notes are not equal! They have different titles.")
    }

    @Test
    internal fun isNotesEqual_differentContent_returnFalse() {
        val note1 = Note(1,"title", "content", TIMESTAMP_1)
        val note2 = Note(1,"title", "content2", TIMESTAMP_1)

        assertNotEquals(note1, note2)
        println("The notes are not equal! They have different contents.")
    }
}