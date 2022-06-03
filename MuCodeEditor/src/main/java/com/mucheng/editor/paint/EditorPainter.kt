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

package com.mucheng.editor.paint

import android.graphics.*
import com.mucheng.editor.R
import com.mucheng.editor.component.Cursor
import com.mucheng.editor.enums.CodeEditorColorToken
import com.mucheng.editor.position.Position
import com.mucheng.editor.text.ContentProvider
import com.mucheng.editor.text.LineContent
import com.mucheng.editor.util.dp
import com.mucheng.editor.util.getBitmapFromVectorDrawable
import com.mucheng.editor.util.getColumnY
import com.mucheng.editor.util.getLineHeight
import com.mucheng.editor.views.MuCodeEditor
import kotlin.math.max

/**
 * @author  苏沐橙
 *  实现了按照可见行绘制内容，节省绘制开销
 * */
@Suppress("PrivatePropertyName")
class EditorPainter(
    private val editor: MuCodeEditor,
    private val contentProvider: ContentProvider,
) {

    //距离左侧的值
    private val SPACE_LEFT = 5f.dp

    //断点的 x 轴
    private var paddingLeft = 0f

    //着色器
    private val paints by lazy { editor.getPaints() }

    private val controller by lazy { editor.getController() }

    //这个是绘制中最大的 x 值
    private var biggestX = 0f

    private val clipRect = Rect()

    private val selectionRect = Rect()

    private val handleDropBitmap =
        getBitmapFromVectorDrawable(editor.context, R.drawable.ic_handle_drop)

    private var handleDropFirstX = 0f

    private var handleDropFirstY = 0f

    private var handleDropSecondX = 0f

    private var handleDropSecondY = 0f

    private var handleDropColor = 0

    private var handleDropColorFilter: PorterDuffColorFilter? = null

    //当开始绘制时
    fun onDraw(canvas: Canvas) {
        resetParams()
        drawBackground(canvas)
        drawClip(canvas)
        if (contentProvider.columnCount == 0) {
            return
        }

        drawLineNumber(canvas)
        drawDividingLine(canvas)
        drawSelection(canvas)
        drawCodeText(canvas)
        drawCursor(canvas)
    }

    private fun resetParams() {
        paddingLeft = SPACE_LEFT
        biggestX = 0f

        paints.lineNumberPaint.typeface = controller.style.typeface
        paints.codeTextPaint.typeface = controller.style.typeface

        paints.codeTextSelectionBackgroundPaint.color =
            controller.theme.getColor(CodeEditorColorToken.SELECT_BACKGROUND_COLOR)

        clipRect.left = editor.getScroller().currX
        clipRect.top = editor.getScroller().currY
        clipRect.right = editor.getScroller().currX + editor.width
        clipRect.bottom = editor.getScroller().currY + editor.height

        paints.cursorPaint.color =
            controller.theme.getColor(CodeEditorColorToken.CURSOR_COLOR)

        paints.lineNumberPaint.color =
            controller.theme.getColor(CodeEditorColorToken.LINE_NUMBER_COLOR)

        paints.dividingLinePaint.color =
            controller.theme.getColor(CodeEditorColorToken.DIVIDING_LINE_COLOR)

        val handleDropNeededColor =
            controller.theme.getColor(CodeEditorColorToken.HANDLE_TEXT_BACKGROUND_COLOR)

        if (handleDropColor != handleDropNeededColor || handleDropColorFilter == null) {
            handleDropColorFilter =
                PorterDuffColorFilter(handleDropNeededColor, PorterDuff.Mode.SRC_IN)
            handleDropColor = handleDropNeededColor
        }
        paints.codeTextSelectionHandlerPaint.colorFilter = handleDropColorFilter
    }

    private fun drawClip(canvas: Canvas) {
        canvas.clipRect(clipRect)
    }

    private fun drawBackground(canvas: Canvas) {
        canvas.drawColor(controller.theme.getColor(CodeEditorColorToken.BACKGROUND_COLOR))
    }

    //绘制行号
    private fun drawLineNumber(canvas: Canvas) {
        if (!controller.displayLineNumber) return

        val startLine = editor.getStartVisibleLine()
        val endLine = editor.getEndVisibleLine()

        var workColumn = startLine

        paddingLeft += SPACE_LEFT * 2

        do {
            canvas.drawText(
                workColumn.toString(),
                paddingLeft,
                getColumnY(paints.lineNumberPaint, workColumn).toFloat(),
                paints.lineNumberPaint
            )
            ++workColumn
        } while (workColumn <= endLine)

        paddingLeft += paints.lineNumberPaint.measureText(endLine.toString()) + SPACE_LEFT
    }

    //绘制分割线
    private fun drawDividingLine(canvas: Canvas) {
        if (!controller.displayLineNumber || !controller.displayDividingLine) return

        canvas.drawLine(
            paddingLeft,
            0f,
            paddingLeft,
            (getColumnY(paints.lineNumberPaint,
                contentProvider.columnCount) + editor.height / 1.2).toFloat(),
            paints.dividingLinePaint
        )
    }

    // 绘制光标
    private fun drawCursor(canvas: Canvas) {
        if (!controller.isEnabled || controller.state.selection) return

        val cursor = contentProvider.getCursor()
        val cursorColumn = cursor.column

        val startVisibleColumn = editor.getStartVisibleLine()
        val endVisibleColumn = editor.getEndVisibleLine()
        val cursorAnimation = controller.style.cursorAnimation

        if (cursorColumn > endVisibleColumn || cursorColumn < startVisibleColumn) {
            return
        }

        val lineHeight = getLineHeight(paints.lineNumberPaint)
        if (cursorColumn > contentProvider.columnCount || (cursorAnimation?.animateY()
                ?: 0f) > getColumnY(
                paints.codeTextPaint,
                contentProvider.columnCount)
        ) {

            canvas.drawLine(
                paddingLeft,
                (getDefaultCursorOffsetY(1) - lineHeight / 1.5).toFloat(),
                paddingLeft,
                getDefaultCursorOffsetY(1) + lineHeight / 9,
                paints.cursorPaint
            )

            return
        }

        val cursorOffsetX = getCursorOffsetX(cursor)
        val textTopY = getCursorOffsetY(cursor)
        canvas.drawLine(
            cursorOffsetX,
            (textTopY - lineHeight / 1.5).toFloat(),
            cursorOffsetX,
            textTopY + lineHeight / 9,
            paints.cursorPaint
        )
    }

    private fun drawSelection(canvas: Canvas) {
        if (!controller.isEnabled || !controller.state.selection) return
        val paddingLeft = paddingLeft + SPACE_LEFT * 2

        val selectionRange = controller.state.selectionRange!!
        val startPos = selectionRange.startPosition
        val endPos = selectionRange.endPosition

        val startColumn = startPos.column
        val endColumn = endPos.column

        if (startColumn == endColumn && startPos.row != endPos.row) {
            val lineContent = contentProvider.getLineContent(startColumn)
            drawSingleColumnSelection(canvas,
                lineContent,
                startPos.row,
                endPos.row,
                startColumn,
                paddingLeft)

            val x =
                paddingLeft + paints.codeTextPaint.measureText(lineContent, 0, startPos.row)

            val y = getColumnY(paints.codeTextPaint,
                startColumn) + paints.codeTextPaint.fontMetrics.descent

            val endX = x + paints.codeTextPaint.measureText(lineContent, startPos.row, endPos.row)

            handleDropFirstX = x
            handleDropFirstY = y

            handleDropSecondX = endX
            handleDropSecondY = y

            drawTextHandler(canvas, x, y)
            drawTextHandler(canvas, endX, y)
            return
        }

        if (startColumn < endColumn) {
            drawMultiColumnSelection(
                canvas, startPos, endPos, paddingLeft
            )

            val startLineContent = contentProvider.getLineContent(startColumn)
            val endLineContent = contentProvider.getLineContent(endColumn)

            val startFirstX = paddingLeft + paints.codeTextPaint.measureText(startLineContent,
                0,
                startPos.row)
            val startFirstY = getColumnY(paints.codeTextPaint,
                startColumn) + paints.codeTextPaint.fontMetrics.descent


            val startSecondX = paddingLeft + paints.codeTextPaint.measureText(endLineContent,
                0,
                endPos.row)
            val startSecondY = getColumnY(paints.codeTextPaint,
                endColumn) + paints.codeTextPaint.fontMetrics.descent

            handleDropFirstX = startFirstX
            handleDropFirstY = startFirstY
            handleDropSecondX = startSecondX
            handleDropSecondY = startSecondY

            drawTextHandler(canvas, startFirstX, startFirstY)
            drawTextHandler(canvas, startSecondX, startSecondY)
            return
        }
    }

    fun getHandleDropFirstX(): Float {
        return handleDropFirstX
    }

    fun getHandleDropFirstY(): Float {
        return handleDropFirstY
    }

    fun getHandleDropSecondX(): Float {
        return handleDropSecondX
    }

    fun getHandleDropSecondY(): Float {
        return handleDropSecondY
    }

    private fun drawTextHandler(canvas: Canvas, startX: Float, startY: Float) {
        canvas.drawBitmap(
            handleDropBitmap, startX - 18.dp, startY, paints.codeTextSelectionHandlerPaint
        )
    }

    private fun drawSingleColumnSelection(
        canvas: Canvas,
        lineContent: LineContent,
        startRow: Int,
        endRow: Int,
        column: Int,
        paddingLeft: Float,
    ) {
        val startVisibleColumn = editor.getStartVisibleLine()
        val endVisibleColumn = editor.getEndVisibleLine()

        if (column < startVisibleColumn || column > endVisibleColumn) {
            return
        }

        val startX =
            paddingLeft + paints.codeTextPaint.measureText(lineContent, 0, startRow)
        val endX =
            startX + paints.codeTextPaint.measureText(lineContent, startRow, endRow)
        val startY = if (column == 1) 0 else getColumnY(paints.codeTextPaint,
            column - 1) + paints.codeTextPaint.fontMetricsInt.descent
        val endY = getColumnY(paints.codeTextPaint,
            column) + paints.codeTextPaint.fontMetricsInt.descent

        selectionRect.left = startX.toInt()
        selectionRect.top = startY
        selectionRect.right = endX.toInt()
        selectionRect.bottom = endY
        canvas.drawRect(selectionRect, paints.codeTextSelectionBackgroundPaint)
    }

    private fun drawMultiColumnSelection(
        canvas: Canvas,
        startPos: Position,
        endPos: Position,
        paddingLeft: Float,
    ) {
        var workColumn = startPos.column

        val startVisibleColumn = editor.getStartVisibleLine()
        val endVisibleColumn = editor.getEndVisibleLine()

        // 绘制起始行
        drawSingleColumnSelection(
            canvas,
            contentProvider.getLineContent(workColumn),
            startPos.row,
            contentProvider.getColumnRowCount(workColumn),
            workColumn,
            paddingLeft
        )

        ++workColumn
        while (workColumn < endPos.column) {
            if (workColumn < startVisibleColumn || workColumn > endVisibleColumn) {
                ++workColumn
                continue
            }
            val lineContent = contentProvider.getLineContent(workColumn)
            drawSingleColumnSelection(canvas,
                lineContent,
                0,
                lineContent.length,
                workColumn,
                paddingLeft)
            ++workColumn
        }

        val endLineContent = contentProvider.getLineContent(endPos.column)
        if (endPos.row > 0 && endLineContent.isNotEmpty()) {
            drawSingleColumnSelection(
                canvas,
                endLineContent,
                0,
                endPos.row,
                workColumn,
                paddingLeft
            )
        }
    }

    private fun getCursorOffsetX(cursor: Cursor): Float {
        val cursorColumn = cursor.column
        val cursorRow = cursor.row
        val content = contentProvider.getLineContent(cursorColumn)

        return paddingLeft + (controller.style.cursorAnimation?.animateX()
            ?: getDefaultCursorOffsetX(
                cursorRow,
                content))
    }

    private fun getCursorOffsetY(cursor: Cursor): Float {
        val cursorColumn = cursor.column
        return controller.style.cursorAnimation?.animateY() ?: getDefaultCursorOffsetY(cursorColumn)
    }

    private fun getDefaultCursorOffsetY(cursorColumn: Int): Float {
        return getColumnY(paints.lineNumberPaint, cursorColumn).toFloat()
    }

    private fun getDefaultCursorOffsetX(cursorRow: Int, content: LineContent): Float {
        return when (cursorRow) {
            0 -> {
                0f
            }

            else -> {
                val widths = FloatArray(cursorRow)
                paints.codeTextPaint.getTextWidths(content, 0, cursorRow, widths)
                var offset = 0f
                widths.forEach { offset += it }
                offset
            }
        }
    }

    //绘制代码
    private fun drawCodeText(canvas: Canvas) {
        paddingLeft += SPACE_LEFT * 2
        //按照可见性绘制代码

        val startLine = editor.getStartVisibleLine()
        val endLine = editor.getEndVisibleLine()

        paints.codeTextPaint.color =
            controller.theme.getColor(CodeEditorColorToken.IDENTIFIER_COLOR)

        var workColumn = startLine
        if (startLine > editor.getContentProvider().columnCount) {
            return
        }

        do {
            val content = editor.getContentProvider().getLineContent(workColumn)
            biggestX = max(biggestX,
                paints.codeTextPaint.measureText(content, 0, getCharSequenceEnd(content)))

            val spans = editor.getSpanProvider().getColumnSpan(workColumn)
            if (spans.isEmpty()) {
                paints.codeTextPaint.color =
                    editor.getController().theme.getColor(CodeEditorColorToken.IDENTIFIER_COLOR)

                //实现词法分析等等
                canvas.drawText(
                    content,
                    0,
                    getCharSequenceEnd(content),
                    paddingLeft,
                    getColumnY(paints.lineNumberPaint, workColumn).toFloat(),
                    paints.codeTextPaint
                )
            } else {
                var startIndex = 0
                var offsetX = paddingLeft
                try {
                    spans.forEach {
                        val token = it.first
                        val range = it.second
                        startIndex = range.first
                        val subText = content.subSequence(range.first, range.last)
                        val width =
                            paints.codeTextPaint.measureText(subText,
                                0,
                                getCharSequenceEnd(subText))
                        paints.codeTextPaint.color =
                            editor.getController().theme.getColor(token.getColorType()!!)

                        canvas.drawText(
                            subText,
                            0,
                            getCharSequenceEnd(subText),
                            offsetX,
                            getColumnY(paints.lineNumberPaint, workColumn).toFloat(),
                            paints.codeTextPaint
                        )

                        offsetX += width
                    }
                } catch (e: IndexOutOfBoundsException) {
                    paints.codeTextPaint.color =
                        editor.getController().theme.getColor(CodeEditorColorToken.IDENTIFIER_COLOR)
                    canvas.drawText(
                        content,
                        startIndex,
                        getCharSequenceEnd(content),
                        offsetX,
                        getColumnY(paints.lineNumberPaint, workColumn).toFloat(),
                        paints.codeTextPaint
                    )
                }
            }
            ++workColumn
        } while (workColumn <= endLine)

    }

    private fun getCharSequenceEnd(content: CharSequence): Int {
        return if (content.isEmpty()) 0 else content.length
    }

    fun getPaddingLeft(): Float {
        return paddingLeft
    }

    fun getMaxWidth(): Int {
        return (paddingLeft + biggestX).toInt()
    }

    fun getMaxHeight(): Int {

        return getColumnY(paints.lineNumberPaint, contentProvider.columnCount)
    }

}