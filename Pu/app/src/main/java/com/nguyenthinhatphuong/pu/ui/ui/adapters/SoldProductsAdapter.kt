package com.nguyenthinhatphuong.pu.ui.ui.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.nguyenthinhatphuong.pu.R
import com.nguyenthinhatphuong.pu.models.SoldProduct
import com.nguyenthinhatphuong.pu.ui.ui.activites.SoldProductDetailsActivity
import com.nguyenthinhatphuong.pu.utils.Constants
import com.nguyenthinhatphuong.pu.utils.GlideLoader

open class SoldProductsAdapter(
    private val context: Context,
    private var list: ArrayList<SoldProduct>
): RecyclerView.Adapter<RecyclerView.ViewHolder>(){
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

        if (holder is MyViewHolder){

            model.image?.let { GlideLoader(context).loadProductPicture(it, holder.itemView.findViewById(R.id.iv_item_image)) }


            val deleteButton = holder.itemView.findViewById<ImageButton>(R.id.ib_delete_product)
            deleteButton.visibility = View.GONE
            holder.itemView.findViewById<com.nguyenthinhatphuong.pu.utils.MSPTextViewBold>(R.id.tv_item_name).text= model.title
            holder.itemView.findViewById<com.nguyenthinhatphuong.pu.utils.MSPTextViewBold>(R.id.tv_item_price).text= "$${model.price}"


            holder.itemView.setOnClickListener {
                val intent = Intent(context, SoldProductDetailsActivity::class.java)
                intent.putExtra(Constants.EXTRA_SOLD_PRODUCT_DETAILS, model)
                context.startActivity(intent)
            }
        }
    }
    private class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)

}