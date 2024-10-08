package com.fallgan.deliveryapp.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.fallgan.deliveryapp.R
import com.fallgan.deliveryapp.helper.Constant
import com.fallgan.deliveryapp.helper.Session
import com.fallgan.deliveryapp.model.WalletHistory

class WalletHistoryAdapter(
    var activity: Activity,
    private var histories: ArrayList<WalletHistory?>
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder?>() {
    // for load more
    var id: String? = "0"
    fun add(position: Int, item: WalletHistory?) {
        histories.add(position, item)
        notifyItemInserted(position)
    }


    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return WalletHistoryHolderItems(
            LayoutInflater.from(activity).inflate(R.layout.lyt_wallet_history_list, parent, false)
        )
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is WalletHistoryHolderItems) {
            val walletHistory = histories[position]
            id = walletHistory!!.id
            holder.tvTxNo.text = walletHistory.id
            holder.tvTxDateAndTime.text = walletHistory.date_created
            holder.tvTxMessage.text = walletHistory.message
            holder.tvTxAmount.text = activity.getString(R.string.amount_title) + Session(
                activity
            ).getData(Constant.CURRENCY) + walletHistory.amount
            when (walletHistory.status) {
                "SUCCESS" -> {
                    holder.tvTxStatus.text = walletHistory.status
                    holder.cardViewTxStatus.setBackgroundColor(activity.getColor(R.color.tx_success_bg))
                }
                "0" -> {
                    holder.tvTxStatus.text = "PENDING"
                    holder.cardViewTxStatus.setBackgroundColor(activity.getColor(R.color.shipped_status_bg))
                }
                "1" -> {
                    holder.tvTxStatus.text = "APPROVED"
                    holder.cardViewTxStatus.setBackgroundColor(activity.getColor(R.color.received_status_bg))
                }
                "2" -> {
                    holder.tvTxStatus.text = "CANCELLED"
                    holder.cardViewTxStatus.setBackgroundColor(activity.getColor(R.color.returned_and_cancel_status_bg))
                }
                else -> {
                    holder.cardViewTxStatus.setBackgroundColor(activity.getColor(R.color.tx_fail_bg))
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return histories.size
    }

    override fun getItemId(position: Int): Long {
        val product = histories[position]
        return if (product != null) product.id!!.toInt().toLong() else position.toLong()
    }

    inner class WalletHistoryHolderItems(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvTxNo: TextView = itemView.findViewById(R.id.tvTxNo)
        var tvTxDateAndTime: TextView = itemView.findViewById(R.id.tvTxDateAndTime)
        var tvTxMessage: TextView = itemView.findViewById(R.id.tvTxMessage)
        var tvTxAmount: TextView = itemView.findViewById(R.id.tvTxAmount)
        var tvTxStatus: TextView = itemView.findViewById(R.id.tvTxStatus)
        var cardViewTxStatus: CardView = itemView.findViewById(R.id.cardViewTxStatus)

    }
}