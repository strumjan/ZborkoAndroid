package com.zborko

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val dictionary = Dictionary(application)

    private val _letters = MutableLiveData<List<Char>>()
    val letters: LiveData<List<Char>> get() = _letters

    private val _score = MutableLiveData<Int>(0)
    val score: LiveData<Int> get() = _score

    private var selectedWord: String = ""

    val macedonianAlphabet = listOf(
        'А', 'Б', 'В', 'Г', 'Д', 'Ѓ', 'Е', 'Ж', 'З', 'Ѕ',
        'И', 'Ј', 'К', 'Л', 'Љ', 'М', 'Н', 'Њ', 'О', 'П',
        'Р', 'С', 'Т', 'Ќ', 'У', 'Ф', 'Х', 'Ц', 'Ч', 'Џ', 'Ш'
    )

    fun generateLetters() {
        val allWords = dictionary.getAllWords().filter { it.length > 3 && it.length <= 5 }
        if (allWords.isEmpty()) return

        selectedWord = allWords.random()
        val baseLetters = selectedWord.toMutableList()

        while (baseLetters.size < 5) {
            baseLetters.add(macedonianAlphabet.random())
        }

        baseLetters.shuffle()
        _letters.value = baseLetters
    }

    fun isWordValid(input: String): Pair<Boolean, String> {
        val valid = dictionary.contains(input.uppercase()) &&
                input.uppercase().toList().all { c ->
                    input.uppercase().count { it == c } <= (_letters.value?.count { it == c } ?: 0)
                }
        return Pair(valid, selectedWord)
    }

    fun calculateScore(input: String): Int {
        val inputUpper = input.uppercase()
        val originalUpper = selectedWord.uppercase()

        if (!dictionary.contains(inputUpper)) return 0

        var score = inputUpper.length

        if (inputUpper == originalUpper) {
            score += 2
        } else if (inputUpper.length >= originalUpper.length) {
            score += 2
        }

        return score
    }

    fun resetScore(): MutableLiveData<Int> {
        _score.value = 0
        return _score
    }

    fun submitWord(input: String): Pair<Boolean, String> {
        val (isValid, original) = isWordValid(input)
        if (isValid) {
            val newScore = (_score.value ?: 0) + calculateScore(input)
            _score.value = newScore
        }
        return Pair(isValid, original)
    }
}
