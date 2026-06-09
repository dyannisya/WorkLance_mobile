package com.example.kelolajasa.model;

public class Pengguna {

    private int idPengguna;
    private int idRole;

    private String username;
    private String namaPengguna;
    private String tanggalLahir;
    private String noTelp;
    private String email;
    private String password;

    private String idProvinsi;
    private String idKabupaten;
    private String idKecamatan;
    private String idDesa;

    private String alamatLengkap;
    private String fotoProfil;

    // Konstruktor sudah diubah menjadi String untuk 4 variabel lokasi
    public Pengguna(int idPengguna, int idRole, String username, String namaPengguna, String tanggalLahir, String noTelp, String email, String password, String idProvinsi, String idKabupaten, String idKecamatan, String idDesa, String alamatLengkap, String fotoProfil) {
        this.idPengguna = idPengguna;
        this.idRole = idRole;
        this.username = username;
        this.namaPengguna = namaPengguna;
        this.tanggalLahir = tanggalLahir;
        this.noTelp = noTelp;
        this.email = email;
        this.password = password;
        this.idProvinsi = idProvinsi;
        this.idKabupaten = idKabupaten;
        this.idKecamatan = idKecamatan;
        this.idDesa = idDesa;
        this.alamatLengkap = alamatLengkap;
        this.fotoProfil = fotoProfil;
    }

    public int getIdPengguna() {
        return idPengguna;
    }

    public void setIdPengguna(int idPengguna) {
        this.idPengguna = idPengguna;
    }

    public int getIdRole() {
        return idRole;
    }

    public void setIdRole(int idRole) {
        this.idRole = idRole;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNamaPengguna() {
        return namaPengguna;
    }

    public void setNamaPengguna(String namaPengguna) {
        this.namaPengguna = namaPengguna;
    }

    public String getTanggalLahir() {
        return tanggalLahir;
    }

    public void setTanggalLahir(String tanggalLahir) {
        this.tanggalLahir = tanggalLahir;
    }

    public String getNoTelp() {
        return noTelp;
    }

    public void setNoTelp(String noTelp) {
        this.noTelp = noTelp;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Getter dan Setter untuk lokasi sudah diubah menjadi String
    public String getIdProvinsi() {
        return idProvinsi;
    }

    public void setIdProvinsi(String idProvinsi) {
        this.idProvinsi = idProvinsi;
    }

    public String getIdKabupaten() {
        return idKabupaten;
    }

    public void setIdKabupaten(String idKabupaten) {
        this.idKabupaten = idKabupaten;
    }

    public String getIdKecamatan() {
        return idKecamatan;
    }

    public void setIdKecamatan(String idKecamatan) {
        this.idKecamatan = idKecamatan;
    }

    public String getIdDesa() {
        return idDesa;
    }

    public void setIdDesa(String idDesa) {
        this.idDesa = idDesa;
    }

    public String getAlamatLengkap() {
        return alamatLengkap;
    }

    public void setAlamatLengkap(String alamatLengkap) {
        this.alamatLengkap = alamatLengkap;
    }

    public String getFotoProfil() {
        return fotoProfil;
    }

    public void setFotoProfil(String fotoProfil) {
        this.fotoProfil = fotoProfil;
    }
}