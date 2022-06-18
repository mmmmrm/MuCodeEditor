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

import com.mucheng.editor.base.BaseLexer
import com.mucheng.editor.position.ColumnRowPosition

class CssLexer : BaseLexer<CssToken>() {

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

            if (handleSymbol()) continue

            if (handleString()) continue

            if (handleAttribute()) continue

            if (handleDigit()) continue

            if (handleIdentifier()) continue

            ++row

        }

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
            CssToken.WHITESPACE,
            ColumnRowPosition(column, start),
            ColumnRowPosition(column, end)
        )
        return true
    }

    private fun handleComments(): Boolean {
        if (scannedChar != '/') {
            return false
        }

        val start = row
        yyChar()
        if (scannedChar != '*') {
            row = start
            getChar()
            return false
        }

        val currentFindPos = scannedColumnSource.indexOf("*/", row + 1)
        if (currentFindPos != -1) {
            val end = currentFindPos + 2
            addToken(
                CssToken.COMMENT,
                ColumnRowPosition(column, start),
                ColumnRowPosition(column, end)
            )
            row = end
            return true
        }

        addToken(
            CssToken.COMMENT,
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

            val findPos = scannedColumnSource.indexOf("*/")
            if (findPos != -1) {
                val end = findPos + 2
                addToken(
                    CssToken.COMMENT,
                    ColumnRowPosition(column, 0),
                    ColumnRowPosition(column, end)
                )
                row = end
                return true
            }

            addToken(
                CssToken.COMMENT,
                ColumnRowPosition(column, 0),
                ColumnRowPosition(column, rowSize())
            )
            ++column
        }
        return true
    }

    private fun handleSymbol(): Boolean {
        if (!isSymbol() || scannedChar == '"') {
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
            CssToken.STRING,
            ColumnRowPosition(column, start),
            ColumnRowPosition(column, end)
        )
        return true
    }

    private fun handleAttribute(): Boolean {
        if (!isLetter()) {
            return false
        }

        val start = row
        while (isLetter() && isNotRowEOF()) {
            yyChar()
        }
        val end = row

        while (isWhitespace() && isNotRowEOF()) {
            yyChar()
        }
        val nextEnd = row
        if (scannedChar == ':') {
            addToken(
                CssToken.ATTRIBUTE,
                ColumnRowPosition(column, start),
                ColumnRowPosition(column, nextEnd)
            )
            addToken(
                CssToken.COLON,
                ColumnRowPosition(column, nextEnd),
                ColumnRowPosition(column, nextEnd + 1)
            )
            row = nextEnd + 1
            return true
        }

        addToken(
            CssToken.IDENTIFIER,
            ColumnRowPosition(column, start),
            ColumnRowPosition(column, end)
        )
        row = end
        return true
    }

    private fun handleDigit(): Boolean {
        if (!isDigit()) {
            return false
        }

        val start = row
        while ((isDigit() || isLetter()) && isNotRowEOF()) {
            yyChar()
        }
        val end = row

        addToken(
            CssToken.DIGIT,
            ColumnRowPosition(column, start),
            ColumnRowPosition(column, end)
        )
        row = end
        return true
    }

    private fun handleIdentifier(): Boolean {
        if (isWhitespace() || isSymbol() || isDigit()) {
            return false
        }

        val buffer = StringBuilder()
        val start = row
        while (!isWhitespace() && !isSymbol() && isNotRowEOF()) {
            buffer.append(scannedChar)
            yyChar()
        }
        val end = row

        addToken(
            CssToken.IDENTIFIER,
            ColumnRowPosition(column, start),
            ColumnRowPosition(column, end)
        )
        return true
    }

    override fun setup() {
        mKeywordTables = emptyMap()
        mSymbolTables = createSymbolTable()
        mSpecialTables = emptyMap()
    }

    private fun createSymbolTable(): Map<Char, CssToken> {
        return hashMapOf(
            '+' to CssToken.PLUS,
            '*' to CssToken.MULTI,
            '/' to CssToken.DIV,
            ':' to CssToken.COLON,
            '!' to CssToken.NOT,
            '%' to CssToken.MOD,
            '^' to CssToken.XOR,
            '&' to CssToken.AND,
            '?' to CssToken.QUESTION,
            '~' to CssToken.COMP,
            '.' to CssToken.DOT,
            ',' to CssToken.COMMA,
            ';' to CssToken.SEMICOLON,
            '=' to CssToken.EQUALS,
            '(' to CssToken.LEFT_PARENTHESIS,
            ')' to CssToken.RIGHT_PARENTHESIS,
            '[' to CssToken.LEFT_BRACKET,
            ']' to CssToken.RIGHT_BRACKET,
            '{' to CssToken.LEFT_BRACE,
            '}' to CssToken.RIGHT_BRACE,
            '|' to CssToken.OR,
            '<' to CssToken.LESS_THAN,
            '>' to CssToken.MORE_THAN
        )
    }


}