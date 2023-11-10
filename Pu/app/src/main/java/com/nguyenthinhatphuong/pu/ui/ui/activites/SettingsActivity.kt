package com.nguyenthinhatphuong.pu.ui.ui.activites


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import com.google.firebase.auth.FirebaseAuth
import com.nguyenthinhatphuong.pu.R
import com.nguyenthinhatphuong.pu.firestore.FirestoreClass
import com.nguyenthinhatphuong.pu.models.User
import com.nguyenthinhatphuong.pu.utils.Constants
import com.nguyenthinhatphuong.pu.utils.GlideLoader

class SettingsActivity : BaseActivity(),View.OnClickListener {
    private lateinit var mUserDetails: User
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setupActionBar()
      val btn_logout = findViewById<com.nguyenthinhatphuong.pu.utils.MSPButton>(R.id.btn_logout)
       val tv_edit = findViewById<com.nguyenthinhatphuong.pu.utils.MSPTextView>(R.id.tv_edit)
        val ll_address = findViewById<LinearLayout>(R.id.ll_address)
        tv_edit.setOnClickListener(this)
        btn_logout.setOnClickListener(this)
        ll_address.setOnClickListener(this)



    }

    private fun setupActionBar() {
        val toolbar_settings_activity = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_settings_activity)
        setSupportActionBar(toolbar_settings_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24)
        }
        toolbar_settings_activity.setNavigationOnClickListener { onBackPressed() }
    }
    private fun getUserDetails(){
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getUserDetails(this)
    }

    fun userDetailsSuccess(user: User) {
       mUserDetails = user
        hideProgressDialog()
        val iv_user_photo = findViewById<ImageView>(R.id.iv_user_photo)
        val tv_name = findViewById<com.nguyenthinhatphuong.pu.utils.MSPTextViewBold>(R.id.tv_name)
        val tv_gender = findViewById<com.nguyenthinhatphuong.pu.utils.MSPTextView>(R.id.tv_gender)
        val tv_email = findViewById<com.nguyenthinhatphuong.pu.utils.MSPTextView>(R.id.tv_email)
        val tv_mobile_number = findViewById<com.nguyenthinhatphuong.pu.utils.MSPTextView>(R.id.tv_mobile_number)




        GlideLoader(this@SettingsActivity).loadUserPicture(user.image,iv_user_photo)
        tv_name.text = "${user.firstName} ${user.lastName}"
        tv_gender.text = user.gender
        tv_email.text = user.email
        tv_mobile_number.text = "${user.mobile}"
    }

    override fun onResume() {
        super.onResume()
        getUserDetails()
}

    override fun onClick(v: View?) {
        if(v != null){
            when (v.id){

                R.id.tv_edit ->{
                    val intent = Intent(this@SettingsActivity, UserProfileActivity::class.java)
                    intent.putExtra(Constants.EXTRA_USER_DETAILS, mUserDetails)
                    startActivity(intent)
                }
                R.id.btn_logout -> {
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(this@SettingsActivity,LoginActivity::class.java)
                    intent.flags=  Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }

                R.id.ll_address ->{
                    val intent = Intent(this@SettingsActivity, AddressListActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }
}


