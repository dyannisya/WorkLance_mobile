package com.example.kelolajasa.model;

public class Layanan {

    private int idLayanan;
    private int idPengguna;
    private int idJasa;
    private int idSatuan;

    private int tarif;

    private String deskripsi;
    private String namaJasa;

    public Layanan(int idLayanan, int idPengguna, int idJasa, int idSatuan, int tarif, String deskripsi, String namaJasa) {
        this.idLayanan = idLayanan;
        this.idPengguna = idPengguna;
        this.idJasa = idJasa;
        this.idSatuan = idSatuan;
        this.tarif = tarif;
        this.deskripsi = deskripsi;
        this.namaJasa = namaJasa;
    }

    public int getIdLayanan() {
        return idLayanan;
    }

    public void setIdLayanan(int idLayanan) {
        this.idLayanan = idLayanan;
    }

    public int getIdPengguna() {
        return idPengguna;
    }

    public void setIdPengguna(int idPengguna) {
        this.idPengguna = idPengguna;
    }

    public int getIdJasa() {
        return idJasa;
    }

    public void setIdJasa(int idJasa) {
        this.idJasa = idJasa;
    }

    public int getIdSatuan() {
        return idSatuan;
    }

    public void setIdSatuan(int idSatuan) {
        this.idSatuan = idSatuan;
    }

    public int getTarif() {
        return tarif;
    }

    public void setTarif(int tarif) {
        this.tarif = tarif;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public String getNamaJasa() {
        return namaJasa;
    }

    public void setNamaJasa(String namaJasa) {
        this.namaJasa = namaJasa;
    }
}
