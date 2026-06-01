package com.example.kelolajasa.model;

public class Kabupaten {

    private int idKabupaten;
    private int idProvinsi;
    private String namaKabupaten;

    public Kabupaten() {
    }

    public Kabupaten(int idKabupaten, int idProvinsi, String namaKabupaten) {
        this.idKabupaten = idKabupaten;
        this.idProvinsi = idProvinsi;
        this.namaKabupaten = namaKabupaten;
    }

    public int getIdKabupaten() {
        return idKabupaten;
    }

    public void setIdKabupaten(int idKabupaten) {
        this.idKabupaten = idKabupaten;
    }

    public int getIdProvinsi() {
        return idProvinsi;
    }

    public void setIdProvinsi(int idProvinsi) {
        this.idProvinsi = idProvinsi;
    }

    public String getNamaKabupaten() {
        return namaKabupaten;
    }

    public void setNamaKabupaten(String namaKabupaten) {
        this.namaKabupaten = namaKabupaten;
    }
}