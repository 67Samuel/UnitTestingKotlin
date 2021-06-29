package com.example.unittestingkotlin.ui.note

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.example.unittestingkotlin.util.Constants.Companion.ACTION_INSERT
import com.example.unittestingkotlin.util.Constants.Companion.GENERIC_ERROR
import com.example.unittestingkotlin.util.Response

/**
 * For deciding what type the result would be
 */
abstract class NoteInsertUpdateHelper<T> {
    private val result: MediatorLiveData<Response<T>> =
        MediatorLiveData<Response<T>>()

    init {
        result.value = Response.Loading(null)
        try {
            val source: LiveData<Response<T>> = getAction()
            result.addSource(source) { response ->
                result.removeSource(source)
                result.value = response
                setNoteIdIfNewNote(response)
                onTransactionComplete()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            result.setValue(Response.GenericError(null, GENERIC_ERROR))
        }
    }

    /**
     * A new note would not have an id yet, so we need to set the note iif it's an insert call (instead of update)
     */
    private fun setNoteIdIfNewNote(response: Response<T>) {
        if (response is Response.Success) {
            response.data?.let {
                if (it is Int) {
                    if (defineAction() == ACTION_INSERT && it >= 0) {
                        setNoteId(it)
                    }
                }
            }
        }
    }

    abstract fun setNoteId(noteId: Int)

    /**
     * Insert or Update?
     */
    abstract fun defineAction(): String

    abstract fun onTransactionComplete()

    abstract fun getAction(): LiveData<Response<T>>

    fun asLiveData(): LiveData<Response<T>> {
        return result
    }
}
