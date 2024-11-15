package com.daniel.ukk

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class
MainActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var welcomeTextView: TextView
    private lateinit var dateTextView: TextView // TextView untuk menampilkan tanggal dan waktu
    private var userId: String? = null  // ID pengguna yang akan diambil dari SharedPreferences
    private val handler = Handler()
    private val updateTimeRunnable = object : Runnable {
        override fun run() {
            // Update waktu dan detik
            updateDateAndTime()
            // Ulangi setiap 1000ms (1 detik)
            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Firebase Database reference
        database = FirebaseDatabase.getInstance("https://ukkpakbinar-default-rtdb.asia-southeast1.firebasedatabase.app")
            .getReference("Register")

        // Initialize TextViews
        welcomeTextView = findViewById(R.id.textView4)
        dateTextView = findViewById(R.id.textView5) // ID TextView untuk tanggal

        // Ambil userId dari SharedPreferences
        val sharedPreferences = getSharedPreferences(LoginActivity.PREFS_NAME, Context.MODE_PRIVATE)
        userId = sharedPreferences.getString(LoginActivity.KEY_USER_ID, null)

        if (userId != null) {
            // Jika userId tersedia, panggil fetchUsername
            fetchUsername()
        } else {
            // Tampilkan pesan jika userId tidak ditemukan
            Toast.makeText(this, "User ID tidak tersedia. Silakan login kembali.", Toast.LENGTH_SHORT).show()
            redirectToLogin()
        }

        // Menampilkan tanggal dan waktu saat ini
        updateDateAndTime()

        // Mulai update waktu setiap detik
        handler.postDelayed(updateTimeRunnable, 1000)

        try {
            // Initialize buttons by finding views in the layout
            val btndaftar = findViewById<LinearLayout>(R.id.daftar)
            val btnkonsul = findViewById<LinearLayout>(R.id.konsul)
            val btnjadwal = findViewById<LinearLayout>(R.id.poli)
            val btnhomevisit = findViewById<LinearLayout>(R.id.homevisit)
            val btnakun = findViewById<ImageButton>(R.id.akun)

            // Set click listeners for each button
            btndaftar.setOnClickListener {
                val keCalon = Intent(this@MainActivity, PendaftaranActivity::class.java)
                startActivity(keCalon)
            }
            btnkonsul.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse("https://wa.me/6283154764741")
                startActivity(intent)
            }
            btnjadwal.setOnClickListener {
                startActivity(Intent(this, JadwalActivity::class.java))
            }
            btnhomevisit.setOnClickListener {
                startActivity(Intent(this, HomevisitActivity::class.java))
            }
            btnakun.setOnClickListener {
                startActivity(Intent(this, AkunActivity::class.java))
            }
        } catch (e: NullPointerException) {
            Toast.makeText(this, "Error initializing buttons: ${e.message}", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "An unexpected error occurred: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchUsername() {
        userId?.let {
            database.child(it).child("username").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val username = snapshot.getValue(String::class.java)
                    if (username != null) {
                        welcomeTextView.text = "Hi, Selamat Datang $username"
                    } else {
                        Toast.makeText(this@MainActivity, "Username tidak ditemukan", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@MainActivity, "Gagal mengambil data: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun updateDateAndTime() {
        // Mendapatkan tanggal dan waktu sekarang
        val currentDate = Calendar.getInstance().time

        // Format tanggal dan jam yang diinginkan dengan detik
        val dateFormat = SimpleDateFormat("dd MMMM yyyy , HH.mm.ss", Locale("id", "ID"))

        // Mengubah tanggal, jam, dan detik menjadi string sesuai format
        val formattedDate = dateFormat.format(currentDate)

        // Menampilkan tanggal dan waktu pada TextView
        dateTextView.text = formattedDate
    }

    private fun redirectToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Membatalkan update setiap detik saat activity dihancurkan
        handler.removeCallbacks(updateTimeRunnable)
    }
}
