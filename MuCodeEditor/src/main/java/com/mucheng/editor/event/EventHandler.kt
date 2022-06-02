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

package com.mucheng.editor.event

import android.view.GestureDetector
import android.view.MotionEvent
import com.mucheng.editor.position.RangePosition
import com.mucheng.editor.text.LineContent
import com.mucheng.editor.util.dp
import com.mucheng.editor.util.execCursorAnimationIfNeeded
import com.mucheng.editor.util.execCursorAnimationNow
import com.mucheng.editor.util.getLineHeight
import com.mucheng.editor.views.MuCodeEditor
import kotlinx.coroutines.Runnable
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

@Suppress("MemberVisibilityCanBePrivate")
class EventHandler(private val editor: MuCodeEditor) : GestureDetector.OnGestureListener,
    GestureDetector.OnDoubleTapListener {

    private val scroller by lazy { editor.getScroller() }

    private var isTouchingFirstHandleDrop = false
    private var isTouchingSecondHandleDrop = false

    override fun onDown(e: MotionEvent): Boolean {
        isTouchingFirstHandleDrop = false
        isTouchingSecondHandleDrop = false

        if (editor.getController().state.selection) {
            val clickX = e.x
            val clickY = e.y
            val painter = editor.getPainter()
            val handleDropStartX = painter.getHandleDropFirstX()
            val handleDropStartY = painter.getHandleDropFirstY()
            val handleDropSecondX = painter.getHandleDropSecondX()
            val handleDropSecondY = painter.getHandleDropSecondY()
            val touchedFirst =
                isTouchedHandleText(clickX, clickY, handleDropStartX, handleDropStartY)
            val touchedSecond =
                isTouchedHandleText(clickX, clickY, handleDropSecondX, handleDropSecondY)

            if (touchedFirst && touchedSecond) {
                // 计算中心点坐标
                val firstCenterY = handleDropStartY + 18.dp
                val secondCenterY = handleDropSecondY + 18.dp

                // 作差比较偏移
                val offsetFirst = abs(clickX - handleDropStartX) + abs(clickY - firstCenterY)
                val offsetSecond = abs(clickX - handleDropSecondX) + abs(clickY - secondCenterY)

                // 第一个最近
                if (offsetFirst < offsetSecond) {
                    isTouchingFirstHandleDrop = true
                }

                // 第二个最近
                if (offsetFirst > offsetSecond) {
                    isTouchingSecondHandleDrop = true
                }

                // 一样近，不进行 touching 操作
                return true
            }

            if (touchedFirst) {
                isTouchingFirstHandleDrop = true
                return true
            }

            if (touchedSecond) {
                isTouchingSecondHandleDrop = true
                return true
            }
        }

        return true
    }

    fun onUp() {
        if (editor.getController().state.selection) {
            forceFinished = true
        }
    }

    companion object {
        private const val LEFT_HANDLE_TEXT = 0
        private const val RIGHT_HANDLE_TEXT = 1
    }

    private fun handleSelectionChanged(e2: MotionEvent): Boolean {
        val controller = editor.getController()
        val cursor = editor.getContentProvider().getCursor()
        val stateController = controller.state
        val styleController = controller.style
        val selectionRange = stateController.selectionRange
        val selectionColumn = editor.getLineByPointY(scroller.currY + e2.y)
        val selectionRow = editor.getRowByPointX(scroller.currX + e2.x, selectionColumn)
        if (selectionRange == null) {
            return false
        }

        val startPos = selectionRange.startPosition
        val endPos = selectionRange.endPosition

        if (isTouchingFirstHandleDrop) {
            if (endPos.column == selectionColumn && endPos.row <= selectionRow) {
                return true
            }

            if (selectionColumn > endPos.column) {
                return true
            }

            scrollIfReachedEdge(LEFT_HANDLE_TEXT, selectionColumn, e2)

            startPos.column = selectionColumn
            startPos.row = selectionRow
            return true
        }

        if (isTouchingSecondHandleDrop) {
            forceFinished = true

            if (startPos.column == selectionColumn && startPos.row >= selectionRow) {
                return true
            }

            if (selectionColumn < startPos.column) {
                return true
            }

            endPos.column = selectionColumn
            endPos.row = selectionRow

            scrollIfReachedEdge(RIGHT_HANDLE_TEXT, selectionColumn, e2)

            execCursorAnimationNow(styleController.cursorAnimation, editor) {
                cursor.column = selectionColumn
                cursor.row = selectionRow
            }

            return true
        }

        return false
    }

    private val edgeScrollRunnable = EdgeScrollRunnable()

    private fun scrollIfReachedEdge(who: Int, selectionColumn: Int, e: MotionEvent) {
        when (who) {
            LEFT_HANDLE_TEXT -> {
                if (selectionColumn <= editor.getStartVisibleLine() + 1) {
                    edgeScrollRunnable.setEvent(MotionEvent.obtain(e))
                    edgeScrollRunnable.setWho(LEFT_HANDLE_TEXT)
                    forceFinished = false
                    editor.post(edgeScrollRunnable)
                }

                if (selectionColumn <= editor.getEndVisibleLine() - 1) {
                    edgeScrollRunnable.setEvent(MotionEvent.obtain(e))
                    edgeScrollRunnable.setWho(LEFT_HANDLE_TEXT)
                    forceFinished = false
                    editor.post(edgeScrollRunnable)
                }
            }

            RIGHT_HANDLE_TEXT -> {
                if (selectionColumn <= editor.getStartVisibleLine() + 1) {
                    edgeScrollRunnable.setEvent(MotionEvent.obtain(e))
                    edgeScrollRunnable.setWho(RIGHT_HANDLE_TEXT)
                    forceFinished = false
                    editor.post(edgeScrollRunnable)
                }

                if (selectionColumn <= editor.getEndVisibleLine() - 1) {
                    edgeScrollRunnable.setEvent(MotionEvent.obtain(e))
                    edgeScrollRunnable.setWho(RIGHT_HANDLE_TEXT)
                    forceFinished = false
                    editor.post(edgeScrollRunnable)
                }
            }

        }
    }

    override fun onShowPress(e: MotionEvent?) {

    }

    override fun onSingleTapUp(e: MotionEvent?): Boolean {
        return true
    }

    private fun isTouchedHandleText(x: Float, y: Float, startX: Float, startY: Float): Boolean {
        return x + scroller.currX > startX - 18.dp &&
                x + scroller.currX < startX + 18.dp &&
                y + scroller.currY > startY &&
                y + scroller.currY < startY + 36.dp &&
                editor.getController().state.selection
    }

    fun scrollTo(targetX: Float, targetY: Float, useAnimation: Boolean) {
        var distanceX = 0f
        var distanceY = 0f

        if (targetX > scroller.currX || targetX < scroller.currX) {
            distanceX = targetX - scroller.currX
        }

        if (targetY > scroller.currX || targetY < scroller.currY) {
            distanceY = targetY - scroller.currY
        }

        scrollBy(distanceX, distanceY, useAnimation)
    }

    fun scrollBy(distanceX: Float, distanceY: Float, useAnimation: Boolean) {
        var endX = scroller.currX + distanceX
        var endY = scroller.currY + distanceY

        endX = max(0f, endX)
        endY = max(0f, endY)
        endX = min(editor.getMaxScrollX().toFloat(), endX)

        endY = min(editor.getMaxScrollY().toFloat(), endY)

        scroller.startScroll(
            scroller.currX,
            scroller.currY,
            (endX - scroller.currX).toInt(),
            (endY - scroller.currY).toInt(),
            if (useAnimation) 200 else 0
        )
        editor.postInvalidate()
    }

    override fun onScroll(
        e1: MotionEvent,
        e2: MotionEvent,
        distanceX: Float,
        distanceY: Float,
    ): Boolean {

        if (handleSelectionChanged(e2)) {
            editor.invalidate()
            return true
        }

        /**
         * 先重新声明下变量，方便维护
         * 算法：平滑滑动算法
         * 先计算偏移，再调用 View.scrollTo(x, y)
         * */

        val maxScrollX = editor.getMaxScrollX()
        val maxScrollY = editor.getMaxScrollY()
        val scrolledX = scroller.currX
        val scrolledY = scroller.currY

        var offsetX = 0f
        var offsetY = 0f

        if (scrolledX + distanceX < maxScrollX && scrolledX + distanceX > 0) {
            offsetX += distanceX
        }

        if (scrolledY + distanceY < maxScrollY && scrolledY + distanceY > 0) {
            offsetY += distanceY
        }
        scroller.startScroll(
            scrolledX,
            scrolledY,
            offsetX.toInt(),
            offsetY.toInt(),
            0
        )
        editor.invalidate()
        return true
    }

    override fun onFling(
        e1: MotionEvent?,
        e2: MotionEvent?,
        velocityX: Float,
        velocityY: Float,
    ): Boolean {
        val maxScrollX = editor.getMaxScrollX()
        val maxScrollY = editor.getMaxScrollY()

        scroller.fling(
            scroller.currX,
            scroller.currY,
            (-velocityX).toInt(),
            (-velocityY).toInt(),
            0,
            maxScrollX,
            0,
            maxScrollY
        )
        editor.postInvalidate()
        return false
    }

    private var forceFinished = false

    override fun onLongPress(e: MotionEvent) {
        if (!editor.isEnabled || !editor.getController().isEnabled) {
            return
        }

        val contentProvider = editor.getContentProvider()
        val cursor = contentProvider.getCursor()
        val column = editor.getLineByPointY(scroller.currY + e.y)
        val row = editor.getRowByPointX(scroller.currX + e.x, column)
        val lineContent = contentProvider.getLineContent(column)

        if (editor.getController().state.selection) {
            return
        }

        execCursorAnimationIfNeeded(editor.getController().style.cursorAnimation, editor) {
            cursor.column = column
            cursor.row = row
        }

        if (lineContent.isEmpty()) {
            return
        }

        val startRow = findSelectionStart(cursor.row, lineContent)
        val endRow = findSelectionEnd(cursor.row, lineContent)

        if (startRow == endRow) {
            return
        }

        val indexer = editor.getIndexer()
        editor.getController().state.selectText(RangePosition(
            indexer.columnRow2Index(column, startRow),
            indexer.columnRow2Index(column, endRow)
        ))
    }

    private fun findSelectionStart(row: Int, lineContent: LineContent): Int {
        var findRow = row

        while (findRow > 0) {
            if ((lineContent.getOrNull(findRow - 1) ?: ' ') == ' ') {
                return findRow
            }
            --findRow
        }

        return findRow
    }

    private fun findSelectionEnd(row: Int, lineContent: LineContent): Int {
        var findRow = row

        while (findRow < lineContent.length) {
            if ((lineContent.getOrNull(findRow + 1) ?: ' ') == ' ') {
                return findRow + 1
            }
            ++findRow
        }

        return findRow
    }

    // 当短时间内用户没有再次点击时，我们弹起软键盘
    override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
        if (!editor.isEnabled || !editor.getController().isEnabled) {
            return true
        }

        scroller.forceFinished(true)
        editor.getController().state.unselectText()

        //再去计算点击的行、列
        val column = editor.getLineByPointY(scroller.currY + e.y)
        val row = editor.getRowByPointX(scroller.currX + e.x, column)

        val cursorAnimation = editor.getController().style.cursorAnimation
        execCursorAnimationIfNeeded(cursorAnimation, editor) {
            //改变光标位置
            editor.getContentProvider().getCursor().also {
                it.column = column
                it.row = row
            }
        }
        editor.showSoftInputMethod()
        return true
    }

    override fun onDoubleTap(e: MotionEvent): Boolean {
        if (!editor.isEnabled || !editor.getController().isEnabled) {
            return true
        }
        scroller.forceFinished(true)

        val contentProvider = editor.getContentProvider()
        val cursor = contentProvider.getCursor()
        val column = editor.getLineByPointY(scroller.currY + e.y)
        val row = editor.getRowByPointX(scroller.currX + e.x, column)
        val lineContent = contentProvider.getLineContent(column)

        execCursorAnimationIfNeeded(editor.getController().style.cursorAnimation, editor) {
            cursor.column = column
            cursor.row = row
        }

        if (lineContent.isEmpty()) {
            return true
        }

        val startRow = findSelectionStart(cursor.row, lineContent)
        val endRow = findSelectionEnd(cursor.row, lineContent)

        if (startRow == endRow) {
            return true
        }

        val indexer = editor.getIndexer()
        editor.getController().state.selectText(RangePosition(
            indexer.columnRow2Index(column, startRow),
            indexer.columnRow2Index(column, endRow)
        ))
        return true
    }

    override fun onDoubleTapEvent(e: MotionEvent?): Boolean {
        return false
    }

    private inner class EdgeScrollRunnable : Runnable {

        private lateinit var event: MotionEvent

        private var who = LEFT_HANDLE_TEXT

        override fun run() {
            val column = editor.getLineByPointY(scroller.currY + event.y)
            val row = editor.getRowByPointX(scroller.currX + event.x, column)
            val selectionRange = editor.getController().state.selectionRange
            if (forceFinished) {
                scroller.forceFinished(true)
                if (selectionRange != null) {
                    if (who == LEFT_HANDLE_TEXT) {
                        val startPos = selectionRange.startPosition
                        startPos.column = column
                        startPos.row = row
                    }

                    if (who == RIGHT_HANDLE_TEXT) {
                        val endPos = selectionRange.endPosition
                        endPos.column = column
                        endPos.row = row
                    }
                }
                editor.invalidate()
                return
            }

            if (selectionRange != null) {
                if (column == editor.getStartVisibleLine() + 1 && who == LEFT_HANDLE_TEXT) {
                    selectionRange.startPosition.apply {
                        this.column = column
                        this.row = row
                    }
                    scrollBy(0f, -getLineHeight(editor.getPaints().lineNumberPaint).toFloat(), false)
                    editor.post(this)
                    return
                }

                if (column == editor.getEndVisibleLine() - 1 && who == LEFT_HANDLE_TEXT) {
                    selectionRange.startPosition.apply {
                        this.column = column
                        this.row = row
                    }
                    scrollBy(0f, getLineHeight(editor.getPaints().lineNumberPaint).toFloat(), false)
                    editor.post(this)
                    return
                }

                if (column == editor.getStartVisibleLine() + 1 && who == RIGHT_HANDLE_TEXT) {
                    selectionRange.endPosition.apply {
                        this.column = column
                        this.row = row
                    }
                    scrollBy(0f, -getLineHeight(editor.getPaints().lineNumberPaint).toFloat(), false)
                    editor.post(this)
                    return
                }

                if (column == editor.getEndVisibleLine() - 1 && who == RIGHT_HANDLE_TEXT) {
                    selectionRange.endPosition.apply {
                        this.column = column
                        this.row = row
                    }
                    scrollBy(0f, getLineHeight(editor.getPaints().lineNumberPaint).toFloat(), false)
                    editor.post(this)
                    return
                }
            }
        }

        fun setEvent(obtain: MotionEvent) {
            event = obtain
        }

        fun setWho(who: Int) {
            this.who = who
        }

    }

}