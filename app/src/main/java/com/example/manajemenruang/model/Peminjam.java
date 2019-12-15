package com.example.manajemenruang.model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

@IgnoreExtraProperties
public class Peminjam implements Serializable {
    private String id;
    private String nim;
    private String nama;
    private String kelas;
    private String id_ruang;

    public Peminjam() {
    }

    public Peminjam(String nim, String nama, String kelas, String id_ruang) {
        this.nim = nim;
        this.nama = nama;
        this.kelas = kelas;
        this.id_ruang = id_ruang;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNim() {
        return nim;
    }

    public void setNim(String nim) {
        this.nim = nim;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getKelas() {
        return kelas;
    }

    public void setKelas(String kelas) {
        this.kelas = kelas;
    }

    public String getId_ruang() {
        return id_ruang;
    }

    public void setId_ruang(String id_ruang) {
        this.id_ruang = id_ruang;
    }
}
