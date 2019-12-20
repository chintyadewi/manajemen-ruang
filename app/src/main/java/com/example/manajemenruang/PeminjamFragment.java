package com.example.manajemenruang;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.manajemenruang.adapter.AdapterPeminjamRecyclerView;
import com.example.manajemenruang.model.Peminjam;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class PeminjamFragment extends Fragment{

    private View view;
    private DatabaseReference database;
    private RecyclerView rvPeminjam;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Peminjam> daftarPeminjam;

    public PeminjamFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Context contextThemeWrapper;

        SharedPreferences preferences = this.getActivity().getSharedPreferences("SETTINGS", MODE_PRIVATE);
        boolean useDarkMode = preferences.getBoolean("DARK_MODE", false);

        if (useDarkMode) {
            contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.ActivityThemeDark);
        } else {
            contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.ActivityThemeLight);
        }

        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
        View view = localInflater.inflate(R.layout.fragment_peminjam, container, false);

        rvPeminjam =view.findViewById(R.id.rv_peminjam);

        rvPeminjam.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(getActivity().getApplicationContext());
        rvPeminjam.setLayoutManager(layoutManager);
        database = FirebaseDatabase.getInstance().getReference();

        database.child("peminjam").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                daftarPeminjam =new ArrayList<>();
                for (DataSnapshot noteDataSnapshot:dataSnapshot.getChildren()){
                    Peminjam peminjam =noteDataSnapshot.getValue(Peminjam.class);
                    peminjam.setId(noteDataSnapshot.getKey());
                    peminjam.setNim(noteDataSnapshot.child("nim").getValue().toString());
                    peminjam.setNama(noteDataSnapshot.child("nama").getValue().toString());
                    peminjam.setKelas(noteDataSnapshot.child("kelas").getValue().toString());
                    peminjam.setId_ruang(noteDataSnapshot.child("id_ruang").getValue().toString());

                    daftarPeminjam.add(peminjam);
                }
                adapter=new AdapterPeminjamRecyclerView(daftarPeminjam, getActivity());
                rvPeminjam.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println(databaseError.getDetails()+" "+databaseError.getMessage());
            }
        });
        return view;
    }
}
