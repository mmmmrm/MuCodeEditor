package com.mucheng.editor.util

import android.annotation.SuppressLint
import android.content.Context
import android.util.TypedValue

@SuppressLint("StaticFieldLeak")
private lateinit var mContext: Context
fun initUnitContext(context: Context) {
    mContext = context
}

fun isInitUnitContext(): Boolean {
    return ::mContext.isInitialized
}

fun getDp(context: Context, dp: Number): Int {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
        dp.toFloat(),
        context.resources.displayMetrics).toInt()
}

object UnitUtil {

    private val cacheDp: MutableMap<Number, Float> = HashMap()

    private val cacheSp: MutableMap<Number, Float> = HashMap()

    fun putDp(value: Number, result: Float) {
        cacheDp[value] = result
    }

    fun getDp(value: Number): Float {
        return cacheDp[value]!!
    }

    fun hasDp(value: Number): Boolean {
        return cacheDp.containsKey(value)
    }

    fun putSp(value: Number, result: Float) {
        cacheSp[value] = result
    }

    fun getSp(value: Number): Float {
        return cacheSp[value]!!
    }

    fun hasSp(value: Number): Boolean {
        return cacheSp.containsKey(value)
    }

}

val Number.sp
    get(): Float {
        if (UnitUtil.hasSp(this)) {
            return UnitUtil.getSp(this)
        }

        val value = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            this.toFloat(),
            mContext.resources.displayMetrics
        )
        UnitUtil.putSp(this, value)
        return value
    }

val Number.dp
    get(): Float {
        if (UnitUtil.hasDp(this)) {
            return UnitUtil.getDp(this)
        }

        val value = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this.toFloat(),
            mContext.resources.displayMetrics
        )
        UnitUtil.putDp(this, value)
        return value
    }