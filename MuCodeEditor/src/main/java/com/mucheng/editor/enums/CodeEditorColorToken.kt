/*
 * Copyright (c) 2022 SuMuCheng
 *
 * CN:
 * 作者：SuMuCheng
 * Github 主页：https://github.com/CaiMuCheng
 *
 * 你可以免费使用、商用以下代码，也可以基于以下代码做出修改，但是必须在你的项目中标注出处
 * 例如：在你 APP 的设置中添加 “关于编辑器” 一栏，其中标注作者以及此编辑器的 Github 主页
 *
 * 此代码使用 MPL 2.0 开源许可证，你必须标注作者信息
 * 若你要修改文件，请勿删除此注释
 * 若你违反以上条例我们有权向您提起诉讼!
 *
 * EN:
 * Author: SuMuCheng
 * Github Homepage: https://github.com/CaiMuCheng
 *
 * You can use the following code for free, commercial use, or make modifications based on the following code, but you must mark the source in your project.
 * For example: add an "About Editor" column in your app's settings, which identifies the author and the Github home page of this editor.
 *
 * This code uses the MPL 2.0 open source license, you must mark the author information
 * Do not delete this comment if you want to modify the file.
 *
 * If you violate the above regulations we have the right to sue you!
 */

package com.mucheng.editor.enums

import com.mucheng.editor.base.BaseToken

/**
 * @sample
 * 编辑器颜色的定义
 *
 * BaseToken 详见：
 * @see com.mucheng.editor.base.BaseToken
 * */
class CodeEditorColorToken private constructor(description: String) : BaseToken(null, description) {

    companion object {

        val DEFAULT_COLOR = CodeEditorColorToken("DEFAULT_COLOR") // 默认颜色，也就是缺省时的颜色
        val BACKGROUND_COLOR = CodeEditorColorToken("BACKGROUND_COLOR") // 编辑器背景色
        val IDENTIFIER_COLOR = CodeEditorColorToken("IDENTIFIER_COLOR") // 标识符颜色
        val SELECT_BACKGROUND_COLOR = CodeEditorColorToken("SELECT_BACKGROUND_COLOR") // 选中背景颜色

        val CURSOR_COLOR = CodeEditorColorToken("CURSOR_COLOR") // 光标颜色
        val LINE_NUMBER_COLOR = CodeEditorColorToken("LINE_NUMBER_COLOR") // 行号颜色
        val DIVIDING_LINE_COLOR = CodeEditorColorToken("DIVIDING_LINE_COLOR") // 分割线颜色
        val HANDLE_TEXT_BACKGROUND_COLOR =
            CodeEditorColorToken("HANDLE_TEXT_BACKGROUND_COLOR") // 选中时处理文本的角标的颜色

        val KEYWORD_COLOR = CodeEditorColorToken("KEYWORD_COLOR") // 关键字颜色
        val NUMERICAL_VALUE_COLOR = CodeEditorColorToken("NUMERICAL_VALUE_COLOR") // 数值颜色
        val STRING_COLOR = CodeEditorColorToken("STRING_COLOR") // 字符串颜色
        val COMMENT_COLOR = CodeEditorColorToken("COMMENT_COLOR") // 注释颜色
        val SYMBOL_COLOR = CodeEditorColorToken("SYMBOL_COLOR")
        val SPECIAL_COLOR = CodeEditorColorToken("SPECIAL_COLOR") // 特殊（值）颜色

        val AUTO_COMPLETE_PANEL_BACKGROUND =
            CodeEditorColorToken("AUTO_COMPLETE_PANEL_BACKGROUND") // 自动补全栏颜色
        val AUTO_COMPLETE_PANEL_ICON_COLOR = CodeEditorColorToken("AUTO_COMPLETE_PANEL_ICON_COLOR")
        val AUTO_COMPLETE_PANEL_TITLE_COLOR =
            CodeEditorColorToken("AUTO_COMPLETE_PANEL_TITLE_COLOR")
        val AUTO_COMPLETE_PANEL_SIMPLE_DESCRIPTION_COLOR =
            CodeEditorColorToken("AUTO_COMPLETE_PANEL_SIMPLE_DESCRIPTION_COLOR")
        val AUTO_COMPLETE_PANEL_TYPE_COLOR = CodeEditorColorToken("AUTO_COMPLETE_PANEL_TYPE_COLOR")

        val TOOL_OPTIONS_PANEL_BACKGROUND = CodeEditorColorToken("TOOL_OPTIONS_PANEL_BACKGROUND")
        val TOOL_OPTIONS_PANEL_ICON_COLOR = CodeEditorColorToken("TOOL_OPTIONS_PANEL_ICON_COLOR")
        val TOOL_OPTIONS_PANEL_TEXT_COLOR = CodeEditorColorToken("TOOL_OPTIONS_PANEL_TEXT_COLOR")

        val SYMBOL_TABLE_PANEL_BACKGROUND = CodeEditorColorToken("SYMBOL_TABLE_PANEL_BACKGROUND")
        val SYMBOL_TABLE_TEXT_COLOR = CodeEditorColorToken("SYMBOL_TABLE_TEXT_COLOR")

        val USER_DEFINE_COLOR = CodeEditorColorToken("USER_DEFINE_COLOR") // 用户定义的颜色
    }

}