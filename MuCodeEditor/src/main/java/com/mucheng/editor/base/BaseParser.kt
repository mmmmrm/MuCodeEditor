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

import com.mucheng.editor.language.ecmascript.EcmaScriptToken
import com.mucheng.editor.position.ColumnRowPosition

@Suppress("MemberVisibilityCanBePrivate")
abstract class BaseParser<T : BaseToken> {

    protected var index = 0

    protected lateinit var mSources: List<Pair<T, Pair<ColumnRowPosition, ColumnRowPosition>>>

    protected val mCompileError: MutableList<Pair<ColumnRowPosition, ColumnRowPosition>> =
        ArrayList()

    protected val neededTokens: MutableList<Pair<String, Pair<T, Pair<ColumnRowPosition, ColumnRowPosition>>>> =
        ArrayList()

    // 进行解析
    abstract fun parse()

    open fun clearAll() {
        mCompileError.clear()
        neededTokens.clear()
    }

    open fun findOffsetPair(offset: Int): Pair<T, Pair<ColumnRowPosition, ColumnRowPosition>>? {
        return mSources.getOrNull(index + offset)
    }

    open fun findOffsetToken(offset: Int): T? {
        return findOffsetPair(offset)?.first
    }

    @Suppress("SameParameterValue")
    protected fun find(
        startOffset: Int,
        targetToken: EcmaScriptToken,
        allowMultiColumn: Boolean = false,
    ): Int {
        var offset = startOffset
        var pair: Pair<T, Pair<ColumnRowPosition, ColumnRowPosition>>? = null
        while (findOffsetPair(offset)?.also { pair = it } != null) {
            if (pair!!.first == targetToken) {
                if (allowMultiColumn) {
                    return offset
                }
                return if (equalsSingleLine(
                        pair!!.second, mSources[index].second
                    )
                ) offset else -1
            }
            ++offset
        }
        return -1
    }

    protected fun equalsSingleLine(
        first: Pair<ColumnRowPosition, ColumnRowPosition>,
        second: Pair<ColumnRowPosition, ColumnRowPosition>,
    ): Boolean {
        return first.first.column == first.second.column &&
                second.first.column == second.second.column &&
                first.first.column == second.first.column &&
                first.second.column == second.second.column
    }

    open fun addNeededToken(
        type: String,
        token: T,
        startPos: ColumnRowPosition,
        endPos: ColumnRowPosition,
    ) {
        neededTokens.add(type to (token to (startPos to endPos)))
    }

    open fun getNeededToken(): List<Pair<String, Pair<T, Pair<ColumnRowPosition, ColumnRowPosition>>>> {
        return neededTokens
    }

    open fun setSources(sources: List<Pair<T, Pair<ColumnRowPosition, ColumnRowPosition>>>) {
        this.mSources = sources
        index = 0
    }

    open fun getCompileError(): MutableList<Pair<ColumnRowPosition, ColumnRowPosition>> {
        return mCompileError
    }

}