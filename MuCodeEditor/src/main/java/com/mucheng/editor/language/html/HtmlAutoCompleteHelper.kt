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

package com.mucheng.editor.language.html

import com.mucheng.editor.R
import com.mucheng.editor.base.BaseAutoCompleteHelper
import com.mucheng.editor.base.BaseLexer
import com.mucheng.editor.common.AutoCompleteItem
import com.mucheng.editor.component.Cursor
import com.mucheng.editor.handler.TextInputConnectionDelegation
import com.mucheng.editor.text.LineContent

class HtmlAutoCompleteHelper : BaseAutoCompleteHelper() {

    companion object {
        const val ELEMENT = "E"
        const val ATTRIBUTE = "P"
    }

    override fun getMySimpleDescription(type: String, name: String): String {
        return when (type) {
            ELEMENT -> "元素 $name"
            ATTRIBUTE -> "属性 $name"
            else -> throw RuntimeException("Stub!")
        }
    }

    override fun getMyTypeIconResource(type: String): Int {
        return when (type) {
            ELEMENT -> R.drawable.ic_auto_completion_element
            ATTRIBUTE -> R.drawable.ic_auto_completion_attribute
            else -> throw RuntimeException("Stub!")
        }
    }

    override fun getMyTypeDescription(type: String): String {
        return when (type) {
            ELEMENT -> "element"
            ATTRIBUTE -> "attribute"
            else -> throw RuntimeException("Stub!")
        }
    }

    override fun skipIfNeeded(lexer: BaseLexer<*>, char: Char): Boolean {
        if (lexer is HtmlLexer) {
            return lexer.isDigit(char) || lexer.isWhitespace(char) || char == '>'
        }

        return super.skipIfNeeded(lexer, char)
    }

    override fun insertText(
        cursor: Cursor,
        item: AutoCompleteItem,
        lineContent: LineContent,
        text: String,
        inputConnection: TextInputConnectionDelegation,
    ) {
        if (item.completeText.startsWith(text)) {
            super.insertText(cursor, item, lineContent, text, inputConnection)
        } else {
            val inputStart = cursor.row - text.length
            val inputEnd = cursor.row
            cursor.row = inputStart
            lineContent.delete(inputStart, inputEnd)
            inputConnection.commitText(item.completeText, 0)
        }
    }

}