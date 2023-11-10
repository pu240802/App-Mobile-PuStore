package com.nguyenthinhatphuong.pu.ui.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nguyenthinhatphuong.pu.R
import com.nguyenthinhatphuong.pu.databinding.FragmentSoldProductsBinding
import com.nguyenthinhatphuong.pu.firestore.FirestoreClass
import com.nguyenthinhatphuong.pu.models.SoldProduct
import com.nguyenthinhatphuong.pu.ui.ui.adapters.SoldProductsAdapter

class SoldProductsFragment : BaseFragment() {

    private var _binding: FragmentSoldProductsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSoldProductsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        getSoldProductsList()
    }

    private fun getSoldProductsList() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getSoldProductsList(this@SoldProductsFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun successSoldProductsList(soldProductsList: ArrayList<SoldProduct>) {
        hideProgressDialog()
        val rvSoldProductItems: RecyclerView = binding.rvSoldProductItems
        val tvNoSoldProductsFound: TextView = binding.tvNoSoldProductFound

        if (soldProductsList.size > 0) {
            rvSoldProductItems.visibility = View.VISIBLE
            tvNoSoldProductsFound.visibility = View.GONE

            rvSoldProductItems.layoutManager = LinearLayoutManager(activity)
            rvSoldProductItems.setHasFixedSize(true)

            val soldProductsListAdapter = SoldProductsAdapter(requireActivity(), soldProductsList)
            rvSoldProductItems.adapter = soldProductsListAdapter
        } else {
            rvSoldProductItems.visibility = View.GONE
            tvNoSoldProductsFound.visibility = View.VISIBLE
        }
    }
}