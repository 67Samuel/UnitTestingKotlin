package com.example.unittestingkotlin.ui.noteslist

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.unittestingkotlin.R
import com.example.unittestingkotlin.databinding.ActivityNotesListBinding
import com.example.unittestingkotlin.models.Note
import com.example.unittestingkotlin.ui.note.NoteActivity
import com.example.unittestingkotlin.util.Response.*
import com.example.unittestingkotlin.util.VerticalSpacingItemDecorator
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.ActivityScoped


@ActivityScoped
@AndroidEntryPoint
class NotesListActivity : AppCompatActivity(), NotesRecyclerAdapter.Interaction{

    private val TAG: String = "NotesListActivityDebug"

    private lateinit var binding: ActivityNotesListBinding
    private val viewModel: NotesListViewModel by viewModels()
    private lateinit var notesRecyclerAdapter: NotesRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNotesListBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        initRecyclerView()

        binding.fab.setOnClickListener {
            val intent = Intent(this, NoteActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onStart() {
        subscribeObservers()
        super.onStart()
    }

    private fun subscribeObservers() {
        viewModel.observeNotes().observe(this@NotesListActivity, Observer{ notes ->
            notes?.let {
                notesRecyclerAdapter.submitList(it)
            }
        })

        viewModel.getNotes()
    }

    override fun onItemSelected(position: Int, item: Note) {
        val intent = Intent(this, NoteActivity::class.java)
        intent.putExtra(getString(R.string.intent_note), item)
        startActivity(intent)
    }

    private fun initRecyclerView() {
        binding.recyclerview.apply {
            layoutManager = LinearLayoutManager(this@NotesListActivity)
            val verticalSpacingDecorator = VerticalSpacingItemDecorator(10)
            removeItemDecoration(verticalSpacingDecorator) // does nothing if not applied already
            addItemDecoration(verticalSpacingDecorator)
            ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(this)
            notesRecyclerAdapter = NotesRecyclerAdapter(this@NotesListActivity)
            adapter = notesRecyclerAdapter
        }
    }

    val itemTouchHelperCallback = object: ItemTouchHelper.SimpleCallback(
        0,
        ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT
    ) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder,
        ): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val note = notesRecyclerAdapter.getNote(viewHolder.adapterPosition)
            notesRecyclerAdapter.removeNote(note)

            try {
                val deleteAction = viewModel.deleteNote(note)
                deleteAction.observeOnce(this@NotesListActivity, Observer { response ->
                    response?.let {
                        when (it) {
                            is Loading -> {}
                            is Success -> {
                                it.message?.let { message ->
                                    showSnackBar(message)
                                }
                            }
                            is GenericError -> {
                                it.message?.let { message ->
                                    showSnackBar(message)
                                }
                            }
                        }
                    }?: println("response is null")
                })
            } catch (e: Exception) {
                e.printStackTrace()
                e.message?.let { showSnackBar(it) }
            }
        }
    }

    private fun showSnackBar(message: String) {
        if (message.isNotEmpty()) {
            Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
        }
    }

    fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
        observe(lifecycleOwner, object : Observer<T> {
            override fun onChanged(t: T?) {
                Log.d(TAG, "deleteNote onChanged: called")
                observer.onChanged(t)
                removeObserver(this)
            }
        })
    }
}