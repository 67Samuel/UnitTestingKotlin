package com.example.unittestingkotlin.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
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
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteRepositoryImpl
@Inject
constructor(
    val noteDao: NoteDao
): NoteRepository
{
    private val TAG: String = "NoteRepositoryImplDebug"

    private val timeDelay = 0L
    private val timeUnit: TimeUnit = TimeUnit.SECONDS

    override fun insertNote(note: Note): Flowable<Response<Int>> {
        return noteDao.insertNote(note)
            .delaySubscription(timeDelay, timeUnit) // for testing
            .map {
                // map Long to Int
                it.toInt()
            }
                // handle error here as LiveData cannot handle errors by default
            .onErrorReturn {
                -1
            }
            .map {
                // map Int to CacheResult<Int>
                if (it<0) {
                    Response.GenericError(
                        data = null,
                        message = INSERT_FAILURE
                    )
                } else {
                    Response.Success(
                        data = it,
                        message = INSERT_SUCCESS
                    )
                }
            }
            .subscribeOn(Schedulers.io())
            .toFlowable()
    }

    override fun updateNote(note: Note): Flowable<Response<Int>> {
        return noteDao.updateNote(note)
            .delaySubscription(timeDelay, timeUnit) // for testing
            // handle error here as LiveData cannot handle errors by default
            .onErrorReturn {
                -1
            }
            .map {
                if (it<0) {
                    Response.GenericError(
                        data = null,
                        message = UPDATE_FAILURE
                    )
                } else {
                    Response.Success(
                        data = it,
                        message = UPDATE_SUCCESS
                    )
                }
            }
            .subscribeOn(Schedulers.io())
            .toFlowable()
    }

    override fun deleteNote(note: Note): LiveData<Response<Int>> {
        println("deleteNote called")
        println("id = ${note.id}")
        println("id = ${note.title}")
        checkId(note)

//        println("${noteDao.deleteNote(note).blockingGet()}")
//        println("${noteDao.deleteNote(note)
//            .onErrorReturn {
//                -1
//            }
//            .map {
//                if (it<1) {
//                    Response.GenericError(
//                        data = null,
//                        message = DELETE_FAILURE
//                    )
//                } else {
//                    Response.Success(
//                        data = it,
//                        message = DELETE_SUCCESS
//                    )
//                }
//            }
//            .subscribeOn(Schedulers.io())
//            .blockingGet()}")

        return LiveDataReactiveStreams.fromPublisher {
            noteDao.deleteNote(note)
                .onErrorReturn {
                    -1
                }
                .map {
                    if (it<1) {
                        Response.GenericError(
                            data = null,
                            message = DELETE_FAILURE
                        )
                    } else {
                        Response.Success(
                            data = it,
                            message = DELETE_SUCCESS
                        )
                    }
                }
                .subscribeOn(Schedulers.io())
                .toFlowable()
        }
    }

    override fun getNotes(): LiveData<List<Note>> {
        return noteDao.getNotes()
    }

    private fun checkId(note: Note) {
        if (note.id < 0) {
            throw Exception(INVALID_NOTE_ID)
        }
    }

}