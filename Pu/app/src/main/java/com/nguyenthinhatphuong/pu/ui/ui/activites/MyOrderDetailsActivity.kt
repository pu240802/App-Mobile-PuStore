package com.nguyenthinhatphuong.pu.ui.ui.activites

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.nguyenthinhatphuong.pu.models.Order
import com.nguyenthinhatphuong.pu.R
import com.nguyenthinhatphuong.pu.ui.ui.adapters.CartItemsListAdapter
import com.nguyenthinhatphuong.pu.utils.Constants
import com.nguyenthinhatphuong.pu.utils.MSPButton
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class MyOrderDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_order_details)
        setupActionBar()


        var myOrderDetails: Order = Order()
        if (intent.hasExtra(Constants.EXTRA_MY_ORDER_DETAILS)) {
            myOrderDetails = intent.getParcelableExtra<Order>(Constants.EXTRA_MY_ORDER_DETAILS)!!
        }
        setupUI(myOrderDetails)
    }


    private fun setupActionBar() {
        val toolbar_my_order_details_activity = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_my_order_details_activity)
        setSupportActionBar(toolbar_my_order_details_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24)
        }
        toolbar_my_order_details_activity.setNavigationOnClickListener { onBackPressed() }

    }

    private fun setupUI(orderDetails: Order){
        val tv_order_details_id = findViewById<com.nguyenthinhatphuong.pu.utils.MSPTextViewBold>(R.id.tv_order_details_id)
        val tv_order_details_date = findViewById<com.nguyenthinhatphuong.pu.utils.MSPTextViewBold>(R.id.tv_order_details_date)
        val tv_order_status = findViewById<com.nguyenthinhatphuong.pu.utils.MSPTextView>(R.id.tv_order_status)
        val rv_my_order_items_list = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rv_my_order_items_list)
        val tv_my_order_details_address_type = findViewById<com.nguyenthinhatphuong.pu.utils.MSPTextView>(R.id.tv_my_order_details_address_type)
        val tv_my_order_details_full_name = findViewById<com.nguyenthinhatphuong.pu.utils.MSPTextView>(R.id.tv_my_order_details_full_name)
        val tv_my_order_details_address = findViewById<com.nguyenthinhatphuong.pu.utils.MSPTextView>(R.id.tv_my_order_details_address)
        val tv_my_order_details_additional_note = findViewById<com.nguyenthinhatphuong.pu.utils.MSPTextView>(R.id.tv_my_order_details_additional_note)
        val tv_my_order_details_other_details = findViewById<com.nguyenthinhatphuong.pu.utils.MSPTextView>(R.id.tv_my_order_details_other_details)
        val tv_my_order_details_mobile_number = findViewById<com.nguyenthinhatphuong.pu.utils.MSPTextView>(R.id.tv_my_order_details_mobile_number)
        val tv_order_details_sub_total = findViewById<com.nguyenthinhatphuong.pu.utils.MSPTextViewBold>(R.id.tv_order_details_subtotal)
        val tv_order_details_shipping_charge= findViewById<com.nguyenthinhatphuong.pu.utils.MSPTextViewBold>(R.id.tv_my_order_details_shipping_charge)
        val tv_order_details_total_amount= findViewById<com.nguyenthinhatphuong.pu.utils.MSPTextViewBold>(R.id.tv_my_order_details_total_amount)
        tv_order_details_id.text = orderDetails.title
        // Nhận thời gian đặt hàng từ Intent
        val orderDateTime = intent.getLongExtra("orderDateTime", 0)

        // Định dạng thời gian đặt hàng thành chuỗi ngày-giờ
        val formattedDateTime = SimpleDateFormat("dd/MM/yyyy hh:mm:ss", Locale.getDefault()).format(Date(orderDateTime))
        tv_order_details_date.text = formattedDateTime





        val diffInMilliSeconds: Long = System.currentTimeMillis() - orderDetails.order_datetime
        val diffInHours: Long = TimeUnit.MILLISECONDS.toHours(diffInMilliSeconds)
        Log.e("Difference in Hours", "$diffInHours")

        when{
            diffInHours < 1 ->{
                tv_order_status.text = resources.getString(R.string.order_status_pending)
                tv_order_status.setTextColor(
                    ContextCompat.getColor(
                        this@MyOrderDetailsActivity,
                        R.color.colorSnackBarError
                    )
                )

            }
            diffInHours < 2 ->{
                tv_order_status.text = resources.getString(R.string.order_status_in_process)
                tv_order_status.setTextColor(
                    ContextCompat.getColor(
                        this@MyOrderDetailsActivity,
                        R.color.colorOrderStatusInProcess
                    )
                )
            }
            else ->{
                tv_order_status.text = resources.getString(R.string.order_status_in_process)
                tv_order_status.setTextColor(
                    ContextCompat.getColor(
                        this@MyOrderDetailsActivity,
                        R.color.colorOrderStatusDelivered
                    )
                )
            }
        }

        rv_my_order_items_list.layoutManager = LinearLayoutManager(this@MyOrderDetailsActivity)
        rv_my_order_items_list.setHasFixedSize(true)

        val cartListAdapter = CartItemsListAdapter(this@MyOrderDetailsActivity,orderDetails.items, false)
        rv_my_order_items_list.adapter = cartListAdapter

        tv_my_order_details_address_type.text = orderDetails.address!!.type
        tv_my_order_details_full_name.text = orderDetails.address.name
        tv_my_order_details_address.text = "${orderDetails.address.address}, ${orderDetails.address.zipcode}"
        tv_my_order_details_additional_note.text = orderDetails.address.additionalNote


        if(orderDetails.address.otherDetails!!.isNotEmpty()){
            tv_my_order_details_other_details.visibility = View.VISIBLE
            tv_my_order_details_other_details.text = orderDetails.address.otherDetails

        }else{
            tv_my_order_details_other_details.visibility = View.GONE
        }
        tv_my_order_details_mobile_number.text = orderDetails.address.mobileNumber

        tv_order_details_sub_total.text = orderDetails.sub_total_amount
        tv_order_details_shipping_charge.text = orderDetails.shipping_charge
        tv_order_details_total_amount.text = orderDetails.total_amount
        val btnConfirmOrder = findViewById<MSPButton>(R.id.btn_confirm_order)

// Ẩn nút button
        btnConfirmOrder.visibility = View.GONE








    }


}