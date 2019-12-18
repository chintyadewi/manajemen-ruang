package com.example.manajemenruang.adapter;

import android.app.Activity;
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

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.manajemenruang.PeminjamanActivity;
import com.example.manajemenruang.R;
import com.example.manajemenruang.TambahRuangActivity;
import com.example.manajemenruang.model.Ruang;
import com.example.manajemenruang.service.MySingleton;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AdapterRuangRecylerView extends RecyclerView.Adapter<AdapterRuangRecylerView.ViewHolder> {
    private DatabaseReference database;
    private ArrayList<Ruang> daftarRuang;
    private Context context;

    final private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    final private String serverKey = "key=" + "AAAA50dW_co:APA91bFn3x5wlb_uIXH5DGrA67hgdF0J5n_HtwrU_D_2R9wn3ZThEwjEv47DXbu4ihTj7UG03ZFaceqlDYMRJ9TXPR0uOEUyhgUSa5-1qE4KuM_tEpDmbXUAAnyCxVF9dHNqVFnkpf2x";
    final private String contentType = "application/json";
    final String TAG = "NOTIFICATION TAG";

    String NOTIFICATION_TITLE;
    String NOTIFICATION_MESSAGE;
    String TOPIC;

    public AdapterRuangRecylerView(ArrayList<Ruang> daftarRuang, Context context) {
        database = FirebaseDatabase.getInstance().getReference();
        this.daftarRuang = daftarRuang;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ruang, parent, false);
        ViewHolder vh=new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        String nama=daftarRuang.get(position).getNama();
        String lantai=String.valueOf(daftarRuang.get(position).getLantai());
        String deskripsi=daftarRuang.get(position).getDeskripsi();
        Boolean dipinjam=daftarRuang.get(position).getDipinjam();

        holder.cvRuang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(context.getApplicationContext(), TambahRuangActivity.class);
                i.putExtra("data", daftarRuang.get(position));
                context.startActivity(i);
            }
        });

        holder.cvRuang.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.dialog_view);
                dialog.setTitle("Hapus Ruang");
                dialog.show();

                Button delButton = (Button) dialog.findViewById(R.id.btn_delete);

                delButton.setOnClickListener(
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                                deleteRuang(daftarRuang.get(position));
                            }
                        }
                );
                return true;
            }
        });

        holder.btnPinjam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(context.getApplicationContext(), PeminjamanActivity.class);
                i.putExtra("data", daftarRuang.get(position));
                context.startActivity(i);
            }
        });

//        holder.btnSelesai.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Ruang ruang=daftarRuang.get(position);
//                ruang.setDipinjam(false);
//                updateRuang(ruang, holder,(Activity) context);
//            }
//        });

        holder.tvNama.setText(nama);
        holder.tvLantai.setText("Lantai "+lantai);
        holder.tvDeskripsi.setText(deskripsi);
        if (dipinjam){
            holder.btnPinjam.setEnabled(false);
        }else {
            holder.btnPinjam.setEnabled(true);
        }
    }

    @Override
    public int getItemCount() {
        return daftarRuang.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView cvRuang;
        TextView tvNama, tvLantai, tvDeskripsi;
        Button btnPinjam;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cvRuang=itemView.findViewById(R.id.cv_ruang);
            tvNama=itemView.findViewById(R.id.tv_nama);
            tvLantai=itemView.findViewById(R.id.tv_lantai);
            tvDeskripsi=itemView.findViewById(R.id.tv_deskripsi);
            btnPinjam=itemView.findViewById(R.id.btn_pinjam);
        }
    }

    private void updateRuang(final Ruang ruang, final ViewHolder holder, Activity activity){
        database.child("ruang") //akses parent index, ibaratnya seperti nama tabel
                .child(ruang.getId()) //select barang berdasarkan key
                .setValue(ruang) //set value barang yang baru
                .addOnSuccessListener(activity, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        holder.btnPinjam.setEnabled(true);
                        Toast.makeText(context.getApplicationContext(), "Selesai dipinjam", Toast.LENGTH_SHORT).show();

                        TOPIC = "/topics/peminjam";
                        NOTIFICATION_TITLE = "Peminjaman Ruang Selesai";
                        NOTIFICATION_MESSAGE = "Ruang "+ruang.getNama()+" telah selesai dipinjam";

                        JSONObject notification = new JSONObject();
                        JSONObject notifcationBody = new JSONObject();
                        try {
                            notifcationBody.put("title", NOTIFICATION_TITLE);
                            notifcationBody.put("message", NOTIFICATION_MESSAGE);

                            notification.put("to", TOPIC);
                            notification.put("data", notifcationBody);
                        } catch (JSONException e) {
                            Log.e(TAG, "onCreate: " + e.getMessage() );
                        }
                        sendNotification(notification);
                    }
                });
    }

    private void sendNotification(JSONObject notification){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, notification,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, "onResponse: " + response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(TAG, "onErrorResponse: Didn't work");
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", serverKey);
                params.put("Content-Type", contentType);
                return params;
            }
        };
        MySingleton.getInstance(context.getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }

    public void deleteRuang(Ruang ruang) {
        if (database != null) {
            database.child("ruang").child(ruang.getId()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(context.getApplicationContext(), "Ruang berhasil dihapus", Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
