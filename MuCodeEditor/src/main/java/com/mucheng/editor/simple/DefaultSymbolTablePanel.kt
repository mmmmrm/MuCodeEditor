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

package com.mucheng.editor.simple

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.*
import android.widget.PopupWindow
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import com.mucheng.editor.R
import com.mucheng.editor.base.BaseSymbolTablePanel
import com.mucheng.editor.controller.EditorController
import com.mucheng.editor.enums.CodeEditorColorToken
import com.mucheng.editor.util.getDp

@SuppressLint("WrongConstant")
@Suppress("LeakingThis")
open class DefaultSymbolTablePanel(context: Context, controller: EditorController) :
    BaseSymbolTablePanel(context, controller), BaseSymbolTablePanel.OnSymbolClickListener {

    private lateinit var content: View

    private var adapter: SymbolTableAdapter? = null

    @SuppressLint("NotifyDataSetChanged")
    override fun addSymbol(symbol: Symbol) {
        super.addSymbol(symbol)
        if (adapter != null) {
            adapter!!.notifyDataSetChanged()
        }
    }

    init {
        softInputMode = PopupWindow.INPUT_METHOD_NEEDED
        softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        setBackgroundDrawable(null)
        setOnSymbolClickListener(this)
        animationStyle = com.google.android.material.R.style.Animation_AppCompat_DropDownUp
        height = getDp(context, 60)
        addSymbols()
        adapter = SymbolTableAdapter()
    }

    override fun show() {
        if (!::content.isInitialized) {
            content = createContentView()
            contentView = content
        }

        val editor = controller.getEditor()
        editor.post {
            width = editor.width
            super.show()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun updateTheme() {
        adapter?.notifyDataSetChanged()
    }

    @SuppressLint("InflateParams")
    private fun createContentView(): View {
        val content =
            LayoutInflater.from(context).inflate(R.layout.layout_symbol_table_panel, null, false)
        val recyclerView: RecyclerView = content.findViewById(R.id.recyclerView)
        recyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = adapter
        return content
    }

    private fun addSymbols() {
        val symbols = listOf(
            Symbol("->", "    "),
            Symbol("{"),
            Symbol("}"),
            Symbol("("),
            Symbol(")"),
            Symbol(","),
            Symbol("."),
            Symbol(";"),
            Symbol("\""),
            Symbol("?"),
            Symbol("+"),
            Symbol("-"),
            Symbol("*"),
            Symbol("/")
        )
        symbols.forEach {
            addSymbol(it)
        }
    }

    open inner class SymbolTableAdapter : RecyclerView.Adapter<ViewHolder>() {

        private val inflater by lazy { LayoutInflater.from(context) }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(
                inflater.inflate(
                    R.layout.layout_symbol_table_panel_item, parent, false
                )
            )
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = getSymbolAt(position)
            holder.symbolRoot.setCardBackgroundColor(controller.theme.getColor(CodeEditorColorToken.SYMBOL_TABLE_PANEL_BACKGROUND))
            holder.symbolRoot.setOnClickListener {
                mOnSymbolClickListener.onSymbolClick(position)
            }
            holder.symbolInsertText.text = item.displayText
            holder.symbolInsertText.setTextColor(controller.theme.getColor(CodeEditorColorToken.SYMBOL_TABLE_TEXT_COLOR))
        }

        override fun getItemCount(): Int {
            return getSymbolCount()
        }

    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val symbolRoot: MaterialCardView = itemView.findViewById(R.id.symbolRoot)
        val symbolInsertText: MaterialTextView = itemView.findViewById(R.id.symbolInsertText)
    }

    override fun onSymbolClick(position: Int) {
        val editor = controller.getEditor()
        val textInputConnection = editor.getTextInputConnection()
        textInputConnection.commitText(getSymbolAt(position).insertText, 0)
    }

}