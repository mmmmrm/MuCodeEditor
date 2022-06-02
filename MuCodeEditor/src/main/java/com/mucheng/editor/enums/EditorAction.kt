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

package com.mucheng.editor.enums

import android.util.Log
import com.mucheng.editor.position.ColumnRowPosition
import com.mucheng.editor.text.ContentProvider
import com.mucheng.editor.text.LineContent
import com.mucheng.editor.util.execCursorAnimationIfNeeded
import com.mucheng.editor.views.MuCodeEditor

class EditorAction private constructor() {

    abstract class Action {

        abstract var startPos: ColumnRowPosition
        abstract var endPos: ColumnRowPosition
        abstract var value: StringBuilder

        abstract fun undo(contentProvider: ContentProvider, editor: MuCodeEditor)
        abstract fun redo(contentProvider: ContentProvider, editor: MuCodeEditor)

        abstract fun merge(action: Action): Boolean
    }

    data class CommitAction(
        override var startPos: ColumnRowPosition,
        override var endPos: ColumnRowPosition, override var value: StringBuilder,
    ) : Action() {

        override fun undo(contentProvider: ContentProvider, editor: MuCodeEditor) {
            val lexCoroutine = editor.getLexCoroutine()
            execCursorAnimationIfNeeded(editor.getController().style.cursorAnimation, editor) {
                contentProvider.delete(startPos, endPos)
                editor.getController().state.lex(
                    lexCoroutine
                )
            }
        }

        override fun redo(contentProvider: ContentProvider, editor: MuCodeEditor) {
            val lexCoroutine = editor.getTextInputConnection().getLexCoroutine()
            execCursorAnimationIfNeeded(editor.getController().style.cursorAnimation, editor) {
                contentProvider.insert(startPos, value)
                editor.getController().state.lex(
                    lexCoroutine
                )
            }
        }

        override fun merge(action: Action): Boolean {
            if (action !is CommitAction) {
                return false
            }

            val nextStartColumn = action.startPos.column
            val nextStartRow = action.startPos.row
            val endColumn = endPos.column
            val endRow = endPos.row
            if (nextStartColumn != endColumn || nextStartRow != endRow || value.length + action.value.length >= 10000) {
                return false
            }

            value.append(action.value)
            endPos = action.endPos
            return true
        }

    }

    data class DeleteAction(
        override var startPos: ColumnRowPosition,
        override var endPos: ColumnRowPosition, override var value: StringBuilder,
    ) : Action() {

        override fun undo(contentProvider: ContentProvider, editor: MuCodeEditor) {
            val lexCoroutine = editor.getTextInputConnection().getLexCoroutine()
            execCursorAnimationIfNeeded(editor.getController().style.cursorAnimation, editor) {
                contentProvider.insert(startPos, value)
                editor.getController().state.lex(
                    lexCoroutine
                )
            }
        }

        override fun redo(contentProvider: ContentProvider, editor: MuCodeEditor) {
            val lexCoroutine = editor.getLexCoroutine()
            execCursorAnimationIfNeeded(editor.getController().style.cursorAnimation, editor) {
                contentProvider.delete(startPos, endPos)
                editor.getController().state.lex(
                    lexCoroutine
                )
            }
        }

        override fun merge(action: Action): Boolean {
            if (action !is DeleteAction) {
                return false
            }

            val beforeEndColumn = action.endPos.column
            val beforeEndRow = action.endPos.row
            val startColumn = startPos.column
            val startRow = startPos.row

            if (beforeEndColumn != startColumn || beforeEndRow != startRow || action.value.length + value.length >= 10000) {
                return false
            }

            value.insert(0, action.value)
            startPos = action.startPos
            return true
        }

    }

}