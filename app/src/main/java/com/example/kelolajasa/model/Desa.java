package com.example.kelolajasa.model;

public class Desa {

    private int idDesa;
    private int idKecamatan;
    private String namaDesa;

    public Desa(int idDesa, int idKecamatan, String namaDesa) {
        this.idDesa = idDesa;
        this.idKecamatan = idKecamatan;
        this.namaDesa = namaDesa;
    }

    public int getIdDesa() {
        return idDesa;
    }

    public void setIdDesa(int idDesa) {
        this.idDesa = idDesa;
    }

    public int getIdKecamatan() {
        return idKecamatan;
    }

    public void setIdKecamatan(int idKecamatan) {
        this.idKecamatan = idKecamatan;
    }

    public String getNamaDesa() {
        return namaDesa;
    }

    public void setNamaDesa(String namaDesa) {
        this.namaDesa = namaDesa;
    }
}
