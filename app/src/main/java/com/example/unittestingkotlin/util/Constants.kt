package com.example.unittestingkotlin.util

import java.util.concurrent.TimeUnit

class Constants {

    companion object {
        const val DATE_FORMAT = "MM-yyyy"
        const val GET_MONTH_ERROR = "Error. Invalid month number: "

        const val NOTE_TITLE_NULL = "Note title cannot be null"
        const val INVALID_NOTE_ID = "Invalid id. Can't delete note"
        const val DELETE_SUCCESS = "Delete success"
        const val DELETE_FAILURE = "Delete failure"
        const val UPDATE_SUCCESS = "Update success"
        const val UPDATE_FAILURE = "Update failure"
        const val INSERT_SUCCESS = "Insert success"
        const val INSERT_FAILURE = "Insert failure"
        const val NULL_NOTE_ERROR = "Note was null"
        const val NO_CONTENT_ERROR = "Can't save note with no content"

        const val ACTION_INSERT = "ACTION_INSERT"
        const val ACTION_UPDATE = "ACTION_UPDATE"
        const val GENERIC_ERROR = "Something went wrong"

    }

}