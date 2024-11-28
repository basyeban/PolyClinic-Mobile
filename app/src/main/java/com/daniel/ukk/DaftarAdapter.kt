package com.daniel.ukk

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase

class DaftarAdapter(
    private val context: Context,
    private val daftarList: MutableList<ModelDaftar>
) : RecyclerView.Adapter<DaftarAdapter.DaftarViewHolder>() {

    class DaftarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val hapusButton: Button = itemView.findViewById(R.id.daftar_btn_hapus)
        val namaTextView: TextView = itemView.findViewById(R.id.tv_nama)
        val umurTextView: TextView = itemView.findViewById(R.id.tv_umur)
        val poliTextView: TextView = itemView.findViewById(R.id.tv_poli)
        val jkTextView: TextView = itemView.findViewById(R.id.tv_jeniskelamin)
        val alamatTextView: TextView = itemView.findViewById(R.id.tv_alamat)
        val noHpTextView: TextView = itemView.findViewById(R.id.tv_no_hp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DaftarViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.item_daftar, parent, false)
        return DaftarViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: DaftarViewHolder, position: Int) {
        val item = daftarList[position]
        holder.namaTextView.text = item.nama
        holder.umurTextView.text = item.umur
        holder.jkTextView.text = item.jk
        holder.alamatTextView.text = item.alamat
        holder.noHpTextView.text = item.noHp
        holder.poliTextView.text = item.pilihanPoli

        val database = FirebaseDatabase.getInstance("https://ukkpakbinar-default-rtdb.asia-southeast1.firebasedatabase.app/")
        val myRef = database.getReference("Pendaftaran")

        // Set OnClickListener untuk hapus button
        holder.hapusButton.setOnClickListener {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Konfirmasi Hapus")
                .setMessage("Apakah Anda yakin ingin menghapus data ini?")
                .setPositiveButton("Ya") { dialog, _ ->
                    myRef.orderByChild("nama").equalTo(item.nama).get().addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val snapshot = task.result
                            if (snapshot.exists()) {
                                for (dataSnapshot in snapshot.children) {
                                    dataSnapshot.ref.removeValue()
                                }
                                daftarList.removeAt(position)
                                notifyItemRemoved(position)
                                notifyItemRangeChanged(position, daftarList.size)
                                Toast.makeText(context, "Data berhasil dihapus", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Data tidak ditemukan", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Toast.makeText(context, "Gagal menghapus data", Toast.LENGTH_SHORT).show()
                        }
                    }
                    dialog.dismiss()
                }
                .setNegativeButton("Tidak") { dialog, _ -> dialog.dismiss() }
            builder.create().show()
        }
    }

    override fun getItemCount(): Int = daftarList.size
}
