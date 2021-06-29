package com.example.unittestingkotlin.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "notes")
data class Note (

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") var id: Int,

    @ColumnInfo(name = "title") var title: String,

    @ColumnInfo(name = "content") var content: String,

    @ColumnInfo(name = "timestamp") var timestamp: String

) : Parcelable {
    override fun toString(): String {
        return "Note(id=$id, title='$title', content='$content', timestamp='$timestamp', ihc='${System.identityHashCode(this)}')"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Note

        if (id != other.id) return false
        if (title != other.title) return false
        if (content != other.content) return false

        return true
    }

}