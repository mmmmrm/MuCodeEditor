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

package com.mucheng.editor.simple

import com.mucheng.editor.colorful.AbstractTheme
import com.mucheng.editor.enums.CodeEditorColorToken

/**
 * 代表编辑器的默认主题，实现自 AbstractTheme
 *
 * AbstractTheme 详见：
 * @see com.mucheng.editor.colorful.AbstractTheme
 * */
@Suppress("SpellCheckingInspection")
class DefaultTheme : AbstractTheme() {

    init {
        // 向两个 colors 添加 color pair
        addLightColors()
        addDarkColors()
    }

    // 添加浅色
    private fun addLightColors() {
        // 编辑器背景颜色
        toPair(CodeEditorColorToken.BACKGROUND_COLOR, "#FFFFFFFF".parseColor()) putTo lightColors

        // 关键字颜色
        toPair(CodeEditorColorToken.KEYWORD_COLOR, "#FFc678dd".parseColor()) putTo lightColors

        // 标识符颜色
        toPair(CodeEditorColorToken.IDENTIFIER_COLOR, "#FF000000".parseColor()) putTo lightColors

        // 字符川颜色
        toPair(CodeEditorColorToken.STRING_COLOR, "#FF009688".parseColor()) putTo lightColors

        // 数字颜色
        toPair(CodeEditorColorToken.NUMERICAL_VALUE_COLOR,
            "#FF497ce3".parseColor()) putTo lightColors

        // 特殊值颜色
        toPair(CodeEditorColorToken.SPECIAL_COLOR, "#FF51AAFF".parseColor()) putTo lightColors

        // 光标颜色
        toPair(CodeEditorColorToken.CURSOR_COLOR, "#FF000000".parseColor()) putTo lightColors

        // 行号颜色
        toPair(CodeEditorColorToken.LINE_NUMBER_COLOR, "#FF000000".parseColor()) putTo lightColors

        // 分割线颜色
        toPair(CodeEditorColorToken.DIVIDING_LINE_COLOR, "#FFE0E0E0".parseColor()) putTo lightColors

        // 选中文本的背景色
        toPair(CodeEditorColorToken.SELECT_BACKGROUND_COLOR,
            "#D08aa8fe".parseColor()) putTo lightColors

        toPair(CodeEditorColorToken.HANDLE_TEXT_BACKGROUND_COLOR,
            "#FF497CE3".parseColor()) putTo lightColors

        // 符号的颜色
        toPair(CodeEditorColorToken.SYMBOL_COLOR, "#FF51AAFF".parseColor()) putTo lightColors

        // 注释的颜色
        toPair(CodeEditorColorToken.COMMENT_COLOR, "#FF586694".parseColor()) putTo lightColors

        toPair(CodeEditorColorToken.AUTO_COMPLETE_PANEL_BACKGROUND,
            "#FFFFFFFF".parseColor()) putTo lightColors

        toPair(CodeEditorColorToken.AUTO_COMPLETE_PANEL_ICON_COLOR,
            "#FF8BA8FF".parseColor()) putTo lightColors

        toPair(CodeEditorColorToken.AUTO_COMPLETE_PANEL_TITLE_COLOR,
            "#FF202331".parseColor()) putTo lightColors

        toPair(CodeEditorColorToken.AUTO_COMPLETE_PANEL_SIMPLE_DESCRIPTION_COLOR,
            "#FF535353".parseColor()) putTo lightColors

        toPair(CodeEditorColorToken.AUTO_COMPLETE_PANEL_TYPE_COLOR,
            "#FF535353".parseColor()) putTo lightColors

        toPair(CodeEditorColorToken.TOOL_OPTIONS_PANEL_BACKGROUND,
            "#FFFFFFFF".parseColor()) putTo lightColors

        toPair(CodeEditorColorToken.TOOL_OPTIONS_PANEL_ICON_COLOR,
            "#FF8BA8FF".parseColor()) putTo lightColors

        toPair(CodeEditorColorToken.TOOL_OPTIONS_PANEL_TEXT_COLOR,
            "#FF202331".parseColor()) putTo lightColors

        toPair(CodeEditorColorToken.SYMBOL_TABLE_PANEL_BACKGROUND, "#FFFFFFFF".parseColor()) putTo lightColors

        toPair(CodeEditorColorToken.SYMBOL_TABLE_TEXT_COLOR, "#FF202331".parseColor()) putTo lightColors
    }

    // 添加深色
    private fun addDarkColors() {
        // 编辑器背景颜色
        toPair(CodeEditorColorToken.BACKGROUND_COLOR, "#FF1E1E1E".parseColor()) putTo darkColors

        // 关键字颜色
        toPair(CodeEditorColorToken.KEYWORD_COLOR, "#FFc678dd".parseColor()) putTo darkColors

        // 标识符颜色
        toPair(CodeEditorColorToken.IDENTIFIER_COLOR, "#FFA4ABCC".parseColor()) putTo darkColors

        // 字符串颜色
        toPair(CodeEditorColorToken.STRING_COLOR, "#FFC3E88D".parseColor()) putTo darkColors

        // 数字颜色
        toPair(CodeEditorColorToken.NUMERICAL_VALUE_COLOR,
            "#FF497CE3".parseColor()) putTo darkColors

        // 特殊值颜色
        toPair(CodeEditorColorToken.SPECIAL_COLOR, "#FF51AAFF".parseColor()) putTo darkColors

        // 光标颜色
        toPair(CodeEditorColorToken.CURSOR_COLOR, "#FFF5F5F7".parseColor()) putTo darkColors

        // 行号颜色
        toPair(CodeEditorColorToken.LINE_NUMBER_COLOR, "#FFA4ABCC".parseColor()) putTo darkColors

        // 分割线颜色
        toPair(CodeEditorColorToken.DIVIDING_LINE_COLOR, "#FFE0E0E0".parseColor()) putTo darkColors

        // 选中文本的背景色
        toPair(CodeEditorColorToken.SELECT_BACKGROUND_COLOR,
            "#FF515C6A".parseColor()) putTo darkColors

        // 选中文本的角标颜色
        toPair(CodeEditorColorToken.HANDLE_TEXT_BACKGROUND_COLOR,
            "#FF497CE3".parseColor()) putTo darkColors

        // 符号的颜色
        toPair(CodeEditorColorToken.SYMBOL_COLOR, "#FF51AAFF".parseColor()) putTo darkColors

        // 注释的颜色
        toPair(CodeEditorColorToken.COMMENT_COLOR, "#FF858C99".parseColor()) putTo darkColors

        toPair(CodeEditorColorToken.AUTO_COMPLETE_PANEL_BACKGROUND,
            "#FF444267".parseColor()) putTo darkColors

        toPair(CodeEditorColorToken.AUTO_COMPLETE_PANEL_ICON_COLOR,
            "#FF8BA8FF".parseColor()) putTo darkColors

        toPair(CodeEditorColorToken.AUTO_COMPLETE_PANEL_TITLE_COLOR,
            "#FFA4ABCC".parseColor()) putTo darkColors

        toPair(CodeEditorColorToken.AUTO_COMPLETE_PANEL_SIMPLE_DESCRIPTION_COLOR,
            "#FF676E95".parseColor()) putTo darkColors

        toPair(CodeEditorColorToken.AUTO_COMPLETE_PANEL_TYPE_COLOR,
            "#FF676E95".parseColor()) putTo darkColors

        toPair(CodeEditorColorToken.TOOL_OPTIONS_PANEL_BACKGROUND,
            "#FF444267".parseColor()) putTo darkColors

        toPair(CodeEditorColorToken.TOOL_OPTIONS_PANEL_ICON_COLOR,
            "#FF8BA8FF".parseColor()) putTo darkColors

        toPair(CodeEditorColorToken.TOOL_OPTIONS_PANEL_TEXT_COLOR,
            "#FFA4ABCC".parseColor()) putTo darkColors

        toPair(CodeEditorColorToken.SYMBOL_TABLE_PANEL_BACKGROUND,
            "#FF444267".parseColor()) putTo darkColors

        toPair(CodeEditorColorToken.SYMBOL_TABLE_TEXT_COLOR,
            "#FFA4ABCC".parseColor()) putTo darkColors
    }

}