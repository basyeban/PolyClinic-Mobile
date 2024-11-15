package com.daniel.ukk

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isEmpty
import com.google.firebase.database.FirebaseDatabase

class PendaftaranActivity : AppCompatActivity() {
    private val database = FirebaseDatabase.getInstance("https://ukkpakbinar-default-rtdb.asia-southeast1.firebasedatabase.app/")
    private val myRef = database.getReference("Pendaftaran")

    private lateinit var nama: EditText
    private lateinit var umur: EditText
    private lateinit var jeniskelamin: Spinner
    private lateinit var alamat: EditText
    private lateinit var no_hp: EditText
    private lateinit var pilihanpoli: Spinner
    private lateinit var btdaftar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daftar)

        // Initialize views
        val back: ImageView = findViewById(R.id.back)
        back.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        nama = findViewById(R.id.nama)
        umur = findViewById(R.id.umur)
        jeniskelamin = findViewById(R.id.spinnerjeniskelamin)
        alamat = findViewById(R.id.alamat)
        no_hp = findViewById(R.id.no_hp)
        pilihanpoli = findViewById(R.id.spinnerPilihPoli)
        btdaftar = findViewById(R.id.simpan)

        // Set up spinners with adapters
        val jeniskelaminAdapter = ArrayAdapter.createFromResource(this, R.array.jeniskelamin, android.R.layout.simple_spinner_item)
        jeniskelaminAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        jeniskelamin.adapter = jeniskelaminAdapter

        val poliAdapter = ArrayAdapter.createFromResource(this, R.array.pilihpoli, android.R.layout.simple_spinner_item)
        poliAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        pilihanpoli.adapter = poliAdapter

        // Handle button click
        btdaftar.setOnClickListener {
            val namaText = nama.text.toString().trim()
            val umurText = umur.text.toString().trim() // Rename to umurText
            val jeniskelaminText = jeniskelamin.selectedItem.toString()
            val alamatText = alamat.text.toString().trim()
            val no_hpText = no_hp.text.toString().trim()
            val pilihanPoliText = pilihanpoli.selectedItem.toString()

            // Validation checks for empty fields
            if (namaText.isEmpty()) {
                Toast.makeText(this, "Nama tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (umurText.isEmpty() || umurText.length > 2) {
                Toast.makeText(this, "Umur tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (jeniskelaminText.isEmpty()) {
                Toast.makeText(this, "Jenis Kelamin tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (alamatText.isEmpty()) {
                Toast.makeText(this, "Alamat tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (no_hpText.isEmpty() || no_hpText.length > 12) {
                Toast.makeText(this, "Nomor HP tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (pilihanpoli.isEmpty()) {
                Toast.makeText(this, "Pilih Poli tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Create User object
            val user = User(namaText, umurText, jeniskelaminText, alamatText, no_hpText, pilihanPoliText)

            // Save data to Firebase
            myRef.push().setValue(user)
                .addOnSuccessListener {
                    Toast.makeText(this, "Data berhasil disimpan", Toast.LENGTH_SHORT).show()
                    // Clear input fields
                    nama.text.clear()
                    umur.text.clear()
                    alamat.text.clear()
                    no_hp.text.clear()
                    jeniskelamin.setSelection(0)
                    pilihanpoli.setSelection(0)
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Terjadi kesalahan saat menyimpan data", Toast.LENGTH_SHORT).show()
                }
        }
    }

    // Data class for representing user data
    data class User(
        val nama: String,
        val umur: String,
        val jk: String,
        val alamat: String,
        val noHp: String,
        val pilihanPoli: String
    )
}
