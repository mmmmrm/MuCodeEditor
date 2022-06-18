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

package com.mucheng.editor.impl

import android.util.Log
import com.mucheng.editor.base.BaseToken
import com.mucheng.editor.colorful.LexInterface
import com.mucheng.editor.common.AutoCompleteItem
import com.mucheng.editor.position.ColumnRowPosition
import com.mucheng.editor.views.MuCodeEditor
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex

@Suppress("UNCHECKED_CAST")
open class DefaultLexCoroutine(private val editor: MuCodeEditor) : LexInterface {

    private var job: Job? = null

    private val controller by lazy { editor.getController() }

    private val stateController by lazy { controller.state }

    private val spanProvider by lazy { editor.getSpanProvider() }

    private val lock = Mutex()

    @Suppress("ControlFlowWithEmptyBody")
    override suspend fun analyze() {
        val contentProvider = editor.getContentProvider()
        spanProvider.clear()

        contentProvider.useLock(true) {
            try {
                val lexer = controller.language.getLexer()!!
                lexer.setSources(contentProvider.getLineContents().toList())
                lexer.analyze()

                val columnTokens = lexer.toColumnTokens()
                spanProvider.setTokens(columnTokens)

                setParseTokens(columnTokens)
            } catch (e: IndexOutOfBoundsException) {
                e.printStackTrace()
            }
            stateController.lexCompletion()
        }
    }

    override suspend fun parse() {
        val panel = controller.autoCompletionPanel
        if (controller.language.getParser() == null || panel == null) {
            return
        }

        panel.clearDefinedAutoCompleteItem()

        val parser = controller.language.getParser()!!
        parser.parse()
        parser.getCompileError().forEach {
            val column = it.first
            val text = it.second
            val range = it.third
            Log.e("SyntaxError", "$text (Script#$column)")
        }

        panel.notifyAutoCompleteItemChanged()
    }

    private fun setParseTokens(token: MutableList<MutableList<Pair<BaseToken, IntRange>>>) {
        if (controller.language.getParser() == null) {
            return
        }

        val parser = controller.language.getParser()!!
        parser.setSources(token as List<List<Pair<Nothing, IntRange>>>)
    }

    override fun start() {
        if (controller.language.getLexer() == null) {
            controller.state.lexCompletion()
            return
        }

        job = CoroutineScope(Dispatchers.IO).launch {
            lock.lock()
            try {
                analyze()
                parse()
            } finally {
                lock.unlock()
            }
        }
    }

    override fun cancel() {
        job?.cancel()
        stateController.lexCompletion()
    }

}