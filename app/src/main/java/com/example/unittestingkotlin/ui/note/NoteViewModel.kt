package com.example.unittestingkotlin.ui.note

import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.unittestingkotlin.models.Note
import com.example.unittestingkotlin.repository.NoteRepository
import com.example.unittestingkotlin.util.Constants.Companion.ACTION_INSERT
import com.example.unittestingkotlin.util.Constants.Companion.ACTION_UPDATE
import com.example.unittestingkotlin.util.Response
import com.example.unittestingkotlin.util.Constants.Companion.NOTE_TITLE_NULL
import com.example.unittestingkotlin.util.Constants.Companion.NO_CONTENT_ERROR
import com.example.unittestingkotlin.util.Constants.Companion.NULL_NOTE_ERROR
import com.example.unittestingkotlin.util.DateUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import org.reactivestreams.Subscription
import javax.inject.Inject
import kotlin.properties.Delegates

@HiltViewModel
class NoteViewModel
@Inject
constructor(
    val noteRepository: NoteRepository
): ViewModel() {

    private val TAG: String = "NoteViewModelDebug"

    enum class ViewState {VIEW, EDIT}
    private var note = MutableLiveData<Note>()
    private var viewState = MutableLiveData<ViewState>()
    private var isNewNote by Delegates.notNull<Boolean>()
    private var insertSubscription: Subscription? = null
    private var updateSubscription: Subscription? = null

    fun insertNote(): LiveData<Response<Int>> {
        note.value?.let {
            return LiveDataReactiveStreams.fromPublisher(
                noteRepository.insertNote(it)
                    .doOnSubscribe { subscription ->
                        insertSubscription = subscription
                    }
            )
        }?: throw NullPointerException(NULL_NOTE_ERROR)
    }

    fun updateNote(): LiveData<Response<Int>> {
        note.value?.let {
            return LiveDataReactiveStreams.fromPublisher(
                noteRepository.updateNote(it)
                    .doOnSubscribe { subscription ->
                        updateSubscription = subscription
                    }
            )
        }?: throw NullPointerException(NULL_NOTE_ERROR)
    }

    fun observeNote(): LiveData<Note> {
        return note
    }

    fun observeViewState(): LiveData<ViewState> {
        return viewState
    }

    fun setViewState(viewState: ViewState) {
        this.viewState.value = viewState
    }

    fun setIsNewNote(isNewNote: Boolean) {
        this.isNewNote = isNewNote
    }

    fun saveNote(): LiveData<Response<Int>> {
        if (!shouldAllowSave()) {
            throw Exception(NO_CONTENT_ERROR)
        }
        cancelPendingTransactions()

        return object: NoteInsertUpdateHelper<Int>() {
            override fun setNoteId(noteId: Int) {
                isNewNote = false
                val currentNote = note.value
                currentNote?.apply {
                    id = noteId
                    note.value = this
                }
            }

            override fun defineAction(): String {
                return if (isNewNote) {
                    ACTION_INSERT
                } else {
                    ACTION_UPDATE
                }
            }

            override fun onTransactionComplete() {
                updateSubscription = null
                insertSubscription = null
            }

            override fun getAction(): LiveData<Response<Int>> {
                return if (isNewNote) {
                    insertNote()
                } else {
                    updateNote()
                }
            }
        }.asLiveData()

    }

    private fun cancelPendingTransactions() {
        updateSubscription?.let {
            cancelUpdateTransaction()
        }
        insertSubscription?.let {
            cancelInsertTransaction()
        }

    }

    private fun cancelUpdateTransaction() {
        updateSubscription?.cancel()
        updateSubscription = null
    }

    private fun cancelInsertTransaction() {
        insertSubscription?.cancel()
        insertSubscription = null
    }

    private fun shouldAllowSave(): Boolean {
        return note.value?.let {
            removeWhiteSpace(it.content).isNotEmpty()
        }?: throw NullPointerException(NULL_NOTE_ERROR)
    }

    fun updateNote(title: String, content: String) {
        if (title == "") {
            throw NullPointerException("Title can't be null")
        }
        val temp = removeWhiteSpace(content)
        if (temp.isNotEmpty()) {
            note.value?.title = title
            note.value?.content = content
            note.value?.timestamp = DateUtil.getCurrentTimestamp()
        }
    }

    private fun removeWhiteSpace(string: String): String {
        return (string.replace("\n", "")).replace(" ", "")
    }

    fun setNote(note: Note) {
        if (note.title.isEmpty()) {
            throw Exception(NOTE_TITLE_NULL)
        }
        this.note.value = note
    }

    fun shouldNavigateBack(): Boolean {
        return viewState.value == ViewState.VIEW
    }
}