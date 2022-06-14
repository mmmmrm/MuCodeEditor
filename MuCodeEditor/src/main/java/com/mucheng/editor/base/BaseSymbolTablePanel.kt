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
import android.widget.PopupWindow
import com.mucheng.editor.colorful.ChangeableTheme
import com.mucheng.editor.controller.EditorController

@Suppress("MemberVisibilityCanBePrivate")
abstract class BaseSymbolTablePanel(
    protected val context: Context,
    protected val controller: EditorController,
) : PopupWindow(context), ChangeableTheme {

    private val symbolTables: MutableList<Symbol> = ArrayList()

    protected lateinit var mOnSymbolClickListener: OnSymbolClickListener

    init {
        isTouchable = true
        isFocusable = false
    }

    open fun setOnSymbolClickListener(listener: OnSymbolClickListener) {
        this.mOnSymbolClickListener = listener
    }

    open fun addSymbol(symbol: Symbol) {
        symbolTables.add(symbol)
    }

    open fun getSymbolAt(position: Int): Symbol {
        return symbolTables[position]
    }

    open fun getSymbolCount(): Int {
        return symbolTables.size
    }

    open fun updateSize() {
        val editor = controller.getEditor()
        update(editor, 0, editor.height, width, height)
    }

    abstract override fun updateTheme()

    open fun show() {
        val editor = controller.getEditor()
        editor.post {
            showAsDropDown(editor, 0, editor.height, Gravity.TOP or Gravity.LEFT)
        }
    }

    override fun dismiss() {
        val editor = controller.getEditor()
        editor.post {
            super.dismiss()
        }
    }

    data class Symbol(val displayText: String, val insertText: String = displayText)

    interface OnSymbolClickListener {
        fun onSymbolClick(position: Int)
    }

}