package com.fallgan.deliveryapp.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.fallgan.deliveryapp.R
import com.fallgan.deliveryapp.activity.OrderDetailActivity
import com.fallgan.deliveryapp.helper.Constant
import com.fallgan.deliveryapp.helper.Session
import com.fallgan.deliveryapp.model.OrderList

class OrderListAdapter(
    val activity: Activity,
    private val orderTrackerArrayList: ArrayList<OrderList?>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    // for load more
    fun add(position: Int, item: OrderList?) {
        orderTrackerArrayList.add(position, item)
        notifyItemInserted(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return TrackerHolderItems(
            LayoutInflater.from(activity).inflate(R.layout.lyt_order_list, parent, false)
        )
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holderParent: RecyclerView.ViewHolder, position: Int) {
        if (holderParent is TrackerHolderItems) {
            val order = orderTrackerArrayList[position]
            holderParent.tvOrderId.text = activity.getString(R.string.order_number) + order!!.id
            val date = order.date_added!!.split("\\s+").toTypedArray()
            holderParent.tvOrderDate.text = activity.getString(R.string.ordered_on) + date[0]
            holderParent.tvOrderAmount.text =
                activity.getString(R.string.for_amount_on) + Session(activity).getData(Constant.CURRENCY) + order.final_total
            holderParent.lytMain.setOnClickListener {
                activity.startActivity(
                    Intent(
                        activity, OrderDetailActivity::class.java
                    ).putExtra(Constant.POSITION, position).putExtra(
                        Constant.ITEM, order
                    )
                )
            }
            val items = ArrayList<String?>()
            for (i in order.items!!.indices) {
                items.add(order.items!![i].name)
            }
            holderParent.tvItems.text =
                items.toTypedArray().contentToString().replace("]", "").replace("[", "")
            holderParent.tvTotalItems.text =
                items.size.toString() + activity.getString(R.string.item)
        }
    }

    override fun getItemCount(): Int {
        return orderTrackerArrayList.size
    }


    override fun getItemId(position: Int): Long {
        return orderTrackerArrayList[position]?.id.toString().toLong()
    }

    class TrackerHolderItems(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvOrderId: TextView = itemView.findViewById(R.id.tvOrderId)
        val tvOrderDate: TextView = itemView.findViewById(R.id.tvOrderDate)
        val lytMain: RelativeLayout = itemView.findViewById(R.id.lytMain)
        val tvOrderAmount: TextView = itemView.findViewById(R.id.tvOrderAmount)
        val tvTotalItems: TextView = itemView.findViewById(R.id.tvTotalItems)
        val tvItems: TextView = itemView.findViewById(R.id.tvItems)

    }
}