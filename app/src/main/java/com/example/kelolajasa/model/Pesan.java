package com.example.kelolajasa.model;

public class Pesan {
    private int idPesan;
    private int idBooking;
    private int idPengirim;
    private int idPenerima;
    private String isiPesan;
    private String waktuKirim;
    private boolean dibaca;

    public Pesan(int idPesan, int idBooking, int idPengirim, int idPenerima,
                 String isiPesan, String waktuKirim, boolean dibaca) {
        this.idPesan = idPesan;
        this.idBooking = idBooking;
        this.idPengirim = idPengirim;
        this.idPenerima = idPenerima;
        this.isiPesan = isiPesan;
        this.waktuKirim = waktuKirim;
        this.dibaca = dibaca;
    }

    /** Format waktu: "HH:mm" atau "dd MMM" jika beda hari */
    public String getWaktuFormatted() {
        if (waktuKirim == null || waktuKirim.length() < 16) return "";
        try {
            // Format: "yyyy-MM-dd HH:mm:ss" → tampilkan "HH:mm"
            return waktuKirim.substring(11, 16);
        } catch (Exception e) {
            return waktuKirim;
        }
    }

    public int getIdPesan()    { return idPesan; }
    public int getIdBooking()  { return idBooking; }
    public int getIdPengirim() { return idPengirim; }
    public int getIdPenerima() { return idPenerima; }
    public String getIsiPesan()   { return isiPesan; }
    public String getWaktuKirim() { return waktuKirim; }
    public boolean isDibaca()  { return dibaca; }
}