package com.nguyenthinhatphuong.pu.ui.ui.activites

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.nguyenthinhatphuong.pu.R
import com.nguyenthinhatphuong.pu.firestore.FirestoreClass
import com.nguyenthinhatphuong.pu.models.Address
import com.nguyenthinhatphuong.pu.ui.ui.adapters.AddressListAdapter
import com.nguyenthinhatphuong.pu.utils.Constants
import com.nguyenthinhatphuong.pu.utils.SwipeToDeleteCallback

class AddressListActivity : BaseActivity() {
    private var mSelectAddress: Boolean  = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_list)

        setupActionBar()


        val tv_title = findViewById<TextView>(R.id.tv_title)
        val tv_add_address = findViewById<com.nguyenthinhatphuong.pu.utils.MSPTextView>(R.id.tv_add_address)
        tv_add_address.setOnClickListener{
            val intent = Intent(this@AddressListActivity, AddEditAddressActivity::class.java)
            startActivityForResult(intent, Constants.ADD_ADDRESS_REQUEST_CODE)
        }
        getAddressList()

        if(intent.hasExtra(Constants.EXTRA_SELECT_ADDRESS)){
            mSelectAddress = intent.getBooleanExtra(Constants.EXTRA_SELECT_ADDRESS, false)
        }

        if( mSelectAddress){
            tv_title.text = resources.getString(R.string.title_select_address)

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK){
            getAddressList()

        }
    }



    private  fun getAddressList(){
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getAddressesList(this)
    }

    fun successAddressListFromFirestore(addressList: ArrayList<Address>){
        hideProgressDialog()
        val rv_address_list = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rv_address_list)
        val tv_no_address_found = findViewById<com.nguyenthinhatphuong.pu.utils.MSPTextView>(R.id.tv_no_address_found)

        if(addressList.size > 0){
            val rv_address_list = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rv_address_list)
            rv_address_list.visibility = View.VISIBLE
            tv_no_address_found.visibility = View.GONE
            rv_address_list.layoutManager = LinearLayoutManager(this@AddressListActivity)
            rv_address_list.setHasFixedSize(true)

            val addressAdapter = AddressListAdapter(this, addressList,  mSelectAddress)
           rv_address_list.adapter = addressAdapter


            if(! mSelectAddress){
                val editSwipeHandler = object: SwipeToEidtCallback(this){
                    override fun onSwiped(viewHolder: ViewHolder, direction: Int) {
                        val adapter = rv_address_list.adapter as AddressListAdapter
                        adapter.notifyEditItem(
                            this@AddressListActivity,
                            viewHolder.adapterPosition
                        )
                    }

                }



                val editItemTouchHelper = ItemTouchHelper(editSwipeHandler)
                editItemTouchHelper.attachToRecyclerView(rv_address_list)

                val deleteSwipeHandler = object: SwipeToDeleteCallback(this){
                    override fun onSwiped(viewHolder: ViewHolder, direction: Int) {
                        showProgressDialog(resources.getString(R.string.please_wait))


                        addressList[viewHolder.adapterPosition].id?.let {
                            FirestoreClass().deleteAddress(this@AddressListActivity,
                                it
                            )
                        }
                    }
                }

                val deleteItemTouchHelper = ItemTouchHelper(deleteSwipeHandler)
                deleteItemTouchHelper.attachToRecyclerView(rv_address_list)
            }



        }else{

            rv_address_list.visibility = View.GONE
            tv_no_address_found.visibility = View.VISIBLE
        }

    }
    fun deleteAddressSuccess(){
        hideProgressDialog()
        Toast.makeText(
            this@AddressListActivity,
            resources.getString(R.string.err_your_address_added_successfully),
            Toast.LENGTH_SHORT
        ).show()

        getAddressList()
    }
    private fun setupActionBar() {
        val toolbar_address_list_activity = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_address_list_activity)
        setSupportActionBar(toolbar_address_list_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24)
        }
        toolbar_address_list_activity.setNavigationOnClickListener { onBackPressed() }
    }
}