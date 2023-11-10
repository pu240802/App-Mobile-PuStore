package com.nguyenthinhatphuong.pu.ui.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.nguyenthinhatphuong.pu.R
import com.nguyenthinhatphuong.pu.models.Product
import com.nguyenthinhatphuong.pu.ui.ui.activites.ProductDetailsActivity
import com.nguyenthinhatphuong.pu.utils.Constants
import com.nguyenthinhatphuong.pu.utils.GlideLoader

open class DashboardItemsListAdapter (
    private val context: Context,
    private val list: ArrayList<Product>
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onClickListener: OnClickListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyProductsListAdapter.MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_dashboard_layout,
                parent,
                false
            )
        )
    }

    fun setOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }

    override fun getItemCount(): Int {
        return  list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]

        if (holder is MyProductsListAdapter.MyViewHolder) {

            model.image?.let {
                GlideLoader(context).loadProductPicture(
                    it,
                    holder.itemView.findViewById(R.id.iv_dashboard_item_image)
                )
            }

            holder.itemView.findViewById<com.nguyenthinhatphuong.pu.utils.MSPTextViewBold>(R.id.tv_dashboard_item_title).text = model.title
            holder.itemView.findViewById<com.nguyenthinhatphuong.pu.utils.MSPTextViewBold>(R.id.tv_dashboard_item_price).text = "$${model.price}"

            holder.itemView.setOnClickListener {
                val intent = Intent(context, ProductDetailsActivity::class.java)
                intent.putExtra(Constants.EXTRA_PRODUCT_ID, model.product_id)
                intent.putExtra(Constants.EXTRA_PRODUCT_OWNER_ID, model.user_id)

                context.startActivity(intent)
            }
            /*holder.itemView.setOnClickListener {
                if(onClickListener != null){
                    onClickListener!!.onClick(position, model)
                }
            }*/
        }
    }
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
    interface OnClickListener{
        fun onClick(position: Int, product: Product)
    }
}