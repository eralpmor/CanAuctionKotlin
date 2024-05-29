package app.ralpdevs.canauction

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.ralpdevs.canauction.adapter.HomeCarAdapter
import app.ralpdevs.canauction.model.CarModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*


class HomeActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var userEmail: TextView
    private lateinit var logoutButton: Button
    private var user: FirebaseUser? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var list: ArrayList<CarModel>
    private lateinit var adapter: HomeCarAdapter
    private lateinit var reference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        initialize()
    }

    private fun initialize() {
        auth = FirebaseAuth.getInstance()
        logoutButton = findViewById(R.id.home_logout_button)
        userEmail = findViewById(R.id.home_user_email_area)
        user = auth.currentUser

        recyclerView = findViewById(R.id.home_car_recycler)

        if (user == null) {
            val intentToLogin = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intentToLogin)
            finish()
        } else {
            userEmail.text = user!!.email
        }

        logoutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intentToLogin = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intentToLogin)
            finish()
        }

        list = ArrayList()

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = HomeCarAdapter(this, list)
        recyclerView.adapter = adapter

        databaseOperation()
    }

    private fun databaseOperation() {
        reference = FirebaseDatabase.getInstance().getReference("Cars")

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                list.clear()
                for (snapshot in dataSnapshot.children) {
                    val car = snapshot.getValue(CarModel::class.java)
                    car?.let { list.add(it) }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(this@HomeActivity, "Failed to load data.", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

