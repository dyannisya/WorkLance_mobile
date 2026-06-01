package com.example.kelolajasa.model;

public class GambarLayanan {

    private int idGambar;
    private int idLayanan;
    private String fileGambar;

    public GambarLayanan(int idGambar, int idLayanan, String fileGambar) {
        this.idGambar = idGambar;
        this.idLayanan = idLayanan;
        this.fileGambar = fileGambar;
    }

    public int getIdGambar() {
        return idGambar;
    }

    public void setIdGambar(int idGambar) {
        this.idGambar = idGambar;
    }

    public int getIdLayanan() {
        return idLayanan;
    }

    public void setIdLayanan(int idLayanan) {
        this.idLayanan = idLayanan;
    }

    public String getFileGambar() {
        return fileGambar;
    }

    public void setFileGambar(String fileGambar) {
        this.fileGambar = fileGambar;
    }
}
