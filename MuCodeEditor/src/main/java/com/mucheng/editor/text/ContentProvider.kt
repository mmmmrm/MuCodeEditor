package com.mucheng.editor.text

import com.mucheng.editor.annotation.Inline
import com.mucheng.editor.annotation.ThreadSafe
import com.mucheng.editor.component.Cursor
import com.mucheng.editor.impl.OpenArrayList
import com.mucheng.editor.position.RangePosition
import com.mucheng.editor.views.MuCodeEditor
import java.util.concurrent.locks.ReadWriteLock
import java.util.concurrent.locks.ReentrantReadWriteLock

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

        val startText =
            getLineContent(startPos.column).let { it.subSequence(startPos.row, it.length) }
        val endContent = getLineContent(endPos.column).subSequence(0, endPos.row)

        buffer.append(startText)
        buffer.append('\n')

        var workColumn = startPos.column
        while (workColumn < endPos.column) {
            buffer.append(getLineContent(workColumn))
            if (workColumn < endPos.column - 1) {
                buffer.append('\n')
            }
            ++workColumn
        }

        buffer.append(endContent)

        return buffer
    }

    fun getLineContents(): List<LineContent> {
        return buffers
    }

}