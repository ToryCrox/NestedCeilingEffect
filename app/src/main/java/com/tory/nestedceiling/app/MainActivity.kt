package com.tory.nestedceiling.app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.tory.nestedceiling.app.page.NestedParentRecyclerViewActivity
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val btn1: Button = findViewById(R.id.btn1)
        val btn2: Button = findViewById(R.id.btn2)
        btn1.setOnClickListener {
            val intent = Intent(this, NestedParentRecyclerViewActivity::class.java)
            intent.putExtra("isViewPager2", false)
            startActivity(intent)
        }

        btn2.setOnClickListener {
            val intent = Intent(this, NestedParentRecyclerViewActivity::class.java)
            intent.putExtra("isViewPager2", true)
            startActivity(intent)
        }
        val interpolator = FastOutSlowInInterpolator()

        val clazz = interpolator.javaClass
        val field = clazz.getDeclaredField("VALUES")
        field.isAccessible = true
        val values = field.get(clazz) as FloatArray
        Log.d("TestTag", "value.size: ${values.size}")


            findViewById<View>(R.id.btn3).setOnClickListener {
            val input = Random().nextFloat()

            val result = interpolator.getInterpolation(input)
            val calInput = calInput(values, result)
            val calInput2 = calInput2(values, result)

            Log.d("TestTag", "input: $input, calInput : $calInput, diff: ${(calInput - input) * 10000}")
            Log.d("TestTag", "input: $input, calInput2: $calInput2, diff: ${(calInput2 - input) * 10000}")
        }
    }
    private fun calInput(values: FloatArray, value: Float): Float {
        if (value >= 1) {
            return 1f
        }
        if (value <= 0) {
            return 0f
        }
        val position = binarySearch(values, value)
        val d = values[position + 1] - values[position]
        if (d == 0f) {
            return values[position]
        }
        val weight = (value - values[position]) / d
        val stepSize = 1f / (values.size - 1)
        val diff = weight * stepSize
        val quantized: Float = position * stepSize
        return quantized + diff
    }

    private fun calInput2(values: FloatArray, value: Float): Float {
        if (value >= 1) {
            return 1f
        }
        if (value <= 0) {
            return 0f
        }
        val position = binarySearch(values, value)
        val d = values[position + 1] - values[position]
        if (d == 0f) {
            return values[position]
        }
        val weight = 1000000 * (value - values[position]).toDouble() / d
        val stepSize = 1.0 / (values.size - 1)
        val diff = weight * stepSize / 1000000
        val quantized = position * stepSize
        return (quantized + diff).toFloat()
    }

    private fun binarySearch(array: FloatArray, value: Float): Int {
        var lo = 0
        var hi: Int = array.size - 1

        while (lo <= hi) {
            val mid = (lo + hi) ushr 1
            val midVal: Float = array[mid]
            if (midVal < value) {
                lo = mid + 1
            } else if (midVal > value) {
                hi = mid - 1
            } else {
                return mid // value found
            }
        }
        return lo
    }
}