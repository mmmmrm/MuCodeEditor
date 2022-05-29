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

import com.mucheng.editor.base.BaseLexer
import com.mucheng.editor.position.ColumnRowPosition

open class EcmaScriptLexer : BaseLexer<EcmaScriptToken>() {

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

            if (handleRegex()) continue

            if (handleSymbol()) continue

            if (handleSpecial()) continue

            if (handleKeyword()) continue

            if (handleIdentifier()) continue

            if (handleDigit()) continue

            ++row

        }

    }

    protected open fun handleWhitespace(): Boolean {
        if (!isWhitespace()) {
            return false
        }

        val start = row
        while (isWhitespace() && isNotRowEOF()) {
            yyChar()
        }
        val end = row
        addToken(
            EcmaScriptToken.WHITESPACE,
            ColumnRowPosition(column, start),
            ColumnRowPosition(column, end)
        )
        return true
    }

    protected open fun handleComments(): Boolean {
        if (scannedChar != '/') {
            return false
        }

        val start = row
        yyChar()
        if (scannedChar == '/') {
            val end = rowSize()
            addToken(
                EcmaScriptToken.SINGLE_COMMENT,
                ColumnRowPosition(column, start),
                ColumnRowPosition(column, end)
            )
            row = end
            return true
        }

        if (scannedChar == '*') {
            val currentFindPos = scannedColumnSource.indexOf("*/", row + 1)
            if (currentFindPos != -1) {
                val end = currentFindPos + 2
                addToken(
                    EcmaScriptToken.SINGLE_COMMENT,
                    ColumnRowPosition(column, start),
                    ColumnRowPosition(column, end)
                )
                row = end
                return true
            }

            addToken(
                EcmaScriptToken.MULTI_COMMENT_START,
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
                        EcmaScriptToken.MULTI_COMMENT_END,
                        ColumnRowPosition(column, 0),
                        ColumnRowPosition(column, end)
                    )
                    row = end
                    return true
                }

                addToken(
                    EcmaScriptToken.MULTI_COMMENT_PART,
                    ColumnRowPosition(column, 0),
                    ColumnRowPosition(column, rowSize())
                )
                ++column
            }
            return true
        }

        row = start
        getChar()
        return false
    }

    protected open fun handleSymbol(): Boolean {
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

    protected open fun handleSpecial(): Boolean {
        if (!isLetter()) {
            return false
        }

        val start = row
        val buffer = StringBuilder()
        while (isLetter() && isNotRowEOF()) {
            buffer.append(scannedChar)
            yyChar()
        }
        val end = row
        val text = buffer.toString()
        if (isSpecial(text)) {
            val token = mSpecialTables[text]!!
            addToken(
                token,
                ColumnRowPosition(column, start),
                ColumnRowPosition(column, end)
            )
            return true
        }

        row = start
        getChar()
        return false
    }

    protected open fun handleKeyword(): Boolean {
        if (!isLetter()) {
            return false
        }

        val buffer = StringBuilder()
        val start = row
        while (isLetter() && isNotRowEOF()) {
            buffer.append(scannedChar)
            yyChar()
        }
        val end = row
        val text = buffer.toString()

        if (isKeyword(text)) {
            val token = mKeywordTables[text]!!
            addToken(
                token,
                ColumnRowPosition(column, start),
                ColumnRowPosition(column, end)
            )
            return true
        }

        row = start
        getChar()
        return false
    }

    protected open fun handleString(): Boolean {
        if (scannedChar != '\'' && scannedChar != '"' && scannedChar != '`') {
            return false
        }

        val start = row
        if (scannedChar == '"') {
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
                EcmaScriptToken.SINGLE_STRING,
                ColumnRowPosition(column, start),
                ColumnRowPosition(column, end)
            )
            return true
        }

        if (scannedChar == '\'') {
            yyChar()

            while (isNotRowEOF()) {
                if (scannedChar == '\'') {
                    yyChar()
                    break
                }
                yyChar()
            }

            val end = row
            addToken(
                EcmaScriptToken.SINGLE_STRING,
                ColumnRowPosition(column, start),
                ColumnRowPosition(column, end)
            )
            return true
        }

        if (scannedChar == '`') {
            val currentFindPos = scannedColumnSource.indexOf('`', row + 1)

            if (currentFindPos != -1) {
                val end = currentFindPos + 1
                addToken(
                    EcmaScriptToken.TEMPLATE_STRING,
                    ColumnRowPosition(column, start),
                    ColumnRowPosition(column, end)
                )
                return true
            }

            addToken(
                EcmaScriptToken.TEMPLATE_STRING,
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

                val findPos = scannedColumnSource.indexOf('`')
                if (findPos != -1) {
                    val end = findPos + 1
                    addToken(
                        EcmaScriptToken.TEMPLATE_STRING,
                        ColumnRowPosition(column, 0),
                        ColumnRowPosition(column, end)
                    )
                    row = end
                    return true
                }

                addToken(
                    EcmaScriptToken.TEMPLATE_STRING,
                    ColumnRowPosition(column, 0),
                    ColumnRowPosition(column, rowSize())
                )
                ++column
            }

            return true
        }

        row = start
        getChar()
        return false
    }

    protected open fun handleRegex(): Boolean {
        if (scannedChar != '/') {
            return false
        }

        val start = row
        val currentFindPos = scannedColumnSource.indexOf('/', row + 1)
        if (currentFindPos != -1) {
            var end = currentFindPos
            row = end
            yyChar()
            if (isLetter()) {
                while (isLetter() && isNotRowEOF()) {
                    yyChar()
                }
                end = row
                addToken(
                    EcmaScriptToken.REGEX,
                    ColumnRowPosition(column, start),
                    ColumnRowPosition(column, end)
                )
                return true
            }
            ++end
            addToken(
                EcmaScriptToken.REGEX,
                ColumnRowPosition(column, start),
                ColumnRowPosition(column, end)
            )
            row = end
            return true
        }

        row = start
        getChar()
        return false
    }

    protected open fun handleIdentifier(): Boolean {
        if (isWhitespace() || isSymbol() || isDigit()) {
            return false
        }

        val start = row
        while (!isWhitespace() && !isSymbol() && isNotRowEOF()) {
            yyChar()
        }
        val end = row

        addToken(
            EcmaScriptToken.IDENTIFIER,
            ColumnRowPosition(column, start),
            ColumnRowPosition(column, end)
        )
        return true
    }

    protected open fun handleDigit(): Boolean {
        if (!isDigit()) {
            return false
        }

        val start = row
        if (scannedChar == '0') {
            yyChar()
            if (scannedChar == 'x') {
                while ((isDigit() || isLetter()) && isNotRowEOF()) {
                    yyChar()
                }
                val end = row
                addToken(
                    EcmaScriptToken.DIGIT_NUMBER,
                    ColumnRowPosition(column, start),
                    ColumnRowPosition(column, end)
                )
                return true
            }
            row = start
            getChar()
        }

        while (isDigit() && isNotRowEOF()) {
            yyChar()
        }

        val end = row

        addToken(
            EcmaScriptToken.DIGIT_NUMBER,
            ColumnRowPosition(column, start),
            ColumnRowPosition(column, end)
        )
        return true
    }

    override fun setup() {
        mKeywordTables = createKeywordTable()
        mSymbolTables = createSymbolTable()
        mSpecialTables = createSpecialTable()
    }

    private fun createKeywordTable(): Map<String, EcmaScriptToken> {
        return hashMapOf(
            "var" to EcmaScriptToken.VAR,
            "let" to EcmaScriptToken.LET,
            "const" to EcmaScriptToken.CONST,
            "if" to EcmaScriptToken.IF,
            "else" to EcmaScriptToken.ELSE,
            "switch" to EcmaScriptToken.SWITCH,
            "case" to EcmaScriptToken.CASE,
            "default" to EcmaScriptToken.DEFAULT,
            "for" to EcmaScriptToken.FOR,
            "while" to EcmaScriptToken.WHILE,
            "do" to EcmaScriptToken.DO,
            "break" to EcmaScriptToken.BREAK,
            "continue" to EcmaScriptToken.CONTINUE,
            "function" to EcmaScriptToken.FUNCTION,
            "return" to EcmaScriptToken.RETURN,
            "yield" to EcmaScriptToken.YIELD,
            "async" to EcmaScriptToken.ASYNC,
            "await" to EcmaScriptToken.AWAIT,
            "throw" to EcmaScriptToken.THROW,
            "try" to EcmaScriptToken.TRY,
            "catch" to EcmaScriptToken.CATCH,
            "finally" to EcmaScriptToken.FINALLY,
            "this" to EcmaScriptToken.THIS,
            "with" to EcmaScriptToken.WITH,
            "in" to EcmaScriptToken.IN,
            "of" to EcmaScriptToken.OF,
            "delete" to EcmaScriptToken.DELETE,
            "instanceof" to EcmaScriptToken.INSTANCEOF,
            "typeof" to EcmaScriptToken.TYPEOF,
            "new" to EcmaScriptToken.NEW,
            "class" to EcmaScriptToken.CLASS,
            "extend" to EcmaScriptToken.EXTEND,
            "set" to EcmaScriptToken.SET,
            "get" to EcmaScriptToken.GET,
            "import" to EcmaScriptToken.IMPORT,
            "as" to EcmaScriptToken.AS,
            "from" to EcmaScriptToken.FROM,
            "export" to EcmaScriptToken.EXPORT,
            "void" to EcmaScriptToken.VOID,
            "debugger" to EcmaScriptToken.DEBUGGER
        )
    }

    private fun createSymbolTable(): Map<Char, EcmaScriptToken> {
        return hashMapOf(
            '+' to EcmaScriptToken.PLUS,
            '-' to EcmaScriptToken.MINUS,
            '*' to EcmaScriptToken.MULTI,
            '/' to EcmaScriptToken.DIV,
            '!' to EcmaScriptToken.NOT,
            '%' to EcmaScriptToken.MOD,
            '^' to EcmaScriptToken.XOR,
            '&' to EcmaScriptToken.AND,
            '?' to EcmaScriptToken.QUESTION,
            '~' to EcmaScriptToken.COMP,
            '.' to EcmaScriptToken.DOT,
            ',' to EcmaScriptToken.COMMA,
            ';' to EcmaScriptToken.SEMICOLON,
            '=' to EcmaScriptToken.EQUALS,
            '(' to EcmaScriptToken.LEFT_PARENTHESIS,
            ')' to EcmaScriptToken.RIGHT_PARENTHESIS,
            '[' to EcmaScriptToken.LEFT_BRACKET,
            ']' to EcmaScriptToken.RIGHT_BRACKET,
            '{' to EcmaScriptToken.LEFT_BRACE,
            '}' to EcmaScriptToken.RIGHT_BRACE,
            '|' to EcmaScriptToken.OR,
            '<' to EcmaScriptToken.LESS_THAN,
            '>' to EcmaScriptToken.MORE_THAN
        )
    }

    private fun createSpecialTable(): Map<String, EcmaScriptToken> {
        return hashMapOf(
            "false" to EcmaScriptToken.FALSE,
            "true" to EcmaScriptToken.TRUE,
            "NaN" to EcmaScriptToken.NAN,
            "undefined" to EcmaScriptToken.UNDEFINED,
            "null" to EcmaScriptToken.NULL
        )
    }

}