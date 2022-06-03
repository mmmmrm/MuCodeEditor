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

import android.util.Log
import com.mucheng.editor.base.BaseLexer
import com.mucheng.editor.language.ecmascript.EcmaScriptToken
import com.mucheng.editor.position.ColumnRowPosition
import java.lang.reflect.Array.getChar

@Suppress("SpellCheckingInspection")
class HtmlLexer : BaseLexer<HtmlToken>() {

    @Synchronized
    override fun analyze() {
        if (column > columnSize()) {
            return
        }

        while (true) {

            if (column > columnSize()) {
                return
            }

            scannedColumnSource = sources[column - 1]

            if (row >= rowSize()) {
                ++column
                row = 0
                continue
            }

            scannedColumnSource = sources[column - 1]
            getChar()

            if (handleWhitespace()) continue

            if (handleComments()) continue

            if (handleString()) continue

            if (handleDOCTYPE()) continue

            if (handleSymbol()) continue

            if (handleElement()) continue

            if (handleAttribute()) continue

            if (handleDigit()) continue

            if (handleIdentifier()) continue

            ++row

        }

    }

    private fun handleString(): Boolean {
        if (scannedChar != '"') {
            return false
        }

        val start = row
        yyChar()

        while (isNotRowEOF()) {
            if (scannedChar == '"') {
                yyChar()
                break
            }
            yyChar()
        }

        val end = row
        addToken(
            HtmlToken.STRING,
            ColumnRowPosition(column, start),
            ColumnRowPosition(column, end)
        )
        return true
    }

    private fun handleDOCTYPE(): Boolean {
        if (scannedChar != '<') {
            return false
        }

        val start = row
        yyChar()
        if (scannedChar != '!') {
            row = start
            getChar()
            return false
        }

        val buffer = StringBuilder()
        yyChar()
        while (isLetter() && isNotRowEOF()) {
            buffer.append(scannedChar)
            yyChar()
        }

        val docText = buffer.toString()
        if (docText == "DOCTYPE" || docText == "doctype") {
            yyChar()
            while (isWhitespace() && isNotRowEOF()) {
                yyChar()
            }
            buffer.clear()
            while (isLetter() && isNotRowEOF()) {
                buffer.append(scannedChar)
                yyChar()
            }
            val htmlText = buffer.toString()
            if (htmlText == "html" && scannedChar == '>') {
                val end = ++row
                addToken(
                    HtmlToken.DOCTYPE,
                    ColumnRowPosition(column, start),
                    ColumnRowPosition(column, end)
                )
                return true
            }
        }
        row = start
        getChar()
        return false
    }

    private fun handleSymbol(): Boolean {
        if (!isSymbol()) {
            return false
        }

        val token = mSymbolTables[scannedChar]!!
        addToken(
            token,
            ColumnRowPosition(column, row),
            ColumnRowPosition(column, row + 1)
        )

        ++row
        return true
    }

    private fun handleElement(): Boolean {
        if (isLetter() && scannedColumnSource.getOrNull(row - 1) == '<') {
            val start = row
            while ((isLetter() || isDigit()) && isNotRowEOF()) {
                yyChar()
            }
            val end = row
            addToken(
                HtmlToken.ELEMENT_NAME,
                ColumnRowPosition(column, start),
                ColumnRowPosition(column, end)
            )
            return true
        }

        if (isLetter() && scannedColumnSource.getOrNull(row - 1) == '/') {
            val start = row
            while ((isLetter() || isDigit()) && isNotRowEOF()) {
                yyChar()
            }
            val end = row
            addToken(
                HtmlToken.ELEMENT_NAME,
                ColumnRowPosition(column, start),
                ColumnRowPosition(column, end)
            )
            return true
        }
        return false
    }

    private fun handleAttribute(): Boolean {
        if (!isLetter()) {
            return false
        }

        val start = row
        while ((isLetter() || isDigit()) && isNotRowEOF()) {
            yyChar()
        }
        if (scannedChar == '=') {
            addToken(
                HtmlToken.ATTRIBUTE,
                ColumnRowPosition(column, start),
                ColumnRowPosition(column, row)
            )
            return true
        }
        row = start
        getChar()
        return false
    }

    private fun handleDigit(): Boolean {
        if (!isDigit()) {
            return false
        }

        val start = row
        while (isDigit() && isNotRowEOF()) {
            yyChar()
        }
        val end = row

        addToken(
            HtmlToken.IDENTIFIER,
            ColumnRowPosition(column, start),
            ColumnRowPosition(column, end)
        )
        return true
    }

    private fun handleIdentifier(): Boolean {
        if (isWhitespace() || isSymbol() || isDigit()) {
            return false
        }

        val start = row
        while (!isWhitespace() && !isSymbol() && isNotRowEOF()) {
            yyChar()
        }
        val end = row

        addToken(
            HtmlToken.IDENTIFIER,
            ColumnRowPosition(column, start),
            ColumnRowPosition(column, end)
        )
        return true
    }

    private fun handleWhitespace(): Boolean {
        if (!isWhitespace()) {
            return false
        }

        val start = row
        while (isWhitespace() && isNotRowEOF()) {
            yyChar()
        }
        val end = row
        addToken(
            HtmlToken.WHITESPACE,
            ColumnRowPosition(column, start),
            ColumnRowPosition(column, end)
        )
        return true
    }

    private fun handleComments(): Boolean {
        if (scannedChar != '<') {
            return false
        }
        val start = row
        yyChar()
        if (scannedChar == '!') {
            yyChar()
            if (scannedChar == '-') {
                yyChar()
                if (scannedChar == '-') {
                    val currentFindPos = scannedColumnSource.indexOf("-->", row + 1)
                    if (currentFindPos != -1) {
                        val end = currentFindPos + 3
                        addToken(
                            HtmlToken.COMMENT,
                            ColumnRowPosition(column, start),
                            ColumnRowPosition(column, end)
                        )
                        row = end
                        return true
                    }
                    addToken(
                        HtmlToken.COMMENT,
                        ColumnRowPosition(column, start),
                        ColumnRowPosition(column, rowSize())
                    )

                    ++column
                    while (column <= columnSize()) {
                        row = 0
                        scannedColumnSource = sources[column - 1]
                        if (scannedColumnSource.isEmpty()) {
                            ++column
                            continue
                        }

                        val findPos = scannedColumnSource.indexOf("-->")
                        if (findPos != -1) {
                            val end = findPos + 3
                            addToken(
                                HtmlToken.COMMENT,
                                ColumnRowPosition(column, 0),
                                ColumnRowPosition(column, end)
                            )
                            row = end
                            return true
                        }

                        addToken(
                            HtmlToken.COMMENT,
                            ColumnRowPosition(column, 0),
                            ColumnRowPosition(column, rowSize())
                        )
                        ++column
                    }
                    return true
                }
            }
        }

        row = start
        getChar()
        return false
    }

    override fun setup() {
        mKeywordTables = emptyMap()
        mSymbolTables = createSymbolTable()
        mSpecialTables = emptyMap()
    }

    private fun createSymbolTable(): Map<Char, HtmlToken> {
        return hashMapOf(
            '+' to HtmlToken.PLUS,
            '-' to HtmlToken.MINUS,
            '*' to HtmlToken.MULTI,
            '/' to HtmlToken.DIV,
            '!' to HtmlToken.NOT,
            '%' to HtmlToken.MOD,
            '^' to HtmlToken.XOR,
            '&' to HtmlToken.AND,
            '?' to HtmlToken.QUESTION,
            '~' to HtmlToken.COMP,
            '.' to HtmlToken.DOT,
            ',' to HtmlToken.COMMA,
            ';' to HtmlToken.SEMICOLON,
            '=' to HtmlToken.EQUALS,
            '(' to HtmlToken.LEFT_PARENTHESIS,
            ')' to HtmlToken.RIGHT_PARENTHESIS,
            '[' to HtmlToken.LEFT_BRACKET,
            ']' to HtmlToken.RIGHT_BRACKET,
            '{' to HtmlToken.LEFT_BRACE,
            '}' to HtmlToken.RIGHT_BRACE,
            '|' to HtmlToken.OR,
            '<' to HtmlToken.LESS_THAN,
            '>' to HtmlToken.MORE_THAN
        )
    }

}