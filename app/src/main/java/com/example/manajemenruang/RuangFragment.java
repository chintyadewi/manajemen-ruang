package com.example.manajemenruang;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.manajemenruang.adapter.AdapterRuangRecylerView;
import com.example.manajemenruang.model.Ruang;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class RuangFragment extends Fragment {

    private View view;
    private DatabaseReference database;
    private RecyclerView rvRuang;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ImageButton btnTambah;
    private ArrayList<Ruang> daftarRuang;

    public RuangFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_ruang, container, false);
        rvRuang =view.findViewById(R.id.rv_ruang);
        btnTambah=view.findViewById(R.id.btn_tambah);

        btnTambah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(getActivity().getApplicationContext(), TambahRuangActivity.class);
                startActivity(i);
            }
        });

        rvRuang.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(getActivity().getApplicationContext());
        rvRuang.setLayoutManager(layoutManager);
        database = FirebaseDatabase.getInstance().getReference();

        database.child("ruang").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                daftarRuang =new ArrayList<>();
                for (DataSnapshot noteDataSnapshot:dataSnapshot.getChildren()){
                    Ruang ruang =noteDataSnapshot.getValue(Ruang.class);
                    ruang.setId(noteDataSnapshot.getKey());
                    ruang.setNama(noteDataSnapshot.child("nama").getValue().toString());
                    ruang.setLantai(Integer.parseInt(noteDataSnapshot.child("lantai").getValue().toString()));
                    ruang.setDeskripsi(noteDataSnapshot.child("deskripsi").getValue().toString());
                    ruang.setDipinjam(Boolean.parseBoolean(noteDataSnapshot.child("dipinjam").getValue().toString()));

                    daftarRuang.add(ruang);
                }
                adapter=new AdapterRuangRecylerView(daftarRuang, getActivity().getApplicationContext());
                rvRuang.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println(databaseError.getDetails()+" "+databaseError.getMessage());
            }
        });
        return view;
    }
}
