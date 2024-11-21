package com.daniel.ukk

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
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
        val editButton: Button = itemView.findViewById(R.id.buttonedit)
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

        // Set OnClickListener untuk edit button
        holder.editButton.setOnClickListener {
            val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_update, null)
            val builder = AlertDialog.Builder(context).setView(dialogView).setTitle("Edit Data")

            val editNama = dialogView.findViewById<EditText>(R.id.editnama)
            val editUmur = dialogView.findViewById<EditText>(R.id.editumur)
            val editKelamin = dialogView.findViewById<Spinner>(R.id.editjk)
            val editAlamat = dialogView.findViewById<EditText>(R.id.editalamat)
            val editNoHp = dialogView.findViewById<EditText>(R.id.editno_hp)
            val editPilihanPoli = dialogView.findViewById<Spinner>(R.id.editpilihanpoli)

            editNama.setText(item.nama)
            editUmur.setText(item.umur)
            editKelamin.setSelection(context.resources.getStringArray(R.array.jeniskelamin).indexOf(item.jk))
            editAlamat.setText(item.alamat)
            editNoHp.setText(item.noHp)
            editPilihanPoli.setSelection(context.resources.getStringArray(R.array.pilihpoli).indexOf(item.pilihanPoli))

            builder.setPositiveButton("Simpan") { dialog, _ ->
                val updatedData = ModelDaftar(
                    nama = editNama.text.toString(),
                    umur = editUmur.text.toString(),
                    jk = editKelamin.selectedItem.toString(),
                    alamat = editAlamat.text.toString(),
                    noHp = editNoHp.text.toString(),
                    pilihanPoli = editPilihanPoli.selectedItem.toString()
                )

                myRef.orderByChild("nama").equalTo(item.nama).get().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val snapshot = task.result
                        if (snapshot.exists()) {
                            for (dataSnapshot in snapshot.children) {
                                dataSnapshot.ref.setValue(updatedData)
                            }
                            daftarList[position] = updatedData
                            notifyItemChanged(position)
                            Toast.makeText(context, "Data berhasil diperbarui", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Data tidak ditemukan", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "Gagal memperbarui data", Toast.LENGTH_SHORT).show()
                    }
                }
                dialog.dismiss()
            }

            builder.setNegativeButton("Batal") { dialog, _ -> dialog.dismiss() }
            builder.create().show()
        }

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
