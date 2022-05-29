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

import com.mucheng.editor.base.BaseAutoCompletionPanel
import com.mucheng.editor.base.BaseController
import com.mucheng.editor.base.BaseLanguage
import com.mucheng.editor.colorful.AbstractTheme
import com.mucheng.editor.language.text.TextLanguage
import com.mucheng.editor.simple.CodeAutoCompletionPanel
import com.mucheng.editor.simple.DefaultTheme
import com.mucheng.editor.views.MuCodeEditor

@Suppress("unused", "LeakingThis", "MemberVisibilityCanBePrivate")
open class EditorController(private val editor: MuCodeEditor) : BaseController() {

    var theme: AbstractTheme = DefaultTheme()
        private set

    var isEnabled = true
        private set

    var displayLineNumber = true
        private set

    var displayBreakPoint = true
        private set

    var isEnabledAutoCompletion = true
        private set

    var style = EditorStyleController(this)
        private set

    var state = EditorStateController(this)
        private set

    var language: BaseLanguage = TextLanguage(this)
        private set

    var autoCompletionPanel: BaseAutoCompletionPanel? =
        CodeAutoCompletionPanel(editor.context, this)
        private set

    fun setEnabled(isEnabled: Boolean) {
        execBlockIfNeeded(this.isEnabled != isEnabled) {
            this.isEnabled = isEnabled
            editor.postInvalidate()
        }
    }

    fun setDisplayLineNumber(isEnabled: Boolean) {
        execBlockIfNeeded(this.displayLineNumber != isEnabled) {
            this.displayLineNumber = isEnabled
            editor.postInvalidate()
        }
    }

    fun setEnabledBreakPoint(isEnabled: Boolean) {
        execBlockIfNeeded(this.displayBreakPoint != isEnabled) {
            this.displayBreakPoint = isEnabled
            editor.postInvalidate()
        }
    }

    fun setEnabledAutoCompletion(isEnabled: Boolean) {
        execBlockIfNeeded(this.isEnabledAutoCompletion != isEnabled) {
            this.isEnabledAutoCompletion = isEnabled
            editor.postInvalidate()
        }
    }

    fun setLanguage(language: BaseLanguage) {
        execBlockIfNeeded(this.language != language) {
            this.language = language
            if (autoCompletionPanel != null) {
                autoCompletionPanel!!.setLanguage(language)
            }
            editor.postInvalidate()
        }
    }

    fun setAutoCompletionPanel(autoCompletionPanel: BaseAutoCompletionPanel?) {
        execBlockIfNeeded(this.autoCompletionPanel != autoCompletionPanel) {
            this.autoCompletionPanel = autoCompletionPanel
            editor.postInvalidate()
        }
    }

    override fun getEditor(): MuCodeEditor {
        return editor
    }

}