package app.ralpdevs.canauction

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.graphics.Color
import android.graphics.drawable.ColorDrawable

import android.os.CountDownTimer
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.ralpdevs.canauction.adapter.BidAdapter
import app.ralpdevs.canauction.model.BidModel
import app.ralpdevs.canauction.model.CarModel
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class CarActivity : AppCompatActivity() {

    private lateinit var coverImage: ImageView
    private lateinit var title: TextView
    private lateinit var bidArea: EditText
    private lateinit var placeBidButton: Button
    private lateinit var bidRecyclerView: RecyclerView
    private lateinit var timeArea: TextView
    private lateinit var countDownTimer: CountDownTimer

    private lateinit var carReference: DatabaseReference
    private lateinit var bidAdapter: BidAdapter
    private val bidList: MutableList<BidModel> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_car)

        initialize()

        val id = intent.getIntExtra("car_id", -1)
        carReference = FirebaseDatabase.getInstance().getReference("Cars").child(id.toString())

        loadCarDetails()
    }

    private fun initialize() {
        coverImage = findViewById(R.id.car_cover_image)
        title = findViewById(R.id.car_title_area)
        bidArea = findViewById(R.id.car_place_bid_area)
        placeBidButton = findViewById(R.id.car_bid_button)
        bidRecyclerView = findViewById(R.id.car_bid_recycler)
        timeArea = findViewById(R.id.car_time_area)

        bidAdapter = BidAdapter(bidList)
        bidRecyclerView.layoutManager = LinearLayoutManager(this)
        bidRecyclerView.adapter = bidAdapter

        val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val email = currentUser.email
            placeBidButton.setOnClickListener {
                val bid = bidArea.text.toString().trim()
                if (bid.isNotEmpty()) {
                    try {
                        val bidValue = bid.replace(".", "").replace(",", ".").toDouble()
                        val formattedBid = String.format("%.2f", bidValue) + "$"
                        showConfirmationDialog(email!!, formattedBid)
                    } catch (e: NumberFormatException) {
                        Toast.makeText(this@CarActivity, "Invalid bid value", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            placeBidButton.isEnabled = false
        }
    }

    private fun showConfirmationDialog(email: String, formattedBid: String) {
        AlertDialog.Builder(this)
            .setMessage("You have placed a bid for $formattedBid. Should we place this as your bid?")
            .setPositiveButton("Yes, Place My Bid") { _, _ -> placeBid(email, formattedBid) }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    private fun loadCarDetails() {
        carReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val car = dataSnapshot.getValue(CarModel::class.java)
                if (car != null) {
                    Glide.with(this@CarActivity).load(car.imgUrl)
                        .placeholder(ColorDrawable(Color.BLACK))
                        .fitCenter()
                        .into(coverImage)

                    title.text = car.title

                    if (car.bids != null) {
                        bidList.clear()
                        bidList.addAll(car.bids!!)
                        sortBidsDescending()
                        bidAdapter.notifyDataSetChanged()

                        if (bidList.isNotEmpty()) {
                            val lastBidTime = bidList[0].timestamp
                            startCountdown(lastBidTime)
                        }
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@CarActivity, "Failed to load car details.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun placeBid(email: String, bid: String) {
        val currentTime = System.currentTimeMillis()
        val bidModel = BidModel(email, bid, currentTime)
        bidList.add(bidModel)
        sortBidsDescending()
        bidAdapter.notifyDataSetChanged()

        carReference.child("bids").setValue(bidList)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this@CarActivity, "Bid placed successfully!", Toast.LENGTH_SHORT).show()
                    startCountdown(bidModel.timestamp)
                } else {
                    Toast.makeText(this@CarActivity, "Failed to place bid.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun sortBidsDescending() {
        bidList.sortByDescending { convertBidToDouble(it.bidValue) }
    }

    private fun convertBidToDouble(bid: String): Double {
        return try {
            bid.replace("$", "").trim().toDouble()
        } catch (e: NumberFormatException) {
            0.0
        }
    }

    private fun startCountdown(lastBidTime: Long) {
        countDownTimer = object : CountDownTimer((lastBidTime + 30 * 60 * 1000) - System.currentTimeMillis(), 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished / 1000
                val minutes = seconds / 60
                val secondsDisplay = seconds % 60
                timeArea.text = String.format("%02d:%02d", minutes, secondsDisplay)
            }

            override fun onFinish() {
                placeBidButton.isEnabled = false
                timeArea.text = "Bidding closed"
            }
        }.start()
    }
}
