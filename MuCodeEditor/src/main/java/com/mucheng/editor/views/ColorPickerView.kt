/*
 *
 *  * Copyright (c) 2022 SuMuCheng
 *  *
 *  * CN:
 *  * 作者：SuMuCheng
 *  * Github 主页：https://github.com/CaiMuCheng
 *  *
 *  * 你可以免费使用、商用以下代码，也可以基于以下代码做出修改，但是必须在你的项目中标注出处
 *  * 例如：在你 APP 的设置中添加 “关于编辑器” 一栏，其中标注作者以及此编辑器的 Github 主页
 *  *
 *  * 此代码使用 MPL 2.0 开源许可证，你必须标注作者信息
 *  * 若你要修改文件，请勿删除此注释
 *  * 若你违反以上条例我们有权向您提起诉讼!
 *  *
 *  * EN:
 *  * Author: SuMuCheng
 *  * Github Homepage: https://github.com/CaiMuCheng
 *  *
 *  * You can use the following code for free, commercial use, or make modifications based on the following code, but you must mark the source in your project.
 *  * For example: add an "About Editor" column in your app's settings, which identifies the author and the Github home page of this editor.
 *  *
 *  * This code uses the MPL 2.0 open source license, you must mark the author information
 *  * Do not delete this comment if you want to modify the file.
 *  *
 *  * If you violate the above regulations we have the right to sue you!
 *
 */

package com.mucheng.editor.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import java.lang.Math.pow
import kotlin.math.sqrt

open class ColorPickerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {

    private val gradientCirclePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        shader = shader
        style = Paint.Style.STROKE
        strokeWidth = 32f
    }

    private val centerCirclePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFFFFFF
        strokeWidth = 5f
    }

    private val shader: Shader = SweepGradient(0f, 0f, GRADIENT_COLORS, null)

    companion object {
        private val GRADIENT_COLORS = intArrayOf(
            0xFFFF0000.toInt(),
            0xFFFF00FF.toInt(),
            0xFF0000FF.toInt(),
            0xFF00FFFF.toInt(),
            0xFF00FF00.toInt(),
            0xFFFFFF00.toInt(),
            0xFFFF0000.toInt()
        )
        private const val CENTER_X = 100
        private const val CENTER_Y = 100
        private const val CENTER_RADIUS = 32
    }

    private val rectF = RectF()

    private var trackingCenter = false
    private var highlightCenter = false

    override fun onDraw(canvas: Canvas) {
        val radius = CENTER_X - gradientCirclePaint.strokeWidth * 0.5f
        canvas.translate(CENTER_X.toFloat(), CENTER_Y.toFloat())
        rectF.left = -radius
        rectF.top = -radius
        rectF.right = radius
        rectF.bottom = radius
        canvas.drawOval(rectF, gradientCirclePaint)

        if (trackingCenter) {
            val color = centerCirclePaint.color
            centerCirclePaint.style = Paint.Style.STROKE
            if (highlightCenter) {
                centerCirclePaint.alpha = 255
            } else {
                centerCirclePaint.alpha = 0x80
            }
            canvas.drawCircle(0f,
                0f,
                CENTER_RADIUS + centerCirclePaint.strokeWidth,
                centerCirclePaint)
            centerCirclePaint.style = Paint.Style.FILL
            centerCirclePaint.color = color
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x - CENTER_RADIUS
        val y = event.y - CENTER_RADIUS
        val inCenter = sqrt(x * x + y * y) <= CENTER_RADIUS

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                trackingCenter = inCenter
                if (inCenter) {
                    highlightCenter = true
                    invalidate()
                }
            }
        }

        return super.onTouchEvent(event)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(CENTER_X * 2, CENTER_Y * 2)
    }

}