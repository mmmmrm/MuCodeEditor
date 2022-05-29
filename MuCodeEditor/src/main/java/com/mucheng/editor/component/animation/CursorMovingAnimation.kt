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

package com.mucheng.editor.component.animation

import android.animation.ValueAnimator
import com.mucheng.editor.component.animation.base.CursorAnimation
import com.mucheng.editor.util.getColumnY
import com.mucheng.editor.views.MuCodeEditor

class CursorMovingAnimation(private val editor: MuCodeEditor) : CursorAnimation,
    ValueAnimator.AnimatorUpdateListener {

    private var animateXAnimator = ValueAnimator().apply {
        addUpdateListener(this@CursorMovingAnimation)
    }
    private var animateYAnimator = ValueAnimator()
    private var startX = 0f
    private var startY = getColumnY(editor.getPaints().codeTextPaint, 1).toFloat()

    override fun start() {
        animateXAnimator.start()
        animateYAnimator.start()
    }

    override fun cancel() {
        animateXAnimator.cancel()
        animateYAnimator.cancel()
    }

    override fun markCursorStartPosition() {
        val contentProvider = editor.getContentProvider()
        val cursor = contentProvider.getCursor()
        val lineContent = contentProvider.getLineContent(cursor.column)
        val paints = editor.getPaints()
        val widths = FloatArray(cursor.row)

        if (lineContent.isEmpty()) {
            startX = 0f
            startY = getColumnY(paints.codeTextPaint, cursor.column).toFloat()
            return
        }

        paints.codeTextPaint.getTextWidths(lineContent, 0, cursor.row, widths)
        var offset = 0f
        widths.forEach {
            offset += it
        }

        startX = offset
        startY = getColumnY(paints.codeTextPaint, cursor.column).toFloat()
    }

    override fun markCursorEndPosition() {
        val contentProvider = editor.getContentProvider()
        val cursor = contentProvider.getCursor()
        val lineContent = contentProvider.getLineContent(cursor.column)
        val paints = editor.getPaints()
        val widths = FloatArray(cursor.row)

        paints.codeTextPaint.getTextWidths(lineContent, 0, cursor.row, widths)

        var offsetX = 0f
        widths.forEach {
            offsetX += it
        }

        animateXAnimator.setFloatValues(startX, offsetX)
        animateYAnimator.setFloatValues(startY,
            getColumnY(paints.codeTextPaint, cursor.column).toFloat())
    }

    override fun animateX(): Float {
        return animateXAnimator.animatedValue as? Float ?: startX
    }

    override fun animateY(): Float {
        return animateYAnimator.animatedValue as? Float ?: startY
    }

    override fun time(time: Long) {
        animateXAnimator.duration = time
        animateYAnimator.duration = time
    }

    override fun onAnimationUpdate(animation: ValueAnimator?) {
        editor.postInvalidateOnAnimation()
    }

}