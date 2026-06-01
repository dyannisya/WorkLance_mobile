package com.example.kelolajasa.model;

public class Kecamatan {

    private int idKecamatan;
    private int idKabupaten;
    private String namaKecamatan;

    public Kecamatan(int idKecamatan, int idKabupaten, String namaKecamatan) {
        this.idKecamatan = idKecamatan;
        this.idKabupaten = idKabupaten;
        this.namaKecamatan = namaKecamatan;
    }

    public int getIdKecamatan() {
        return idKecamatan;
    }

    public void setIdKecamatan(int idKecamatan) {
        this.idKecamatan = idKecamatan;
    }

    public int getIdKabupaten() {
        return idKabupaten;
    }

    public void setIdKabupaten(int idKabupaten) {
        this.idKabupaten = idKabupaten;
    }

    public String getNamaKecamatan() {
        return namaKecamatan;
    }

    public void setNamaKecamatan(String namaKecamatan) {
        this.namaKecamatan = namaKecamatan;
    }
}