package com.example.verificationcodedemo

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.captchaedit.*
import kotlinx.android.synthetic.main.captchaedit.view.*
import com.example.myedittext.MyEditText

class MainActivity : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        //requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.i("myl","调用onCreate")
        sendVCBtn.setOnClickListener {
            Toast.makeText(this,"验证码已发送",Toast.LENGTH_SHORT).show()
            sendVCBtn.setTextColor(Color.parseColor("#006400"))
        }

        CaptchaEdit.setOnHandlerListener(object : CaptchaEdit.InputListener{
            override fun finishInput() {
                Log.i("myl","输入完成")
                sendVCBtn.performClick()
            }
        })
        val captchaEdit : CaptchaEdit = findViewById(R.id.captchaedit)
        captchaEdit.setetCount(6)
        val etCount = captchaEdit.getetCount()
        captchaEdit.setetWidth(100)
        val etWidth = captchaEdit.getetWidth()
        captchaEdit.setetBgColor(Color.parseColor("#FF0000"))
        val etBgColor = captchaEdit.getetBgColor()
        captchaEdit.setetDivideSize(30)
        val etDivideSize = captchaEdit.getetDivideSize()
        captchaEdit.settextColor(Color.parseColor("#0000FF"))
        val textColor = captchaEdit.gettextColor()
        captchaEdit.settextSize(16.0F)
        val textSize = captchaEdit.gettextSize()
        captchaEdit.setcursorColor(Color.parseColor("#00FF00"))
        val cursorColor = captchaEdit.getcursorColor()
        captchaEdit.show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onConfigurationChanged(newConfig: Configuration) {
        //检测翻转
        super.onConfigurationChanged(newConfig)
        Log.i("myl","调用onConfiguration")
        setContentView(R.layout.activity_main)
    }
}