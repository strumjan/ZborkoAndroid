package com.zborko

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt

class MainActivity : AppCompatActivity() {

    private val viewModel: GameViewModel by viewModels()

    private lateinit var availableLayout: GridLayout
    private lateinit var selectedLayout: GridLayout
    private lateinit var submitButton: Button
    private lateinit var newGameButton: Button
    private lateinit var resultTextView: TextView
    private lateinit var scoreTextView: TextView
    private lateinit var newActivity: Button
    private lateinit var exit: Button

    private val availableLetters = mutableListOf<Char?>()
    private val selectedLetters = MutableList<Char?>(5) { null }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        Handler(Looper.getMainLooper()).postDelayed({
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }, 30 * 60 * 1000) // 30 minutes in milliseconds

        availableLayout = findViewById(R.id.availableLettersLayout)
        selectedLayout = findViewById(R.id.selectedLettersLayout)
        submitButton = findViewById(R.id.submitButton)
        newGameButton = findViewById(R.id.newGameButton)
        resultTextView = findViewById(R.id.resultTextView)
        scoreTextView = findViewById(R.id.scoreTextView)
        newActivity = findViewById(R.id.newActivityButton)
        exit = findViewById(R.id.exitButton)

        viewModel.letters.observe(this) { letters ->
            for (i in 0 until 5) {
                availableLetters.add(i, letters.getOrNull(i))
                selectedLetters[i] = null
            }
            updateUI()
        }

        viewModel.score.observe(this) { score ->
            scoreTextView.text = getString(R.string.score_label, score)
        }

        submitButton.setOnClickListener {
            val word = selectedLetters.filterNotNull().joinToString("")
            val (isValid, original) = viewModel.submitWord(word)
            resultTextView.text = if (isValid) {
                getString(R.string.valid_word, original, word)
            } else {
                getString(R.string.invalid_word, original, word)
            }
            viewModel.generateLetters()
        }

        newGameButton.setOnClickListener {
            viewModel.generateLetters()
            resultTextView.text = ""
        }

        viewModel.generateLetters()

        newActivity.setOnClickListener {
            resultTextView.text = ""
            viewModel.resetScore()
            viewModel.generateLetters()
        }

        exit.setOnClickListener {
            finishAffinity()
        }
    }

    private fun updateUI() {
        availableLayout.removeAllViews()
        selectedLayout.removeAllViews()

        // Update available letters layout
        for (i in 0 until 5) {
            val letter = availableLetters.getOrNull(i)
            val view = if (letter != null) {
                createLetterButton(letter).apply {
                    setOnClickListener {
                        val targetIndex = selectedLetters.indexOfFirst { it == null }
                        if (targetIndex != -1) {
                            selectedLetters[targetIndex] = letter
                            availableLetters[i] = null
                            updateUI()
                        }
                    }
                }
            } else {
                createPlaceholder()
            }
            availableLayout.addView(view)
        }

        // Update selected letters layout
        for (i in 0 until 5) {
            val letter = selectedLetters[i]
            val view = if (letter != null) {
                createLetterButton(letter).apply {
                    setOnClickListener {
                        val returnIndex = availableLetters.indexOfFirst { it == null }
                        if (returnIndex != -1) {
                            availableLetters[returnIndex] = letter
                            selectedLetters[i] = null
                            updateUI()
                        }
                    }
                }
            } else {
                createPlaceholder()
            }
            selectedLayout.addView(view)
        }
    }

    private fun createLetterButton(letter: Char): TextView {
        return TextView(this).apply {
            text = letter.toString()
            textSize = 28f
            gravity = Gravity.CENTER
            background = ContextCompat.getDrawable(context, R.drawable.letter_box)
            isClickable = true
            isFocusable = true
            foreground = null
            stateListAnimator = null
            setTextColor("#333333".toColorInt())
            val size = resources.getDimensionPixelSize(R.dimen.letter_size)
            val params = GridLayout.LayoutParams().apply {
                width = size
                height = size
                gravity = Gravity.CENTER
            }
            val margin = resources.getDimensionPixelSize(R.dimen.letter_margin)
            (params as ViewGroup.MarginLayoutParams).setMargins(margin, margin, margin, margin)
            layoutParams = params
        }
    }

    private fun createPlaceholder(): TextView {
        return TextView(this).apply {
            text = ""
            textSize = 28f
            gravity = Gravity.CENTER
            background = ContextCompat.getDrawable(context, R.drawable.letter_box)
            isClickable = true
            isFocusable = true
            foreground = null
            stateListAnimator = null
            val size = resources.getDimensionPixelSize(R.dimen.letter_size)
            val params = GridLayout.LayoutParams().apply {
                width = size
                height = size
            }
            val margin = resources.getDimensionPixelSize(R.dimen.letter_margin)
            (params as ViewGroup.MarginLayoutParams).setMargins(margin, margin, margin, margin)
            layoutParams = params
        }
    }

}
