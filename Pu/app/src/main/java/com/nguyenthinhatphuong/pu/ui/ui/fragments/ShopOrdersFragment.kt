package com.nguyenthinhatphuong.pu.ui.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nguyenthinhatphuong.pu.R
import com.nguyenthinhatphuong.pu.databinding.FragmentShopOrdersBinding
import com.nguyenthinhatphuong.pu.firestore.FirestoreClass
import com.nguyenthinhatphuong.pu.models.Order
import com.nguyenthinhatphuong.pu.ui.ui.adapters.MyOrderListAdapter

class ShopOrdersFragment : BaseFragment() {
    private var _binding: FragmentShopOrdersBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentShopOrdersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun populateOrdersListInUI(ordersList: ArrayList<Order>) {
        val rv_shop_orders_items: RecyclerView = binding.rvShopOrdersItems
        val tv_no_shop_orders: TextView = binding.tvNoShopOrders
        hideProgressDialog()

        if (ordersList.size > 0) {
            rv_shop_orders_items.visibility = View.VISIBLE
            tv_no_shop_orders.visibility = View.GONE

            rv_shop_orders_items.layoutManager = LinearLayoutManager(activity)
            rv_shop_orders_items.setHasFixedSize(true)

            val myOrdersAdapter = MyOrderListAdapter(requireActivity(), ordersList)
            rv_shop_orders_items.adapter = myOrdersAdapter
        } else {
            rv_shop_orders_items.visibility = View.GONE
            tv_no_shop_orders.visibility = View.VISIBLE
        }
    }
    private fun getShopOrdersList(){
        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().getShopOrdersList(this@ShopOrdersFragment)
    }

    override fun onResume() {
        super.onResume()
        getShopOrdersList()
    }
}