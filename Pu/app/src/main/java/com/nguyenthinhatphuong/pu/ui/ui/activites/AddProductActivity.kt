package com.nguyenthinhatphuong.pu.ui.ui.activites

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.SyncStateContract.Constants
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.nguyenthinhatphuong.pu.R
import com.nguyenthinhatphuong.pu.firestore.FirestoreClass
import com.nguyenthinhatphuong.pu.models.Product
import com.nguyenthinhatphuong.pu.utils.GlideLoader
import java.io.IOException


class AddProductActivity : BaseActivity(), View.OnClickListener {

    private  var mSelectedImageFileURI: Uri? = null
    private var mProductImageURL: String =""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_product)
        setupActionBar()

        val iv_add_update_product = findViewById<ImageView>(R.id.iv_add_update_product)
        iv_add_update_product.setOnClickListener(this)

        val btn_submit_add_product = findViewById<com.nguyenthinhatphuong.pu.utils.MSPButton>(R.id.btn_submit_add_product)
        btn_submit_add_product.setOnClickListener(this)



    }
    private fun setupActionBar(){
        val toolbar_add_product_activity = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_add_product_activity)
        setSupportActionBar(toolbar_add_product_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24)
        }
        toolbar_add_product_activity.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onClick(v: View?) {
        if(v != null){
            when (v.id){
                R.id.iv_add_update_product ->{
                    if (ContextCompat.checkSelfPermission(
                            this,
                            android.Manifest.permission.READ_EXTERNAL_STORAGE
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        com.nguyenthinhatphuong.pu.utils.Constants.showImageChooser(this@AddProductActivity)
                    } else {
                        // Bạn chưa có quyền, yêu cầu quyền đọc lưu trữ từ người dùng.
                        ActivityCompat.requestPermissions(
                            this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                            com.nguyenthinhatphuong.pu.utils.Constants.READ_STORAGE_PERMISSION_CODE
                        )
                    }

                }

                R.id.btn_submit_add_product ->{
                    if(validateProductDetails()){
                       uploadProductImage()
                    }
                }
            }
        }
    }
    private fun uploadProductImage(){
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().uploadImageToCloudStorage(this, mSelectedImageFileURI, com.nguyenthinhatphuong.pu.utils.Constants.PRODUCT_IMAGE)

    }
    fun productUploadSuccess(){
        hideProgressDialog()
        Toast.makeText(this@AddProductActivity,
        resources.getString(R.string.product_uploaded_success_message),
        Toast.LENGTH_SHORT).show()
        finish()
    }
    fun imageUploadSuccess(imageURL: String) {
        //hideProgressDialog()
       // showErrorSnackBar("Product image is uplpaded successfully. Image URL: $imageURL",false)
        mProductImageURL = imageURL
        uploadProductDetails()


    }
    private fun uploadProductDetails(){
        val et_product_title = findViewById<com.nguyenthinhatphuong.pu.utils.MSPEditText>(R.id.et_product_title)
        val et_product_price = findViewById<com.nguyenthinhatphuong.pu.utils.MSPEditText>(R.id.et_product_price)
        val et_product_description = findViewById<com.nguyenthinhatphuong.pu.utils.MSPEditText>(R.id.et_product_description)
        val et_product_quantity = findViewById<com.nguyenthinhatphuong.pu.utils.MSPEditText>(R.id.et_product_quantity)
        val username = this.getSharedPreferences(com.nguyenthinhatphuong.pu.utils.Constants.PU_PREFERENCES,
            Context.MODE_PRIVATE).getString(com.nguyenthinhatphuong.pu.utils.Constants.LOGGED_IN_USERNAME,"")!!

        val product = Product(
            FirestoreClass().getCurrentUserID(),
            username,
            et_product_title.text.toString().trim { it <= ' ' },
            et_product_price.text.toString().trim{ it <= ' '},
            et_product_description.text.toString().trim { it <= ' ' },
            et_product_quantity.text.toString().trim { it <= ' ' },
            mProductImageURL


        )
        FirestoreClass().uploadProductDetails(this, product)

    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == com.nguyenthinhatphuong.pu.utils.Constants.READ_STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                com.nguyenthinhatphuong.pu.utils.Constants.showImageChooser(this)
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
            if (requestCode == com.nguyenthinhatphuong.pu.utils.Constants.PICK_IMAGE_REQUEST_CODE) {
                if (data != null) {
                    val iv_add_update_product = findViewById<ImageView>(R.id.iv_add_update_product)
                    val iv_product_image = findViewById<ImageView>(R.id.tv_product_image)
                    iv_add_update_product.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_vector_edit))
                    mSelectedImageFileURI = data.data!!
                    try{
                        GlideLoader(this).loadUserPicture(mSelectedImageFileURI!!, iv_product_image)
                    }catch(e: IOException){
                        e.printStackTrace()
                    }
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            Log.e("Request Cancelled", "Image selection cancelled")
        }
    }
    private fun validateProductDetails(): Boolean {
        val et_product_title = findViewById<com.nguyenthinhatphuong.pu.utils.MSPEditText>(R.id.et_product_title)
        val et_product_price = findViewById<com.nguyenthinhatphuong.pu.utils.MSPEditText>(R.id.et_product_price)
        val et_product_description = findViewById<com.nguyenthinhatphuong.pu.utils.MSPEditText>(R.id.et_product_description)
        val et_product_quantity = findViewById<com.nguyenthinhatphuong.pu.utils.MSPEditText>(R.id.et_product_quantity)

        return when {
            mSelectedImageFileURI == null -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_select_product_image), true)
                false
            }
            TextUtils.isEmpty(et_product_title.text.toString().trim{ it <= ' '})->{
                showErrorSnackBar(resources.getString(R.string.err_msg_select_product_title),true)
                false
            }
            TextUtils.isEmpty(et_product_price.text.toString().trim{ it <= ' '})->{
                showErrorSnackBar(resources.getString(R.string.err_msg_select_product_price),true)
                false
            }
            TextUtils.isEmpty(et_product_description.text.toString().trim{ it <= ' '})->{
                showErrorSnackBar(resources.getString(R.string.err_msg_select_product_description),true)
                false
            }
            TextUtils.isEmpty(et_product_quantity.text.toString().trim{ it <= ' '})->{
                showErrorSnackBar(resources.getString(R.string.err_msg_select_product_quatity),true)
                false
            }else ->{
                true
            }

        }
    }
}