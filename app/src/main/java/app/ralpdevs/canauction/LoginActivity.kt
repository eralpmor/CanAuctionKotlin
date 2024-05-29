package app.ralpdevs.canauction

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var editTextEmail: TextInputEditText
    private lateinit var editTextPassword: TextInputEditText
    private lateinit var signIn: Button
    private lateinit var signUp: TextView
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

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
        setContentView(R.layout.activity_login)
        FirebaseApp.initializeApp(this)
        initialize()
    }

    private fun initialize() {
        editTextEmail = findViewById(R.id.login_email_input_area)
        editTextPassword = findViewById(R.id.login_password_input_area)
        signIn = findViewById(R.id.login_sign_in_button)
        signUp = findViewById(R.id.login_sign_up_button)

        signUp.setOnClickListener {
            val intentToRegister = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intentToRegister)
            finish()
        }

        signIn.setOnClickListener {
            val email = editTextEmail.text.toString().trim()
            val password = editTextPassword.text.toString().trim()

            if (email.isEmpty()) {
                Toast.makeText(this@LoginActivity, "Enter Email Please", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                Toast.makeText(this@LoginActivity, "Enter Password Please", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this@LoginActivity, "Login Successful!", Toast.LENGTH_SHORT).show()
                        val intentToHome = Intent(this@LoginActivity, HomeActivity::class.java)
                        startActivity(intentToHome)
                        finish()
                    } else {
                        Toast.makeText(this@LoginActivity, "Authentication Failed!", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}
