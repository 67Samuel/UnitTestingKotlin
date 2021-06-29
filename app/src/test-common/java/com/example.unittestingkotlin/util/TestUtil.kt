package com.example.unittestingkotlin.util

import com.example.unittestingkotlin.models.Note

class TestUtil {

    companion object {
        const val TIMESTAMP_1 = "06-2021"
        fun getTEST_NOTE_1(): Note {
            return Note(1,  "title1", "content1", TIMESTAMP_1)
        }
        const val TIMESTAMP_2 = "05-2021"
        fun getTEST_NOTE_2(): Note {
            return Note(2, "title2", "content2", TIMESTAMP_2)
        }

        val TEST_NOTES_LIST = listOf(getTEST_NOTE_1(), getTEST_NOTE_2())
    }

}