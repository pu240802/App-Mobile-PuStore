package com.nguyenthinhatphuong.pu.ui.ui.activites

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.nguyenthinhatphuong.pu.R
import com.nguyenthinhatphuong.pu.utils.MSPButton
import com.nguyenthinhatphuong.pu.utils.MSPEditText

class ForgotPasswordActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        setupActionBar()
    }
    private fun setupActionBar() {
        val toolbar_forgot_password_activity = findViewById<Toolbar>(R.id.toolbar_forgot_password_activity)
        setSupportActionBar(toolbar_forgot_password_activity)
        val actionBar = supportActionBar

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24)

        }

        toolbar_forgot_password_activity.setNavigationOnClickListener { onBackPressed() }


        val btn_submit = findViewById<MSPButton>(R.id.btn_submit)
        btn_submit.setOnClickListener{
            val et_email_forgot_pw = findViewById<MSPEditText>(R.id.et_email_forgot_pw)
            val  email: String = et_email_forgot_pw.text.toString().trim{ it <= ' '}
            if(email.isEmpty()){
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email),true)
            }else{
                showProgressDialog(resources.getString(R.string.please_wait))
                FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener { task ->
                    hideProgressDialog()
                    if(task.isSuccessful){
                        Toast.makeText(this@ForgotPasswordActivity,resources.getString(R.string.email_sent_success),
                        Toast.LENGTH_LONG).show()
                        finish()
                    }else{
                        showErrorSnackBar(task.exception!!.message.toString(),true)
                    }
                }
            }
        }
    }
}