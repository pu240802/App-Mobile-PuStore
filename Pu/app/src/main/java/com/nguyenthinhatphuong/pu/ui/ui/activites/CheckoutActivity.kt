package com.nguyenthinhatphuong.pu.ui.ui.activites

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.nguyenthinhatphuong.pu.R
import com.nguyenthinhatphuong.pu.firestore.FirestoreClass
import com.nguyenthinhatphuong.pu.models.Address
import com.nguyenthinhatphuong.pu.models.CartItem
import com.nguyenthinhatphuong.pu.models.Order
import com.nguyenthinhatphuong.pu.models.Product
import com.nguyenthinhatphuong.pu.ui.ui.adapters.CartItemsListAdapter
import com.nguyenthinhatphuong.pu.utils.Constants

class CheckoutActivity : BaseActivity() {


    private var mAddressDetails: Address? = null
    private lateinit var mProductsList: ArrayList<Product>
    private lateinit var mCartItemsList: ArrayList<CartItem>
    private  var mSubTotal: Double =0.0
    private var mTotalAmount: Double = 0.0
    private lateinit var mOrderDetails: Order


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        setupActionBar()

        val tv_checkout_address_type =findViewById<com.nguyenthinhatphuong.pu.utils.MSPTextView>(R.id.tv_checkout_address_type)
        val tv_checkout_full_name = findViewById<com.nguyenthinhatphuong.pu.utils.MSPTextView>(R.id.tv_checkout_full_name)
        val tv_checkout_address = findViewById<com.nguyenthinhatphuong.pu.utils.MSPTextView>(R.id.tv_checkout_address)
        val tv_checkout_additional_note = findViewById<com.nguyenthinhatphuong.pu.utils.MSPTextView>(R.id.tv_checkout_additional_note)
        val tv_checkout_other_details = findViewById<com.nguyenthinhatphuong.pu.utils.MSPTextView>(R.id.tv_checkout_other_details)
        val tv_mobile_number = findViewById<com.nguyenthinhatphuong.pu.utils.MSPTextView>(R.id.tv_mobile_number)



        if(intent.hasExtra(Constants.EXTRA_SELECTED_ADDRESS)){
            mAddressDetails = intent.getParcelableExtra<Address>(Constants.EXTRA_SELECTED_ADDRESS)
        }
        if(mAddressDetails != null){
            tv_checkout_address_type.text = mAddressDetails?.type
            tv_checkout_full_name.text = mAddressDetails?.name
            tv_checkout_address.text = "${mAddressDetails!!.address},${mAddressDetails!!.zipcode}"
            tv_checkout_additional_note.text = mAddressDetails?.additionalNote

            if(mAddressDetails?.otherDetails!!.isNotEmpty()){
                tv_checkout_other_details.text = mAddressDetails?.otherDetails
            }
            tv_mobile_number.text = mAddressDetails?.mobileNumber



        }
        getProductList()


        val btn_place_order = findViewById<com.nguyenthinhatphuong.pu.utils.MSPButton>(R.id.btn_place_order)
        btn_place_order.setOnClickListener {
            placeAnOrder()
        }


    }


    fun allDetailsUpdatedSuccessfully(){
        hideProgressDialog()
        Toast.makeText(this@CheckoutActivity,"Your order was placed successfully.", Toast.LENGTH_SHORT)
            .show()
        val intent = Intent(this@CheckoutActivity,DashboardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    fun orderPlaceSuccess(){
        FirestoreClass().updateAllDetails(this, mCartItemsList, mOrderDetails)

    }
    fun successProductsListFromFireStore(productsList: ArrayList<Product>){
        mProductsList = productsList
        getCartItemsList()
    }
    private fun getCartItemsList(){
        FirestoreClass().getCartList(this@CheckoutActivity)
    }


    private fun placeAnOrder() {
        showProgressDialog(resources.getString(R.string.please_wait))
        if (mAddressDetails != null) {
            // Lấy thời gian đặt hàng
            val orderDateTime = System.currentTimeMillis()

             mOrderDetails = Order(
                FirestoreClass().getCurrentUserID(),
                mCartItemsList,
                mAddressDetails!!,
                "My order$orderDateTime",
                mCartItemsList[0].image,
                mSubTotal.toString(),
                "10.0",
                mTotalAmount.toString(),
                orderDateTime
            )

            FirestoreClass().placeOrder(this, mOrderDetails)

            // Chuyển sang trang My Order Details và truyền thời gian đặt hàng
            val intent = Intent(this, MyOrderDetailsActivity::class.java)
            intent.putExtra("orderDateTime", orderDateTime)
            startActivity(intent)
        }
    }
    fun successCartItemsList(cartList: ArrayList<CartItem>){
        val tv_checkout_subtotal = findViewById<com.nguyenthinhatphuong.pu.utils.MSPTextViewBold>(R.id.tv_checkout_subtotal)
        val tv_checkout_shipping_charge = findViewById<com.nguyenthinhatphuong.pu.utils.MSPTextViewBold>(R.id.tv_checkout_shipping_charge)
        val ll_checkout_place_order = findViewById<LinearLayout>(R.id.ll_checkout_place_order)
        val tv_checkout_total_amount = findViewById<com.nguyenthinhatphuong.pu.utils.MSPTextViewBold>(R.id.tv_checkout_total_amount)
        hideProgressDialog()

        for( product in mProductsList){
            for (cartItem in cartList){
                if(product.product_id == cartItem.product_id){
                    cartItem.stock_quantity = product.stock_quantity
                }
            }
        }


        mCartItemsList = cartList

        val rv_cart_list_items = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rv_cart_list_items)
        rv_cart_list_items.layoutManager = LinearLayoutManager(this@CheckoutActivity)
        rv_cart_list_items.setHasFixedSize(true)

        val cartListAdapter = CartItemsListAdapter(this@CheckoutActivity, mCartItemsList, false)
        rv_cart_list_items.adapter = cartListAdapter

        for (item in mCartItemsList){
            val availableQuantity = item.stock_quantity!!.toInt()
            if(availableQuantity >0 ){
                val price = item.price!!.toDouble()
                val quantity = item.cart_quantity!!.toInt()
                mSubTotal += (price * quantity)

            }
        }

        tv_checkout_subtotal.text = "$$mSubTotal"
        tv_checkout_shipping_charge.text = "$10.0"

        if(mSubTotal > 0){

            ll_checkout_place_order.visibility = View.VISIBLE
            mTotalAmount = mSubTotal + 10.0
            tv_checkout_total_amount.text = "$$mTotalAmount"

        }else{
            ll_checkout_place_order.visibility= View.GONE
        }

    }

    private fun getProductList(){
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getAllProductsList(this@CheckoutActivity)
    }
    private fun setupActionBar() {
        val toolbar_checkout_activity = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_checkout_activity)
        setSupportActionBar(toolbar_checkout_activity)

        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24)
        }
        toolbar_checkout_activity.setNavigationOnClickListener { onBackPressed() }
    }
}