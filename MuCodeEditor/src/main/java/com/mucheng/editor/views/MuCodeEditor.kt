package com.mucheng.editor.views

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.view.inputmethod.InputMethodManager
import android.widget.OverScroller
import com.mucheng.editor.annotation.Suspend
import com.mucheng.editor.annotation.UnsupportedUserUsage
import com.mucheng.editor.base.ColumnRowIndexer
import com.mucheng.editor.handler.TextInputConnectionDelegation
import com.mucheng.editor.controller.EditorController
import com.mucheng.editor.event.EventHandler
import com.mucheng.editor.impl.DefaultLexCoroutine
import com.mucheng.editor.impl.DefaultTextInputConnection
import com.mucheng.editor.indexer.CacheColumnRowIndexer
import com.mucheng.editor.paint.EditorPainter
import com.mucheng.editor.paint.EditorPaints
import com.mucheng.editor.position.RangePosition
import com.mucheng.editor.provider.SpanProvider
import com.mucheng.editor.text.ContentProvider
import com.mucheng.editor.util.execCursorAnimationNow
import com.mucheng.editor.util.getLineHeight
import com.mucheng.editor.util.initUnitContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

@Suppress("LeakingThis", "unused", "MemberVisibilityCanBePrivate")
open class MuCodeEditor @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0,
) : View(context, attrs, defStyleAttr) {

    //代码编辑器的控制器，用于更改属性什么的
    protected open val mController = EditorController(this)

    //文本提供者
    protected open val mContentProvider = ContentProvider(this)

    protected open val mSpanProvider = SpanProvider(this)

    //手势事件处理
    protected open val mEventHandler = EventHandler(this)

    //手势捕捉
    protected open val mGestureDetector = createGestureDetector()

    //绘制器，用于绘制编辑器的
    protected open val mPainter: EditorPainter

    //输入连接
    protected open val mInputConnection =
        TextInputConnectionDelegation(this, DefaultTextInputConnection(this@MuCodeEditor))

    //一些回调事件的注册
    protected open val mScroller = OverScroller(context)

    //索引变换的东西
    protected open val mLineRowIndexer = CacheColumnRowIndexer(mContentProvider)

    // 存放画笔
    protected open val mPaints: EditorPaints

    protected open val mLexCoroutine = DefaultLexCoroutine(this)

    init {
        //初始化 UnitUtil
        initUnitContext(context)

        //初始化绘制器
        mPainter = EditorPainter(this, mContentProvider)
        //推送代码控制器

        mPaints = EditorPaints()
    }

    fun setText(text: String) {
        mContentProvider.clear()

        text.replace("\r\n", "\n").split("\n").forEach {
            mContentProvider.addColumnContent(it)
        }
        // 通知需要进行 Rescan
        mController.state.lex(mLexCoroutine)
    }

    /**
     * 从 InputStream 中读取内容并加载
     * @param inputStream 需要读取输入流
     * @return Result<Unit> isFailed —— 是否失败，true 就是失败，即出现异常
     * */
    @Suspend
    open suspend fun open(inputStream: InputStream): Result<Unit> {
        return coroutineScope {
            return@coroutineScope withContext(Dispatchers.IO) {
                val byteArrayOutputStream = ByteArrayOutputStream()
                var flag: Int
                val buffer = ByteArray(1024)
                val result = runCatching {
                    byteArrayOutputStream.use {
                        inputStream.use {
                            while (inputStream.read(buffer).also { flag = it } != -1) {
                                byteArrayOutputStream.write(buffer, 0, flag)
                                byteArrayOutputStream.flush()
                            }
                        }
                    }
                }

                setText(String(byteArrayOutputStream.toByteArray()))
                return@withContext result
            }
        }
    }

    /**
     * 从文件路径中读取内容并加载
     * @param path 文件路径
     * @return Result<Unit> isFailed —— 是否失败，true 就是失败，即出现异常
     * */
    @Suspend
    open suspend fun open(path: String): Result<Unit> {
        return runCatching {
            open(FileInputStream(path).buffered())
        }
    }

    /**
     * 保存内容到路径
     * @param path 保存到的路径
     * @return Result<Unit> isFailed —— 是否失败，true 就是失败，即出现异常
     * */
    @Suspend
    open suspend fun save(path: String): Result<Unit> {
        return coroutineScope {
            return@coroutineScope withContext(Dispatchers.IO) {
                val sr = mContentProvider.contentToString().reader()
                val result = runCatching {
                    val bw = FileOutputStream(path).bufferedWriter()
                    var flag: Int
                    val buffer = CharArray(1024)
                    sr.use {
                        bw.use {
                            while (sr.read(buffer).also { flag = it } != -1) {
                                bw.write(buffer, 0, flag)
                                bw.flush()
                            }
                        }
                    }
                }
                return@withContext result
            }
        }
    }

    open fun getText(): String {
        return mContentProvider.contentToString()
    }

    // 返回文本总大小（Index）
    open fun getContentSize(): Int {
        val column = mContentProvider.columnCount
        val row = mContentProvider.getColumnRowCount(column)
        return mLineRowIndexer.columnRow2Index(column, row).index
    }

    open fun getController(): EditorController {
        return mController
    }

    open fun getSpanProvider(): SpanProvider {
        return mSpanProvider
    }

    override fun computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.currX, mScroller.currY)
            invalidate()
        }
        super.computeScroll()
    }

    //绘制
    @UnsupportedUserUsage
    override fun onDraw(canvas: Canvas) {
        //测量绘制用时
        val startTime = System.currentTimeMillis()
        //绘制类
        mPainter.onDraw(canvas)
        //结束用时
        val endTime = System.currentTimeMillis()

        //Log.e("Editor onDraw", "共花费: ${endTime - startTime} ms.")
    }

    // 先得让别人知道这玩意是个输入框
    override fun onCheckIsTextEditor(): Boolean {
        return isEnabled && mController.isEnabled
    }

    // 与输入法建立联系
    @UnsupportedUserUsage
    override fun onCreateInputConnection(outAttrs: EditorInfo): InputConnection? {
        if (!isEnabled || !mController.isEnabled) {
            return null
        }

        // 设置我们需要的输入是多行的文本
        outAttrs.inputType = EditorInfo.TYPE_TEXT_FLAG_MULTI_LINE
        // 创建我们的 输入连接
        return mInputConnection
    }

    @UnsupportedUserUsage
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {

        if (event.action == KeyEvent.ACTION_DOWN) {
            when (keyCode) {
                KeyEvent.KEYCODE_DEL, KeyEvent.KEYCODE_FORWARD_DEL ->
                    mInputConnection.deleteSurroundingText(0, 0)

                KeyEvent.KEYCODE_ENTER ->
                    mInputConnection.commitText("\n", 0)

                KeyEvent.KEYCODE_TAB -> {
                    mInputConnection.commitText("   ", 0)
                }

                KeyEvent.KEYCODE_DPAD_LEFT -> {
                    mInputConnection.onCursorLeft()
                }

                KeyEvent.KEYCODE_DPAD_RIGHT -> {
                    mInputConnection.onCursorRight()
                }

                KeyEvent.KEYCODE_DPAD_UP -> {
                    mInputConnection.onCursorTop()
                }

                KeyEvent.KEYCODE_DPAD_DOWN -> {
                    mInputConnection.onCursorBottom()
                }

                KeyEvent.KEYCODE_DPAD_UP_LEFT -> {
                    mInputConnection.onCursorTop()
                    mInputConnection.onCursorLeft()
                }

                KeyEvent.KEYCODE_DPAD_UP_RIGHT -> {
                    mInputConnection.onCursorTop()
                    mInputConnection.onCursorRight()
                }

                KeyEvent.KEYCODE_DPAD_DOWN_LEFT -> {
                    mInputConnection.onCursorBottom()
                    mInputConnection.onCursorLeft()
                }

                KeyEvent.KEYCODE_DPAD_DOWN_RIGHT -> {
                    mInputConnection.onCursorBottom()
                    mInputConnection.onCursorRight()
                }

                KeyEvent.KEYCODE_M -> {
                    val clipText = getClipboardText()
                    if (clipText.isNotEmpty()) {
                        mInputConnection.commitText(clipText, 0)
                    }
                }

                KeyEvent.KEYCODE_COPY -> {
                    copySelectionText()
                }

                KeyEvent.KEYCODE_PAGE_UP -> {
                    // 移动光标
                    mInputConnection.onCursorHome()
                }

                KeyEvent.KEYCODE_PAGE_DOWN -> {
                    // 移动光标
                    mInputConnection.onCursorEnd()
                }

                KeyEvent.KEYCODE_MOVE_HOME -> {
                    // 移动光标
                    mInputConnection.onCursorHome()
                }

                KeyEvent.KEYCODE_MOVE_END -> {
                    // 移动光标
                    mInputConnection.onCursorEnd()
                }

                else -> {
                    mInputConnection.onVirtualKeyboardInput(event)
                }
            }
        }

        //通知编辑器更新
        invalidate()
        return super.onKeyDown(keyCode, event)
    }

    private fun getClipboardText(): String {
        val clipboardManager =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = clipboardManager.primaryClip

        if (clipData != null && clipData.itemCount > 0) {
            return clipData.getItemAt(0).text.toString()
        }

        return ""
    }

    open fun copySelectionText() {
        val stateController = mController.state
        if (!stateController.selection || stateController.selectionRange == null) {
            return
        }
        val selectionRange = stateController.selectionRange!!

        val clipboardManager =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText(null, mContentProvider.subText(
            selectionRange
        ))
        clipboardManager.setPrimaryClip(clipData)
    }

    open fun selectAll() {
        requestFocus()

        val stateController = mController.state
        val count = mContentProvider.columnCount
        val startPos = mLineRowIndexer.columnRow2Index(1, 0)
        val endPos = mLineRowIndexer.columnRow2Index(count,
            mContentProvider.getColumnRowCount(count))

        stateController.selectText(RangePosition(
            startPos, endPos
        ))

        val cursor = mContentProvider.getCursor()
        execCursorAnimationNow(mController.style.cursorAnimation, this) {
            cursor.column = endPos.column
            cursor.row = endPos.row
        }
        scrollToBottom()
    }

    open fun scrollToTop() {
        scrollToColumn(1)
    }

    open fun scrollToBottom() {
        scrollToColumn(mContentProvider.columnCount)
    }

    open fun scrollToColumn(column: Int) {
        val startVisibleColumn = min(mContentProvider.columnCount, getStartVisibleLine() + 1)
        val endVisibleColumn = max(1, getEndVisibleLine() - 1)

        if (startVisibleColumn == endVisibleColumn) {
            return
        }

        if (column == 1) {
            val offsetY = -mScroller.currY.toFloat()
            mEventHandler.scrollBy(0f, offsetY, false)
            return
        }

        if (column == mContentProvider.columnCount) {
            val offsetY = mEventHandler.getEditorMaxScrollY() - mScroller.currY
            mEventHandler.scrollBy(0f, offsetY.toFloat(), false)
            return
        }

        if (column in startVisibleColumn..endVisibleColumn) {
            return
        }

        if (column < startVisibleColumn) {
            val offsetY = (column - startVisibleColumn) * getLineHeight(mPaints.lineNumberPaint)
            mEventHandler.scrollBy(0f, offsetY.toFloat(), true)
            return
        }

        if (column > endVisibleColumn) {
            val offsetY = (column - endVisibleColumn) * getLineHeight(mPaints.lineNumberPaint)
            mEventHandler.scrollBy(0f, offsetY.toFloat(), true)
            return
        }

    }

    // 弹出软键盘
    fun showSoftInputMethod() {
        requestFocus()

        val inputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(this, InputMethodManager.SHOW_FORCED)
    }

    // 由 x 坐标推到至列
    open fun getRowByPointX(x: Float, column: Int): Int {
        if (x <= mPainter.getPaddingLeft()) {
            return 0
        }

        val lineContent = mContentProvider.getLineContent(column)

        if (x >= getMaxScrollX()) {
            return lineContent.length
        }

        val charArray = lineContent.accessCharArray()
        val widths = FloatArray(charArray.size)
        mPaints.codeTextPaint.getTextWidths(charArray, 0, widths.size, widths)

        var offsetX = mPainter.getPaddingLeft()
        val sorts = widths.mapIndexed { index, it ->
            offsetX += it
            Pair(index, abs(offsetX - x))
        }.toList().sortedWith { o1, o2 ->
            // 以自然数的方式比较两个量
            o1.second.compareTo(o2.second)
        }

        return min(sorts.getOrNull(0)?.let { it.first + 1 } ?: 0, lineContent.length)
    }

    // 由 x 坐标推到至列
    open fun getLineByPointY(y: Float): Int {
        // l :: Int -> y / lineHeight + 1
        // 即：行 = y / 行高（取整） + 1

        return min(
            (y / getLineHeight(mPaints.lineNumberPaint)).toInt() + 1,
            mContentProvider.columnCount
        )
    }

    //代码编辑器的最大 x 坐标（相当于滑动的最大宽）
    open fun getMaxScrollX(): Int {
        return mPainter.getMaxWidth()
    }

    //代码编辑器的最大 y 坐标（相当于滑动的最大高）
    open fun getMaxScrollY(): Int {
        return mPainter.getMaxHeight()
    }

    // 显示可见的起始行，这玩意真的特别节省性能
    open fun getStartVisibleLine(): Int {
        /**
         *  算法：offsetY / lineHeight = 偏移的行数
         * */
        val startVisibleLine = mScroller.currY / getLineHeight(mPaints.lineNumberPaint)

        return max(startVisibleLine, 1)
    }

    // 显示可见的结束行，这玩意真的特别节省性能
    open fun getEndVisibleLine(): Int {
        val lineHeight = getLineHeight(mPaints.lineNumberPaint)
        val endVisibleLine = (height + mScroller.currY) / lineHeight + 1

        return min(endVisibleLine, mContentProvider.columnCount)
    }

    open fun getContentProvider(): ContentProvider {
        return mContentProvider
    }

    //拦截事件
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP) {
            mEventHandler.onUp()
        }
        return mGestureDetector.onTouchEvent(event)
    }

    //创建手势检测器
    private fun createGestureDetector(): GestureDetector {
        return GestureDetector(context, mEventHandler).apply {
            setOnDoubleTapListener(mEventHandler)
        }
    }

    open fun getPaints(): EditorPaints {
        return this.mPaints
    }

    open fun getLexCoroutine(): DefaultLexCoroutine {
        return mLexCoroutine
    }

    open fun getScroller(): OverScroller {
        return mScroller
    }

    fun getIndexer(): ColumnRowIndexer {
        return mLineRowIndexer
    }

    @UnsupportedUserUsage
    internal fun getPainter(): EditorPainter {
        return mPainter
    }

    fun showCodeAutoCompletionPanel() {
        if (!mController.isEnabledAutoCompletion) {
            return
        }

        mController.autoCompletionPanel?.requireAutoCompletionPanel()
    }

    fun dismissCodeAutoCompletionPanel() {
        mController.autoCompletionPanel?.dismiss()
    }

    fun getTextInputConnection(): TextInputConnectionDelegation {
        return mInputConnection
    }

}