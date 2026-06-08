package com.example.kelolajasa.model;

public class ChatListItem {
    private int idBooking;
    private int idLawan;           // id pengguna lawan bicara
    private String namaLawan;      // nama freelancer (utk user) atau nama client (utk freelancer)
    private String namaLayanan;
    private String pesanTerakhir;
    private String waktuTerakhir;
    private int jumlahUnread;

    public ChatListItem(int idBooking, int idLawan, String namaLawan,
                        String namaLayanan, String pesanTerakhir,
                        String waktuTerakhir, int jumlahUnread) {
        this.idBooking = idBooking;
        this.idLawan = idLawan;
        this.namaLawan = namaLawan;
        this.namaLayanan = namaLayanan;
        this.pesanTerakhir = pesanTerakhir;
        this.waktuTerakhir = waktuTerakhir;
        this.jumlahUnread = jumlahUnread;
    }

    /** Format waktu terakhir → "HH:mm" */
    public String getWaktuFormatted() {
        if (waktuTerakhir == null || waktuTerakhir.length() < 16) return "";
        try {
            return waktuTerakhir.substring(11, 16);
        } catch (Exception e) {
            return "";
        }
    }

    public int    getIdBooking()      { return idBooking; }
    public int    getIdLawan()        { return idLawan; }
    public String getNamaLawan()      { return namaLawan; }
    public String getNamaLayanan()    { return namaLayanan; }
    public String getPesanTerakhir()  { return pesanTerakhir; }
    public String getWaktuTerakhir()  { return waktuTerakhir; }
    public int    getJumlahUnread()   { return jumlahUnread; }
}