package com.zborko

import android.app.Application

class Dictionary(app: Application) {
    private val words: Set<String>

    init {
        val inputStream = app.resources.openRawResource(R.raw.recnik5)
        words = inputStream.bufferedReader()
            .lineSequence()
            .map { it.trim().uppercase() }
            .filter { it.isNotEmpty() }
            .toSet()
    }

    fun contains(word: String): Boolean {
        return words.contains(word)
    }

    fun getAllWords(): Set<String> {
        return words
    }

}
