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

package com.mucheng.editor.controller

import android.util.Log
import com.mucheng.editor.base.BaseController
import com.mucheng.editor.enums.EditorAction
import com.mucheng.editor.views.MuCodeEditor
import java.util.*

open class EditorActionController(private val controller: EditorController) : BaseController() {

    private val stack: MutableList<EditorAction.Action> = ArrayList()

    private val maxStackSize = 300

    private var stackPointer = 0

    protected val contentProvider by lazy { getEditor().getContentProvider() }

    private fun clearBeforePush() {
        while (stackPointer < stack.size) {
            stack.removeLast()
        }
    }

    private fun clearStack() {
        while (stackPointer > 1 && stack.size > maxStackSize) {
            stack.removeAt(0)
            --stackPointer
        }
    }

    open fun push(action: EditorAction.Action) {
        clearBeforePush()
        Log.e("Stacks", stack.toString())
        if (stackPointer - 1 < 0) {
            stack.add(action)
            clearStack()
            ++stackPointer
            return
        }

        // 尝试合并
        val element = stack.last()
        if (!element.merge(action)) {
            stack.add(action)
            ++stackPointer
        }
        clearStack()
    }

    open fun pop(): EditorAction.Action {
        return stack.removeLast()
    }

    open fun undo() {
        if (!canUndo()) {
            return
        }
        stack[stackPointer - 1].undo(contentProvider, getEditor())
        --stackPointer
        getEditor().invalidate()
    }

    open fun redo() {
        if (!canRedo()) {
            return
        }
        stack[stackPointer].redo(contentProvider, getEditor())
        ++stackPointer
        getEditor().invalidate()
    }

    open fun canUndo(): Boolean {
        return stackPointer > 0 && !controller.state.selection
    }

    open fun canRedo(): Boolean {
        return stackPointer < stack.size && !controller.state.selection
    }

    override fun getEditor(): MuCodeEditor {
        return controller.getEditor()
    }

}