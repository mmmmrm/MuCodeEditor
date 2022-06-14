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

package com.mucheng.editor.colorful

import android.graphics.Color
import com.mucheng.editor.base.BaseToken
import com.mucheng.editor.exceptions.ColorNotFoundException

/**
 * 指定编辑器的主题的基类
 * 一个主题应由浅色与深色的 colors 组成
 * */
@Suppress("MemberVisibilityCanBePrivate", "unused")
abstract class AbstractTheme {

    // 代表浅色主题的颜色
    protected val lightColors: MutableMap<BaseToken, Int> = HashMap()

    // 代码深色主题的颜色
    protected val darkColors: MutableMap<BaseToken, Int> = HashMap()

    private var mDefaultToken: BaseToken? = null

    private var useDarkColor = false

    /**
     * 将 String 转为 IntColor
     * @receiver String 要转为 Color 的字符串
     * @return Int 对应十六进制颜色值
     * */
    protected fun String.parseColor(): Int {
        return Color.parseColor(this)
    }

    /**
     * @param token 需要映射的 BaseToken
     * @param value 对应的 16 进制颜色值
     * @return Pair<BaseToken, Int> 打包后的 pair
     * */
    protected fun toPair(token: BaseToken, value: Int): Pair<BaseToken, Int> {
        return token to value
    }

    /**
     * @receiver Pair<BaseToken, Int> 打包好的 Pair
     * @param colors 添加到的可变 Map
     * */
    protected infix fun Pair<BaseToken, Int>.putTo(colors: MutableMap<BaseToken, Int>) {
        colors[this.first] = this.second
    }

    fun setUseDarkColors(useDarkColor: Boolean) {
        this.useDarkColor = useDarkColor
    }

    protected fun setDefaultToken(defaultToken: BaseToken?) {
        this.mDefaultToken = defaultToken
    }

    /**
     * 从 colors 中获取颜色
     * 找不到抛出 ColorNotFoundException
     *
     * @param token 需要获取的颜色的 Token
     * @return Int 此 Token 对应的 16 进制颜色
     * @throws com.mucheng.editor.exceptions.ColorNotFoundException
     * */
    fun getColor(token: BaseToken): Int {
        return if (useDarkColor) {
            darkColors.getOrElse(token) {
                if (mDefaultToken == null) {
                    throw ColorNotFoundException(token)
                }
                darkColors[mDefaultToken]
            }!!
        } else {
            lightColors.getOrElse(token) {
                if (mDefaultToken == null) {
                    throw ColorNotFoundException(token)
                }
                lightColors[mDefaultToken]
            }!!
        }
    }

}