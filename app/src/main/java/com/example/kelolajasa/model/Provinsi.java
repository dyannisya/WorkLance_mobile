package com.example.kelolajasa.model;

public class Provinsi {

    private int idProvinsi;
    private String namaProvinsi;

    public Provinsi(int idProvinsi, String namaProvinsi) {
        this.idProvinsi = idProvinsi;
        this.namaProvinsi = namaProvinsi;
    }

    public int getIdProvinsi() {
        return idProvinsi;
    }

    public void setIdProvinsi(int idProvinsi) {
        this.idProvinsi = idProvinsi;
    }

    public String getNamaProvinsi() {
        return namaProvinsi;
    }

    public void setNamaProvinsi(String namaProvinsi) {
        this.namaProvinsi = namaProvinsi;
    }
}