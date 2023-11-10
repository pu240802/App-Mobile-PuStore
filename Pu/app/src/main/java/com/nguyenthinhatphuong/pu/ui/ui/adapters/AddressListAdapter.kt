package com.nguyenthinhatphuong.pu.ui.ui.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.nguyenthinhatphuong.pu.R
import com.nguyenthinhatphuong.pu.models.Address
import com.nguyenthinhatphuong.pu.ui.ui.activites.AddEditAddressActivity
import com.nguyenthinhatphuong.pu.ui.ui.activites.CheckoutActivity
import com.nguyenthinhatphuong.pu.utils.Constants

open class AddressListAdapter (
    private  val context: Context,
    private var list: ArrayList<Address>,
    private val selectAddress: Boolean
): RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
       return MyViewHolder(
           LayoutInflater.from(context).inflate(
               R.layout.item_address_layout,
               parent,
               false
           )
       )
    }



    fun notifyEditItem(activity: Activity, position: Int){
        val intent = Intent(context, AddEditAddressActivity::class.java)
        intent.putExtra(Constants.EXTRA_ADDRESS_DETAILS, list[position])
        activity.startActivityForResult(intent, Constants.ADD_ADDRESS_REQUEST_CODE)
        notifyItemChanged(position)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val model = list[position]
        if( holder is MyViewHolder){
            holder.itemView.findViewById<com.nguyenthinhatphuong.pu.utils.MSPTextViewBold>(R.id.tv_address_full_name).text = model.name
            holder.itemView.findViewById<com.nguyenthinhatphuong.pu.utils.MSPTextView>(R.id.tv_address_type).text = model.type
            holder.itemView.findViewById<com.nguyenthinhatphuong.pu.utils.MSPTextView>(R.id.tv_address_details).text ="${model.address}, ${model.zipcode}"
            holder.itemView.findViewById<com.nguyenthinhatphuong.pu.utils.MSPTextView>(R.id.tv_address_mobile_number).text = model.mobileNumber

            if(selectAddress){
                holder.itemView.setOnClickListener {

                    val intent = Intent(holder.itemView.context, CheckoutActivity::class.java)
                    intent.putExtra(Constants.EXTRA_SELECTED_ADDRESS, model)
                    holder.itemView.context.startActivity(intent)
                }
            }

        }
    }
    private class MyViewHolder(view: View) : RecyclerView.ViewHolder(view)

}