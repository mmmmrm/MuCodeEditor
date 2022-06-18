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

package com.mucheng.editor

import com.mucheng.editor.base.BaseToken
import com.mucheng.editor.language.css.CssLexer
import com.mucheng.editor.language.css.CssToken
import com.mucheng.editor.language.ecmascript.EcmaScriptLexer
import com.mucheng.editor.language.ecmascript.EcmaScriptToken
import com.mucheng.editor.language.html.HtmlToken
import org.junit.After
import org.junit.Test
import java.io.File

class LexerUnitTest {

    companion object {
        private const val WORK_DIR =
            "D:\\AndroidProjects\\MuCodeEditorExample\\MuCodeEditor\\src\\test\\java\\com\\mucheng\\editor"
    }

    private val lexer = CssLexer()

    private val sourcePath = "$WORK_DIR/source.in"

    private val lexerOutput = "$WORK_DIR/lexer-output.out"

    private val skips: MutableList<BaseToken> = ArrayList()

    private var source = fetchSource()

    init {
        skips.addAll(getFilterWhitespaces())
        skips.addAll(getFilterTokens())
    }

    private fun getFilterWhitespaces(): List<BaseToken> {
        return mutableListOf(
            EcmaScriptToken.WHITESPACE,
            HtmlToken.WHITESPACE,
            CssToken.WHITESPACE
        )
    }

    private fun getFilterTokens(): List<BaseToken> {
        return mutableListOf(

        )
    }

    @Test
    fun run() {
        lexer.setSources(source)
        lexer.analyze()
    }

    @After
    fun print() {
        val file = File(lexerOutput)
        file.createNewFile()

        file.printWriter().use { printer ->
            lexer.getTokens().filter { pair ->
                val token = pair.first
                !skips.contains(token)
            }.map {
                val column = it.second.first.column
                val range = it.second.first.row until it.second.second.row
                "Token:: `${it.first}` > ($column, $range) -> ${source[column - 1].substring(range)}"
            }.forEach {
                printer.println(it)
            }
        }
    }

    private fun fetchSource(): List<CharSequence> {
        val file = File(sourcePath)
        if (!file.exists()) {
            throw IllegalStateException("No source file 'source.in' in $WORK_DIR")
        }

        return file.readLines()
    }

}