package com.example.expensetrackerapp.Activity

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.example.expensetrackerapp.R
import com.example.expensetrackerapp.RetrofitClient
import kotlinx.coroutines.*
import kotlin.random.Random

class SignIn : AppCompatActivity() {

    private val API_KEY1 = "\$2a\$10$"
    private val API_KEY2 = "hPDzuJOstFCGQJp/WyXF/OCUkVjzUbrXHE1W6CMVm4jMb.MXdAz92"
    private val API_KEY = API_KEY1 + API_KEY2
    private val BIN_ID = "69171b69d0ea881f40e7f4cd"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (isLoggedIn()) {
            startActivity(Intent(this, Home::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_sign_in)

        findViewById<TextView>(R.id.txtSignUp).setOnClickListener {
            startActivity(Intent(this, SignUp::class.java))
        }
        findViewById<AppCompatButton>(R.id.btn).setOnClickListener {
            val email = findViewById<EditText>(R.id.edtEmailSignIn).text.toString().trim()
            val password = findViewById<EditText>(R.id.edtPasswordSignIn).text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            loginUser(email, password)
        }
    }

    private fun loginUser(email: String, password: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.instance.getUsers(BIN_ID, API_KEY)
                val user = response.record.users.find {
                    it.email.equals(email, ignoreCase = true) && it.password == password
                }

                withContext(Dispatchers.Main) {
                    if (user != null) {
                        saveLoginState(email)
                        Toast.makeText(this@SignIn, "Welcome back, ${user.name}!", Toast.LENGTH_LONG).show()
                        startActivity(Intent(this@SignIn, Home::class.java))
                        finish()
                    } else {
                        Toast.makeText(this@SignIn, "Invalid email or password", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@SignIn, "Network Error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun saveLoginState(email: String) {
        getSharedPreferences("auth", MODE_PRIVATE).edit()
            .putBoolean("isLoggedIn", true)
            .putString("email", email)
            .apply()
    }

    private fun isLoggedIn(): Boolean {
        return getSharedPreferences("auth", MODE_PRIVATE)
            .getBoolean("isLoggedIn", false)
    }
}