package com.nguyenthinhatphuong.pu.ui.ui.activites

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.nguyenthinhatphuong.pu.R
import com.nguyenthinhatphuong.pu.models.SoldProduct
import com.nguyenthinhatphuong.pu.ui.ui.adapters.CartItemsListAdapter
import com.nguyenthinhatphuong.pu.utils.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class SoldProductDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sold_product_details)

        var productDetails: SoldProduct = SoldProduct()
        if (intent.hasExtra(Constants.EXTRA_SOLD_PRODUCT_DETAILS)) {
            productDetails =
                intent.getParcelableExtra<SoldProduct>(Constants.EXTRA_SOLD_PRODUCT_DETAILS)!!
        }

        setupActionBar()
        setupUI(productDetails)
    }

    private fun setupActionBar() {
        val toolbar_sold_product_details_activity =
            findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_sold_product_details_activity)
        setSupportActionBar(toolbar_sold_product_details_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24)
        }
        toolbar_sold_product_details_activity.setNavigationOnClickListener { onBackPressed() }
    }

    private fun setupUI(productDetails: SoldProduct) {



        val tv_sold_product_details_id = findViewById<MSPTextViewBold>(R.id.tv_sold_product_details_id)
        val tv_sold_product_details_date = findViewById<MSPTextViewBold>(R.id.tv_sold_product_details_date)
        val iv_product_item_image = findViewById<ImageView>(R.id.iv_product_item_image)
        val tv_product_item_name = findViewById<MSPTextViewBold>(R.id.tv_product_item_name)
        val tv_product_item_price = findViewById<MSPTextViewBold>(R.id.tv_product_item_price)
        val tv_sold_product_quantity = findViewById<TextView>(R.id.tv_sold_product_quantity)
        val tv_sold_product_details_address_type = findViewById<MSPTextView>(R.id.tv_sold_product_details_address_type)
        val tv_sold_product_details_full_name = findViewById<MSPTextView>(R.id.tv_sold_product_details_full_name)
        val tv_sold_product_details_address = findViewById<MSPTextView>(R.id.tv_sold_product_details_address)
        val tv_sold_product_details_additional_note = findViewById<MSPTextView>(R.id.tv_sold_product_details_additional_note)
        val tv_sold_product_details_other_details = findViewById<MSPTextView>(R.id.tv_sold_product_details_other_details)
        val tv_sold_product_details_mobile_number = findViewById<MSPTextView>(R.id.tv_sold_product_details_mobile_number)
        val tv_sold_product_details_sub_total = findViewById<MSPTextViewBold>(R.id.tv_sold_product_details_subtotal)
        val tv_sold_product_details_shipping_charge = findViewById<MSPTextViewBold>(R.id.tv_sold_product_details_shipping_charge)
        val tv_sold_product_details_total_amount = findViewById<MSPTextViewBold>(R.id.tv_sold_product_details_total_amount)
        if (productDetails.address != null) {
            tv_sold_product_details_address_type.text = productDetails.address.type
            tv_sold_product_details_full_name.text = productDetails.address.name
            tv_sold_product_details_address.text =
                "${productDetails.address.address}, ${productDetails.address.zipcode}"
            tv_sold_product_details_additional_note.text = productDetails.address.additionalNote

            if (productDetails.address.otherDetails != null && productDetails.address.otherDetails.isNotEmpty()) {
                tv_sold_product_details_other_details.visibility = View.VISIBLE
                tv_sold_product_details_other_details.text = productDetails.address.otherDetails
            } else {
                tv_sold_product_details_other_details.visibility = View.GONE
            }
            tv_sold_product_details_mobile_number.text = productDetails.address.mobileNumber
        }

        tv_sold_product_details_id.text = productDetails.order_id
        val dateFormat = "dd MMM yyyy HH:mm"
        val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = productDetails.order_date
        tv_sold_product_details_date.text = formatter.format(calendar.time)

        productDetails.image?.let {
            GlideLoader(this@SoldProductDetailsActivity).loadProductPicture(
                it,
                iv_product_item_image
            )
        }
        tv_product_item_name.text = productDetails.title
        tv_product_item_price.text = "$${productDetails.price}"
        tv_sold_product_quantity.text = productDetails.sold_quantity




        tv_sold_product_details_sub_total.text = productDetails.sub_total_amount
        tv_sold_product_details_shipping_charge.text = productDetails.shipping_charge
        tv_sold_product_details_total_amount.text = productDetails.total_amount




    }
}