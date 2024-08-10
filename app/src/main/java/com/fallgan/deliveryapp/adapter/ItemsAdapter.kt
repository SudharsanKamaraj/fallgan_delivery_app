package com.fallgan.deliveryapp.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.fallgan.deliveryapp.R
import com.fallgan.deliveryapp.adapter.ItemsAdapter.OrderItemHolder
import com.fallgan.deliveryapp.helper.Constant
import com.fallgan.deliveryapp.helper.Session
import com.fallgan.deliveryapp.model.Items

class ItemsAdapter(val activity: Activity, private val items: ArrayList<Items>) :
    RecyclerView.Adapter<OrderItemHolder>() {
    val session = Session(activity)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderItemHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.lyt_items, null)
        return OrderItemHolder(v)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: OrderItemHolder, position: Int) {
        val item = items[position]
        holder.tvProductName.text = item.name
        holder.tvUnit.text = activity.getString(R.string.unit_) + item.measurement + " " + item.unit
        holder.tvQuantity.text = activity.getString(R.string.qty_) + item.quantity
        holder.tvPrice.text = activity.getString(R.string.price_) + Session(
            activity
        ).getData(Constant.CURRENCY) + "): " + item.price
        holder.tvDiscountPrice.text =
            activity.getString(R.string.discount_) + Session(
                activity
            ).getData(Constant.CURRENCY) + "): " + item.discounted_price
        holder.tvTaxPercentage.text =
            activity.getString(R.string.tax_) + Session(
                activity
            ).getData(Constant.CURRENCY) + "): " + item.tax_amount
        holder.tvTax.text = activity.getString(R.string.tax__) + item.tax_percentage
        holder.tvSubTotal.text = activity.getString(R.string.subtotal_) + Session(
            activity
        ).getData(Constant.CURRENCY) + "): " + item.sub_total
        if (item.active_status.equals(Constant.CANCELLED, ignoreCase = true)) {
            holder.tvStatus.visibility = View.VISIBLE
            holder.tvStatus.text = Constant.CANCELLED
        } else if (item.active_status.equals(Constant.RETURNED, ignoreCase = true)) {
            holder.tvStatus.visibility = View.VISIBLE
            holder.tvStatus.text = Constant.RETURNED
        }
        Picasso.get().load(item.image)
            .fit()
            .centerInside()
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.placeholder)
            .into(holder.imgProduct)
        val arrayList = ArrayList<String>()
        arrayList.add(activity.getString(R.string.awaiting_payment))
        arrayList.add(activity.getString(R.string.received))
        arrayList.add(activity.getString(R.string.processed))
        arrayList.add(activity.getString(R.string.shipped))
        arrayList.add(activity.getString(R.string.delivered))
        arrayList.add(activity.getString(R.string.cancelled))
        arrayList.add(activity.getString(R.string.returned))
        val arrayAdapter = ArrayAdapter(activity, android.R.layout.simple_spinner_item, arrayList)
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    class OrderItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvProductName: TextView = itemView.findViewById(R.id.tvProductName)
        val tvUnit: TextView = itemView.findViewById(R.id.tvUnit)
        val tvQuantity: TextView = itemView.findViewById(R.id.tvQuantity)
        val tvPrice: TextView = itemView.findViewById(R.id.tvPrice)
        val tvDiscountPrice: TextView = itemView.findViewById(R.id.tvDiscountPrice)
        val tvSubTotal: TextView = itemView.findViewById(R.id.tvSubTotal)
        val tvTaxPercentage: TextView = itemView.findViewById(R.id.tvTaxPercentage)
        val tvTax: TextView = itemView.findViewById(R.id.tvTax)
        val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        val imgProduct: ImageView = itemView.findViewById(R.id.imgProduct)

    }
}