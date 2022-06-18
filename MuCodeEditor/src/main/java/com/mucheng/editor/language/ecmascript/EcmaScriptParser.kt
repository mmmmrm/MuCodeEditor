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

package com.mucheng.editor.language.ecmascript

import com.mucheng.editor.base.BaseParser

open class EcmaScriptParser : BaseParser<EcmaScriptToken>() {

    override var eofToken: EcmaScriptToken = EcmaScriptToken.EOF

    override fun parse() {
        if (column > columnSize()) {
            return
        }

        while (true) {

            if (column > columnSize()) {
                return
            }

            scannedColumnSource = sources[column - 1]

            if (index >= indexSize()) {
                ++column
                index = 0
                continue
            }

            scannedColumnSource = sources[column - 1]
            getToken()

            if (parseVariableStatement()) continue

            ++index
        }
    }

    private fun parseVariableStatement(): Boolean {
        if (scannedToken != EcmaScriptToken.VAR && scannedToken != EcmaScriptToken.LET && scannedToken != EcmaScriptToken.CONST) {
            return false
        }

        val startIndex = index
        yyToken()
        // 规定变量名必须是标识符，不是则编译错误
        if (scannedToken != EcmaScriptToken.IDENTIFIER) {

            addCompileError("Unexpected token type '$scannedToken'", scannedRange)
            return true
        }

        // var identifier[=expr]
        if (scannedToken == EcmaScriptToken.VAR) {

        }

        // let identifier[=expr]
        if (scannedToken == EcmaScriptToken.LET) {

        }

        // const identifier=expr
        if (scannedToken == EcmaScriptToken.CONST) {

        }
        return false
    }

    override fun setSources(sources: List<List<Pair<EcmaScriptToken, IntRange>>>) {
        super.setSources(sources.map {
            it.filter { pair ->
                val token = pair.first
                token != EcmaScriptToken.WHITESPACE &&
                        token != EcmaScriptToken.SINGLE_COMMENT &&
                        token != EcmaScriptToken.MULTI_COMMENT_START &&
                        token != EcmaScriptToken.MULTI_COMMENT_PART &&
                        token != EcmaScriptToken.MULTI_COMMENT_END
            }
        })
    }

}