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

import com.mucheng.editor.base.BaseToken
import com.mucheng.editor.enums.CodeEditorColorToken

@Suppress("MemberVisibilityCanBePrivate")
class EcmaScriptToken private constructor(type: CodeEditorColorToken, value: String) :
    BaseToken(type, value) {

    companion object {

        val EOF = EcmaScriptToken(CodeEditorColorToken.KEYWORD_COLOR, "EOF")
        val REGEX = EcmaScriptToken(CodeEditorColorToken.SPECIAL_COLOR, "REGEX")
        val SINGLE_COMMENT = EcmaScriptToken(CodeEditorColorToken.COMMENT_COLOR, "SINGLE_COMMENT")
        val MULTI_COMMENT_START =
            EcmaScriptToken(CodeEditorColorToken.COMMENT_COLOR, "MULTI_COMMENT_START")
        val MULTI_COMMENT_PART =
            EcmaScriptToken(CodeEditorColorToken.COMMENT_COLOR, "MULTI_COMMENT_PART")
        val MULTI_COMMENT_END =
            EcmaScriptToken(CodeEditorColorToken.COMMENT_COLOR, "MULTI_COMMENT_END")

        val SINGLE_STRING = EcmaScriptToken(CodeEditorColorToken.STRING_COLOR, "SINGLE_STRING")
        val TEMPLATE_STRING = EcmaScriptToken(CodeEditorColorToken.STRING_COLOR, "TEMPLATE_STRING")

        val DIGIT_NUMBER =
            EcmaScriptToken(CodeEditorColorToken.NUMERICAL_VALUE_COLOR, "DIGIT_NUMBER")
        val IDENTIFIER = EcmaScriptToken(CodeEditorColorToken.IDENTIFIER_COLOR, "IDENTIFIER")

        val WHITESPACE = EcmaScriptToken(CodeEditorColorToken.KEYWORD_COLOR, "WHITESPACE")

        val FALSE = EcmaScriptToken(CodeEditorColorToken.SPECIAL_COLOR, "FALSE")
        val TRUE = EcmaScriptToken(CodeEditorColorToken.SPECIAL_COLOR, "TRUE")
        val NAN = EcmaScriptToken(CodeEditorColorToken.SPECIAL_COLOR, "NAN")
        val UNDEFINED = EcmaScriptToken(CodeEditorColorToken.SPECIAL_COLOR, "UNDEFINED")
        val NULL = EcmaScriptToken(CodeEditorColorToken.SPECIAL_COLOR, "NULL")

        val VAR = EcmaScriptToken(CodeEditorColorToken.KEYWORD_COLOR, "VAR")
        val LET = EcmaScriptToken(CodeEditorColorToken.KEYWORD_COLOR, "LET")
        val CONST = EcmaScriptToken(CodeEditorColorToken.KEYWORD_COLOR, "CONST")

        val IF = EcmaScriptToken(CodeEditorColorToken.KEYWORD_COLOR, "IF")
        val ELSE = EcmaScriptToken(CodeEditorColorToken.KEYWORD_COLOR, "ELSE")
        val SWITCH = EcmaScriptToken(CodeEditorColorToken.KEYWORD_COLOR, "SWITCH")
        val CASE = EcmaScriptToken(CodeEditorColorToken.KEYWORD_COLOR, "CASE")
        val DEFAULT = EcmaScriptToken(CodeEditorColorToken.KEYWORD_COLOR, "DEFAULT")

        val FOR = EcmaScriptToken(CodeEditorColorToken.KEYWORD_COLOR, "FOR")
        val WHILE = EcmaScriptToken(CodeEditorColorToken.KEYWORD_COLOR, "WHILE")
        val DO = EcmaScriptToken(CodeEditorColorToken.KEYWORD_COLOR, "DO")
        val BREAK = EcmaScriptToken(CodeEditorColorToken.KEYWORD_COLOR, "BREAK")
        val CONTINUE = EcmaScriptToken(CodeEditorColorToken.KEYWORD_COLOR, "CONTINUE")

        val FUNCTION = EcmaScriptToken(CodeEditorColorToken.KEYWORD_COLOR, "FUNCTION")
        val RETURN = EcmaScriptToken(CodeEditorColorToken.KEYWORD_COLOR, "RETURN")
        val YIELD = EcmaScriptToken(CodeEditorColorToken.KEYWORD_COLOR, "YIELD")
        val ASYNC = EcmaScriptToken(CodeEditorColorToken.KEYWORD_COLOR, "ASYNC")
        val AWAIT = EcmaScriptToken(CodeEditorColorToken.KEYWORD_COLOR, "AWAIT")

        val THROW = EcmaScriptToken(CodeEditorColorToken.KEYWORD_COLOR, "THROW")
        val TRY = EcmaScriptToken(CodeEditorColorToken.KEYWORD_COLOR, "TRY")
        val CATCH = EcmaScriptToken(CodeEditorColorToken.KEYWORD_COLOR, "CATCH")
        val FINALLY = EcmaScriptToken(CodeEditorColorToken.KEYWORD_COLOR, "FINALLY")

        val THIS = EcmaScriptToken(CodeEditorColorToken.KEYWORD_COLOR, "THIS")
        val WITH = EcmaScriptToken(CodeEditorColorToken.KEYWORD_COLOR, "WITH")
        val IN = EcmaScriptToken(CodeEditorColorToken.KEYWORD_COLOR, "IN")
        val OF = EcmaScriptToken(CodeEditorColorToken.KEYWORD_COLOR, "OF")
        val DELETE = EcmaScriptToken(CodeEditorColorToken.KEYWORD_COLOR, "DELETE")
        val INSTANCEOF = EcmaScriptToken(CodeEditorColorToken.KEYWORD_COLOR, "INSTANCEOF")
        val TYPEOF = EcmaScriptToken(CodeEditorColorToken.KEYWORD_COLOR, "TYPEOF")

        val NEW = EcmaScriptToken(CodeEditorColorToken.KEYWORD_COLOR, "NEW")
        val CLASS = EcmaScriptToken(CodeEditorColorToken.KEYWORD_COLOR, "CLASS")
        val EXTEND = EcmaScriptToken(CodeEditorColorToken.KEYWORD_COLOR, "EXTEND")
        val SET = EcmaScriptToken(CodeEditorColorToken.KEYWORD_COLOR, "SET")
        val GET = EcmaScriptToken(CodeEditorColorToken.KEYWORD_COLOR, "GET")

        val IMPORT = EcmaScriptToken(CodeEditorColorToken.KEYWORD_COLOR, "IMPORT")
        val AS = EcmaScriptToken(CodeEditorColorToken.KEYWORD_COLOR, "AS")
        val FROM = EcmaScriptToken(CodeEditorColorToken.KEYWORD_COLOR, "FROM")
        val EXPORT = EcmaScriptToken(CodeEditorColorToken.KEYWORD_COLOR, "EXPORT")

        val VOID = EcmaScriptToken(CodeEditorColorToken.KEYWORD_COLOR, "VOID")
        val DEBUGGER = EcmaScriptToken(CodeEditorColorToken.KEYWORD_COLOR, "DEBUGGER")

        val PLUS = EcmaScriptToken(CodeEditorColorToken.SYMBOL_COLOR, "PLUS") // '+'
        val MINUS = EcmaScriptToken(CodeEditorColorToken.SYMBOL_COLOR, "MINUS") // '-'
        val MULTI = EcmaScriptToken(CodeEditorColorToken.SYMBOL_COLOR, "MULTI") // '*'
        val DIV = EcmaScriptToken(CodeEditorColorToken.SYMBOL_COLOR, "DIV") // '/'
        val COLON = EcmaScriptToken(CodeEditorColorToken.SYMBOL_COLOR, "COLON") // ':'
        val NOT = EcmaScriptToken(CodeEditorColorToken.SYMBOL_COLOR, "NOT") // '!'
        val MOD = EcmaScriptToken(CodeEditorColorToken.SYMBOL_COLOR, "MOD") // '%'
        val XOR = EcmaScriptToken(CodeEditorColorToken.SYMBOL_COLOR, "XOR") // '^'
        val AND = EcmaScriptToken(CodeEditorColorToken.SYMBOL_COLOR, "AND") // '&'
        val QUESTION = EcmaScriptToken(CodeEditorColorToken.SYMBOL_COLOR, "QUESTION") // '?'
        val COMP = EcmaScriptToken(CodeEditorColorToken.SYMBOL_COLOR, "COMP") // '~'
        val DOT = EcmaScriptToken(CodeEditorColorToken.SYMBOL_COLOR, "DOT") // '.'
        val COMMA = EcmaScriptToken(CodeEditorColorToken.SYMBOL_COLOR, "COMMA") // ','
        val SEMICOLON = EcmaScriptToken(CodeEditorColorToken.SYMBOL_COLOR, "SEMICOLON") // ';'
        val EQUALS = EcmaScriptToken(CodeEditorColorToken.SYMBOL_COLOR, "EQUALS") // '='
        val LEFT_PARENTHESIS =
            EcmaScriptToken(CodeEditorColorToken.SYMBOL_COLOR, "LEFT_PARENTHESIS") // '('
        val RIGHT_PARENTHESIS =
            EcmaScriptToken(CodeEditorColorToken.SYMBOL_COLOR, "RIGHT_PARENTHESIS") // ')'
        val LEFT_BRACKET = EcmaScriptToken(CodeEditorColorToken.SYMBOL_COLOR, "LEFT_BRACKET") // '['
        val RIGHT_BRACKET =
            EcmaScriptToken(CodeEditorColorToken.SYMBOL_COLOR, "RIGHT_BRACKET") // ']'
        val LEFT_BRACE = EcmaScriptToken(CodeEditorColorToken.SYMBOL_COLOR, "LEFT_BRACE") // '{'
        val RIGHT_BRACE = EcmaScriptToken(CodeEditorColorToken.SYMBOL_COLOR, "RIGHT_BRACE") // '}'
        val OR = EcmaScriptToken(CodeEditorColorToken.SYMBOL_COLOR, "OR") // '|'
        val LESS_THAN = EcmaScriptToken(CodeEditorColorToken.SYMBOL_COLOR, "LESS_THAN") // '<'
        val MORE_THAN = EcmaScriptToken(CodeEditorColorToken.SYMBOL_COLOR, "MORE_THAN") // '>'
    }

}