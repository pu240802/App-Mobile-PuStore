package com.nguyenthinhatphuong.pu.ui.ui.activites

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.nguyenthinhatphuong.pu.R
import com.nguyenthinhatphuong.pu.firestore.FirestoreClass
import com.nguyenthinhatphuong.pu.models.CartItem
import com.nguyenthinhatphuong.pu.models.Product
import com.nguyenthinhatphuong.pu.utils.Constants
import com.nguyenthinhatphuong.pu.utils.GlideLoader

class ProductDetailsActivity : BaseActivity(),View.OnClickListener {

    private var mProductId: String = ""
    private lateinit var mProductDetails: Product
    private var mproductOwnerId: String = ""

    // Khai báo biến btn_add_to_cart nhưng không gán giá trị ban đầu


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_details)
        setupActionBar()


        if (intent.hasExtra(Constants.EXTRA_PRODUCT_ID)) {
            mProductId = intent.getStringExtra(Constants.EXTRA_PRODUCT_ID)!!
        }

        //var productOwnerId: String = ""
        if (intent.hasExtra(Constants.EXTRA_PRODUCT_OWNER_ID)) {
            mproductOwnerId = intent.getStringExtra(Constants.EXTRA_PRODUCT_OWNER_ID)!!
        }

        if (FirestoreClass().getCurrentUserID() == mproductOwnerId) {
            // Thiết lập tình trạng hiển thị của nút sau khi đã khởi tạo
            var btn_add_to_cart = findViewById<com.nguyenthinhatphuong.pu.utils.MSPButton>(R.id.btn_add_to_cart)
            var btn_go_to_cart = findViewById<com.nguyenthinhatphuong.pu.utils.MSPButton>(R.id.btn_go_to_cart)
            btn_add_to_cart?.visibility = View.GONE
            btn_go_to_cart?.visibility = View.GONE

        } else {
            // Thiết lập tình trạng hiển thị của nút sau khi đã khởi tạo
            var btn_add_to_cart = findViewById<com.nguyenthinhatphuong.pu.utils.MSPButton>(R.id.btn_add_to_cart)
            btn_add_to_cart?.visibility = View.VISIBLE

        }

        getProductDetails()
        var btn_add_to_cart = findViewById<com.nguyenthinhatphuong.pu.utils.MSPButton>(R.id.btn_add_to_cart)
        var btn_go_to_cart = findViewById<com.nguyenthinhatphuong.pu.utils.MSPButton>(R.id.btn_go_to_cart)
        btn_add_to_cart?.setOnClickListener(this)
        btn_go_to_cart?.setOnClickListener(this)
    }


    private  fun getProductDetails(){
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getProductDeatails(this,mProductId)
    }

    fun productExistsInCart() {
        hideProgressDialog()
        var btn_add_to_cart = findViewById<com.nguyenthinhatphuong.pu.utils.MSPButton>(R.id.btn_add_to_cart)
        var btn_go_to_cart = findViewById<com.nguyenthinhatphuong.pu.utils.MSPButton>(R.id.btn_go_to_cart)
        btn_add_to_cart.visibility = View.GONE
        btn_go_to_cart.visibility = View.VISIBLE
    }

    fun productDetailsSuccess(product: Product){
        val iv_product_detail_image = findViewById<ImageView>(R.id.iv_product_detail_image)
        val tv_product_details_title = findViewById<com.nguyenthinhatphuong.pu.utils.MSPTextViewBold>(R.id.tv_product_details_title)
        val tv_product_details_price = findViewById<com.nguyenthinhatphuong.pu.utils.MSPTextView>(R.id.tv_product_details_price)
        val tv_product_details_description = findViewById<com.nguyenthinhatphuong.pu.utils.MSPTextView>(R.id.tv_product_details_description)
        val tv_product_details_available_quantity = findViewById<com.nguyenthinhatphuong.pu.utils.MSPTextView>(R.id.tv_product_details_available_quantity)
       mProductDetails = product

        product.image?.let {
            GlideLoader(this@ProductDetailsActivity).loadProductPicture(
                it,
                iv_product_detail_image
            )
            tv_product_details_title.text= product.title
            tv_product_details_price.text = "$${product.price}"
            tv_product_details_description.text = product.description
            tv_product_details_available_quantity.text= product.stock_quantity

            if( product.stock_quantity!!.toInt() == 0){
                hideProgressDialog()
                val btn_add_to_cart = findViewById<com.nguyenthinhatphuong.pu.utils.MSPButton>(R.id.btn_add_to_cart)
                btn_add_to_cart.visibility = View.GONE

                val tv_product_details_available_quantity = findViewById<com.nguyenthinhatphuong.pu.utils.MSPTextView>(R.id.tv_product_details_available_quantity)
                tv_product_details_available_quantity.text = resources.getString(R.string.lbl_out_of_stock)

                tv_product_details_available_quantity.setTextColor(
                    ContextCompat.getColor(
                        this@ProductDetailsActivity,
                        R.color.colorSnackBarError
                    )
                )

            }else{
                if(FirestoreClass().getCurrentUserID() == product.user_id){
                    hideProgressDialog()
                }else{
                    FirestoreClass().checkIfItemExistInCart(this, mProductId)
                }
            }



        }
    }

    private fun setupActionBar(){
        val toolbar_product_details_activity = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_product_details_activity)
        setSupportActionBar(toolbar_product_details_activity)

        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24)
        }
        toolbar_product_details_activity.setNavigationOnClickListener { onBackPressed() }

    }
    private fun addToCart(){
        val cartItem = CartItem(
            FirestoreClass().getCurrentUserID(),
            mproductOwnerId,
            mProductId,
            mProductDetails.title,
            mProductDetails.price,
            mProductDetails.image,
            Constants.DEFEAULT_CART_QUANTITY
        )
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().addCartItems(this,cartItem)
    }

    fun addToCartSuccess(){
        hideProgressDialog()
        Toast.makeText(
            this@ProductDetailsActivity,
            resources.getString(R.string.success_message_item_added_to_cart),
            Toast.LENGTH_SHORT
        ).show()
        var btn_add_to_cart = findViewById<com.nguyenthinhatphuong.pu.utils.MSPButton>(R.id.btn_add_to_cart)
        var btn_go_to_cart = findViewById<com.nguyenthinhatphuong.pu.utils.MSPButton>(R.id.btn_go_to_cart)
         btn_add_to_cart?.visibility = View.GONE
        btn_go_to_cart?.visibility = View.VISIBLE
    }

        override fun onClick(v: View?) {
           if(v!= null){
               when(v.id){
               R.id.btn_add_to_cart -> {
                   addToCart()
                }
                   R.id.btn_go_to_cart ->{
                       startActivity(Intent(this@ProductDetailsActivity, CartListActivity::class.java))
                   }
           }

       }
    }
}