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

import com.mucheng.editor.R

abstract class BaseAutoCompleteHelper {

    companion object {

        const val VARIABLE = "V"

        const val KEYWORD = "K"

        const val FUNCTION = "F"

    }

    fun getSimpleDescription(type: String, name: String): String {
        return when (type) {
            VARIABLE -> "变量 $name"
            KEYWORD -> "关键字 $name"
            FUNCTION -> "函数 $name"
            else -> getMySimpleDescription(type, name)
        }
    }

    abstract fun getMySimpleDescription(type: String, name: String): String

    fun getTypeIconResource(type: String): Int {
        return when (type) {
            VARIABLE -> R.drawable.ic_auto_completion_variable
            KEYWORD -> R.drawable.ic_auto_completion_keyword
            FUNCTION -> R.drawable.ic_auto_completion_function
            else -> getMyTypeIconResource(type)
        }
    }

    abstract fun getMyTypeIconResource(type: String): Int

    fun getTypeDescription(type: String): String {
        return when(type) {
            VARIABLE -> "variable"
            KEYWORD -> "keyword"
            FUNCTION -> "function"
            else -> getMyTypeDescription(type)
        }
    }

    abstract fun getMyTypeDescription(type: String): String

}