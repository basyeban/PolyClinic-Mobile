package com.daniel.ukk

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DaftarBaruActivity : AppCompatActivity() {
    private val database = FirebaseDatabase.getInstance("https://ukkpakbinar-default-rtdb.asia-southeast1.firebasedatabase.app/")
    private val myRef = database.getReference("Pendaftaran")

    private lateinit var daftarAdapter: DaftarAdapter
    private val daftarList = mutableListOf<ModelDaftar>() // Menggunakan MutableList

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daftar_baru)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerviewdaftar)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Inisialisasi adapter dengan daftarList yang kosong
        daftarAdapter = DaftarAdapter(this, daftarList)
        recyclerView.adapter = daftarAdapter

        // Memanggil fungsi untuk mendapatkan data dari Firebase
        getDaftarData()
    }

    private fun getDaftarData() {
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                daftarList.clear() // Menghapus data lama untuk mencegah duplikasi
                for (data in dataSnapshot.children) {
                    val nama = data.child("nama").getValue(String::class.java)
                    val umur = data.child("umur").getValue(String::class.java)
                    val pilihanPoli = data.child("pilihanPoli").getValue(String::class.java)
                    val jk = data.child("jk").getValue(String::class.java)
                    val alamat = data.child("alamat").getValue(String::class.java)
                    val noHp = data.child("noHp").getValue(String::class.java)

                    if (nama != null && umur != null && pilihanPoli != null && jk != null && alamat != null && noHp != null) {
                        val daftar = ModelDaftar(nama, umur, pilihanPoli, jk, alamat, noHp)
                        daftarList.add(daftar)
                    }
                }
                daftarAdapter.notifyDataSetChanged() // Memberitahu adapter tentang perubahan data
            }

            override fun onCancelled(databaseError: DatabaseError) {
                databaseError.toException().printStackTrace()
            }
        })
        val back: ImageView = findViewById(R.id.back_informasi)
        back.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}
