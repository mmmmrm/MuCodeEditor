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

package com.mucheng.editor.handler

import android.view.KeyEvent
import android.view.inputmethod.BaseInputConnection
import com.mucheng.editor.annotation.Simple
import com.mucheng.editor.annotation.UnsupportedUserUsage
import com.mucheng.editor.colorful.LexInterface
import com.mucheng.editor.event.TextInputConnection
import com.mucheng.editor.views.MuCodeEditor

@Simple
open class TextInputConnectionDelegation(
    private val view: MuCodeEditor,
    private var inputConnection: TextInputConnection,
) :
    BaseInputConnection(view, true) {

    private val controller by lazy { view.getController() }

    override fun commitText(text: CharSequence, newCursorPosition: Int): Boolean {
        return commitText(text, newCursorPosition, true)
    }

    fun commitText(
        text: CharSequence,
        newCursorPosition: Int,
        showAutoCompletionPanel: Boolean = true,
    ): Boolean {
        if (controller.state.selection) {
            inputConnection.onSelectionTextReplace(text)
        } else {
            inputConnection.onCommit(text, showAutoCompletionPanel)
        }
        return super.commitText(text, newCursorPosition)
    }

    override fun deleteSurroundingText(beforeLength: Int, afterLength: Int): Boolean {
        if (controller.state.selection) {
            inputConnection.onSelectionTextDelete()
        } else {
            inputConnection.onDelete()
        }
        return super.deleteSurroundingText(beforeLength, afterLength)
    }

    @UnsupportedUserUsage
    internal fun onCursorLeft() {
        controller.state.unselectText()
        inputConnection.toCursorLeft()
    }

    @UnsupportedUserUsage
    internal fun onCursorRight() {
        controller.state.unselectText()
        inputConnection.toCursorRight()
    }

    @UnsupportedUserUsage
    internal fun onCursorTop() {
        controller.state.unselectText()
        inputConnection.toCursorTop()
    }

    @UnsupportedUserUsage
    internal fun onCursorBottom() {
        controller.state.unselectText()
        inputConnection.toCursorBottom()
    }

    internal fun onVirtualKeyboardInput(event: KeyEvent) {
        inputConnection.onVirtualKeyboardInput(event)
    }

    fun onCursorHome() {
        inputConnection.toCursorHome()
    }

    fun onCursorEnd() {
        inputConnection.toCursorEnd()
    }

    override fun closeConnection() {
        super.closeConnection()
        inputConnection.closeConnection()
    }

    fun getLexCoroutine(): LexInterface {
        return inputConnection.getLexCoroutine()
    }

}