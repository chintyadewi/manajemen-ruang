package com.example.manajemenruang;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.manajemenruang.model.Ruang;
import com.example.manajemenruang.service.MySingleton;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PeminjamanActivity extends AppCompatActivity {

    private Button btnOk;
    private EditText edtNama;
    private EditText edtNim;
    private EditText edtKelas;
    private TextView tvNama;
    private TextView tvLantai;
    private TextView tvDeskripsi;
    private DatabaseReference database;

    final private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    final private String serverKey = "key=" + "AAAA50dW_co:APA91bFn3x5wlb_uIXH5DGrA67hgdF0J5n_HtwrU_D_2R9wn3ZThEwjEv47DXbu4ihTj7UG03ZFaceqlDYMRJ9TXPR0uOEUyhgUSa5-1qE4KuM_tEpDmbXUAAnyCxVF9dHNqVFnkpf2x";
    final private String contentType = "application/json";
    final String TAG = "NOTIFICATION TAG";

    String NOTIFICATION_TITLE;
    String NOTIFICATION_MESSAGE;
    String TOPIC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peminjaman);

        database = FirebaseDatabase.getInstance().getReference();

        edtNama=findViewById(R.id.edt_nama);
        edtNim=findViewById(R.id.edt_nim);
        edtKelas=findViewById(R.id.edt_kelas);
        btnOk=findViewById(R.id.btn_ok);

        tvNama=findViewById(R.id.tv_nama);
        tvLantai=findViewById(R.id.tv_lantai);
        tvDeskripsi=findViewById(R.id.tv_deskripsi);

        final Ruang ruang = (Ruang) getIntent().getSerializableExtra("data");
        if(ruang!=null){
            tvNama.setText(ruang.getNama());
            tvLantai.setText(String.valueOf(ruang.getLantai()));
            tvDeskripsi.setText(String.valueOf(ruang.getDeskripsi()));
        }

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ruang.setDipinjam(true);
                updateRuang(ruang);

                TOPIC = "/topics/peminjam";
                NOTIFICATION_TITLE = "Peminjaman Ruang";
                NOTIFICATION_MESSAGE = "Ruang "+tvNama.getText().toString()+" telah dipinjam oleh "+edtNama.getText().toString()+"" +
                        " kelas "+edtKelas.getText().toString();

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

    public static Intent getActIntent(Activity activity) {
        return new Intent(activity, PeminjamanActivity.class);
    }

    private void sendNotification(JSONObject notification){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, notification,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, "onResponse: " + response.toString());
                        startActivity(new Intent(PeminjamanActivity.this, MainActivity.class));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(PeminjamanActivity.this, "Request error", Toast.LENGTH_LONG).show();
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
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }

    private void updateRuang(Ruang ruang) {
        database.child("ruang") //akses parent index, ibaratnya seperti nama tabel
                .child(ruang.getId()) //select barang berdasarkan key
                .setValue(ruang) //set value barang yang baru
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(PeminjamanActivity.this, "Ruang berhasil dipinjam", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}