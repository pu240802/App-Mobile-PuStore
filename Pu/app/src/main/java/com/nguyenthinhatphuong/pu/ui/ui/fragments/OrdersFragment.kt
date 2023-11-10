package com.nguyenthinhatphuong.pu.ui.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nguyenthinhatphuong.pu.R

import com.nguyenthinhatphuong.pu.databinding.FragmentOrdersBinding
import com.nguyenthinhatphuong.pu.firestore.FirestoreClass
import com.nguyenthinhatphuong.pu.models.Order
import com.nguyenthinhatphuong.pu.ui.ui.adapters.MyOrderListAdapter

class OrdersFragment : BaseFragment() {
    private var _binding: FragmentOrdersBinding? = null

    // This property is only valid between onCreateView and
// onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // val notificationsViewModel =
        // ViewModelProvider(this).get(NotificationsViewModel::class.java)

        _binding = FragmentOrdersBinding.inflate(inflater, container, false)
        val root: View = binding.root



        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun populateOrdersListInUI(ordersList: ArrayList<Order>){
        val rv_my_order_items: RecyclerView = binding.rvMyOrderItems
        val tv_no_orders_found: TextView = binding.tvNoOrdersFound
        hideProgressDialog()

        if(ordersList.size > 0){
            rv_my_order_items.visibility = View.VISIBLE
            tv_no_orders_found.visibility = View.GONE

            rv_my_order_items.layoutManager = LinearLayoutManager(activity)
            rv_my_order_items.setHasFixedSize(true)

            val myOrdersAdapter = MyOrderListAdapter(requireActivity(), ordersList)
            rv_my_order_items.adapter = myOrdersAdapter


        }
        else{
            rv_my_order_items.visibility = View.GONE
            tv_no_orders_found.visibility = View.VISIBLE
        }

    }



    private fun getMyOrdersList(){
        showProgressDialog(resources.getString(R.string.please_wait))

        FirestoreClass().getMyOrdersList(this@OrdersFragment)
    }

    override fun onResume() {
        super.onResume()
        getMyOrdersList()
    }
}

