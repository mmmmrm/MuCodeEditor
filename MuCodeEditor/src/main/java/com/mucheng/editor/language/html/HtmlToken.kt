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

import com.mucheng.editor.base.BaseToken
import com.mucheng.editor.enums.CodeEditorColorToken
import com.mucheng.editor.language.ecmascript.EcmaScriptToken

@Suppress("SpellCheckingInspection")
class HtmlToken(type: CodeEditorColorToken?, value: String) : BaseToken(type, value) {

    companion object {
        val IDENTIFIER = HtmlToken(CodeEditorColorToken.IDENTIFIER_COLOR, "IDENTIFIER")
        val ATTRIBUTE = HtmlToken(CodeEditorColorToken.NUMERICAL_VALUE_COLOR, "ATTRIBUTE")
        val ELEMENT_NAME = HtmlToken(CodeEditorColorToken.KEYWORD_COLOR, "ELEMENT_NAME")
        val WHITESPACE = HtmlToken(CodeEditorColorToken.KEYWORD_COLOR, "WHITESPACE")
        val DOCTYPE = HtmlToken(CodeEditorColorToken.NUMERICAL_VALUE_COLOR, "DOCTYPE")
        val COMMENT = HtmlToken(CodeEditorColorToken.COMMENT_COLOR, "COMMENT")
        val STRING = HtmlToken(CodeEditorColorToken.STRING_COLOR, "STRING")

        val PLUS = HtmlToken(CodeEditorColorToken.SYMBOL_COLOR, "PLUS") // '+'
        val MINUS = HtmlToken(CodeEditorColorToken.SYMBOL_COLOR, "MINUS") // '-'
        val MULTI = HtmlToken(CodeEditorColorToken.SYMBOL_COLOR, "MULTI") // '*'
        val DIV = HtmlToken(CodeEditorColorToken.SYMBOL_COLOR, "DIV") // '/'
        val COLON = HtmlToken(CodeEditorColorToken.SYMBOL_COLOR, "COLON") // ':'
        val NOT = HtmlToken(CodeEditorColorToken.SYMBOL_COLOR, "NOT") // '!'
        val MOD = HtmlToken(CodeEditorColorToken.SYMBOL_COLOR, "MOD") // '%'
        val XOR = HtmlToken(CodeEditorColorToken.SYMBOL_COLOR, "XOR") // '^'
        val AND = HtmlToken(CodeEditorColorToken.SYMBOL_COLOR, "AND") // '&'
        val QUESTION = HtmlToken(CodeEditorColorToken.SYMBOL_COLOR, "QUESTION") // '?'
        val COMP = HtmlToken(CodeEditorColorToken.SYMBOL_COLOR, "COMP") // '~'
        val DOT = HtmlToken(CodeEditorColorToken.SYMBOL_COLOR, "DOT") // '.'
        val COMMA = HtmlToken(CodeEditorColorToken.SYMBOL_COLOR, "COMMA") // ','
        val SEMICOLON = HtmlToken(CodeEditorColorToken.SYMBOL_COLOR, "SEMICOLON") // ';'
        val EQUALS = HtmlToken(CodeEditorColorToken.SYMBOL_COLOR, "EQUALS") // '='
        val LEFT_PARENTHESIS =
            HtmlToken(CodeEditorColorToken.SYMBOL_COLOR, "LEFT_PARENTHESIS") // '('
        val RIGHT_PARENTHESIS =
            HtmlToken(CodeEditorColorToken.SYMBOL_COLOR, "RIGHT_PARENTHESIS") // ')'
        val LEFT_BRACKET = HtmlToken(CodeEditorColorToken.SYMBOL_COLOR, "LEFT_BRACKET") // '['
        val RIGHT_BRACKET =
            HtmlToken(CodeEditorColorToken.SYMBOL_COLOR, "RIGHT_BRACKET") // ']'
        val LEFT_BRACE = HtmlToken(CodeEditorColorToken.SYMBOL_COLOR, "LEFT_BRACE") // '{'
        val RIGHT_BRACE = HtmlToken(CodeEditorColorToken.SYMBOL_COLOR, "RIGHT_BRACE") // '}'
        val OR = HtmlToken(CodeEditorColorToken.SYMBOL_COLOR, "OR") // '|'
        val LESS_THAN = HtmlToken(CodeEditorColorToken.SYMBOL_COLOR, "LESS_THAN") // '<'
        val MORE_THAN = HtmlToken(CodeEditorColorToken.SYMBOL_COLOR, "MORE_THAN") // '>'

    }

}