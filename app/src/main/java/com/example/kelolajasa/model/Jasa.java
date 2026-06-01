package com.example.kelolajasa.model;

public class Jasa {

    private int idJasa;
    private int idKategori;
    private String namaJasa;

    public Jasa(int idJasa, int idKategori, String namaJasa) {
        this.idJasa = idJasa;
        this.idKategori = idKategori;
        this.namaJasa = namaJasa;
    }

    public int getIdJasa() {
        return idJasa;
    }

    public void setIdJasa(int idJasa) {
        this.idJasa = idJasa;
    }

    public int getIdKategori() {
        return idKategori;
    }

    public void setIdKategori(int idKategori) {
        this.idKategori = idKategori;
    }

    public String getNamaJasa() {
        return namaJasa;
    }

    public void setNamaJasa(String namaJasa) {
        this.namaJasa = namaJasa;
    }
}