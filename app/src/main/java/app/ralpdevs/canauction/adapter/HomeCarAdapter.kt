package app.ralpdevs.canauction.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import app.ralpdevs.canauction.CarActivity
import app.ralpdevs.canauction.R
import app.ralpdevs.canauction.model.CarModel
import com.bumptech.glide.Glide

class HomeCarAdapter(
    private val context: Context,
    private var list: ArrayList<CarModel>
) : RecyclerView.Adapter<HomeCarAdapter.HomeCarHolder>() {

    fun setCarList(carList: ArrayList<CarModel>) {
        this.list = carList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeCarHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.home_car_list_item, parent, false)
        return HomeCarHolder(view)
    }

    override fun onBindViewHolder(holder: HomeCarHolder, position: Int) {
        val carModel = list[position]

        Glide.with(context).load(carModel.imgUrl)
            .placeholder(ColorDrawable(Color.BLACK))
            .fitCenter()
            .into(holder.coverImage)

        holder.title.text = carModel.title

        holder.itemView.setOnClickListener {
            val carId = carModel.id

            val intentToDetail = Intent(context, CarActivity::class.java)
            intentToDetail.putExtra("car_id", carId - 1)
            context.startActivity(intentToDetail)
        }
    }

    override fun getItemCount(): Int = list.size

    inner class HomeCarHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val coverImage: ImageView = itemView.findViewById(R.id.car_item_image)
        val title: TextView = itemView.findViewById(R.id.car_item_title)
    }
}
