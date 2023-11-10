package com.nguyenthinhatphuong.pu.ui.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nguyenthinhatphuong.pu.R
import com.nguyenthinhatphuong.pu.databinding.FragmentDashboardBinding
import com.nguyenthinhatphuong.pu.firestore.FirestoreClass
import com.nguyenthinhatphuong.pu.models.Product
import com.nguyenthinhatphuong.pu.ui.ui.activites.CartListActivity
import com.nguyenthinhatphuong.pu.ui.ui.activites.ProductDetailsActivity
import com.nguyenthinhatphuong.pu.ui.ui.activites.SettingsActivity
import com.nguyenthinhatphuong.pu.ui.ui.adapters.DashboardItemsListAdapter
import com.nguyenthinhatphuong.pu.utils.Constants

class DashboardFragment : BaseFragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private var isSearchVisible: Boolean = false
    private var searchKeyword: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onResume() {
        super.onResume()
        getDashboardItemsList()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.dashboard_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)

        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                searchKeyword = query.trim()
                getDashboardItemsList()
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                searchKeyword = newText.trim()
                getDashboardItemsList()
                return true
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(activity, SettingsActivity::class.java))
                return true
            }
            R.id.action_cart -> {
                startActivity(Intent(activity, CartListActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun successDashboardItemsList(dashboardItemsList: ArrayList<Product>) {
        val rvDashboardItems: RecyclerView = binding.rvDashboardItems

        hideProgressDialog()

        if (dashboardItemsList.size > 0) {
            rvDashboardItems.visibility = View.VISIBLE
            binding.tvNoDashboardItemsFound.visibility = View.GONE

            rvDashboardItems.layoutManager = GridLayoutManager(activity, 2)
            rvDashboardItems.setHasFixedSize(true)

            val filteredItemsList = if (searchKeyword.isNotEmpty()) {
                filterItemsByKeyword(dashboardItemsList, searchKeyword)
            } else {
                dashboardItemsList
            }

            val adapter = DashboardItemsListAdapter(requireActivity(), filteredItemsList)
            rvDashboardItems.adapter = adapter
        } else {
            rvDashboardItems.visibility = View.GONE
            binding.tvNoDashboardItemsFound.visibility = View.VISIBLE
        }
    }

    private fun getDashboardItemsList() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getDashboardItemsList(this@DashboardFragment)
    }

    private fun filterItemsByKeyword(itemsList: ArrayList<Product>, keyword: String): ArrayList<Product> {
        val filteredList = ArrayList<Product>()

        for (item in itemsList) {
            if (item.title!!.contains(keyword, ignoreCase = true)) {
                filteredList.add(item)
            }
        }

        return filteredList
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}