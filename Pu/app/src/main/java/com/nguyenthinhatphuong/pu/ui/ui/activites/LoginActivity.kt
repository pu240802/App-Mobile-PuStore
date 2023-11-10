package com.nguyenthinhatphuong.pu.ui.ui.activites

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import com.google.firebase.auth.FirebaseAuth
import com.nguyenthinhatphuong.pu.R
import com.nguyenthinhatphuong.pu.firestore.FirestoreClass
import com.nguyenthinhatphuong.pu.models.User
import com.nguyenthinhatphuong.pu.utils.Constants
import com.nguyenthinhatphuong.pu.utils.MSPButton
import com.nguyenthinhatphuong.pu.utils.MSPEditText
import com.nguyenthinhatphuong.pu.utils.MSPTextViewBold

class LoginActivity : BaseActivity(), View.OnClickListener {
    private lateinit var et_email: MSPEditText
    private lateinit var et_password: MSPEditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        @Suppress("DEPRECATION")
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        }else{
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN

            )
        }
        val tv_forgot_password = findViewById<MSPTextViewBold>(R.id.tv_forgot_password)
        tv_forgot_password.setOnClickListener(this)
        val btn_login = findViewById<MSPButton>(R.id.btn_login)
        btn_login.setOnClickListener(this)
        val tv_register = findViewById<MSPTextViewBold>(R.id.tv_register)
        tv_register.setOnClickListener(this)



    }

    fun userLoggedInSuccess(user: User) {
        hideProgressDialog()

        if (user.profileCompleted == 0) {
            var intent = Intent(this@LoginActivity, UserProfileActivity::class.java)
            intent.putExtra(Constants.EXTRA_USER_DETAILS,user)
            startActivity(intent)
        } else {
            startActivity(Intent(this@LoginActivity, DashboardActivity::class.java))


        }
        finish()
    }
    override fun onClick(view: View?){
        if(view != null){
            when (view.id){
                R.id.tv_forgot_password ->{
                    val intent = Intent(this@LoginActivity, ForgotPasswordActivity::class.java)
                    startActivity(intent)

                }
                R.id.btn_login ->{
                    logInRegisteredUser()
                }
                R.id.tv_register ->{
                    val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                    startActivity(intent)

                }

            }
        }
    }

    private fun validateLoginDetails():Boolean{
        et_email = findViewById(R.id.et_email)
        et_password = findViewById(R.id.et_password)

        return when{

            TextUtils.isEmpty(et_email.text.toString().trim{ it <= ' '}) ->{
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email),true)
                false
            }
            TextUtils.isEmpty(et_password.text.toString().trim{ it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                false
            }else ->{
                //showErrorSnackBar("Your details are valid.",false)
                true
            }
        }
    }
    private fun logInRegisteredUser() {
        et_email = findViewById(R.id.et_email)
        et_password = findViewById(R.id.et_password)
        if (validateLoginDetails()) {
            showProgressDialog(resources.getString(R.string.please_wait))
            val email = et_email.text.toString().trim { it <= ' ' }
            val password = et_password.text.toString().trim { it <= ' ' }

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val adminEmail = "nguyenthinhatphuong02@gmail.com" // Email của tài khoản admin mặc định

                        val isAdmin = email == adminEmail
                        val intent = Intent(this@LoginActivity, DashboardActivity::class.java)

                        intent.putExtra(Constants.EXTRA_IS_ADMIN, isAdmin)

                        startActivity(intent)
                        finish()
                    } else {
                        hideProgressDialog()
                        showErrorSnackBar(task.exception?.message.toString(), true)
                    }
                }
                .addOnFailureListener { exception ->
                    hideProgressDialog()
                    showErrorSnackBar(exception.message.toString(), true)
                }
        }
    }
}