package com.nguyenthinhatphuong.pu.ui.ui.activites

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.SyncStateContract.Constants
import android.text.TextUtils
import android.view.View
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import com.nguyenthinhatphuong.pu.R
import com.nguyenthinhatphuong.pu.firestore.FirestoreClass
import com.nguyenthinhatphuong.pu.models.Address
import org.w3c.dom.Text

class AddEditAddressActivity : BaseActivity() {

    private var mAddressDetails: Address? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_edit_address)
        setupActionBar()

        if(intent.hasExtra(com.nguyenthinhatphuong.pu.utils.Constants.EXTRA_ADDRESS_DETAILS)){
            mAddressDetails = intent.getParcelableExtra(com.nguyenthinhatphuong.pu.utils.Constants.EXTRA_ADDRESS_DETAILS)
        }

        if(mAddressDetails != null){
            if(mAddressDetails!!.id!!.isNotEmpty()){
                val tv_title = findViewById<TextView>(R.id.tv_title)
                tv_title.text = resources.getString(R.string.title_edit_address)
                val btn_submit_address = findViewById<com.nguyenthinhatphuong.pu.utils.MSPButton>(R.id.btn_submit_address)
                btn_submit_address.text = resources.getString(R.string.btn_lbl_update)
                val et_full_name = findViewById<com.nguyenthinhatphuong.pu.utils.MSPEditText>(R.id.et_full_name)
                val et_phone_number = findViewById<com.nguyenthinhatphuong.pu.utils.MSPEditText>(R.id.et_phone_number)
                val et_address = findViewById<com.nguyenthinhatphuong.pu.utils.MSPEditText>(R.id.et_address)
                val et_zip_code = findViewById<com.nguyenthinhatphuong.pu.utils.MSPEditText>(R.id.et_zip_code)
                val et_additional_note = findViewById<com.nguyenthinhatphuong.pu.utils.MSPEditText>(R.id.et_additional_note)

                et_full_name.setText(mAddressDetails?.name)
                et_phone_number.setText(mAddressDetails?.mobileNumber)
                et_address.setText(mAddressDetails?.address)
                et_zip_code.setText(mAddressDetails?.zipcode)
                et_additional_note.setText(mAddressDetails?.additionalNote)
                val rb_home = findViewById<com.nguyenthinhatphuong.pu.utils.MSPRadioButton>(R.id.rb_home)
                val rb_office = findViewById<com.nguyenthinhatphuong.pu.utils.MSPRadioButton>(R.id.rb_office)
                val rb_other = findViewById<com.nguyenthinhatphuong.pu.utils.MSPRadioButton>(R.id.rb_other)
                val et_other_detail = findViewById<com.nguyenthinhatphuong.pu.utils.MSPEditText>(R.id.et_other_details)
                val til_other_details = findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.til_other_details)
                when( mAddressDetails?. type){
                    com.nguyenthinhatphuong.pu.utils.Constants.HOME ->{

                        rb_home.isChecked = true

                    }
                    com.nguyenthinhatphuong.pu.utils.Constants.OFFICE ->{

                        rb_office.isChecked = true

                    }
                    else ->{
                        rb_other.isChecked = true
                        til_other_details.visibility = View.VISIBLE
                        et_other_detail.setText(mAddressDetails?.otherDetails)



                    }
                }


            }
        }
        val btn_submit_address = findViewById<com.nguyenthinhatphuong.pu.utils.MSPButton>(R.id.btn_submit_address)

        btn_submit_address.setOnClickListener{saveAddressToFirestore()}

        val rg_type = findViewById<RadioGroup>(R.id.rg_type)
        rg_type.setOnCheckedChangeListener {_, checkedId ->
            val til_other_details = findViewById<com.google.android.material.textfield.TextInputLayout>(R.id.til_other_details)
            if(checkedId == R.id.rb_other){


                til_other_details.visibility = View.VISIBLE

            }else{
                til_other_details.visibility = View.GONE
            }
        }
    }

    private fun setupActionBar() {
        val toolbar_add_edit_address_activity = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_add_edit_address_activity)
        setSupportActionBar(toolbar_add_edit_address_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24)
        }
        toolbar_add_edit_address_activity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun saveAddressToFirestore(){
        val et_full_name = findViewById<com.nguyenthinhatphuong.pu.utils.MSPEditText>(R.id.et_full_name)
        val et_phone_number = findViewById<com.nguyenthinhatphuong.pu.utils.MSPEditText>(R.id.et_phone_number)
        val et_address = findViewById<com.nguyenthinhatphuong.pu.utils.MSPEditText>(R.id.et_address)
        val et_zip_code = findViewById<com.nguyenthinhatphuong.pu.utils.MSPEditText>(R.id.et_zip_code)
        val rb_other = findViewById<com.nguyenthinhatphuong.pu.utils.MSPRadioButton>(R.id.rb_other)
        val rb_home = findViewById<com.nguyenthinhatphuong.pu.utils.MSPRadioButton>(R.id.rb_home)
        val rb_office = findViewById<com.nguyenthinhatphuong.pu.utils.MSPRadioButton>(R.id.rb_office)
        val et_additional_note = findViewById<com.nguyenthinhatphuong.pu.utils.MSPEditText>(R.id.et_additional_note)
        val et_other_detail = findViewById<com.nguyenthinhatphuong.pu.utils.MSPEditText>(R.id.et_other_details)
        val fullName: String = et_full_name.text.toString().trim { it <= ' ' }
        val phoneNumber: String = et_phone_number.text.toString().trim { it <= ' ' }
        val address: String = et_address.text.toString().trim { it <= ' ' }
        val zipcode: String = et_zip_code.text.toString().trim { it <= ' ' }
        val additionalNote: String = et_additional_note.text.toString().trim { it <= ' ' }
        val otherDetails: String = et_other_detail.text.toString().trim { it <= ' ' }

        if(validateData()){
            showProgressDialog(resources.getString(R.string.please_wait))

            val addressType: String = when{
                rb_home.isChecked ->{
                    com.nguyenthinhatphuong.pu.utils.Constants.HOME
                }
                rb_office.isChecked ->{
                    com.nguyenthinhatphuong.pu.utils.Constants.OFFICE
                }
                else ->{
                    com.nguyenthinhatphuong.pu.utils.Constants.OTHER
                }
            }

            val addressModel = Address(
            FirestoreClass().getCurrentUserID(),
            fullName,
            phoneNumber,
            address,
            zipcode,
            additionalNote,
            addressType,
            otherDetails
            )

            if(mAddressDetails != null && mAddressDetails!!.id!!.isNotEmpty()){
                mAddressDetails!!.id?.let { FirestoreClass().updateAddress(this, addressModel, it) }
            }else{
                FirestoreClass().addAddress(this, addressModel)
            }




        }

    }
    fun addUpdateAddressSuccess(){
        hideProgressDialog()
        val notifySuccessMessage: String = if(mAddressDetails != null && mAddressDetails!!.id!!.isNotEmpty()){
            resources.getString(R.string.msg_your_address_updated_successfully)
        }else{
            resources.getString(R.string.err_your_address_added_successfully)
        }

        Toast.makeText(
            this@AddEditAddressActivity,
           notifySuccessMessage,
            Toast.LENGTH_SHORT
        ).show()
        setResult(RESULT_OK)
        finish()
    }

    private  fun validateData(): Boolean{
        val et_full_name = findViewById<com.nguyenthinhatphuong.pu.utils.MSPEditText>(R.id.et_full_name)
        val et_phone_number = findViewById<com.nguyenthinhatphuong.pu.utils.MSPEditText>(R.id.et_phone_number)
        val et_address = findViewById<com.nguyenthinhatphuong.pu.utils.MSPEditText>(R.id.et_address)
        val et_zip_code = findViewById<com.nguyenthinhatphuong.pu.utils.MSPEditText>(R.id.et_zip_code)
        val rb_other = findViewById<com.nguyenthinhatphuong.pu.utils.MSPRadioButton>(R.id.rb_other)

        return when {

            TextUtils.isEmpty(et_full_name.text.toString().trim { it <= ' ' }) ->{
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_please_enter_full_name),true
                )
                false
            }
            TextUtils.isEmpty(et_phone_number.text.toString().trim { it <= ' ' }) ->{
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_please_enter_phone_number),true
                )
                false
            }
            TextUtils.isEmpty(et_address.text.toString().trim { it <= ' ' }) ->{
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_please_enter_address),true
                )
                false
            }
            TextUtils.isEmpty(et_zip_code.text.toString().trim { it <= ' ' }) ->{
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_please_enter_zip_code),true
                )
                false
            }
            rb_other.isChecked && TextUtils.isEmpty(
                et_zip_code.text.toString().trim{ it <= ' '})->{
                    showErrorSnackBar(resources.getString(R.string.err_msg_please_enter_other_details),true)
                false
                }
            else ->{
                    true
                }


        }
    }
}