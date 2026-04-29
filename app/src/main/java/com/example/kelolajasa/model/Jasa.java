package com.example.kelolajasa.model;

public class Jasa {
    private int id;
    private String nama;
    private String kategori;

    public Jasa(int id, String nama, String kategori) {
        this.id = id;
        this.nama = nama;
        this.kategori = kategori;
    }

    public int getId() { return id; }
    public String getNama() { return nama; }
    public String getKategori() { return kategori; }
}
