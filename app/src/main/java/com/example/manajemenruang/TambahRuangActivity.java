package com.example.manajemenruang;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.manajemenruang.R;
import com.example.manajemenruang.model.Ruang;
import com.example.manajemenruang.service.MySingleton;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class TambahRuangActivity extends AppCompatActivity {
    private EditText edtNama, edtLantai, edtDeskripsi;
    private Button btnSimpan;
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
        SharedPreferences preferences = getSharedPreferences("SETTINGS", MODE_PRIVATE);
        boolean useDarkMode = preferences.getBoolean("DARK_MODE", false);

        if (useDarkMode) {
            setTheme(R.style.ActivityThemeDark);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_ruang);

        edtNama=findViewById(R.id.edt_nama);
        edtLantai=findViewById(R.id.edt_lantai);
        edtDeskripsi=findViewById(R.id.edt_deskripsi);
        btnSimpan=findViewById(R.id.btn_simpan);

        database= FirebaseDatabase.getInstance().getReference();

        final Ruang ruang = (Ruang) getIntent().getSerializableExtra("data");
        if (ruang!=null){
            edtNama.setText(ruang.getNama());
            edtLantai.setText(String.valueOf(ruang.getLantai()));
            edtDeskripsi.setText(ruang.getDeskripsi());
            btnSimpan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ruang.setNama(edtNama.getText().toString());
                    ruang.setLantai(Integer.parseInt(edtLantai.getText().toString()));
                    ruang.setDeskripsi(edtDeskripsi.getText().toString());

                    updateRuang(ruang);
                }
            });
        }else{
            btnSimpan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!TextUtils.isEmpty(edtNama.getText().toString()) && !TextUtils.isEmpty(edtLantai.getText().toString()) && !TextUtils.isEmpty(edtDeskripsi.getText().toString()))
                        tambahRuang(new Ruang(edtNama.getText().toString(), Integer.parseInt(edtLantai.getText().toString()), edtDeskripsi.getText().toString(), false));
                    else
                        Toast.makeText(TambahRuangActivity.this,"Data ruang tidak boleh kosong", Toast.LENGTH_SHORT).show();

                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(edtNama.getWindowToken(), 0);
                }
            });
        }
    }

    private void tambahRuang(Ruang ruang) {
        database.child("ruang").push().setValue(ruang).addOnSuccessListener(this, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                TOPIC = "/topics/peminjam";
                NOTIFICATION_TITLE = "Ruang Kosong";
                NOTIFICATION_MESSAGE = "Ruang kosong baru: "+edtNama.getText().toString();

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

                startActivity(new Intent(TambahRuangActivity.this, MainActivity.class));
            }
        });
    }

    private void updateRuang(Ruang r) {
        database.child("ruang")
                .child(r.getId())
                .setValue(r)
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(TambahRuangActivity.this, "Ruang berhasil diedit", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(TambahRuangActivity.this, MainActivity.class));
                    }
                });
    }

    private void sendNotification(JSONObject notification){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, notification,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, "onResponse: " + response.toString());
                        startActivity(new Intent(TambahRuangActivity.this, MainActivity.class));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(TambahRuangActivity.this, "Request error", Toast.LENGTH_LONG).show();
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

}
