package com.nguyenthinhatphuong.pu.ui.ui.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.nguyenthinhatphuong.pu.R
import com.nguyenthinhatphuong.pu.models.Order
import com.nguyenthinhatphuong.pu.ui.ui.activites.MyOrderDetailsActivity
import com.nguyenthinhatphuong.pu.utils.Constants
import com.nguyenthinhatphuong.pu.utils.GlideLoader

open class MyOrderListAdapter (
    private val context: Context,
    private val list: ArrayList<Order>
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MyViewHolder(
            LayoutInflater.from(context).inflate(
                R.layout.item_list_layout,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if (holder is MyViewHolder) {
            model.image?.let {
                GlideLoader(context).loadProductPicture(
                    it,
                    holder.itemView.findViewById<ImageView>(R.id.iv_item_image)
                )
                holder.itemView.findViewById<com.nguyenthinhatphuong.pu.utils.MSPTextViewBold>(R.id.tv_item_name).text =
                    model.title
                holder.itemView.findViewById<com.nguyenthinhatphuong.pu.utils.MSPTextViewBold>(R.id.tv_item_price).text =
                    "$${model.total_amount}"

                val deleteButton =
                    holder.itemView.findViewById<ImageButton>(R.id.ib_delete_cart_item)
                if (deleteButton != null) {
                    deleteButton.visibility = View.GONE
                }

                holder.itemView.setOnClickListener {
                    val intent = Intent(context, MyOrderDetailsActivity::class.java)
                    intent.putExtra(Constants.EXTRA_MY_ORDER_DETAILS, model)
                    context.startActivity(intent)

                }

            }
        }
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)
}