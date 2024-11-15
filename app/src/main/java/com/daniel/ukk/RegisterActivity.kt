package com.daniel.ukk

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {
    private lateinit var database: DatabaseReference
    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var editTextUsername: EditText

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Initialize Firebase Database reference
        database = FirebaseDatabase.getInstance("https://ukkpakbinar-default-rtdb.asia-southeast1.firebasedatabase.app")
            .getReference("Register")

        editTextEmail = findViewById(R.id.txemail)
        editTextPassword = findViewById(R.id.txpassword)
        editTextUsername = findViewById(R.id.txuser)

        val buttonRegister: Button = findViewById(R.id.buttonregister)
        buttonRegister.setOnClickListener {
            registerUser()
        }
    }

    private fun registerUser() {
        val email = editTextEmail.text.toString().trim()
        val password = editTextPassword.text.toString().trim()
        val username = editTextUsername.text.toString().trim()

        // Validate username
        if (username.isEmpty()) {
            Toast.makeText(this, "Username tidak boleh kosong!", Toast.LENGTH_SHORT).show()
            return
        }

        // Validate email and password
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Email dan Password tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return
        }

        // Generate a unique ID for the user in Firebase
        val userId = database.push().key

        if (userId != null) {
            // Create the user data map
            val user = mapOf(
                "email" to email,
                "password" to password,
                "username" to username
            )

            // Save user data using the generated userId
            database.child(userId).setValue(user)
                .addOnSuccessListener {
                    // Clear the EditText fields after successful registration
                    editTextEmail.text.clear()
                    editTextPassword.text.clear()
                    editTextUsername.text.clear()

                    // Show success message
                    Toast.makeText(this, "Data berhasil disimpan", Toast.LENGTH_SHORT).show()

                    // Navigate to LoginActivity
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()  // Prevent going back to this activity
                }
                .addOnFailureListener {
                    // Show error message
                    Toast.makeText(this, "Terjadi kesalahan saat menyimpan data", Toast.LENGTH_SHORT).show()
                }
        } else {
            // Show error if userId generation failed
            Toast.makeText(this, "Gagal membuat ID pengguna", Toast.LENGTH_SHORT).show()
        }
    }
}
