package com.daniel.ukk

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AkunActivity : AppCompatActivity() {
    private val database = FirebaseDatabase.getInstance("https://ukkpakbinar-default-rtdb.asia-southeast1.firebasedatabase.app/")
    private val myRef = database.getReference("Register")

    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var btnUpdatePassword: Button
    private lateinit var btnDeleteAccount: Button
    private lateinit var btnLogout: Button

    private lateinit var userId: String

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_akun)

        // Get user ID from SharedPreferences
        val sharedPreferences = getSharedPreferences(LoginActivity.PREFS_NAME, Context.MODE_PRIVATE)
        userId = sharedPreferences.getString(LoginActivity.KEY_USER_ID, null) ?: ""

        if (userId.isNotEmpty()) {
            // Initialize views
            editTextEmail = findViewById(R.id.etEmail)
            editTextPassword = findViewById(R.id.etPassword)
            btnUpdatePassword = findViewById(R.id.btnUpdatePassword)
            btnDeleteAccount = findViewById(R.id.btnDeleteAccount)
            btnLogout = findViewById(R.id.btnLogout)

            // Load account data
            loadAccountData()

            // Set button listeners
            btnUpdatePassword.setOnClickListener { updateAccount() }
            btnDeleteAccount.setOnClickListener { showDeleteConfirmationDialog() }
            btnLogout.setOnClickListener { showLogoutDialog() }

            // Back button listener
            val backakun = findViewById<ImageButton>(R.id.btn_back)
            backakun.setOnClickListener {
                finish()
            }
        } else {
            Toast.makeText(this, "ID pengguna tidak ditemukan", Toast.LENGTH_SHORT).show()
        }
    }

    // Method to load account data from Firebase using dynamic ID
    private fun loadAccountData() {
        myRef.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val email = snapshot.child("email").getValue(String::class.java)
                    val password = snapshot.child("password").getValue(String::class.java)

                    editTextEmail.setText(email ?: "")
                    editTextPassword.setText(password ?: "")
                } else {
                    Toast.makeText(this@AkunActivity, "Data akun tidak ditemukan", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@AkunActivity, "Gagal mengambil data: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Method to update email and password
    private fun updateAccount() {
        val newEmail = editTextEmail.text.toString().trim()
        val newPassword = editTextPassword.text.toString().trim()

        if (newEmail.isNotEmpty() && newPassword.isNotEmpty()) {
            val updates = mapOf("email" to newEmail, "password" to newPassword)
            myRef.child(userId).updateChildren(updates)
                .addOnSuccessListener {
                    Toast.makeText(this, "Akun berhasil diperbarui", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Gagal memperbarui akun", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Email dan password tidak boleh kosong", Toast.LENGTH_SHORT).show()
        }
    }

    // Method to show delete confirmation dialog
    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Konfirmasi Hapus Akun")
            .setMessage("Apakah Anda yakin ingin menghapus akun ini?")
            .setPositiveButton("Ya") { _, _ -> deleteAccount() }
            .setNegativeButton("Tidak") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    // Method to delete account
    private fun deleteAccount() {
        myRef.child(userId).removeValue()
            .addOnSuccessListener {
                Toast.makeText(this, "Akun berhasil dihapus", Toast.LENGTH_SHORT).show()

                // Clear user ID from SharedPreferences
                val sharedPreferences = getSharedPreferences(LoginActivity.PREFS_NAME, Context.MODE_PRIVATE)
                sharedPreferences.edit()
                    .putBoolean(LoginActivity.KEY_IS_LOGGED_IN, false) // Set login state to false
                    .remove(LoginActivity.KEY_USER_ID) // Remove user ID
                    .apply()

                // Redirect to LoginActivity after deleting the account
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Clear back stack
                startActivity(intent)
                finish() // Close AkunActivity
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal menghapus akun", Toast.LENGTH_SHORT).show()
            }
    }


    // Method to show logout confirmation dialog
    // Method to show logout confirmation dialog
    private fun showLogoutDialog() {
        val dialogBuilder = AlertDialog.Builder(this)
            .setMessage("Apakah Anda yakin ingin keluar?")
            .setCancelable(false)
            .setPositiveButton("Ya") { _, _ -> logout() }
            .setNegativeButton("Tidak") { dialog, _ -> dialog.dismiss() }
            .setTitle("Konfirmasi Keluar") // Set the title here

        dialogBuilder.create().show() // Create and show the dialog
    }


    // Method to log out
    private fun logout() {
        // Clear the login state from SharedPreferences
        val sharedPreferences = getSharedPreferences(LoginActivity.PREFS_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean(LoginActivity.KEY_IS_LOGGED_IN, false).apply()

        // Navigate back to LoginActivity
        startActivity(Intent(this, LoginActivity::class.java))
        finish() // Close AkunActivity
    }
}
