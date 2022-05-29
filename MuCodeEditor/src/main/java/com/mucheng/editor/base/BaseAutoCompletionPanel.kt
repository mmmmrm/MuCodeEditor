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

package com.mucheng.editor.base

import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.PopupWindow
import com.mucheng.editor.common.AutoCompleteItem
import com.mucheng.editor.controller.EditorController
import com.mucheng.editor.simple.DefaultAutoCompleteHelper

@Suppress("MemberVisibilityCanBePrivate")
abstract class BaseAutoCompletionPanel(
    protected val context: Context,
    protected val controller: EditorController,
) : PopupWindow(context) {

    protected val items: MutableList<AutoCompleteItem> = ArrayList()

    protected var mAutoCompleteHelper: BaseAutoCompleteHelper = DefaultAutoCompleteHelper()

    protected var mOnAutoCompletionItemClickListener: OnAutoCompletionItemClickListener =
        DefaultAutoCompletionItemClickListener()

    private var mAutoCompleteFilter: AutoCompleteFilter? = null

    private var mLanguage: BaseLanguage = controller.language

    open fun addAutoCompleteItem(item: AutoCompleteItem) {
        items.add(item)
    }

    abstract fun notifyAutoCompleteItemChanged()

    fun setAutoCompleteHelper(helper: BaseAutoCompleteHelper) {
        this.mAutoCompleteHelper = helper
    }

    fun setOnAutoCompletionItemClickListener(onAutoCompletionItemClickListener: OnAutoCompletionItemClickListener) {
        this.mOnAutoCompletionItemClickListener = onAutoCompletionItemClickListener
    }

    fun getAutoCompletionItemClickListener(): OnAutoCompletionItemClickListener {
        return this.mOnAutoCompletionItemClickListener
    }

    fun setAutoCompleteFilter(filter: AutoCompleteFilter?) {
        this.mAutoCompleteFilter = filter
    }

    fun getAutoCompleteFilter(): AutoCompleteFilter? {
        return mAutoCompleteFilter
    }

    fun setLanguage(target: BaseLanguage) {
        this.mLanguage = target
    }

    fun getLanguage(): BaseLanguage {
        return mLanguage
    }

    protected open suspend fun requireAutoCompletionItem() {}

    abstract fun requireAutoCompletionPanel()

    protected open fun show() {
        val editor = controller.getEditor()
        editor.post {
            showAsDropDown(editor, 0, 0, Gravity.CENTER)
        }
    }

    interface OnAutoCompletionItemClickListener {
        fun onAutoCompletionItemClick(view: View, item: AutoCompleteItem)
    }

    inner class DefaultAutoCompletionItemClickListener : OnAutoCompletionItemClickListener {

        override fun onAutoCompletionItemClick(view: View, item: AutoCompleteItem) {
            val editor = controller.getEditor()
            val cursor = editor.getContentProvider().getCursor()
            val inputConnection = editor.getTextInputConnection()
            var row = cursor.row
            val lineContent = editor.getContentProvider().getLineContent(cursor.column)
            val lexer = getLanguage().getLexer()!!
            val words = StringBuilder()

            while (row > 0) {
                --row

                // 获取当前字符
                val char = lineContent[row]

                // 字符串不能是空格等界符
                if (lexer.isSymbol(char) || lexer.isWhitespace(char) || lexer.isDigit(char)) {
                    //直接退出循环
                    break
                }

                // 否则加上对应数值
                if (words.isEmpty()) {
                    words.append(char)
                    continue
                }

                words.insert(0, char)
            }
            if (words.isEmpty()) {
                return
            }

            val text = words.toString()

            if (text == item.name) {
                dismiss()
                return
            }

            val nextCommit = item.name.replace(text, "")
            inputConnection.commitText(nextCommit, 0)
            dismiss()
        }

    }

    abstract class AutoCompleteFilter(protected val autoCompletePanel: BaseAutoCompletionPanel) {

        abstract suspend fun filter(words: StringBuilder)

        abstract fun getItemCount(): Int

    }

    inner class DefaultAutoCompleteFilter : BaseAutoCompletionPanel.AutoCompleteFilter(this) {

        override suspend fun filter(words: StringBuilder) {}

        override fun getItemCount(): Int {
            return items.size
        }

    }

}