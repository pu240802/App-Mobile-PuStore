package com.nguyenthinhatphuong.pu.ui.ui.activites

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.Contacts
import android.widget.TextView
import com.nguyenthinhatphuong.pu.R
import com.nguyenthinhatphuong.pu.utils.Constants

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val sharedPreferences =
            getSharedPreferences(Constants.PU_PREFERENCES, Context.MODE_PRIVATE)
        val username = sharedPreferences.getString(Constants.LOGGED_IN_USERNAME, "")!!
        val tv_main = findViewById<TextView>(R.id.tv_main)
        tv_main.text ="Hello $username."
    }
}