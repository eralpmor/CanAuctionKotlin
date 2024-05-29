package app.ralpdevs.canauction.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import app.ralpdevs.canauction.R
import app.ralpdevs.canauction.model.BidModel


class BidAdapter(private val bidList: List<BidModel>) : RecyclerView.Adapter<BidAdapter.BidHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BidHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.car_bid_list_item, parent, false)
        return BidHolder(view)
    }

    override fun onBindViewHolder(holder: BidHolder, position: Int) {
        val bidModel = bidList[position]
        holder.bidText.text = bidModel.bidValue
        holder.emailText.text = bidModel.email
    }

    override fun getItemCount(): Int {
        return bidList.size
    }

    class BidHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val bidText: TextView = itemView.findViewById(R.id.car_bid_text)
        val emailText: TextView = itemView.findViewById(R.id.car_bid_email_text)
    }
}
