package com.example.mathpractice

import android.os.Bundle
import android.view.View
import android.view.animation.AlphaAnimation
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.mathpractice.adapters.HistoryAdapter
import com.example.mathpractice.databinding.ActivityMainBinding
import com.example.mathpractice.dialogs.SettingsDialog
import com.example.mathpractice.viewModels.MainActivityViewModel

class MainActivity : AppCompatActivity(), SettingsDialog.CallbackListener {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainActivityViewModel by viewModels()
    private val adapter = HistoryAdapter()

    // Списки иконок для обратной связи
    private val positiveIcons = arrayOf(
        R.drawable.icon_positive_1,
        R.drawable.icon_positive_2,
        R.drawable.icon_positive_3,
        R.drawable.icon_positive_4,
        R.drawable.icon_positive_5
    )

    private val negativeIcons = arrayOf(
        R.drawable.icon_negative_1,
        R.drawable.icon_negative_2,
        R.drawable.icon_negative_3,
        R.drawable.icon_negative_4,
        R.drawable.icon_negative_5
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupObservers()
        setupListeners()

        // Show settings dialog on start
        if (savedInstanceState == null) {
            showSettingsDialog()
        }
    }

    private fun setupRecyclerView() {
        binding.rvHistory.adapter = adapter
    }

    private fun setupObservers() {
        viewModel.currentProblem.observe(this) { problem ->
            binding.tvProblem.text = problem?.toString() ?: getString(R.string.problem_placeholder)
            binding.etAnswer.text?.clear()
        }

        viewModel.score.observe(this) { (correct, incorrect) ->
            binding.tvScore.text = getString(R.string.score_template, correct, incorrect)
        }

        viewModel.history.observe(this) { history ->
            adapter.submitList(history) {
                if (history.isNotEmpty()) {
                    binding.rvHistory.scrollToPosition(0)
                }
            }
        }

        viewModel.showFeedback.observe(this) { isCorrect ->
            if (isCorrect == null) {
                binding.ivFeedback.visibility = View.INVISIBLE
                binding.btnCheck.isEnabled = true // Разблокируем кнопку для нового ввода
            } else {
                // Блокируем кнопку, чтобы избежать повторных нажатий во время анимации
                binding.btnCheck.isEnabled = false

                // Выбираем случайную иконку в зависимости от ответа
                val iconRes = if (isCorrect) {
                    positiveIcons.random()
                } else {
                    negativeIcons.random()
                }

                binding.ivFeedback.setImageResource(iconRes)
                binding.ivFeedback.visibility = View.VISIBLE
                
                val fadeIn = AlphaAnimation(0f, 1f).apply { duration = 500 }
                binding.ivFeedback.startAnimation(fadeIn)
                
                if (isCorrect) {
                    // Если ответ верный, через 1.5 сек генерируем новый вопрос
                    binding.root.postDelayed({ viewModel.generateNewProblem() }, 1500)
                } else {
                    // Если ответ неверный, через 1.5 сек скрываем иконку и разрешаем попробовать снова
                    binding.root.postDelayed({
                        binding.ivFeedback.visibility = View.INVISIBLE
                        binding.btnCheck.isEnabled = true
                    }, 1500)
                }
            }
        }
    }

    private fun setupListeners() {
        binding.btnCheck.setOnClickListener {
            val answer = binding.etAnswer.text.toString()
            viewModel.checkAnswer(answer)
        }

        binding.btnSettings.setOnClickListener {
            showSettingsDialog()
        }

        binding.btnReset.setOnClickListener {
            viewModel.reset()
        }
    }

    private fun showSettingsDialog() {
        val dialog = SettingsDialog()
        dialog.setCallback(this)
        dialog.show(supportFragmentManager, SettingsDialog.TAG)
    }

    override fun onSettingsConfirmed(operations: List<String>, digits: Int) {
        viewModel.setSettings(operations, digits)
    }
}
