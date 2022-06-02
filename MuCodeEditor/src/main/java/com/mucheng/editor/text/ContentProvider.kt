package com.mucheng.editor.text

import android.util.Log
import com.mucheng.editor.annotation.Inline
import com.mucheng.editor.annotation.ThreadSafe
import com.mucheng.editor.component.Cursor
import com.mucheng.editor.impl.OpenArrayList
import com.mucheng.editor.position.ColumnRowPosition
import com.mucheng.editor.position.RangePosition
import com.mucheng.editor.views.MuCodeEditor
import java.util.concurrent.locks.ReadWriteLock
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.math.max
import kotlin.math.min

class ContentProvider(private val editor: MuCodeEditor, threadSafe: Boolean = true) {

    private val buffers: OpenArrayList<LineContent> = OpenArrayList()

    private val cursor = Cursor()

    private var lock: ReadWriteLock? = null

    init {
        buffers.add(LineContent(""))
        // 是否需要线程安全，默认需要
        if (threadSafe) {
            lock = ReentrantReadWriteLock()
        }
    }

    val columnCount: Int
        get() {
            return buffers.size
        }

    @ThreadSafe
    fun getColumnRowCount(line: Int): Int {
        return getLineContent(line).length
    }

    @ThreadSafe
    fun getLineContent(line: Int): LineContent {
        return withLocking(false) {
            buffers[line - 1]
        }
    }

    fun removeRangeLineContent(startColumn: Int, endColumn: Int) {
        return withLocking(true) {
            buffers.removeRange(startColumn - 1, endColumn - 1)
        }
    }

    fun getCursor(): Cursor {
        return cursor
    }

    /**
     * 对 scope 块加 读/写 锁
     *
     * @param write 是否为写锁
     * @param scope 代码块
     * @return scope 的返回值
     * */
    @Inline
    private inline fun <T : Any> withLocking(write: Boolean, scope: () -> T): T {
        return if (lock != null) {
            if (write) lock!!.writeLock().lock() else lock!!.readLock().lock()
            try {
                scope()
            } finally {
                // 一定要释放锁！否者就是死锁了!
                if (write) lock!!.writeLock().unlock() else lock!!.readLock().unlock()
            }
        } else {
            scope()
        }
    }

    @ThreadSafe
    fun addColumnContent(content: String) {
        withLocking(true) {
            buffers.add(LineContent(content))
        }
    }

    fun contentToString(): String {
        val buffer = StringBuffer()
        buffer.ensureCapacity(editor.getContentSize())

        var column = 1
        buffers.forEach {
            buffer.append(it)
            if (column < columnCount) {
                buffer.append('\n')
            }
            ++column
        }

        return buffer.toString()
    }

    override fun toString(): String {
        return "ContentProvider(columnCount -> $columnCount)"
    }

    fun clear() {
        withLocking(true) {
            buffers.clear()
        }
    }

    fun insertLineContent(column: Int, lineContent: LineContent) {
        withLocking(true) {
            if (column == columnCount + 1) {
                buffers.add(lineContent)
                return
            }

            buffers.add(column - 1, lineContent)
        }
    }

    fun remove(lineContent: LineContent): Boolean {
        return withLocking(true) {
            buffers.remove(lineContent)
        }
    }

    fun subText(selectionRange: RangePosition): CharSequence {
        val startPos = selectionRange.startPosition
        val endPos = selectionRange.endPosition
        val buffer = StringBuilder()

        if (startPos.column == endPos.column) {
            buffer.append(getLineContent(startPos.column).subSequence(startPos.row, endPos.row))
        }

        if (endPos.column > startPos.column) {
            val startLineContent = getLineContent(startPos.column)
            buffer.append(startLineContent.subSequence(startPos.row, startLineContent.length))
            buffer.append('\n')

            var workColumn = startPos.column + 1
            while (workColumn < endPos.column) {
                buffer.append(getLineContent(workColumn))
                buffer.append('\n')
                ++workColumn
            }
            val endLineContent = getLineContent(endPos.column)
            buffer.append(endLineContent.subSequence(0, endPos.row))
        }
        return buffer
    }

    fun getLineContents(): List<LineContent> {
        return buffers
    }

    fun delete(startPos: ColumnRowPosition, endPos: ColumnRowPosition) {
        val startColumn = startPos.column
        val startRow = startPos.row
        val endColumn = endPos.column
        val endRow = endPos.row

        withLocking(true) {
            deleteInternal(startColumn, startRow, endColumn, endRow)
        }
    }

    private fun deleteInternal(startColumn: Int, startRow: Int, endColumn: Int, endRow: Int) {
        var cursorColumn = cursor.column
        var cursorRow = cursor.row
        val startLineContent = getLineContent(startColumn)
        val endLineContent = getLineContent(endColumn)

        if (startColumn == endColumn) {
            startLineContent.delete(startRow, endRow)
            if (cursorColumn == startColumn && cursorRow > startRow) {
                cursorRow = startRow
            }
        }

        if (startColumn + 1 == endColumn) {
            startLineContent.delete(startRow, startLineContent.length)
            val insertText = endLineContent.subSequence(endRow, endLineContent.length)
            remove(endLineContent)
            startLineContent.append(insertText)
            when {
                cursorColumn == startColumn && cursorRow > startRow -> {
                    cursorRow = startRow
                }

                cursorColumn in startColumn..endColumn -> {
                    cursorColumn = startColumn
                    cursorRow = startRow
                }

                cursorColumn > endColumn -> {
                    --cursorColumn
                }
            }
        }

        if (startColumn + 1 < endColumn) {
            startLineContent.delete(startRow, startLineContent.length)
            val insertText = endLineContent.subSequence(endRow, endLineContent.length)
            removeRangeLineContent(startColumn + 1, endColumn + 1)
            startLineContent.append(insertText)
            when {
                cursorColumn == startColumn && cursorRow > startRow -> {
                    cursorRow = startRow
                }

                cursorColumn in startColumn..endColumn -> {
                    cursorColumn = startColumn
                    cursorRow = startRow
                }

                cursorColumn > endColumn -> {
                    cursorColumn -= endColumn - startColumn
                }
            }
        }

        cursor.column = cursorColumn
        cursor.row = cursorRow
    }

    fun insert(startPos: ColumnRowPosition, value: CharSequence) {
        val startColumn = startPos.column
        val startRow = startPos.row

        withLocking(true) {
            insertInternal(startColumn, startRow, value)
        }
    }

    private fun insertInternal(startColumn: Int, startRow: Int, value: CharSequence) {
        var cursorColumn = cursor.column
        var cursorRow = cursor.row
        val startLineContent = getLineContent(startColumn)
        var workColumn = startColumn
        val buffers = value.split('\n')
        buffers.forEach {
            if (workColumn == startColumn) {
                if (startRow == startLineContent.length) {
                    startLineContent.append(it)
                } else {
                    startLineContent.insert(startRow, it)
                }
                ++workColumn
                return@forEach
            }

            insertLineContent(workColumn, LineContent(it))
            ++workColumn
        }

        val endColumn = workColumn - 1
        if (startColumn == endColumn && cursorColumn == startColumn && cursorRow > startRow) {
            cursorRow += value.length
        } else if (cursorColumn == startColumn && cursorRow == startRow) {
            cursorColumn = endColumn
            cursorRow = getColumnRowCount(endColumn)
        } else if (cursorColumn > workColumn - 1) {
            cursorColumn += endColumn - startColumn
        }

        cursor.column = cursorColumn
        cursor.row = cursorRow
    }

}