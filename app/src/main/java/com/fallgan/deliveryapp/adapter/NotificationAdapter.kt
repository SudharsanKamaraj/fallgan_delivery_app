package com.fallgan.deliveryapp.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.fallgan.deliveryapp.R
import com.fallgan.deliveryapp.model.Notification

class NotificationAdapter(
    var activity: Activity,
    private var notifications: ArrayList<Notification?>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder?>() {
    private var showMore = false
    fun add(position: Int, item: Notification?) {
        notifications.add(position, item)
        notifyItemInserted(position)
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return NotificationHolderItems(
            LayoutInflater.from(activity).inflate(R.layout.lyt_notification_list, parent, false)
        )
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is NotificationHolderItems) {
            val notification = notifications[position]
            holder.tvTitle.text =
                activity.getString(R.string.order_item_number) + notification!!.order_id
            holder.tvMessage.text = notification.title
            holder.tvOrderDate.text = notification.date_created
            holder.tvMessageMore.text = notification.message
            holder.tvShowMore.setOnClickListener {
                if (!showMore) {
                    showMore = true
                    holder.tvMessageMore.visibility = View.GONE
                    holder.tvShowMore.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.ic_show_more,
                        0
                    )
                } else {
                    showMore = false
                    holder.tvMessageMore.visibility = View.VISIBLE
                    holder.tvShowMore.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        0,
                        0,
                        R.drawable.ic_show_less,
                        0
                    )
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return notifications.size
    }

    override fun getItemViewType(position: Int): Int {
        return notifications[position]?.id.toString().toInt()
    }

    override fun getItemId(position: Int): Long {
        val product = notifications[position]
        return if (product != null) product.id!!.toInt().toLong() else position.toLong()
    }

    inner class NotificationHolderItems(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        var tvMessage: TextView = itemView.findViewById(R.id.tvMessage)
        var tvMessageMore: TextView = itemView.findViewById(R.id.tvMessageMore)
        var tvOrderDate: TextView = itemView.findViewById(R.id.tvOrderDate)
        var tvShowMore: TextView = itemView.findViewById(R.id.tvShowMore)

    }
}