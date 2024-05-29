package app.ralpdevs.canauction

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {

    private lateinit var editTextEmail: TextInputEditText
    private lateinit var editTextPassword: TextInputEditText
    private lateinit var signUp: Button
    private lateinit var signIn: TextView
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var email: String
    private lateinit var password: String

    override fun onStart() {
        super.onStart()
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            val intentToHome = Intent(applicationContext, HomeActivity::class.java)
            startActivity(intentToHome)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        initialize()
    }

    private fun initialize() {
        firebaseAuth = FirebaseAuth.getInstance()
        editTextEmail = findViewById(R.id.register_email_area)
        editTextPassword = findViewById(R.id.register_password_area)
        signUp = findViewById(R.id.register_sign_up_button)
        signIn = findViewById(R.id.register_sign_in_button)

        signUp.setOnClickListener {
            email = editTextEmail.text.toString().trim()
            password = editTextPassword.text.toString().trim()

            if (email.isEmpty()) {
                Toast.makeText(this@RegisterActivity, "Enter Email Please", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                Toast.makeText(this@RegisterActivity, "Enter Password Please", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            registerUser(email, password)
        }

        signIn.setOnClickListener {
            val intentToRegister = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intentToRegister)
            finish()
        }
    }

    private fun registerUser(email: String, password: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this@RegisterActivity, "Registration Successfully!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Authentication Failed! ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}
