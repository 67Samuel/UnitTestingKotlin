package com.example.unittestingkotlin.util

sealed class Response<out T> {

    data class Success<out T>(
        val data: T,
        val message: String? = null
        ): Response<T>() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Success<*>

            if (data != other.data) return false
            if (message != other.message) return false
            return true
        }

    }

    data class GenericError<out T>(
        val data: T?,
        val message: String? = null
    ): Response<T>() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as GenericError<*>

            if (data != other.data) return false
            if (message != other.message) return false
            return true
        }
    }

    data class Loading<out T>(
        val data: T?
    ): Response<T>() {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Loading<*>

            if (data != other.data) return false
            return true
        }
    }
}
