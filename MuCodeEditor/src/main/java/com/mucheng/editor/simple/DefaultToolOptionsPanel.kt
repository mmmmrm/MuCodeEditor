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
import android.view.LayoutInflater
import android.view.View
import com.google.android.material.card.MaterialCardView
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textview.MaterialTextView
import com.mucheng.editor.R
import com.mucheng.editor.base.BaseToolOptionsPanel
import com.mucheng.editor.controller.EditorController
import com.mucheng.editor.enums.CodeEditorColorToken
import com.mucheng.editor.util.getDp

@Suppress("LeakingThis")
open class DefaultToolOptionsPanel(context: Context, controller: EditorController) :
    BaseToolOptionsPanel(context, controller), BaseToolOptionsPanel.OnToolOptionsSelectListener {

    val content by lazy { createContentView() }

    init {
        animationStyle = com.google.android.material.R.style.Animation_AppCompat_Tooltip
        width = getDp(context, 250)
        height = getDp(context, 70)
        setBackgroundDrawable(null)
        isTouchable = true
        isFocusable = false
        contentView = content
        setOnToolOptionsSelectListener(this)
    }

    override fun show() {
        val theme = controller.theme
        val backgroundColor = theme.getColor(CodeEditorColorToken.TOOL_OPTIONS_PANEL_BACKGROUND)
        val iconColor = theme.getColor(CodeEditorColorToken.TOOL_OPTIONS_PANEL_ICON_COLOR)
        val textColor = theme.getColor(CodeEditorColorToken.TOOL_OPTIONS_PANEL_TEXT_COLOR)

        val root: MaterialCardView = content.findViewById(R.id.root)
        root.setBackgroundColor(backgroundColor)

        val selectAllIcon: ShapeableImageView = content.findViewById(R.id.icon_select_all)
        selectAllIcon.setColorFilter(iconColor)

        val copyIcon: ShapeableImageView = content.findViewById(R.id.icon_copy)
        copyIcon.setColorFilter(iconColor)

        val pasteIcon: ShapeableImageView = content.findViewById(R.id.icon_paste)
        pasteIcon.setColorFilter(iconColor)

        val cutIcon: ShapeableImageView = content.findViewById(R.id.icon_cut)
        cutIcon.setColorFilter(iconColor)

        val selectAll: MaterialTextView = content.findViewById(R.id.select_all)
        selectAll.setTextColor(textColor)

        val copy: MaterialTextView = content.findViewById(R.id.copy)
        copy.setTextColor(textColor)

        val paste: MaterialTextView = content.findViewById(R.id.paste)
        paste.setTextColor(textColor)

        val cut: MaterialTextView = content.findViewById(R.id.cut)
        cut.setTextColor(textColor)

        val roots = listOf(
            R.id.select_all_root,
            R.id.copy_root,
            R.id.paste_root,
            R.id.cut_root
        )
        roots.forEach { id ->
            val partRoot: MaterialCardView = content.findViewById(id)
            partRoot.setOnClickListener {
                mOnToolOptionsListener.onToolOptionsSelect(id)
            }
        }

        super.show()
    }

    override fun updateTheme() {
        val theme = controller.theme
        val backgroundColor = theme.getColor(CodeEditorColorToken.TOOL_OPTIONS_PANEL_BACKGROUND)
        val iconColor = theme.getColor(CodeEditorColorToken.TOOL_OPTIONS_PANEL_ICON_COLOR)
        val textColor = theme.getColor(CodeEditorColorToken.TOOL_OPTIONS_PANEL_TEXT_COLOR)

        val root: MaterialCardView = content.findViewById(R.id.root)
        root.setBackgroundColor(backgroundColor)

        val selectAllIcon: ShapeableImageView = content.findViewById(R.id.icon_select_all)
        selectAllIcon.setColorFilter(iconColor)

        val copyIcon: ShapeableImageView = content.findViewById(R.id.icon_copy)
        copyIcon.setColorFilter(iconColor)

        val pasteIcon: ShapeableImageView = content.findViewById(R.id.icon_paste)
        pasteIcon.setColorFilter(iconColor)

        val cutIcon: ShapeableImageView = content.findViewById(R.id.icon_cut)
        cutIcon.setColorFilter(iconColor)

        val selectAll: MaterialTextView = content.findViewById(R.id.select_all)
        selectAll.setTextColor(textColor)

        val copy: MaterialTextView = content.findViewById(R.id.copy)
        copy.setTextColor(textColor)

        val paste: MaterialTextView = content.findViewById(R.id.paste)
        paste.setTextColor(textColor)

        val cut: MaterialTextView = content.findViewById(R.id.cut)
        cut.setTextColor(textColor)
    }

    @SuppressLint("InflateParams")
    private fun createContentView(): View {
        val root = MaterialCardView(context)
        root.setBackgroundColor(Color.TRANSPARENT)

        root.addView(LayoutInflater.from(context)
            .inflate(R.layout.layout_tool_options_panel_item, root, false))
        return root
    }

    override fun onToolOptionsSelect(rootId: Int) {
        val editor = controller.getEditor()
        when (rootId) {
            R.id.select_all_root -> editor.selectAll()
            R.id.copy_root -> editor.copySelectionText()
            R.id.paste_root -> editor.insert(editor.getClipboardText())
            R.id.cut_root -> {
                editor.copySelectionText()
                val textInputConnection = editor.getTextInputConnection()
                textInputConnection.deleteSurroundingText(0, 0)
            }
        }
    }

}