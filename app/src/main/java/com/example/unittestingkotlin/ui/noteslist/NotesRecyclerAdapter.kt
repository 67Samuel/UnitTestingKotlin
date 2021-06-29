package com.example.unittestingkotlin.ui.noteslist

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.unittestingkotlin.R
import com.example.unittestingkotlin.models.Note
import com.example.unittestingkotlin.util.DateUtil


/**
 * - List of objects are kept in the AsyncListDiffer differ, referenced by differ.currentList
 */

class NotesRecyclerAdapter(private val interaction: Interaction? = null) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Note>() {

        override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
            return (oldItem.title == newItem.title
                    && oldItem.content == newItem.content
                    && oldItem.timestamp == newItem.timestamp)
        }

    }
    private val differ = AsyncListDiffer(this, DIFF_CALLBACK)

    fun getNote(noteId: Int): Note {
        return differ.currentList[noteId]
    }

    fun removeNote(note: Note) {
        val list = differ.currentList.toMutableList()
        list.remove(note)
        submitList(list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return NoteViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.layout_note_list_item,
                parent,
                false
            ),
            interaction
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is NoteViewHolder -> {
                holder.bind(differ.currentList[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    // for submitting new list items to the current list
    fun submitList(list: List<Note>) {
        differ.submitList(list)
    }

    class NoteViewHolder
    constructor(
        itemView: View,
        private val interaction: Interaction?,
    ) : RecyclerView.ViewHolder(itemView) {

        private val TAG: String = "NotesRecyclerAdapterDebug, position: $adapterPosition"

        fun bind(item: Note) = with(itemView) {
            itemView.setOnClickListener {
                interaction?.onItemSelected(adapterPosition, item)
            }
            try {
                var month: String = item.timestamp.substring(0, 2)
                month = DateUtil.getMonthFromNumber(month)
                val year: String = item.timestamp.substring(3)
                val timestamp = "$month $year"
                itemView.findViewById<TextView>(R.id.note_timestamp).text = timestamp
                itemView.findViewById<TextView>(R.id.note_title).text = item.title
            } catch (e: NullPointerException) {
                Log.e(TAG, "onBindViewHolder: Null Pointer: " + e.message)
            }
        }
    }

    // interface for detecting clicks
    // usage: pass in an object that extends Interaction when initializing the adapter
    interface Interaction {
        fun onItemSelected(position: Int, item: Note)
    }
}