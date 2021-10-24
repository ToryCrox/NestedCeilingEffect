package com.tory.nestedceiling.app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.animation.PathInterpolatorCompat
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.tory.nestedceiling.app.page.NestedParentRecyclerViewActivity
import com.tory.nestedceiling.app.utils.TableInterpolatorHelper
import java.util.*
import kotlin.math.abs

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
        val interpolatorHelper = TableInterpolatorHelper()

        findViewById<View>(R.id.btn3).setOnClickListener {
            val count = 1000000
            val list = List(count) {
                val input = Random().nextFloat()
                val result = interpolatorHelper.interpolator.getInterpolation(input)
                val calInput = interpolatorHelper.getInputByResult(result)
                val diff = calInput - input
                DtoInfo(input, result, calInput, diff)
            }
            val diffSum = list.sumOf { abs(it.diff).toDouble() }

            Log.d("TestTag", "diff: ${(diffSum / count) * 100}")
            val maxDto = list.maxByOrNull { abs(it.diff) }
            Log.d("TestTag", "maxDto: $maxDto")
        }

        findViewById<View>(R.id.btn4).setOnClickListener {
            val interpolator = FastOutSlowInInterpolator()
            val interpolator2 = PathInterpolatorCompat.create(0.4f, 0f, 0.2f, 1f)
            val count = 1000000
            val list = List(count) {
                val input = Random().nextFloat()
                val result = interpolator.getInterpolation(input)
                val result2 = interpolator2.getInterpolation(input)
                val diff = result2 - result
                Dto2Info(input, result, result2, diff)
            }

            val diffSum = list.sumOf { abs(it.diff).toDouble() }

            Log.d("TestTag", "interpolator 区别 diff: ${(diffSum / count) * 100}")
            val maxDto = list.maxByOrNull { abs(it.diff) }
            Log.d("TestTag", "interpolator maxDto: $maxDto")
        }

        findViewById<View>(R.id.btn5).setOnClickListener {
            val interpolator1 = FastOutSlowInInterpolator()
            val interpolator2 = PathInterpolatorCompat.create(0.4f, 0f, 0.2f, 1f)
            val count = 200

            repeat(count + 1) {index ->
                val input = index * 1.0f/ count
                val result1 = interpolator1.getInterpolation(input)
                val result2 = interpolator2.getInterpolation(input)
                Log.d("TestTag", "input: $input, result1: $result1, result2: $result2, diff: ${(result2 - result1) * 100}")
            }
        }
    }

    data class DtoInfo(
        val input: Float,
        val result: Float,
        val calInput: Float,
        val diff: Float
    )

    data class Dto2Info(
        val input: Float,
        val result: Float,
        val result2: Float,
        val diff: Float
    )
}