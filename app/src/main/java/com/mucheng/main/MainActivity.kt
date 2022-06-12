package com.mucheng.main

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.system.Os.close
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import com.google.android.material.appbar.MaterialToolbar
import com.mucheng.editor.component.ColorPickerDialog
import com.mucheng.editor.component.animation.CursorMovingAnimation
import com.mucheng.editor.enums.CodeEditorColorToken
import com.mucheng.editor.language.css.CssLanguage
import com.mucheng.editor.language.ecmascript.EcmaScriptLanguage
import com.mucheng.editor.language.html.HtmlLanguage
import com.mucheng.editor.views.MuCodeEditor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    companion object {

        private const val REQUEST_SELECT_FILE: Int = 200

    }

    private var language = "html"
    private lateinit var ecmaScriptLanguage: EcmaScriptLanguage
    private lateinit var htmlLanguage: HtmlLanguage
    private lateinit var cssLanguage: CssLanguage

    private var path: String? = null
    private lateinit var editor: MuCodeEditor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        editor = findViewById(R.id.editor)

        editor.getController().apply {
            setEnabled(true)
            htmlLanguage = HtmlLanguage(this)
            setLanguage(htmlLanguage)
            setDisplayDividingLine(false)
            theme.setUseDarkColors(true)
        }.style.apply {
            setCursorAnimation(CursorMovingAnimation(editor))
            setTypefaceFromAssets(this@MainActivity, "font/HarmonyOS-Sans-Regular.ttf")
        }

        openHtml()

        val alertDialog = ColorPickerDialog(this,
            com.google.android.material.R.style.ThemeOverlay_MaterialComponents_Dark)
            .setBackground(ColorDrawable(Color.parseColor("#1a1a1a")))
            .show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    @Suppress("DEPRECATION")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val actionController = editor.getController().action
        val controller = editor.getController()

        when (item.itemId) {
            R.id.undo -> {
                actionController.undo()
            }

            R.id.redo -> {
                actionController.redo()
            }

            R.id.open -> {
                editor.hideSoftInputMethod()
                val intent = Intent(this, FileSelectorActivity::class.java)
                startActivityForResult(intent, REQUEST_SELECT_FILE)
            }

            R.id.save -> {
                if (this.path == null) {
                    "你还没有打开文件".showToast()
                    return false
                }

                save()
            }

            R.id.close -> {
                close()
            }

            R.id.select_language_es -> {
                if (!::ecmaScriptLanguage.isInitialized) {
                    ecmaScriptLanguage = EcmaScriptLanguage(controller)
                }
                controller.setLanguage(ecmaScriptLanguage)
                openES()
            }

            R.id.select_language_html -> {
                if (!::htmlLanguage.isInitialized) {
                    htmlLanguage = HtmlLanguage(controller)
                }
                controller.setLanguage(htmlLanguage)
                openHtml()
            }

            R.id.select_language_css -> {
                if (!::cssLanguage.isInitialized) {
                    cssLanguage = CssLanguage(controller)
                }
                controller.setLanguage(cssLanguage)
                openCss()
            }

        }
        return super.onOptionsItemSelected(item)
    }

    private inline fun save(crossinline onComplete: () -> Unit = {}) {
        CoroutineScope(Dispatchers.IO).launch {
            if (path == null) {
                "你还没有打开文件!".showToast()
                return@launch
            }
            val result = editor.save(path!!)
            if (result.isFailure) {
                "保存失败：${result.exceptionOrNull()}".showToast()
            } else {
                "保存成功".showToast()
            }
            onComplete()
        }
    }

    private fun close() {
        save {
            this.path = null
            when (language) {
                "es" -> openES()
                "html" -> openHtml()
                "css" -> openCss()
            }
        }
    }

    private fun String.showToast() {
        runOnUiThread {
            Toast.makeText(this@MainActivity, this, Toast.LENGTH_SHORT).show()
        }
    }

    private fun openES() {
        language = "es"
        if (path != null) {
            return
        }
        CoroutineScope(Dispatchers.IO).launch {
            addText("test/main.js", editor)
        }
    }

    private fun openHtml() {
        language = "html"
        if (path != null) {
            return
        }
        CoroutineScope(Dispatchers.IO).launch {
            addText("test/index.html", editor)
        }
    }

    private fun openCss() {
        language = "css"
        if (path != null) {
            return
        }
        CoroutineScope(Dispatchers.IO).launch {
            addText("test/style.css", editor)
        }
    }

    @Suppress("DEPRECATION")
    @Deprecated("Deprecated in Java",
        ReplaceWith("super.onActivityResult(requestCode, resultCode, data)",
            "androidx.appcompat.app.AppCompatActivity"))
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_SELECT_FILE && resultCode == RESULT_OK) {
            val path = data!!.getStringExtra("path")!!
            if (this.path.equals(path)) {
                "你已经在编辑这个文件了!".showToast()
                return
            }
            this.path = path
            CoroutineScope(Dispatchers.IO).launch {
                editor.open(path)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    private suspend fun addText(path: String, editor: MuCodeEditor) {

        withContext(Dispatchers.IO) {
            val result = editor.open(assets.open(path))
            if (result.isFailure) {
                "打开失败：${result.exceptionOrNull()}".showToast()
            }
        }
    }

}