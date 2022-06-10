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

package com.mucheng.editor.language.css

import com.mucheng.editor.base.BaseToken
import com.mucheng.editor.enums.CodeEditorColorToken

open class CssToken(type: CodeEditorColorToken?, value: String) : BaseToken(type, value) {

    companion object {
        val WHITESPACE = CssToken(CodeEditorColorToken.KEYWORD_COLOR, "WHITESPACE")
        val COMMENT = CssToken(CodeEditorColorToken.COMMENT_COLOR, "/* Comment */")
        val STRING = CssToken(CodeEditorColorToken.STRING_COLOR, "\"String\"")
        val DIGIT = CssToken(CodeEditorColorToken.NUMERICAL_VALUE_COLOR, "DIGIT")
        val ATTRIBUTE = CssToken(CodeEditorColorToken.KEYWORD_COLOR, "attribute:")
        val IDENTIFIER = CssToken(CodeEditorColorToken.IDENTIFIER_COLOR, "identifier")

        val PLUS = CssToken(CodeEditorColorToken.SYMBOL_COLOR, "+")
        val MINUS = CssToken(CodeEditorColorToken.SYMBOL_COLOR, "-")
        val MULTI = CssToken(CodeEditorColorToken.SYMBOL_COLOR, "*")
        val DIV = CssToken(CodeEditorColorToken.SYMBOL_COLOR, "/")
        val NOT = CssToken(CodeEditorColorToken.SYMBOL_COLOR, "!")
        val MOD = CssToken(CodeEditorColorToken.SYMBOL_COLOR, "%")
        val XOR = CssToken(CodeEditorColorToken.SYMBOL_COLOR, "^")
        val AND = CssToken(CodeEditorColorToken.SYMBOL_COLOR, "&")
        val QUESTION = CssToken(CodeEditorColorToken.SYMBOL_COLOR, "?")
        val COMP = CssToken(CodeEditorColorToken.SYMBOL_COLOR, "~")
        val DOT = CssToken(CodeEditorColorToken.SYMBOL_COLOR, ".")
        val COMMA = CssToken(CodeEditorColorToken.SYMBOL_COLOR, ",")
        val SEMICOLON = CssToken(CodeEditorColorToken.SYMBOL_COLOR, ";")
        val EQUALS = CssToken(CodeEditorColorToken.SYMBOL_COLOR, "=")
        val LEFT_PARENTHESIS = CssToken(CodeEditorColorToken.SYMBOL_COLOR, "(")
        val RIGHT_PARENTHESIS = CssToken(CodeEditorColorToken.SYMBOL_COLOR, ")")
        val LEFT_BRACKET = CssToken(CodeEditorColorToken.SYMBOL_COLOR, "[")
        val RIGHT_BRACKET = CssToken(CodeEditorColorToken.SYMBOL_COLOR, "]")
        val LEFT_BRACE = CssToken(CodeEditorColorToken.SYMBOL_COLOR, "{")
        val RIGHT_BRACE = CssToken(CodeEditorColorToken.SYMBOL_COLOR, "}")
        val OR = CssToken(CodeEditorColorToken.SYMBOL_COLOR, "|")
        val LESS_THAN = CssToken(CodeEditorColorToken.SYMBOL_COLOR, "<")
        val MORE_THAN = CssToken(CodeEditorColorToken.SYMBOL_COLOR, ">")

    }

}