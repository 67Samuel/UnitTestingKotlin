package com.example.unittestingkotlin.ui.note

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GestureDetectorCompat
import com.example.unittestingkotlin.R
import com.example.unittestingkotlin.databinding.ActivityNoteBinding
import com.example.unittestingkotlin.models.Note
import com.example.unittestingkotlin.ui.note.NoteViewModel.ViewState.*
import com.example.unittestingkotlin.util.Response
import com.example.unittestingkotlin.util.DateUtil
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.scopes.ActivityScoped


@ActivityScoped
@AndroidEntryPoint
class NoteActivity : AppCompatActivity(),
    View.OnTouchListener,
    GestureDetector.OnGestureListener,
    GestureDetector.OnDoubleTapListener,
    View.OnClickListener,
    TextWatcher {

    private val TAG: String = "NoteActivityDebug"

    private val viewModel: NoteViewModel by viewModels()
    private lateinit var mDetector: GestureDetectorCompat

    private lateinit var binding: ActivityNoteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoteBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        subscribeObservers()
        setListeners()

        if(savedInstanceState == null) {
            getIncomingIntent()
            enableEditMode()
        }
    }

    private fun getIncomingIntent() {
        try {
            val note: Note
            if (intent.hasExtra(getString(R.string.intent_note))) {
                note = intent.getParcelableExtra<Note>(getString(R.string.intent_note))?.let {
                    viewModel.setIsNewNote(false)
                    it
                }?: Note(0, "Title", "", DateUtil.getCurrentTimestamp())
            } else {
                note = Note(0, "Title", "", DateUtil.getCurrentTimestamp())
                viewModel.setIsNewNote(true)
            }
            viewModel.setNote(note)
        } catch (e: Exception) {
            e.printStackTrace()
            showSnackBar(getString(R.string.error_intent_note))
        }
    }


    private fun setListeners() {
        mDetector = GestureDetectorCompat(this, this)
        binding.noteText.setOnTouchListener(this)
        binding.layoutNoteToolbar.toolbarCheck.setOnClickListener(this)
        binding.layoutNoteToolbar.noteTextTitle.setOnClickListener(this)
        binding.layoutNoteToolbar.toolbarBackArrow.setOnClickListener(this)
        binding.layoutNoteToolbar.noteEditTitle.addTextChangedListener(this)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean("has_started", true)
    }


    private fun subscribeObservers() {
        viewModel.observeNote().observe(this, { note ->
            setNoteProperties(note)
        })
        viewModel.observeViewState().observe(this, { viewState ->
                when (viewState) {
                    EDIT -> {
                        enableContentInteraction()
                    }
                    VIEW -> {
                        disableContentInteraction()
                    }
                    else -> {
                        Log.d(TAG, "subscribeObservers: unknown ViewState: $viewState")
                    }
                }
            })
    }

    private fun saveNote() {
        Log.d(TAG, "saveNote: called.")
        try {
            viewModel.saveNote()
                .observe(this, { intResponse ->
                        try {
                            if (intResponse != null) {
                                when (intResponse) {
                                    is Response.Success -> {
                                        Log.e(TAG, "onChanged: save note: success...")
                                        showSnackBar(intResponse.message)
                                    }
                                    is Response.GenericError -> {
                                        Log.e(TAG, "onChanged: save note: error...")
                                        showSnackBar(intResponse.message)
                                    }
                                    is Response.Loading -> {
                                        Log.e(TAG, "onChanged: save note: loading...")
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    })
        } catch (e: Exception) {
            e.printStackTrace()
            showSnackBar(e.message)
        }
    }

    private fun hideKeyboard(activity: Activity) {
        val imm: InputMethodManager =
            activity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        //Find the currently focused view, so we can grab the correct window token from it.
        var view: View? = activity.currentFocus
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun showSnackBar(message: String?) {
        if (!TextUtils.isEmpty(message)) {
            Snackbar.make(binding.parent, message!!, Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun setNoteProperties(note: Note) {
        try {
            binding.layoutNoteToolbar.noteTextTitle.text = note.title
            binding.layoutNoteToolbar.noteEditTitle.setText(note.title)
            binding.noteText.setText(note.content)
        } catch (e: NullPointerException) {
            e.printStackTrace()
            showSnackBar("Error displaying note properties")
        }
    }

    private fun enableEditMode() {
        Log.d(TAG, "enableEditMode: called.")
        viewModel.setViewState(EDIT)
    }

    private fun disableEditMode() {
        Log.d(TAG, "disableEditMode: called.")
        viewModel.setViewState(VIEW)
        if (!TextUtils.isEmpty(binding.noteText.text)) {
            try {
                viewModel.updateNote(binding.layoutNoteToolbar.noteEditTitle.text.toString(),
                    binding.noteText.text.toString())
            } catch (e: Exception) {
                e.printStackTrace()
                showSnackBar("Error setting note properties")
            }
        }
        saveNote()
    }

    private fun disableContentInteraction() {
        hideKeyboard(this)
        binding.layoutNoteToolbar.backArrowContainer.visibility = View.VISIBLE
        binding.layoutNoteToolbar.checkContainer.visibility = View.GONE
        binding.layoutNoteToolbar.noteEditTitle.visibility = View.GONE
        binding.layoutNoteToolbar.noteTextTitle.visibility = View.VISIBLE
        binding.noteText.keyListener = null
        binding.noteText.isFocusable = false
        binding.noteText.isFocusableInTouchMode = false
        binding.noteText.isCursorVisible = false
        binding.noteText.clearFocus()
    }

    private fun enableContentInteraction() {
        binding.layoutNoteToolbar.backArrowContainer.visibility = View.GONE
        binding.layoutNoteToolbar.checkContainer.visibility = View.VISIBLE
        binding.layoutNoteToolbar.noteEditTitle.visibility = View.VISIBLE
        binding.layoutNoteToolbar.noteTextTitle.visibility = View.GONE
        binding.noteText.keyListener = EditText(this).keyListener
        binding.noteText.isFocusable = true
        binding.noteText.isFocusableInTouchMode = true
        binding.noteText.isCursorVisible = true
        binding.noteText.requestFocus()
    }


    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        binding.layoutNoteToolbar.noteTextTitle.text = s.toString()
    }

    override fun afterTextChanged(s: Editable?) {}

    override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
        return false
    }

    override fun onDoubleTap(e: MotionEvent?): Boolean {
        enableEditMode()
        return false
    }

    override fun onDoubleTapEvent(e: MotionEvent?): Boolean {
        return false
    }

    override fun onDown(e: MotionEvent?): Boolean {
        return false
    }

    override fun onShowPress(e: MotionEvent?) {}

    override fun onSingleTapUp(e: MotionEvent?): Boolean {
        return false
    }

    override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
        return false
    }

    override fun onLongPress(e: MotionEvent?) {}

    override fun onFling(e1: MotionEvent?, e2: MotionEvent?, velocityX: Float, velocityY: Float): Boolean {
        return false
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.toolbar_back_arrow -> {
                finish()
            }
            R.id.toolbar_check -> {
                disableEditMode()
            }
            R.id.note_text_title -> {
                enableEditMode()
                binding.layoutNoteToolbar.noteEditTitle.requestFocus()
                binding.layoutNoteToolbar.noteEditTitle.setSelection(binding.layoutNoteToolbar.noteEditTitle.length())
            }
        }
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        return mDetector.onTouchEvent(event)
    }

    override fun onBackPressed() {
        if (viewModel.shouldNavigateBack()) {
            super.onBackPressed()
        } else {
            onClick(binding.layoutNoteToolbar.toolbarCheck)
        }
    }
}