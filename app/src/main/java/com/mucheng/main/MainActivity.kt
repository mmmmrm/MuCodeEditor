package com.mucheng.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mucheng.editor.component.animation.CursorMovingAnimation
import com.mucheng.editor.language.ecmascript.EcmaScriptLanguage
import com.mucheng.editor.views.MuCodeEditor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val editor = findViewById<MuCodeEditor>(R.id.editor)

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

    @Suppress("BlockingMethodInNonBlockingContext")
    private suspend fun addText(editor: MuCodeEditor) {
        withContext(Dispatchers.IO) {
            editor.open(assets.open("test/main.js"))
        }
    }

}