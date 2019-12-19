package com.example.manajemenruang.adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.manajemenruang.EditPeminjamActivity;
import com.example.manajemenruang.MainActivity;
import com.example.manajemenruang.R;
import com.example.manajemenruang.model.Peminjam;
import com.example.manajemenruang.model.Ruang;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdapterPeminjamRecyclerView extends RecyclerView.Adapter<AdapterPeminjamRecyclerView.ViewHolder> {
    private DatabaseReference database;
    private ArrayList<Peminjam> daftarPeminjam;
    private Context context;

    final private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    final private String serverKey = "key=" + "AAAA50dW_co:APA91bFn3x5wlb_uIXH5DGrA67hgdF0J5n_HtwrU_D_2R9wn3ZThEwjEv47DXbu4ihTj7UG03ZFaceqlDYMRJ9TXPR0uOEUyhgUSa5-1qE4KuM_tEpDmbXUAAnyCxVF9dHNqVFnkpf2x";
    final private String contentType = "application/json";
    final String TAG = "NOTIFICATION TAG";

    String NOTIFICATION_TITLE;
    String NOTIFICATION_MESSAGE;
    String TOPIC;

    public AdapterPeminjamRecyclerView(ArrayList<Peminjam> daftarPeminjam, Context context) {
        database = FirebaseDatabase.getInstance().getReference();
        this.daftarPeminjam = daftarPeminjam;
        this.context = context;
    }

    @NonNull
    @Override
    public AdapterPeminjamRecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_peminjam, parent, false);
        ViewHolder vh=new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final AdapterPeminjamRecyclerView.ViewHolder holder, final int position) {
        String nim=daftarPeminjam.get(position).getNim();
        String nama=daftarPeminjam.get(position).getNama();
        String kelas=daftarPeminjam.get(position).getKelas();

        Query namaRuang=database.child("ruang").orderByKey().equalTo(daftarPeminjam.get(position).getId_ruang());
        namaRuang.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    String ruang= data.child("nama").getValue().toString();
                    holder.tvRuang.setText(ruang);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w("onCancelled", databaseError.toException());
            }
        });

        holder.cvPeminjam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(context.getApplicationContext(), EditPeminjamActivity.class);
                i.putExtra("data", daftarPeminjam.get(position));
                i.putExtra("ruang", holder.tvRuang.getText());
                context.startActivity(i);
            }
        });

        holder.btnSelesai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Query updateRuang=database.child("ruang").orderByKey().equalTo(daftarPeminjam.get(position).getId_ruang());
                updateRuang.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot data : dataSnapshot.getChildren()) {

                            Ruang r=new Ruang(data.child("nama").getValue().toString(), Integer.parseInt(data.child("lantai").getValue().toString())
                                    ,data.child("deskripsi").getValue().toString(), false);

                            updateRuang(r, data.getKey());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.w("onCancelled", databaseError.toException());
                    }
                });

                Peminjam peminjam=daftarPeminjam.get(position);
                database.child("peminjam").child(peminjam.getId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context.getApplicationContext(),"Peminjaman selesai", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        holder.tvNim.setText(String.valueOf(nim));
        holder.tvNama.setText(nama);
        holder.tvKelas.setText("Kelas "+kelas);
    }

    @Override
    public int getItemCount() {
        return daftarPeminjam.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView cvPeminjam;
        TextView tvNim, tvNama, tvKelas, tvRuang;
        Button btnSelesai;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cvPeminjam=itemView.findViewById(R.id.cv_peminjam);
            tvNim=itemView.findViewById(R.id.tv_nim);
            tvNama=itemView.findViewById(R.id.tv_nama);
            tvKelas=itemView.findViewById(R.id.tv_kelas);
            tvRuang=itemView.findViewById(R.id.tv_ruang);
            btnSelesai=itemView.findViewById(R.id.btn_selesai);
        }
    }

    private void updateRuang(Ruang ruang, String id) {
        database.child("ruang")
                .child(id)
                .setValue(ruang);
    }
}
