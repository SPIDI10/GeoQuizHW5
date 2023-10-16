package com.alsam.msu.geoquiz

import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.alsam.msu.geoquiz.databinding.ActivityMainBinding

private const val TAG = "MainActivity"
private const val KEY_INDEX = "index"
private const val KEY_SCORE = "score"
private const val KEY_CHEATED_QUESTIONS = "cheatedQuestions"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val Questions = listOf(
        Question(R.string.question_australia, true),
        Question(R.string.question_oceans, true),
        Question(R.string.question_mideast, false),
        Question(R.string.question_africa, false),
        Question(R.string.question_americas, true),
        Question(R.string.question_asia, true)
    )
    private var currentIndex = 0
    private var numberCorrect = 0 // Counter to keep track of correct answers
    private val cheatedQuestions = BooleanArray(Questions.size) { false } // Track cheated questions

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(KEY_INDEX, currentIndex)
        outState.putInt(KEY_SCORE, numberCorrect)
        outState.putBooleanArray(KEY_CHEATED_QUESTIONS, cheatedQuestions)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        currentIndex = savedInstanceState.getInt(KEY_INDEX)
        numberCorrect = savedInstanceState.getInt(KEY_SCORE)
        val restoredCheatedQuestions = savedInstanceState.getBooleanArray(KEY_CHEATED_QUESTIONS)
        if (restoredCheatedQuestions != null) {
            System.arraycopy(restoredCheatedQuestions, 0, cheatedQuestions, 0, Questions.size)
        }
        updateQuestion()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate (Bundle) called")

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.trueButton.setOnClickListener {
            checkAnswer(true)
        }

        binding.falseButton.setOnClickListener {
            checkAnswer(false)
        }

        binding.nextButton.setOnClickListener {
            currentIndex = (currentIndex + 1) % Questions.size
            updateQuestion()
        }

        // Check if savedInstanceState is not null before restoring the state
        savedInstanceState?.let {
            currentIndex = it.getInt(KEY_INDEX, 0)
            numberCorrect = it.getInt(KEY_SCORE, 0)
            val restoredCheatedQuestions = it.getBooleanArray(KEY_CHEATED_QUESTIONS)
            if (restoredCheatedQuestions != null) {
                System.arraycopy(restoredCheatedQuestions, 0, cheatedQuestions, 0, Questions.size)
            }
        }

        updateQuestion()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // No need to do anything here, just prevent the activity from being recreated
    }

    private fun updateQuestion() {
        val questionTextResId = Questions[currentIndex].textResId
        binding.questionTextView.setText(questionTextResId)

        // Disable the buttons if the user has cheated on this question
        binding.trueButton.isEnabled = !cheatedQuestions[currentIndex]
        binding.falseButton.isEnabled = !cheatedQuestions[currentIndex]
    }

    private fun checkAnswer(userAnswer: Boolean) {
        val correctAnswer = Questions[currentIndex].answer
        if (userAnswer == correctAnswer) {
            numberCorrect++
        } else {
            // If the user answered incorrectly, mark this question as cheated
            cheatedQuestions[currentIndex] = true
        }

        val messageResId = if (userAnswer == correctAnswer) {
            if (cheatedQuestions[currentIndex]) {
                R.string.judgment_toast
            } else {
                R.string.correct_toast
            }
        } else {
            if (cheatedQuestions[currentIndex]) {
                R.string.judgment_toast
            } else {
                R.string.incorrect_toast
            }
        }
        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()

        // Disable the buttons after the user answers the question
        binding.trueButton.isEnabled = false
        binding.falseButton.isEnabled = false

        // Check if all questions have been answered
        if (currentIndex == Questions.size - 1) {
            displayScore()
        }
    }

    private fun displayScore() {
        // Calculate the percentage score
        val score = (numberCorrect.toFloat() / Questions.size) * 100

        // Format the score to one decimal place and add "%"
        val formattedScore = String.format("%.1f%%", score)

        // Display a Toast with the score
        Toast.makeText(this, "Your Score: $formattedScore", Toast.LENGTH_LONG).show()

        // Reset the number correct to 0 for a new quiz
        numberCorrect = 0
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart() called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume() called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause() called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy() called")
    }
}
