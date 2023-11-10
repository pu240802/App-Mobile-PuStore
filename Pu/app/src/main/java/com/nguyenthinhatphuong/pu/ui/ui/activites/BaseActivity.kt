package com.nguyenthinhatphuong.pu.ui.ui.activites

import android.app.Dialog
import android.app.ProgressDialog
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.nguyenthinhatphuong.pu.R
import com.nguyenthinhatphuong.pu.utils.MSPTextView


open class BaseActivity : AppCompatActivity() {

    private var doubleBackToExitPressedOnce = false
    private lateinit var mProgressDialog: Dialog

    fun showErrorSnackBar(message: String, errorMessage: Boolean){
        val snackbar =
            Snackbar.make(findViewById(android.R.id.content),message, Snackbar.LENGTH_LONG)
        val snackBarView = snackbar.view

        if(errorMessage){
            snackBarView.setBackgroundColor(ContextCompat.getColor(this@BaseActivity,R.color.colorSnackBarError))
        }else{
            snackBarView.setBackgroundColor(ContextCompat.getColor(this@BaseActivity,R.color.colorSnackBarSuccess))
        }
        snackbar.show()
    }

    fun showProgressDialog(text: String){

        mProgressDialog = Dialog(this)
        mProgressDialog.setContentView(R.layout.dialog_progress)
        val tv_progress_text = mProgressDialog.findViewById<MSPTextView>(R.id.tv_progress_text)
        tv_progress_text.text = text
        mProgressDialog.setCancelable(false)
        mProgressDialog.setCanceledOnTouchOutside(false)
        mProgressDialog.show()
    }
    fun hideProgressDialog(){
        mProgressDialog.dismiss()
    }

    fun doubleBackToExit(){
        if(doubleBackToExitPressedOnce){
            super.onBackPressed()
            return
        }
        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this,
        resources.getString(R.string.please_click_back_again_to_exit),
        Toast.LENGTH_SHORT).show()
        @Suppress("DEPRECATION")

        Handler().postDelayed({ doubleBackToExitPressedOnce = false },2000)
    }
}