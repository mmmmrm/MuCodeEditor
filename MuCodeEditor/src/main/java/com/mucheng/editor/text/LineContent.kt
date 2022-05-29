package com.mucheng.editor.text

class LineContent(content: String) : CharSequence {

    private val buffer = StringBuilder(content)

    private var value: CharArray? = null

    override val length: Int
        get() = buffer.length

    override fun get(index: Int): Char {
        return buffer[index]
    }

    override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
        return buffer.subSequence(startIndex, endIndex)
    }

    fun accessCharArray(): CharArray {
        return toString().toCharArray()
    }

    override fun toString(): String {
        return buffer.toString()
    }

    fun insert(index: Int, text: CharSequence) {
        buffer.insert(index, text)
    }

    fun append(text: CharSequence) {
        buffer.append(text)
    }

    fun deleteCharAt(index: Int) {
        buffer.deleteCharAt(index)
    }

    fun delete(startIndex: Int, endIndex: Int) {
        buffer.delete(startIndex, endIndex)
    }

}