package com.nguyenthinhatphuong.pu.ui.ui.activites

import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowInsets
import android.view.WindowManager
import com.nguyenthinhatphuong.pu.R
import androidx.appcompat.widget.Toolbar
import android.widget.CheckBox
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.nguyenthinhatphuong.pu.firestore.FirestoreClass
import com.nguyenthinhatphuong.pu.models.User


class RegisterActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)



        @Suppress("DEPRECATION")
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        }else{
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN

            )
        }
        setupActionBar()
        val tvLogin = findViewById<com.nguyenthinhatphuong.pu.utils.MSPTextViewBold>(R.id.tv_login)

// Set an OnClickListener
        tvLogin.setOnClickListener {
            onBackPressed()

        }
        val btn_register = findViewById<com.nguyenthinhatphuong.pu.utils.MSPButton>(R.id.btn_register)


        btn_register.setOnClickListener {
           registerUser()

        }

    }

    private fun setupActionBar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar_register_activity)
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24)
        }

        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun validateRegisterDetails():Boolean{
        val et_first_name = findViewById<com.nguyenthinhatphuong.pu.utils.MSPEditText>(R.id.et_first_name)
        val et_last_name = findViewById<com.nguyenthinhatphuong.pu.utils.MSPEditText>(R.id.et_last_name)
        val et_email = findViewById<com.nguyenthinhatphuong.pu.utils.MSPEditText>(R.id.et_email)
        val et_password = findViewById<com.nguyenthinhatphuong.pu.utils.MSPEditText>(R.id.et_password)
        val et_confirm_password = findViewById<com.nguyenthinhatphuong.pu.utils.MSPEditText>(R.id.et_confirm_password)
        val cb_terms_and_condition = findViewById<CheckBox>(R.id.cb_terms_and_condition)


        return when{


            TextUtils.isEmpty(et_first_name.text.toString().trim{ it <= ' '}) ->{
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_first_name),true)
                false
            }
            TextUtils.isEmpty(et_last_name.text.toString().trim{ it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_last_name), true)
                false
            }
            TextUtils.isEmpty(et_email.text.toString().trim{ it <= ' '}) ->{
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email),true)
                false
            }
            TextUtils.isEmpty(et_password.text.toString().trim{ it <= ' '}) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                false
            }
            TextUtils.isEmpty(et_confirm_password.text.toString().trim{ it <= ' '}) ->{
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_confirm_password),true)
                false
            }
            et_password.text.toString().trim{ it <= ' '} != et_confirm_password.text.toString().trim{it <= ' ' }->{
                showErrorSnackBar(resources.getString(R.string.err_msg_password_and_confirm_password_mismatch),true)
                false
            }
            !cb_terms_and_condition.isChecked ->{
                showErrorSnackBar(resources.getString(R.string.err_msg_agree_terms_and_condition),true)
                false
            }
            else ->{
                //showErrorSnackBar(resources.getString(R.string.registery_successfull), false)
                true
            }
        }
    }

    private fun registerUser(){
        val et_first_name = findViewById<com.nguyenthinhatphuong.pu.utils.MSPEditText>(R.id.et_first_name)
        val et_last_name = findViewById<com.nguyenthinhatphuong.pu.utils.MSPEditText>(R.id.et_last_name)
        val et_email = findViewById<com.nguyenthinhatphuong.pu.utils.MSPEditText>(R.id.et_email)
        val et_password = findViewById<com.nguyenthinhatphuong.pu.utils.MSPEditText>(R.id.et_password)
        val et_confirm_password = findViewById<com.nguyenthinhatphuong.pu.utils.MSPEditText>(R.id.et_confirm_password)
        val cb_terms_and_condition = findViewById<CheckBox>(R.id.cb_terms_and_condition)
        if(validateRegisterDetails()){

            showProgressDialog(resources.getString(R.string.please_wait))
            val email: String = et_email.text.toString().trim{it <= ' '}
            val password: String = et_password.text.toString().trim{it <= ' '}

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener(
                OnCompleteListener<AuthResult>{  task ->


                    hideProgressDialog()
                    if(task.isSuccessful){
                        val firebaseUser: FirebaseUser = task.result!!.user!!
                        val user = User(

                            firebaseUser.uid,
                            et_first_name.text.toString().trim { it <= ' ' },
                            et_last_name.text.toString().trim { it <= ' ' },
                            et_email.text.toString().trim { it <= ' ' }

                        )
                        FirestoreClass().registerUser(this@RegisterActivity,user)




                        //FirebaseAuth.getInstance().signOut()
                        //finish()

                    }else{
                        hideProgressDialog()
                        showErrorSnackBar(task.exception!!.message.toString(),true)
                    }


                }
            )
        }
    }
    fun userRegistrationSuccess(){
        //Hide the progress dialog
        hideProgressDialog()

        Toast.makeText(
            this@RegisterActivity,
            resources.getString(R.string.register_success),
            Toast.LENGTH_LONG
        ).show()


    }

}