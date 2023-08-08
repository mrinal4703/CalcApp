package com.example.calcapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import com.example.calcapp.databinding.ActivityMainBinding
import net.objecthunter.exp4j.Expression
import net.objecthunter.exp4j.ExpressionBuilder
import java.lang.ArithmeticException
import android.animation.ValueAnimator
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    var lastNumeric = false
    var stateError = false
    var lastDot = false
    private var isEqualClicked = false
    private var isBackClicked = false

    private lateinit var expression: Expression

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

    private fun animateTextSizeChange(
        startSize: Float,
        endSize: Float,
        duration: Long,
        targetView: TextView
    ) {
        val animator = ValueAnimator.ofFloat(startSize, endSize)
        animator.duration = duration
        animator.addUpdateListener { valueAnimator ->
            val animatedValue = valueAnimator.animatedValue as Float
            targetView.setTextSize(animatedValue)
        }
        animator.start()
    }


    fun onEqualClick(view: View) {
        onEqual()
        isEqualClicked = true
        animateTextSizeChange(40f, 60f, 300, binding.resultTv as TextView)
        animateTextSizeChange(60f, 40f, 300, binding.dataTv as TextView)
        binding.resultTv.setTextColor(0xFF000000.toInt())
        binding.dataTv.setTextColor(0xFF706E6E.toInt())
        binding.resultTv.visibility = View.VISIBLE
        lastNumeric = false
        stateError = false
        lastDot = false
    }

    fun onDigitClick(view: View) {
        val buttonText = (view as Button).text
        if (isEqualClicked) {
            if (!isBackClicked) {
                onAllclearClick(view)
                isEqualClicked = false
            } else {
                if (buttonText == ".") {
                    if (!lastDot && lastNumeric) {
                        binding.dataTv.append(buttonText)
                        lastDot = true
                        lastNumeric = false
                    }
                } else {
                    if (stateError) {
                        binding.dataTv.text = buttonText
                        stateError = false
                    } else {
                        binding.dataTv.append(buttonText)
                    }
                    lastNumeric = true
                    onEqual()
                }
            }
        } else {
            if (buttonText == ".") {
                if (!lastDot && lastNumeric) {
                    binding.dataTv.append(buttonText)
                    lastDot = true
                    lastNumeric = false
                }
            } else {
                if (stateError) {
                    binding.dataTv.text = buttonText
                    stateError = false
                } else {
                    binding.dataTv.append(buttonText)
                }
                lastNumeric = true
                onEqual()
            }
        }
    }

    fun onAllclearClick(view: View) {
        binding.dataTv.text = ""
        binding.resultTv.text = ""
        stateError = false
        lastDot = false
        lastNumeric = false
        binding.resultTv.visibility = View.GONE
    }

    fun onOperatorClick(view: View) {
        if (isEqualClicked) {
            binding.resultTv.textSize = 40f
            binding.dataTv.textSize = 60f
            binding.dataTv.setTextColor(0xFF000000.toInt())
            binding.resultTv.setTextColor(0xFF706E6E.toInt())
            binding.resultTv.visibility = View.VISIBLE

            val resultText = binding.resultTv.text.toString()
            if (resultText.isNotEmpty()) {
                binding.dataTv.text = resultText + (view as Button).text
                lastDot = false
                lastNumeric = false
                isEqualClicked = false
            }
        } else {
            if (!stateError && lastNumeric) {
                binding.dataTv.append((view as Button).text)
                lastDot = false
                lastNumeric = false
                onEqual()
            }
        }
    }


    fun onBackClick(view: View) {
        if (isEqualClicked) {
            animateTextSizeChange(40f, 60f, 300, binding.dataTv as TextView)
            animateTextSizeChange(60f, 40f, 300, binding.resultTv as TextView)
            binding.dataTv.setTextColor(0xFF000000.toInt())
            binding.resultTv.setTextColor(0xFF706E6E.toInt())
            binding.resultTv.visibility = View.VISIBLE
            isBackClicked = true

            binding.dataTv.text = binding.dataTv.text.toString().dropLast(1)
            try {
                val lastCharacter = binding.dataTv.text.toString().last()
                if (lastCharacter.isDigit()) {
                    onEqual()
                }
            } catch (e: Exception) {
                binding.resultTv.text = ""
                binding.resultTv.visibility = View.GONE
                Log.e("Last character error", e.toString())
            }
        } else {
            binding.dataTv.text = binding.dataTv.text.toString().dropLast(1)
            try {
                val lastCharacter = binding.dataTv.text.toString().last()
                if (lastCharacter.isDigit()) {
                    onEqual()
                }
            } catch (e: Exception) {
                binding.resultTv.text = ""
                binding.resultTv.visibility = View.GONE
                Log.e("Last character error", e.toString())
            }
        }
    }

    fun onEqual() {
        if (lastNumeric && !stateError) {
            val text = binding.dataTv.text.toString()
            expression = ExpressionBuilder(text).build()

            try {
                val result = expression.evaluate()
                binding.resultTv.textSize = 40f
                binding.dataTv.textSize = 60f
                binding.dataTv.setTextColor(0xFF000000.toInt())
                binding.resultTv.setTextColor(0xFF706E6E.toInt())
                binding.resultTv.visibility = View.VISIBLE
                binding.resultTv.text = if (result % 1 == 0.0) {
                    result.toLong().toString()
                } else {
                    result.toString()
                }
            } catch (ex: ArithmeticException) {
                Log.e("Evaluation Error", ex.toString())
                binding.resultTv.text = "Error"
                stateError = true
                lastNumeric = false
            }
        }
    }

}