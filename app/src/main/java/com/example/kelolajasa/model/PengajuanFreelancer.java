package com.example.kelolajasa.model;

public class PengajuanFreelancer {

    private int idPengajuan;
    private int idPengguna;

    private String nik;
    private String deskripsi;
    private String status;
    private String catatanAdmin;
    private String tanggalPengajuan;

    public PengajuanFreelancer(int idPengajuan, int idPengguna, String nik, String deskripsi, String status, String catatanAdmin, String tanggalPengajuan) {
        this.idPengajuan = idPengajuan;
        this.idPengguna = idPengguna;
        this.nik = nik;
        this.deskripsi = deskripsi;
        this.status = status;
        this.catatanAdmin = catatanAdmin;
        this.tanggalPengajuan = tanggalPengajuan;
    }

    public int getIdPengajuan() {
        return idPengajuan;
    }

    public void setIdPengajuan(int idPengajuan) {
        this.idPengajuan = idPengajuan;
    }

    public int getIdPengguna() {
        return idPengguna;
    }

    public void setIdPengguna(int idPengguna) {
        this.idPengguna = idPengguna;
    }

    public String getNik() {
        return nik;
    }

    public void setNik(String nik) {
        this.nik = nik;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCatatanAdmin() {
        return catatanAdmin;
    }

    public void setCatatanAdmin(String catatanAdmin) {
        this.catatanAdmin = catatanAdmin;
    }

    public String getTanggalPengajuan() {
        return tanggalPengajuan;
    }

    public void setTanggalPengajuan(String tanggalPengajuan) {
        this.tanggalPengajuan = tanggalPengajuan;
    }
}
