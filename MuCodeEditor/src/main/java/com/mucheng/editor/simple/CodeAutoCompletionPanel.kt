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
import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textview.MaterialTextView
import com.mucheng.editor.R
import com.mucheng.editor.base.BaseAutoCompletionPanel
import com.mucheng.editor.controller.EditorController
import com.mucheng.editor.util.DeviceUtil
import com.mucheng.editor.util.dp
import kotlinx.coroutines.*

@Suppress("LeakingThis", "BlockingMethodInNonBlockingContext")
open class CodeAutoCompletionPanel(
    context: Context,
    controller: EditorController,
) : BaseAutoCompletionPanel(context, controller) {

    private val layoutManager: LinearLayoutManager by lazy {
        LinearLayoutManager(context,
            LinearLayoutManager.VERTICAL,
            false)
    }

    private val adapter by lazy { CodePanelAdapter() }

    private val content by lazy { createContentView() }

    private val background by lazy { createPanelBackground() }

    init {
        //设置默认弹出样式
        animationStyle = androidx.appcompat.R.style.Animation_AppCompat_DropDownUp
        width = DeviceUtil.getDeviceWidth(context)
        height = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
            183f,
            context.resources.displayMetrics).toInt()
        isTouchable = true
        isFocusable = false
        isOutsideTouchable = true
        setBackgroundDrawable(null)
        setAutoCompleteFilter(DefaultAutoCompleteFilter())
    }

    override fun show() {
        if (items.isEmpty() || isShowing) {
            return
        }

        contentView = content
        super.show()
    }


    private fun createPanelBackground(): GradientDrawable {
        val drawable = GradientDrawable()
        drawable.isAutoMirrored
        drawable.cornerRadius = 16f
        drawable.gradientType = GradientDrawable.LINEAR_GRADIENT

        drawable.orientation = GradientDrawable.Orientation.TL_BR
        drawable.colors = intArrayOf(
            Color.parseColor("#ffffff"),
            Color.parseColor("#ffffff")
        )
        drawable.alpha = 200

        return drawable
    }

    private fun createContentView(): View {

        return LinearLayoutCompat(context)
            .also {
                it.orientation = LinearLayoutCompat.VERTICAL

                it.layoutParams = LinearLayoutCompat.LayoutParams(
                    LinearLayoutCompat.LayoutParams.MATCH_PARENT,
                    LinearLayoutCompat.LayoutParams.MATCH_PARENT
                )
            }

            .also { layout ->
                val root = MaterialCardView(context)
                val margins = 8.dp.toInt()
                root.layoutParams = LinearLayoutCompat.LayoutParams(-1, -1).apply {
                    setMargins(margins, margins, margins, margins)
                }

                //设置背景
                root.background = background
                root.cardElevation = 18f
                root.setCardBackgroundColor(0)
                root.isClickable = true
                root.isFocusable = true
                layout.addView(root)

                val recyclerView = RecyclerView(context)
                recyclerView.layoutManager = layoutManager
                recyclerView.adapter = adapter
                recyclerView.layoutParams = FrameLayout.LayoutParams(-1, -1)
                root.addView(recyclerView)
            }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun notifyAutoCompleteItemChanged() {
        controller.getEditor().post {
            // 当数据更新时调用
            adapter.notifyDataSetChanged()
        }
    }

    override fun dismiss() {
        controller.getEditor().post {
            super.dismiss()
        }
    }

    override suspend fun requireAutoCompletionItem() {
        super.requireAutoCompletionItem()
        coroutineScope {
            withContext(Dispatchers.IO) {
                val cursor = controller.getEditor().getContentProvider().getCursor()
                val lineContent =
                    controller.getEditor().getContentProvider().getLineContent(cursor.column)
                val lexer = getLanguage().getLexer()
                val words = StringBuilder()
                var row = cursor.row

                if (lexer == null) {
                    return@withContext
                }

                //先遍历字符串
                while (row > 0) {
                    row--

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

                //调用过滤器
                getAutoCompleteFilter()?.filter(words)
            }
        }
    }

    // 请求自动补全面板
    override fun requireAutoCompletionPanel() {
        CoroutineScope(Dispatchers.Main).launch {
            // 进行文本过滤
            requireAutoCompletionItem()
        }
    }

    inner class DefaultAutoCompleteFilter : AutoCompleteFilter(this) {

        private var itemCount = items.size

        @Suppress("BlockingMethodInNonBlockingContext")
        override suspend fun filter(words: StringBuilder) {
            withContext(Dispatchers.IO) {

                items.clear()
                getLanguage().getAutoCompleteItem().forEach {
                    addAutoCompleteItem(it)
                }

                if (items.isEmpty() || words.isEmpty()) {
                    dismiss()
                    return@withContext
                }

                val matches = items.asSequence().filter { it -> it.name.startsWith(words) }.toList()

                items.clear()
                items.addAll(matches)

                if (matches.isEmpty()) {
                    dismiss()
                    return@withContext
                }

                itemCount = matches.count()

                show()
                notifyAutoCompleteItemChanged()
            }
        }

        override fun getItemCount(): Int {
            return itemCount
        }

    }


    inner class CodePanelAdapter : RecyclerView.Adapter<CodePanelAdapter.CodePanelViewHolder>() {

        private val inflater by lazy { LayoutInflater.from(context) }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CodePanelViewHolder {

            return CodePanelViewHolder(
                inflater.inflate(R.layout.layout_code_auto_completion_panel_item, parent, false)
            )
        }

        override fun onBindViewHolder(holder: CodePanelViewHolder, position: Int) {
            if (items.isEmpty()) {
                return
            }
            val item = items[position]
            val name = item.name
            val type = item.type

            holder.card.setOnClickListener {
                mOnAutoCompletionItemClickListener.onAutoCompletionItemClick(it, item)
            }
            holder.icon.setImageResource(mAutoCompleteHelper.getTypeIconResource(type))
            holder.name.text = name
            holder.simpleDescription.text = mAutoCompleteHelper.getSimpleDescription(type, name)
            holder.type.text = mAutoCompleteHelper.getTypeDescription(type)
        }

        override fun getItemCount() = getAutoCompleteFilter()?.getItemCount() ?: items.size

        inner class CodePanelViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val card: MaterialCardView = itemView.findViewById(R.id.card)
            val icon: ShapeableImageView = itemView.findViewById(R.id.icon)
            val name: MaterialTextView = itemView.findViewById(R.id.name)
            val simpleDescription: MaterialTextView = itemView.findViewById(R.id.simple_description)
            val type: MaterialTextView = itemView.findViewById(R.id.type)
        }

    }

}