package com.example.manajemenruang.model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

@IgnoreExtraProperties
public class Ruang implements Serializable {
    public String id;
    public String nama;
    public int lantai;
    public String deskripsi;
    public boolean dipinjam;

    public Ruang(){};

    public Ruang(String nama, int lantai, String deskripsi, boolean dipinjam) {
        this.nama = nama;
        this.lantai = lantai;
        this.deskripsi = deskripsi;
        this.dipinjam = dipinjam;
    }

    public String getId() {
        return id;
    }

    public String getNama() {
        return nama;
    }

    public int getLantai() {
        return lantai;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public boolean getDipinjam() {
        return dipinjam;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public void setLantai(int lantai) {
        this.lantai = lantai;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public void setDipinjam(boolean dipinjam) {
        this.dipinjam = dipinjam;
    }
}
