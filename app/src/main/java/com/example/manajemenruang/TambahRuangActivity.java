package com.example.manajemenruang;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.manajemenruang.R;
import com.example.manajemenruang.model.Ruang;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TambahRuangActivity extends AppCompatActivity {
    private EditText edtNama, edtLantai, edtDeskripsi;
    private Button btnSimpan;
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_ruang);

        edtNama=findViewById(R.id.edt_nama);
        edtLantai=findViewById(R.id.edt_lantai);
        edtDeskripsi=findViewById(R.id.edt_deskripsi);
        btnSimpan=findViewById(R.id.btn_simpan);

        database= FirebaseDatabase.getInstance().getReference();

        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!TextUtils.isEmpty(edtNama.getText().toString()) && !TextUtils.isEmpty(edtLantai.getText().toString()) && !TextUtils.isEmpty(edtDeskripsi.getText().toString()))
                    tambahRuang(new Ruang(edtNama.getText().toString(), Integer.parseInt(edtLantai.getText().toString()), edtDeskripsi.getText().toString(), false));
                else
                    Toast.makeText(TambahRuangActivity.this,"Data barang tidak boleh kosong", Toast.LENGTH_SHORT).show();

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(edtNama.getWindowToken(), 0);
            }
        });
    }

    private void tambahRuang(Ruang ruang) {
        database.child("ruang").push().setValue(ruang).addOnSuccessListener(this, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                startActivity(new Intent(TambahRuangActivity.this, MainActivity.class));
            }
        });
    }
}
