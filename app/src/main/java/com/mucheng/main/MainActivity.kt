package com.mucheng.main

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.PopupMenu
import com.google.android.material.appbar.MaterialToolbar
import com.mucheng.editor.component.animation.CursorMovingAnimation
import com.mucheng.editor.language.ecmascript.EcmaScriptLanguage
import com.mucheng.editor.views.MuCodeEditor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    companion object {

        private const val REQUEST_CODE_GET_CONTENT: Int = 200

    }

    private lateinit var uri: Uri
    private lateinit var editor: MuCodeEditor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        editor = findViewById(R.id.editor)

        editor.getController().apply {
            setEnabled(true)
            setLanguage(EcmaScriptLanguage(this))
            setDisplayDividingLine(false)
            theme.setUseDarkColors(true)
        }.style.apply {
            setCursorAnimation(CursorMovingAnimation(editor))
            setTypefaceFromAssets(this@MainActivity, "font/HarmonyOS-Sans-Regular.ttf")
        }

        CoroutineScope(Dispatchers.Main).launch {
            addText(editor)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.activity_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    @Suppress("DEPRECATION")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val actionController = editor.getController().action

        when (item.itemId) {
            R.id.undo -> {
                actionController.undo()
            }

            R.id.redo -> {
                actionController.redo()
            }

            R.id.open -> {
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "*/*"
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
                intent.addCategory(Intent.CATEGORY_OPENABLE)
                startActivityForResult(intent, REQUEST_CODE_GET_CONTENT)
            }

            R.id.save -> {
                if (!::uri.isInitialized) {
                    return false
                }

                val path = uri.path!!
                CoroutineScope(Dispatchers.IO).launch {
                    editor.save(path)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_OK && requestCode == REQUEST_CODE_GET_CONTENT) {
            // 多选的情况
            val clipData = data?.clipData
            if (clipData != null && clipData.itemCount > 0) {
                for (i in 0 until clipData.itemCount) {
                    val item = clipData.getItemAt(i)
                    val uri = item.uri ?: continue
                    handleSelectedFile(uri)
                }
            }
            // 单选的情况
            uri = data?.data ?: return
            handleSelectedFile(uri)
        }
    }

    private fun handleSelectedFile(uri: Uri) {
        // 获取选取返回的文件资源, 结果为 "content://" 开头的 Uri 格式的资源,
        // Uri 格式参考: content://com.android.providers.media.documents/document/document%3A145

        // 获取文件的数据, 可以使用 ContentResolver 直接打开输入流
        val fileInputStream = contentResolver.openInputStream(uri)!!

        CoroutineScope(Dispatchers.IO).launch {
            editor.open(fileInputStream)
        }
    }


    @Suppress("BlockingMethodInNonBlockingContext")
    private suspend fun addText(editor: MuCodeEditor) {
        withContext(Dispatchers.IO) {
            editor.open(assets.open("test/main.js"))
        }
    }

}