package com.tory.nestedceiling.app.utils

import android.util.Log
import android.view.animation.Interpolator
import androidx.interpolator.view.animation.FastOutSlowInInterpolator

/**
 * - Author: xutao
 * - Date: 2021/10/20
 * - Email: xutao@shizhuang-inc.com
 * - Description:
 */
class TableInterpolatorHelper {

    val interpolator = FastOutSlowInInterpolator()

    private val tabValues: FloatArray
    val isEnable: Boolean

    init {
        tabValues = reflectValueArray(interpolator)
        isEnable = tabValues.isNotEmpty()
    }


    fun getInputByResult(value: Float) : Float {
        return if (isEnable) calInput(tabValues, value) else value
    }

    private fun reflectValueArray(interpolator: Interpolator): FloatArray {
        return try {
            val clazz = interpolator.javaClass
            val field = clazz.getDeclaredField("VALUES")
            field.isAccessible = true
            field.get(clazz) as FloatArray
        } catch (e: Exception) {
            Log.e("TableInterpolatorHelper", "reflectValueArray error  $e")
            floatArrayOf()
        }
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

}
