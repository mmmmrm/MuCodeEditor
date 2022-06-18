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

import com.mucheng.editor.enums.CodeEditorColorToken
import com.mucheng.editor.language.ecmascript.EcmaScriptAST
import com.mucheng.editor.language.ecmascript.EcmaScriptToken

@Suppress("MemberVisibilityCanBePrivate")
abstract class BaseParser<T : BaseToken> {

    protected var column = 1

    protected var index = 0

    protected lateinit var sources: List<List<Pair<T, IntRange>>>
        private set

    protected lateinit var scannedColumnSource: List<Pair<T, IntRange>>

    protected lateinit var scannedRange: IntRange

    protected lateinit var scannedToken: T

    protected abstract var eofToken: T

    protected var astRoot = EcmaScriptAST(null)

    protected val mCompileError: MutableList<Triple<Int, String, IntRange>> = ArrayList()

    // 进行解析
    abstract fun parse()

    open fun columnSize(): Int {
        return sources.size
    }

    open fun indexSize(): Int {
        return scannedColumnSource.size
    }

    open fun clearAll() {
        mCompileError.clear()
    }

    open fun getToken() {
        val pair = scannedColumnSource[index]
        scannedToken = pair.first
        scannedRange = pair.second
    }

    open fun yyToken() {
        ++index
        if (isNotRowEOF()) {
            val pair = scannedColumnSource[index]
            scannedToken = pair.first
            scannedRange = pair.second
        } else {
            scannedToken = eofToken
        }
    }

    open fun isNotRowEOF(): Boolean {
        return !isRowEOF()
    }

    open fun isRowEOF(): Boolean {
        return index >= indexSize()
    }

    open fun setSources(sources: List<List<Pair<T, IntRange>>>) {
        clearAll()

        this.sources = sources
        column = 1
        index = 0
    }

    open fun addCompileError(description: String, range: IntRange) {
        mCompileError.add(Triple(column, description, range))
    }

    open fun getCompileError(): List<Triple<Int, String, IntRange>> {
        return mCompileError
    }

}
