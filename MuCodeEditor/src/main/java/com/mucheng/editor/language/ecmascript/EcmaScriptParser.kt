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

package com.mucheng.editor.language.ecmascript

import com.mucheng.editor.base.BaseAutoCompleteHelper
import com.mucheng.editor.base.BaseParser
import com.mucheng.editor.enums.CodeEditorColorToken
import com.mucheng.editor.position.ColumnRowPosition
import java.nio.file.Files.find

open class EcmaScriptParser : BaseParser<EcmaScriptToken>() {

    override fun parse() {
        var lastToken: EcmaScriptToken? = null
        var lastRange: Pair<ColumnRowPosition, ColumnRowPosition>? = null
        mSources.forEachIndexed { index, pair ->
            val token = pair.first
            val range = pair.second

            this.index = index
            if (lastToken != null) {
                handleVariable(lastToken!!, lastRange!!, token, range)
            }

            lastToken = token
            lastRange = range
        }
    }

    private fun handleVariable(
        lastToken: EcmaScriptToken,
        lastRange: Pair<ColumnRowPosition, ColumnRowPosition>,
        token: EcmaScriptToken,
        range: Pair<ColumnRowPosition, ColumnRowPosition>,
    ) {
        /*
        * var 的判定如下
        * var variable (=expr)
        * */
        if (equalsSingleLine(lastRange, range) &&
            (lastToken == EcmaScriptToken.VAR ||
                    lastToken == EcmaScriptToken.LET) &&
            token == EcmaScriptToken.IDENTIFIER
        ) {
            addNeededToken(BaseAutoCompleteHelper.VARIABLE, token, range.first, range.second)
            return
        }

        /*
        * const 判定如下
        * const name = expr(expr ∉ Symbol)
        * */
        if (equalsSingleLine(lastRange, range) &&
            lastToken == EcmaScriptToken.CONST &&
            token == EcmaScriptToken.IDENTIFIER &&
            findOffsetToken(1) == EcmaScriptToken.EQUALS &&
            findOffsetToken(2) != null &&
            findOffsetToken(2) != EcmaScriptToken.SEMICOLON
        ) {
            addNeededToken(BaseAutoCompleteHelper.VARIABLE, token, range.first, range.second)
        }

        /**
         * 满足以下条件判断为 function
         * function identifier(...){
         * ...
         * }
         * */
        if (equalsSingleLine(lastRange, range) &&
            lastToken == EcmaScriptToken.FUNCTION &&
            token == EcmaScriptToken.IDENTIFIER &&
            findOffsetToken(1) == EcmaScriptToken.LEFT_PARENTHESIS &&
            find(2, EcmaScriptToken.RIGHT_PARENTHESIS, true).run {
                if (this != -1) {
                    val offset = this
                    findOffsetToken(offset + 1) == EcmaScriptToken.LEFT_BRACE &&
                            (find(offset + 2, EcmaScriptToken.RIGHT_BRACE, true) != -1)
                } else {
                    false
                }
            }
        ) {
            addNeededToken(BaseAutoCompleteHelper.FUNCTION, token, range.first, range.second)
            return
        }
    }

    override fun setSources(sources: List<Pair<EcmaScriptToken, Pair<ColumnRowPosition, ColumnRowPosition>>>) {
        // Parse 阶段 Token 不应包含 WHITESPACE
        super.setSources(sources.filter { it.first != EcmaScriptToken.WHITESPACE })
    }

}