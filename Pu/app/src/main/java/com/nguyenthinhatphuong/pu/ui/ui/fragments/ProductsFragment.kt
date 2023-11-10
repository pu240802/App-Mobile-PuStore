package com.nguyenthinhatphuong.pu.ui.ui.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nguyenthinhatphuong.pu.R
import com.nguyenthinhatphuong.pu.databinding.FragmentProductsBinding
import com.nguyenthinhatphuong.pu.firestore.FirestoreClass
import com.nguyenthinhatphuong.pu.models.Product
import com.nguyenthinhatphuong.pu.ui.ui.activites.AddProductActivity
import com.nguyenthinhatphuong.pu.ui.ui.adapters.MyProductsListAdapter

class ProductsFragment : BaseFragment() {

    private var _binding: FragmentProductsBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }
    fun deleteProduct(productID: String){
        showAlertDialogToDeleteProduct(productID)
    }
    private fun showAlertDialogToDeleteProduct(productID: String){
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(resources.getString(R.string.delete_dialog_title))
        builder.setMessage(resources.getString(R.string.delete_dialog_message))
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        builder.setPositiveButton(resources.getString(R.string.yes)){
            dialogInterface,_ ->
            showProgressDialog(resources.getString(R.string.please_wait))

            FirestoreClass().deleteProduct(this, productID)

            dialogInterface.dismiss()
        }
        builder.setNegativeButton(resources.getString(R.string.no)){
                dialogInterface,_ ->

            dialogInterface.dismiss()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }
    fun productDeleteSuccess() {
        hideProgressDialog()
        Toast.makeText(
            requireActivity(),
            resources.getString(R.string.product_delete_success_message),
            Toast.LENGTH_SHORT

        ).show()
        getProductListFromFireStore()


    }




    fun successProductsListFromFireStore(productsList: ArrayList<Product>) {
        hideProgressDialog()
        val rv_my_product_items: RecyclerView = binding.rvMyProductItems
        val tv_no_products_found: TextView = binding.tvNoProductsFound

        if (productsList.size > 0) {
            rv_my_product_items.visibility = View.VISIBLE
            tv_no_products_found.visibility = View.GONE

            rv_my_product_items.layoutManager = LinearLayoutManager(activity)
            rv_my_product_items.setHasFixedSize(true)

            // Kiểm tra nếu người dùng là quản trị viên
            val isAdmin = activity?.intent?.getBooleanExtra(com.nguyenthinhatphuong.pu.utils.Constants.EXTRA_IS_ADMIN, false) ?: false

            val adapterProduct = MyProductsListAdapter(requireContext(), productsList, isAdmin, this)
            rv_my_product_items.adapter = adapterProduct
        } else {
            rv_my_product_items.visibility = View.GONE
            tv_no_products_found.visibility = View.VISIBLE
        }
    }

    private fun getProductListFromFireStore() {
        showProgressDialog(resources.getString(R.string.please_wait))
        FirestoreClass().getProductsList(this)
    }

    override fun onResume() {
        super.onResume()
        getProductListFromFireStore()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_product_menu, menu)
        val isAdmin = activity?.intent?.getBooleanExtra(com.nguyenthinhatphuong.pu.utils.Constants.EXTRA_IS_ADMIN, false) ?: false
        val addProductMenuItem = menu.findItem(R.id.action_add_product)
        addProductMenuItem?.isVisible = isAdmin
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            R.id.action_add_product -> {
                startActivity(Intent(activity, AddProductActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
