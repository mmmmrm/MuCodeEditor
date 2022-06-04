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
 *  * Do not delete this comment if you want into modify the file.
 *  *
 *  * If you violate the above regulations we have the right into sue you!
 *
 */

package com.mucheng.editor.language.html

import com.mucheng.editor.base.BaseAutoCompleteHelper
import com.mucheng.editor.base.BaseLanguage
import com.mucheng.editor.base.BaseParser
import com.mucheng.editor.common.AutoCompleteItem
import com.mucheng.editor.controller.EditorController

@Suppress("SpellCheckingInspection")
class HtmlLanguage(controller: EditorController) : BaseLanguage(controller) {

    override val mAutoCompleteItem: MutableList<AutoCompleteItem>

    override val mLexer: HtmlLexer = HtmlLexer()

    override val mParser: BaseParser<*>? = null

    private val autoCompleteHelper = HtmlAutoCompleteHelper()

    override fun getAutoCompleteHelper(): BaseAutoCompleteHelper {
        return autoCompleteHelper
    }

    init {
        mAutoCompleteItem = createAutoCompleteItem()
    }

    private fun createAutoCompleteItem(): MutableList<AutoCompleteItem> {
        val list: MutableList<AutoCompleteItem> = ArrayList()
        addElements(list)
        addAttributes(list)
        return list
    }

    private fun addAttributes(list: MutableList<AutoCompleteItem>) {
        AutoCompleteItem("class",
            HtmlAutoCompleteHelper.ATTRIBUTE,
            createAttribute("class")) putTo list
        AutoCompleteItem("accesskey",
            HtmlAutoCompleteHelper.ATTRIBUTE,
            createAttribute("accesskey")) putTo list
        AutoCompleteItem("dir", HtmlAutoCompleteHelper.ATTRIBUTE, createAttribute("dir")) putTo list
        AutoCompleteItem("id", HtmlAutoCompleteHelper.ATTRIBUTE, createAttribute("id")) putTo list
        AutoCompleteItem("lang",
            HtmlAutoCompleteHelper.ATTRIBUTE,
            createAttribute("lang")) putTo list
        AutoCompleteItem("style",
            HtmlAutoCompleteHelper.ATTRIBUTE,
            createAttribute("style")) putTo list
        AutoCompleteItem("tabindex",
            HtmlAutoCompleteHelper.ATTRIBUTE,
            createAttribute("tabindex")) putTo list
        AutoCompleteItem("charset",
            HtmlAutoCompleteHelper.ATTRIBUTE,
            createAttribute("charset")) putTo list
        AutoCompleteItem("content",
            HtmlAutoCompleteHelper.ATTRIBUTE,
            createAttribute("content")) putTo list
        AutoCompleteItem("href",
            HtmlAutoCompleteHelper.ATTRIBUTE,
            createAttribute("href")) putTo list
        AutoCompleteItem("hreflang",
            HtmlAutoCompleteHelper.ATTRIBUTE,
            createAttribute("hreflang")) putTo list
        AutoCompleteItem("rel", HtmlAutoCompleteHelper.ATTRIBUTE, createAttribute("rel")) putTo list
        AutoCompleteItem("rev", HtmlAutoCompleteHelper.ATTRIBUTE, createAttribute("rev")) putTo list
        AutoCompleteItem("target",
            HtmlAutoCompleteHelper.ATTRIBUTE,
            createAttribute("target")) putTo list
        AutoCompleteItem("code",
            HtmlAutoCompleteHelper.ATTRIBUTE,
            createAttribute("code")) putTo list
        AutoCompleteItem("object",
            HtmlAutoCompleteHelper.ATTRIBUTE,
            createAttribute("object")) putTo list
        AutoCompleteItem("align",
            HtmlAutoCompleteHelper.ATTRIBUTE,
            createAttribute("align")) putTo list
        AutoCompleteItem("alt", HtmlAutoCompleteHelper.ATTRIBUTE, createAttribute("alt")) putTo list
        AutoCompleteItem("archive",
            HtmlAutoCompleteHelper.ATTRIBUTE,
            createAttribute("archive")) putTo list
        AutoCompleteItem("codebase",
            HtmlAutoCompleteHelper.ATTRIBUTE,
            createAttribute("codebase")) putTo list
        AutoCompleteItem("height",
            HtmlAutoCompleteHelper.ATTRIBUTE,
            createAttribute("height")) putTo list
        AutoCompleteItem("hspace",
            HtmlAutoCompleteHelper.ATTRIBUTE,
            createAttribute("hspace")) putTo list
        AutoCompleteItem("onclick",
            HtmlAutoCompleteHelper.ATTRIBUTE,
            createAttribute("onclick")) putTo list
        AutoCompleteItem("name",
            HtmlAutoCompleteHelper.ATTRIBUTE,
            createAttribute("name")) putTo list
        AutoCompleteItem("width",
            HtmlAutoCompleteHelper.ATTRIBUTE,
            createAttribute("width")) putTo list
        AutoCompleteItem("type",
            HtmlAutoCompleteHelper.ATTRIBUTE,
            createAttribute("type")) putTo list
        AutoCompleteItem("value",
            HtmlAutoCompleteHelper.ATTRIBUTE,
            createAttribute("value")) putTo list
        AutoCompleteItem("span",
            HtmlAutoCompleteHelper.ATTRIBUTE,
            createAttribute("span")) putTo list
        AutoCompleteItem("accept-charset",
            HtmlAutoCompleteHelper.ATTRIBUTE,
            createAttribute("accept-charset")) putTo list
        AutoCompleteItem("enctype",
            HtmlAutoCompleteHelper.ATTRIBUTE,
            createAttribute("enctype")) putTo list
        AutoCompleteItem("method",
            HtmlAutoCompleteHelper.ATTRIBUTE,
            createAttribute("method")) putTo list
        AutoCompleteItem("src", HtmlAutoCompleteHelper.ATTRIBUTE, createAttribute("src")) putTo list
        AutoCompleteItem("maxlength",
            HtmlAutoCompleteHelper.ATTRIBUTE,
            createAttribute("maxlength")) putTo list
    }

    private fun addElements(list: MutableList<AutoCompleteItem>) {
        AutoCompleteItem("DOCTYPE", HtmlAutoCompleteHelper.ELEMENT, "<!DOCTYPE html>") putTo list
        AutoCompleteItem("doctype", HtmlAutoCompleteHelper.ELEMENT, "<!doctype html>") putTo list
        AutoCompleteItem("a", HtmlAutoCompleteHelper.ELEMENT, createElement("a")) putTo list
        AutoCompleteItem("abbr", HtmlAutoCompleteHelper.ELEMENT, createElement("abbr")) putTo list
        AutoCompleteItem("acronym",
            HtmlAutoCompleteHelper.ELEMENT,
            createElement("acronym")) putTo list
        AutoCompleteItem("address",
            HtmlAutoCompleteHelper.ELEMENT,
            createElement("address")) putTo list
        AutoCompleteItem("applet",
            HtmlAutoCompleteHelper.ELEMENT,
            createElement("applet")) putTo list
        AutoCompleteItem("area", HtmlAutoCompleteHelper.ELEMENT, createElement("area")) putTo list
        AutoCompleteItem("article",
            HtmlAutoCompleteHelper.ELEMENT,
            createElement("article")) putTo list
        AutoCompleteItem("aside", HtmlAutoCompleteHelper.ELEMENT, createElement("aside")) putTo list
        AutoCompleteItem("audio", HtmlAutoCompleteHelper.ELEMENT, createElement("audio")) putTo list
        AutoCompleteItem("html", HtmlAutoCompleteHelper.ELEMENT, createElement("html")) putTo list
        AutoCompleteItem("head", HtmlAutoCompleteHelper.ELEMENT, createElement("head")) putTo list
        AutoCompleteItem("body", HtmlAutoCompleteHelper.ELEMENT, createElement("body")) putTo list
        AutoCompleteItem("h1", HtmlAutoCompleteHelper.ELEMENT, createElement("h1")) putTo list
        AutoCompleteItem("h2", HtmlAutoCompleteHelper.ELEMENT, createElement("h2")) putTo list
        AutoCompleteItem("h3", HtmlAutoCompleteHelper.ELEMENT, createElement("h3")) putTo list
        AutoCompleteItem("h4", HtmlAutoCompleteHelper.ELEMENT, createElement("h4")) putTo list
        AutoCompleteItem("h5", HtmlAutoCompleteHelper.ELEMENT, createElement("h5")) putTo list
        AutoCompleteItem("h6", HtmlAutoCompleteHelper.ELEMENT, createElement("h6")) putTo list
        AutoCompleteItem("header",
            HtmlAutoCompleteHelper.ELEMENT,
            createElement("header")) putTo list
        AutoCompleteItem("hr", HtmlAutoCompleteHelper.ELEMENT, createElement("hr")) putTo list
        AutoCompleteItem("i", HtmlAutoCompleteHelper.ELEMENT, createElement("i")) putTo list
        AutoCompleteItem("iframe",
            HtmlAutoCompleteHelper.ELEMENT,
            createElement("iframe")) putTo list
        AutoCompleteItem("img", HtmlAutoCompleteHelper.ELEMENT, createElement("img")) putTo list
        AutoCompleteItem("input", HtmlAutoCompleteHelper.ELEMENT, createElement("input")) putTo list
        AutoCompleteItem("ins", HtmlAutoCompleteHelper.ELEMENT, createElement("ins")) putTo list
        AutoCompleteItem("kbd", HtmlAutoCompleteHelper.ELEMENT, createElement("kbd")) putTo list
        AutoCompleteItem("keygen",
            HtmlAutoCompleteHelper.ELEMENT,
            createElement("keygen")) putTo list
        AutoCompleteItem("label", HtmlAutoCompleteHelper.ELEMENT, createElement("label")) putTo list
        AutoCompleteItem("legend",
            HtmlAutoCompleteHelper.ELEMENT,
            createElement("legend")) putTo list
        AutoCompleteItem("li", HtmlAutoCompleteHelper.ELEMENT, createElement("li")) putTo list
        AutoCompleteItem("link", HtmlAutoCompleteHelper.ELEMENT, createElement("link")) putTo list
        AutoCompleteItem("meta", HtmlAutoCompleteHelper.ELEMENT, createElement("meta")) putTo list
        AutoCompleteItem("main", HtmlAutoCompleteHelper.ELEMENT, createElement("main")) putTo list
        AutoCompleteItem("map", HtmlAutoCompleteHelper.ELEMENT, createElement("map")) putTo list
        AutoCompleteItem("mark", HtmlAutoCompleteHelper.ELEMENT, createElement("mark")) putTo list
        AutoCompleteItem("menu", HtmlAutoCompleteHelper.ELEMENT, createElement("menu")) putTo list
        AutoCompleteItem("item", HtmlAutoCompleteHelper.ELEMENT, createElement("item")) putTo list
        AutoCompleteItem("meter", HtmlAutoCompleteHelper.ELEMENT, createElement("meter")) putTo list
        AutoCompleteItem("nav", HtmlAutoCompleteHelper.ELEMENT, createElement("nav")) putTo list
        AutoCompleteItem("noframes",
            HtmlAutoCompleteHelper.ELEMENT,
            createElement("noframes")) putTo list
        AutoCompleteItem("noscript",
            HtmlAutoCompleteHelper.ELEMENT,
            createElement("noscript")) putTo list
        AutoCompleteItem("object",
            HtmlAutoCompleteHelper.ELEMENT,
            createElement("object")) putTo list
        AutoCompleteItem("ol", HtmlAutoCompleteHelper.ELEMENT, createElement("ol")) putTo list
        AutoCompleteItem("optgroup",
            HtmlAutoCompleteHelper.ELEMENT,
            createElement("optgroup")) putTo list
        AutoCompleteItem("option",
            HtmlAutoCompleteHelper.ELEMENT,
            createElement("option")) putTo list
        AutoCompleteItem("p", HtmlAutoCompleteHelper.ELEMENT, createElement("p")) putTo list
        AutoCompleteItem("param", HtmlAutoCompleteHelper.ELEMENT, createElement("param")) putTo list
        AutoCompleteItem("pre", HtmlAutoCompleteHelper.ELEMENT, createElement("pre")) putTo list
        AutoCompleteItem("progress",
            HtmlAutoCompleteHelper.ELEMENT,
            createElement("progress")) putTo list
        AutoCompleteItem("q", HtmlAutoCompleteHelper.ELEMENT, createElement("q")) putTo list
        AutoCompleteItem("rq", HtmlAutoCompleteHelper.ELEMENT, createElement("rq")) putTo list
        AutoCompleteItem("rt", HtmlAutoCompleteHelper.ELEMENT, createElement("rt")) putTo list
        AutoCompleteItem("ruby", HtmlAutoCompleteHelper.ELEMENT, createElement("ruby")) putTo list
        AutoCompleteItem("s", HtmlAutoCompleteHelper.ELEMENT, createElement("s")) putTo list
        AutoCompleteItem("samp", HtmlAutoCompleteHelper.ELEMENT, createElement("samp")) putTo list
        AutoCompleteItem("script",
            HtmlAutoCompleteHelper.ELEMENT,
            createElement("script")) putTo list
        AutoCompleteItem("section",
            HtmlAutoCompleteHelper.ELEMENT,
            createElement("section")) putTo list
        AutoCompleteItem("select",
            HtmlAutoCompleteHelper.ELEMENT,
            createElement("select")) putTo list
        AutoCompleteItem("small", HtmlAutoCompleteHelper.ELEMENT, createElement("small")) putTo list
        AutoCompleteItem("source",
            HtmlAutoCompleteHelper.ELEMENT,
            createElement("source")) putTo list
        AutoCompleteItem("span", HtmlAutoCompleteHelper.ELEMENT, createElement("span")) putTo list
        AutoCompleteItem("strike",
            HtmlAutoCompleteHelper.ELEMENT,
            createElement("strike")) putTo list
        AutoCompleteItem("strong",
            HtmlAutoCompleteHelper.ELEMENT,
            createElement("strong")) putTo list
        AutoCompleteItem("sub", HtmlAutoCompleteHelper.ELEMENT, createElement("sub")) putTo list
        AutoCompleteItem("title", HtmlAutoCompleteHelper.ELEMENT, createElement("title")) putTo list
        AutoCompleteItem("table", HtmlAutoCompleteHelper.ELEMENT, createElement("table")) putTo list
        AutoCompleteItem("tbody", HtmlAutoCompleteHelper.ELEMENT, createElement("tbody")) putTo list
        AutoCompleteItem("td", HtmlAutoCompleteHelper.ELEMENT, createElement("td")) putTo list
        AutoCompleteItem("textarea",
            HtmlAutoCompleteHelper.ELEMENT,
            createElement("textarea")) putTo list
        AutoCompleteItem("tfoot", HtmlAutoCompleteHelper.ELEMENT, createElement("tfoot")) putTo list
        AutoCompleteItem("th", HtmlAutoCompleteHelper.ELEMENT, createElement("th")) putTo list
        AutoCompleteItem("thead", HtmlAutoCompleteHelper.ELEMENT, createElement("thead")) putTo list
        AutoCompleteItem("time", HtmlAutoCompleteHelper.ELEMENT, createElement("time")) putTo list
        AutoCompleteItem("tr", HtmlAutoCompleteHelper.ELEMENT, createElement("tr")) putTo list
        AutoCompleteItem("track", HtmlAutoCompleteHelper.ELEMENT, createElement("track")) putTo list
        AutoCompleteItem("tt", HtmlAutoCompleteHelper.ELEMENT, createElement("tt")) putTo list
        AutoCompleteItem("u", HtmlAutoCompleteHelper.ELEMENT, createElement("u")) putTo list
        AutoCompleteItem("ul", HtmlAutoCompleteHelper.ELEMENT, createElement("ul")) putTo list
        AutoCompleteItem("var", HtmlAutoCompleteHelper.ELEMENT, createElement("var")) putTo list
        AutoCompleteItem("video", HtmlAutoCompleteHelper.ELEMENT, createElement("video")) putTo list
        AutoCompleteItem("wbr", HtmlAutoCompleteHelper.ELEMENT, createElement("wbr")) putTo list

        AutoCompleteItem("b", HtmlAutoCompleteHelper.ELEMENT, createElement("b")) putTo list
        AutoCompleteItem("base", HtmlAutoCompleteHelper.ELEMENT, createElement("base")) putTo list
        AutoCompleteItem("basefont",
            HtmlAutoCompleteHelper.ELEMENT,
            createElement("basefont")) putTo list
        AutoCompleteItem("bdi", HtmlAutoCompleteHelper.ELEMENT, createElement("bdi")) putTo list
        AutoCompleteItem("bdo", HtmlAutoCompleteHelper.ELEMENT, createElement("bdo")) putTo list
        AutoCompleteItem("big", HtmlAutoCompleteHelper.ELEMENT, createElement("big")) putTo list
        AutoCompleteItem("blockquote",
            HtmlAutoCompleteHelper.ELEMENT,
            createElement("blockquote")) putTo list
        AutoCompleteItem("br", HtmlAutoCompleteHelper.ELEMENT, "<br/>") putTo list
        AutoCompleteItem("button",
            HtmlAutoCompleteHelper.ELEMENT,
            createElement("button")) putTo list
        AutoCompleteItem("canvas",
            HtmlAutoCompleteHelper.ELEMENT,
            createElement("canvas")) putTo list
        AutoCompleteItem("caption",
            HtmlAutoCompleteHelper.ELEMENT,
            createElement("caption")) putTo list
        AutoCompleteItem("center",
            HtmlAutoCompleteHelper.ELEMENT,
            createElement("center")) putTo list
        AutoCompleteItem("cite", HtmlAutoCompleteHelper.ELEMENT, createElement("cite")) putTo list
        AutoCompleteItem("code", HtmlAutoCompleteHelper.ELEMENT, createElement("code")) putTo list
        AutoCompleteItem("col", HtmlAutoCompleteHelper.ELEMENT, createElement("col")) putTo list
        AutoCompleteItem("colgroup",
            HtmlAutoCompleteHelper.ELEMENT,
            createElement("colgroup")) putTo list
        AutoCompleteItem("command",
            HtmlAutoCompleteHelper.ELEMENT,
            createElement("command")) putTo list
        AutoCompleteItem("datalist",
            HtmlAutoCompleteHelper.ELEMENT,
            createElement("datalist")) putTo list
        AutoCompleteItem("dd", HtmlAutoCompleteHelper.ELEMENT, createElement("dd")) putTo list
        AutoCompleteItem("del", HtmlAutoCompleteHelper.ELEMENT, createElement("del")) putTo list
        AutoCompleteItem("dfn", HtmlAutoCompleteHelper.ELEMENT, createElement("dfn")) putTo list
        AutoCompleteItem("details",
            HtmlAutoCompleteHelper.ELEMENT,
            createElement("details")) putTo list
        AutoCompleteItem("dialog",
            HtmlAutoCompleteHelper.ELEMENT,
            createElement("dialog")) putTo list
        AutoCompleteItem("dir", HtmlAutoCompleteHelper.ELEMENT, createElement("dir")) putTo list
        AutoCompleteItem("div", HtmlAutoCompleteHelper.ELEMENT, createElement("div")) putTo list
        AutoCompleteItem("dl", HtmlAutoCompleteHelper.ELEMENT, createElement("dl")) putTo list
        AutoCompleteItem("dt", HtmlAutoCompleteHelper.ELEMENT, createElement("dt")) putTo list
        AutoCompleteItem("em", HtmlAutoCompleteHelper.ELEMENT, createElement("em")) putTo list
        AutoCompleteItem("embed", HtmlAutoCompleteHelper.ELEMENT, createElement("embed")) putTo list
        AutoCompleteItem("fieldset",
            HtmlAutoCompleteHelper.ELEMENT,
            createElement("fieldset")) putTo list
        AutoCompleteItem("figcaption",
            HtmlAutoCompleteHelper.ELEMENT,
            createElement("figcaption")) putTo list
        AutoCompleteItem("figure",
            HtmlAutoCompleteHelper.ELEMENT,
            createElement("figure")) putTo list
        AutoCompleteItem("font", HtmlAutoCompleteHelper.ELEMENT, createElement("font")) putTo list
        AutoCompleteItem("footer",
            HtmlAutoCompleteHelper.ELEMENT,
            createElement("footer")) putTo list
        AutoCompleteItem("form", HtmlAutoCompleteHelper.ELEMENT, createElement("form")) putTo list
        AutoCompleteItem("frame", HtmlAutoCompleteHelper.ELEMENT, createElement("frame")) putTo list
        AutoCompleteItem("frameset",
            HtmlAutoCompleteHelper.ELEMENT,
            createElement("frameset")) putTo list
        AutoCompleteItem("summary",
            HtmlAutoCompleteHelper.ELEMENT,
            createElement("summary")) putTo list
        AutoCompleteItem("sup", HtmlAutoCompleteHelper.ELEMENT, createElement("sup")) putTo list
    }

    private fun createElement(elementName: String): String {
        return "<$elementName></$elementName>"
    }

    private fun createAttribute(attribute: String): String {
        return "$attribute=\"\""
    }

    private infix fun AutoCompleteItem.putTo(list: MutableList<AutoCompleteItem>) {
        list.add(this)
    }

}