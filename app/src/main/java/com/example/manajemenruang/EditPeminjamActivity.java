package com.example.manajemenruang;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.manajemenruang.model.Peminjam;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditPeminjamActivity extends AppCompatActivity {

    private EditText edtNim, edtNama, edtKelas;
    private TextView tvRuang;
    private Button btnSimpan;
    private DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        SharedPreferences preferences = getSharedPreferences("SETTINGS", MODE_PRIVATE);
        boolean useDarkMode = preferences.getBoolean("DARK_MODE", false);

        if (useDarkMode) {
            setTheme(R.style.ActivityThemeDark);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_peminjam);

        tvRuang=findViewById(R.id.tv_ruang);
        edtNim=findViewById(R.id.edt_nim);
        edtNama=findViewById(R.id.edt_nama);
        edtKelas=findViewById(R.id.edt_kelas);
        btnSimpan=findViewById(R.id.btn_simpan);

        database= FirebaseDatabase.getInstance().getReference();

        final Peminjam peminjam = (Peminjam) getIntent().getSerializableExtra("data");
        String ruang=getIntent().getStringExtra("ruang");

        tvRuang.setText("Ruang "+ruang);
        edtNim.setText(peminjam.getNim());
        edtNama.setText(peminjam.getNama());
        edtKelas.setText(peminjam.getKelas());

        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                peminjam.setNim(edtNim.getText().toString());
                peminjam.setNama(edtNama.getText().toString());
                peminjam.setKelas(edtKelas.getText().toString());

                updatePeminjam(peminjam);
            }
        });
    }

    private void updatePeminjam(Peminjam peminjam) {
        database.child("peminjam")
                .child(peminjam.getId())
                .setValue(peminjam)
                .addOnSuccessListener(this, new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(EditPeminjamActivity.this, "Peminjam berhasil diedit", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(EditPeminjamActivity.this, MainActivity.class));
                    }
                });
    }
}
