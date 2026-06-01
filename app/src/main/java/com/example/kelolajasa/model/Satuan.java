package com.example.kelolajasa.model;

public class Satuan {

    private int idSatuan;
    private String namaSatuan;

    public Satuan(int idSatuan, String namaSatuan) {
        this.idSatuan = idSatuan;
        this.namaSatuan = namaSatuan;
    }

    public int getIdSatuan() {
        return idSatuan;
    }

    public void setIdSatuan(int idSatuan) {
        this.idSatuan = idSatuan;
    }

    public String getNamaSatuan() {
        return namaSatuan;
    }

    public void setNamaSatuan(String namaSatuan) {
        this.namaSatuan = namaSatuan;
    }
}
