package com.mucheng.editor.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Build
import androidx.appcompat.widget.AppCompatDrawableManager
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat


// 计算文本的高度
fun getLineHeight(paint: Paint): Int {
    val fontMetrics = paint.fontMetricsInt
    return fontMetrics.descent - fontMetrics.ascent
}

fun getColumnY(paint: Paint, column: Int): Int {
    return getLineHeight(paint) * column
}

fun getBitmapFromVectorDrawable(context: Context, drawableId: Int): Bitmap {
    val drawable = ContextCompat.getDrawable(context, drawableId)!!
    val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth,
        drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return bitmap
}