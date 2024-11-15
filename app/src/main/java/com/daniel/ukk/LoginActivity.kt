package com.daniel.ukk

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class LoginActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private val database = FirebaseDatabase.getInstance("https://ukkpakbinar-default-rtdb.asia-southeast1.firebasedatabase.app/")
    private val myRef = database.getReference("Register")

    companion object {
        const val PREFS_NAME = "LoginPrefs"
        const val KEY_IS_LOGGED_IN = "isLoggedIn"
        const val KEY_USER_ID = "userId"
    }

    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var buttonLogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        editTextEmail = findViewById(R.id.editTextEmail)
        editTextPassword = findViewById(R.id.editTextPassword)
        buttonLogin = findViewById(R.id.buttonLogin)

        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        // Check if the user is already logged in
        if (sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)) {
            navigateToAkunActivity()
            finish()
        }

        buttonLogin.setOnClickListener {
            val email = editTextEmail.text.toString()
            val password = editTextPassword.text.toString()
            loginUser(email, password)
        }
    }

    private fun loginUser(email: String, password: String) {
        myRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (userSnapshot in snapshot.children) {
                        val userPassword = userSnapshot.child("password").getValue(String::class.java)
                        if (userPassword == password) {
                            val userId = userSnapshot.key // Get the user ID

                            // Save login state and user ID in SharedPreferences
                            sharedPreferences.edit()
                                .putBoolean(KEY_IS_LOGGED_IN, true)
                                .putString(KEY_USER_ID, userId)
                                .apply()

                            // Navigate to AkunActivity
                            navigateToAkunActivity()
                            finish()
                        } else {
                            Toast.makeText(this@LoginActivity, "Password salah", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this@LoginActivity, "Email tidak ditemukan", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@LoginActivity, "Terjadi kesalahan: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun navigateToAkunActivity() {
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        startActivity(intent)
    }

    fun onRegisterClick(view: android.view.View) {
        val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
        startActivity(intent)
    }
}
