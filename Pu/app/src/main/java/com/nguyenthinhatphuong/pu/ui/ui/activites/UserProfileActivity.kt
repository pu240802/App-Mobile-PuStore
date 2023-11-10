package com.nguyenthinhatphuong.pu.ui.ui.activites

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


import com.nguyenthinhatphuong.pu.R
import com.nguyenthinhatphuong.pu.firestore.FirestoreClass
import com.nguyenthinhatphuong.pu.models.User
import com.nguyenthinhatphuong.pu.utils.Constants
import com.nguyenthinhatphuong.pu.utils.GlideLoader
import java.io.IOException

class UserProfileActivity : BaseActivity(), View.OnClickListener {
    private lateinit var mUserDetails: User
    private var mSelectedImageFileUri: Uri? = null
    private var mUserProfileImageURL: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var rb_male =
            findViewById<com.nguyenthinhatphuong.pu.utils.MSPRadioButton>(R.id.rb_male)
        setContentView(R.layout.activity_user_profile)
        var rb_female =
            findViewById<com.nguyenthinhatphuong.pu.utils.MSPRadioButton>(R.id.rb_female)


        if (intent.hasExtra(Constants.EXTRA_USER_DETAILS)) {
            mUserDetails = intent.getParcelableExtra(Constants.EXTRA_USER_DETAILS)!!
        }
        val et_first_name = findViewById<com.nguyenthinhatphuong.pu.utils.MSPEditText>(R.id.et_first_name)
        val et_last_name = findViewById<com.nguyenthinhatphuong.pu.utils.MSPEditText>(R.id.et_last_name)
        val et_email = findViewById<com.nguyenthinhatphuong.pu.utils.MSPEditText>(R.id.et_email)
        et_first_name.setText(mUserDetails.firstName)
        et_last_name.setText(mUserDetails.lastName)
        et_email.isEnabled = false
        et_email.setText(mUserDetails.email)
        if(mUserDetails.profileCompleted == 0){
            val tv_title = findViewById<TextView>(R.id.tv_title)
            tv_title.text = resources.getString(R.string.title_complete_profile)
            et_first_name.isEnabled = false


            et_last_name.isEnabled = false
        }else{
            setupActionBar()
            val tv_title = findViewById<TextView>(R.id.tv_title)
            tv_title.text = resources.getString(R.string.title_edit_profile)
            val iv_user_photo = findViewById<ImageView>(R.id.iv_user_photo)
            GlideLoader(this@UserProfileActivity).loadUserPicture(mUserDetails.image,iv_user_photo)


            if(mUserDetails.mobile !=0L){
                var et_mobile_number =
                    findViewById<com.nguyenthinhatphuong.pu.utils.MSPEditText>(R.id.et_mobile_number)
                et_mobile_number.setText(mUserDetails.mobile.toString())

            }
            if(mUserDetails.gender == Constants.MALE){
                rb_male.isChecked = true
            }else{
                rb_female.isChecked = true
            }

        }






        val iv_user_photo = findViewById<ImageView>(R.id.iv_user_photo)
        val btn_submit = findViewById<com.nguyenthinhatphuong.pu.utils.MSPButton>(R.id.btn_submit)
        iv_user_photo.setOnClickListener(this@UserProfileActivity)
        btn_submit.setOnClickListener(this@UserProfileActivity)
    }
    private fun setupActionBar() {
        val toolbar_user_profile_activity = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_user_profile_activity)
        setSupportActionBar(toolbar_user_profile_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24)
        }
        toolbar_user_profile_activity.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.iv_user_photo -> {
                    if (ContextCompat.checkSelfPermission(
                            this,
                            android.Manifest.permission.READ_EXTERNAL_STORAGE
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        // Bạn đã có quyền, thực hiện công việc cần truy cập lưu trữ ở đây.
                    } else {
                        // Bạn chưa có quyền, yêu cầu quyền đọc lưu trữ từ người dùng.
                        ActivityCompat.requestPermissions(
                            this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                            Constants.READ_STORAGE_PERMISSION_CODE
                        )
                    }
                }

                R.id.btn_submit -> {
                    if (validateUserProfileDetails()) {
                        showProgressDialog(resources.getString(R.string.please_wait))

                        if(mSelectedImageFileUri != null){
                            FirestoreClass().uploadImageToCloudStorage(this, mSelectedImageFileUri, Constants.USER_PROFILE_IMAGE)
                        }else{
                            updateUserProfileDetails()
                        }
                    }
                }
            }
        }
    }
    private fun updateUserProfileDetails(){
        val userHashMap = HashMap<String, Any>()

        val et_first_name = findViewById<com.nguyenthinhatphuong.pu.utils.MSPEditText>(R.id.et_first_name)
        val et_last_name = findViewById<com.nguyenthinhatphuong.pu.utils.MSPEditText>(R.id.et_last_name)
        val et_email = findViewById<com.nguyenthinhatphuong.pu.utils.MSPEditText>(R.id.et_email)
        var et_mobile_number = findViewById<com.nguyenthinhatphuong.pu.utils.MSPEditText>(R.id.et_mobile_number)


         val firstName = et_first_name.text.toString().trim{it <= ' '}
        if(firstName != mUserDetails.firstName){
            userHashMap[Constants.FIRST_NAME] = firstName
        }


        val lastName = et_last_name.text.toString().trim{it <= ' '}
        if(lastName != mUserDetails.lastName){
            userHashMap[Constants.LAST_NAME] = lastName
        }


        val mobileNumber = et_mobile_number.text.toString().trim { it <= ' ' }

        var rb_male =
            findViewById<com.nguyenthinhatphuong.pu.utils.MSPRadioButton>(R.id.rb_male)
        val gender = if (rb_male.isChecked) {
            Constants.MALE
        } else {
            Constants.FEMALE
        }

        if(mUserProfileImageURL.isNotEmpty()){
            userHashMap[Constants.IMAGE]= mUserProfileImageURL
        }

        if (mobileNumber.isNotEmpty() && mobileNumber != mUserDetails.mobile.toString()) {
            userHashMap[Constants.MOBILE] = mobileNumber.toLong()
        }

        if(gender.isNotEmpty() && gender != mUserDetails.gender){
            userHashMap[Constants.GENDER] = gender
        }


        userHashMap[Constants.GENDER] = gender

        userHashMap[Constants.COMPLETE_PROFILE]=1

        //showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().updateUserProfileData(this, userHashMap)

    }

    fun userProfileUpdateSuccess() {
        hideProgressDialog()

        Toast.makeText(
            this@UserProfileActivity,
            resources.getString(R.string.msg_profile_update_success),
            Toast.LENGTH_SHORT
        ).show()

        startActivity(Intent(this@UserProfileActivity, DashboardActivity::class.java))
        finish()
    }


    fun validateUserProfileDetails(): Boolean {
        val et_moblie_number =
            findViewById<com.nguyenthinhatphuong.pu.utils.MSPEditText>(R.id.et_mobile_number)
        return when {
            TextUtils.isEmpty(et_moblie_number.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_enter_mobile_number),
                    true
                )
                false
            }
            else -> {
                true
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.READ_STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Constants.showImageChooser(this)
            } else {
                Toast.makeText(
                    this,
                    resources.getString(R.string.read_storage_permission_denied),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constants.PICK_IMAGE_REQUEST_CODE) {
                if (data != null) {
                    try {
                        mSelectedImageFileUri = data.data!!
                        val iv_user_photo = findViewById<ImageView>(R.id.iv_user_photo)
                        GlideLoader(this).loadUserPicture(
                            mSelectedImageFileUri!!,
                            iv_user_photo
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(
                            this@UserProfileActivity,
                            resources.getString(R.string.image_selection_failed),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.e("Request Cancelled", "Image selection cancelled")
        }
    }


    fun imageUploadSuccess(imageURL: String) {
       // hideProgressDialog()
        mUserProfileImageURL = imageURL
        updateUserProfileDetails()
    }
}
