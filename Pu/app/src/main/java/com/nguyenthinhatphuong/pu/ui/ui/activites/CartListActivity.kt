package com.nguyenthinhatphuong.pu.ui.ui.activites

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.nguyenthinhatphuong.pu.R
import com.nguyenthinhatphuong.pu.firestore.FirestoreClass
import com.nguyenthinhatphuong.pu.models.CartItem
import com.nguyenthinhatphuong.pu.models.Product
import com.nguyenthinhatphuong.pu.ui.ui.adapters.CartItemsListAdapter
import com.nguyenthinhatphuong.pu.utils.Constants

class CartListActivity : BaseActivity() {

    private lateinit var mProductsList :ArrayList<Product>
    private lateinit var mCartListItems: ArrayList<CartItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart_list)
        setupActionBar()

        val btn_checkout = findViewById<com.nguyenthinhatphuong.pu.utils.MSPButton>(R.id.btn_checkout)
        btn_checkout.setOnClickListener {
            val intent = Intent(this@CartListActivity, AddressListActivity:: class.java)
            intent.putExtra(Constants.EXTRA_SELECT_ADDRESS, true)
            startActivity(intent)

        }

    }

    fun successCartItemsList(cartList: ArrayList<CartItem>){
        hideProgressDialog()

        for (product in mProductsList){
            for(cartItem in cartList){
                if(product.product_id == cartItem.product_id){
                    cartItem.stock_quantity = product.stock_quantity

                    if(product.stock_quantity!!.toInt() == 0){
                        cartItem.cart_quantity = product.stock_quantity
                    }
                }
            }
        }
        mCartListItems = cartList
        val rv_cart_items_list = findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.rv_cart_items_list)
        val ll_checkout = findViewById<LinearLayout>(R.id.ll_checkout)
        val tv_no_cart_item_found = findViewById<com.nguyenthinhatphuong.pu.utils.MSPTextView>(R.id.tv_no_cart_item_found)
        if(mCartListItems.size> 0){

            rv_cart_items_list.visibility = View.VISIBLE
            ll_checkout.visibility = View.VISIBLE
            tv_no_cart_item_found.visibility = View.GONE

            rv_cart_items_list.layoutManager = LinearLayoutManager(this@CartListActivity)
            rv_cart_items_list.setHasFixedSize(true)
            val cartListAdapter = CartItemsListAdapter(this@CartListActivity, mCartListItems,true)
            rv_cart_items_list.adapter = cartListAdapter
            var subToTal: Double = 0.0
            for(item in mCartListItems){

                val availableQuantity = item.stock_quantity?.toInt()
                if (availableQuantity != null) {
                    if(availableQuantity > 0 ){
                        val price = item.price?.toDouble()
                        val quantity = item.cart_quantity?.toInt()
                        subToTal +=(price?.times(quantity!!)!!)
                    }
                }

            }
            val tv_sub_toal = findViewById<com.nguyenthinhatphuong.pu.utils.MSPTextView>(R.id.tv_sub_total)
            val tv_shipping_charge = findViewById<com.nguyenthinhatphuong.pu.utils.MSPTextView>(R.id.tv_shipping_charge)
            tv_sub_toal.text ="$$subToTal"
            tv_shipping_charge.text="$10.0"

            if (subToTal >0){
                ll_checkout.visibility = View.VISIBLE

                val total = subToTal + 10
                val tv_total_amount = findViewById<com.nguyenthinhatphuong.pu.utils.MSPTextViewBold>(R.id.tv_total_amount)
                tv_total_amount.text ="$$total"
            }else{
                ll_checkout.visibility = View.GONE
            }


        }
        else{
            rv_cart_items_list.visibility = View.GONE
            ll_checkout.visibility = View.GONE
            tv_no_cart_item_found.visibility = View.VISIBLE
        }
    }

    fun successProductsListFromFireStore(productsList: ArrayList<Product>){
        hideProgressDialog()
        mProductsList = productsList
        getCartItemsList()


    }

    private fun getProductList(){
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getAllProductsList(this)
    }

    private fun getCartItemsList(){
        //showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getCartList(this@CartListActivity)
    }

    fun itemUpdateSuccess(){
        hideProgressDialog()
        getCartItemsList()
    }

    override fun onResume() {
        super.onResume()
        //getCartItemsList()
        getProductList()
    }


    fun itemRemoveSuccess(){
        hideProgressDialog()
        Toast.makeText(
            this@CartListActivity,
            resources.getString(R.string.msg_item_removed_successfully),
            Toast.LENGTH_SHORT
        ).show()

        getCartItemsList()
    }
    private fun setupActionBar(){
        val toolbar_cart_list_activity = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar_cart_list_activity)
        setSupportActionBar(toolbar_cart_list_activity)

        val actionBar = supportActionBar
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24)
        }
       toolbar_cart_list_activity.setNavigationOnClickListener { onBackPressed() }

    }
}