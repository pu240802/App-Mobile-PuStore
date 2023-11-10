package com.nguyenthinhatphuong.pu.ui.ui.activites

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowInsets
import android.view.WindowManager.LayoutParams.*
import com.nguyenthinhatphuong.pu.R

class SplashActivity : AppCompatActivity() {
    private fun startActivity(
        intent: Any,
        splashActivity: SplashActivity,
        java: Class<MainActivity>
    ) {
        TODO("Not yet implemented")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)


        @Suppress("DEPRECATION")
        if(Build.VERSION.SDK_INT >=Build.VERSION_CODES.R){
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        }else{
            window.setFlags(
                FLAG_FULLSCREEN,
                FLAG_FULLSCREEN

            )
        }
        @Suppress("DEPRECATION")
        Handler().postDelayed(
            {
                startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
                finish()
            },
            1500
        )
        //val typeface: Typeface = Typeface.createFromAsset(assets,"Montserrat-Bold.ttf")
        //val tvAppName = findViewById<TextView>(R.id.tv_app_name)
        //tvAppName.typeface = typeface




    }
    
}