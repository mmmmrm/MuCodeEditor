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

package com.mucheng.editor.base

import com.mucheng.editor.position.ColumnRowPosition

@Suppress("MemberVisibilityCanBePrivate", "LeakingThis")
abstract class BaseLexer<T : BaseToken> {

    protected val mTokens: MutableList<Triple<T, ColumnRowPosition, ColumnRowPosition>> =
        ArrayList()

    protected lateinit var sources: List<CharSequence>
        private set

    protected lateinit var scannedColumnSource: CharSequence

    protected var column = 1

    protected var row = 0

    protected var scannedChar = '\u0000'

    protected lateinit var mSpecialTables: Map<String, T>

    protected lateinit var mKeywordTables: Map<String, T>

    protected lateinit var mSymbolTables: Map<Char, T>

    open fun getSpecialTables(): Map<String, T> {
        return mSpecialTables
    }

    open fun getKeywordTables(): Map<String, T> {
        return mKeywordTables
    }

    open fun analyze() {

    }

    protected open fun addToken(token: T, startPos: ColumnRowPosition, endPos: ColumnRowPosition) {
        mTokens.add(Triple(token, startPos, endPos))
    }

    protected open fun getChar() {
        scannedChar = scannedColumnSource[row]
    }

    protected open fun yyChar() {
        ++row
        scannedChar = if (isNotRowEOF()) {
            scannedColumnSource[row]
        } else {
            '\u0000'
        }
    }

    open fun columnSize(): Int {
        return sources.size
    }

    open fun rowSize(): Int {
        return scannedColumnSource.length
    }

    open fun setSources(sources: List<CharSequence>) {
        clearAll()
        this.sources = sources

        column = 1
        row = 0
    }

    open fun getTokens(): List<Triple<T, ColumnRowPosition, ColumnRowPosition>> {
        return mTokens
    }

    open fun toColumnTokens(): MutableList<MutableList<Pair<BaseToken, IntRange>>> {
        val buffer: MutableList<MutableList<Pair<BaseToken, IntRange>>> = ArrayList()
        buffer.add(ArrayList())

        for (triple in getTokens().toList()) {

            val token = triple.first
            val column = triple.second.column
            val range = triple.second.row..triple.third.row
            if (buffer.size == column) {
                buffer.last().add(token to range)
            } else if (column > buffer.size) {
                while (column > buffer.size) {
                    buffer.add(ArrayList())
                }

                buffer.last().add(token to range)
            }
        }

        return buffer
    }

    open fun isNotRowEOF(): Boolean {
        return !isRowEOF()
    }

    open fun isRowEOF(): Boolean {
        return row >= rowSize()
    }

    open fun isWhitespace(): Boolean {
        return scannedChar == ' '
    }

    open fun isWhitespace(target: Char): Boolean {
        return target == ' '
    }

    open fun isLetter(): Boolean {
        return scannedChar in 'a'..'z' || scannedChar in 'A'..'Z'
    }

    open fun isLetter(target: Char): Boolean {
        return target in 'a'..'z' || target in 'A'..'Z'
    }

    open fun isDigit(): Boolean {
        return scannedChar in '0'..'9'
    }

    open fun isDigit(target: Char): Boolean {
        return target in '0'..'9'
    }

    open fun isSymbol(): Boolean {
        return mSymbolTables.containsKey(scannedChar)
    }

    open fun isSymbol(target: Char): Boolean {
        return mSymbolTables.containsKey(target)
    }

    open fun isKeyword(buffer: String): Boolean {
        return mKeywordTables.containsKey(buffer)
    }

    open fun isSpecial(buffer: String): Boolean {
        return mSpecialTables.containsKey(buffer)
    }

    open fun clearAll() {
        mTokens.clear()
    }

    protected abstract fun setup()

    init {
        setup()
    }

}