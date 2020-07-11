package com.example.engineerquiz1;

import com.google.firebase.firestore.DocumentId;

public class QuizListModel {
    @DocumentId
    private String quiz_id;
    private String nama, deskripsi, gambar, visibility, level;
    private long pertanyaan;

    public QuizListModel(){}

    public QuizListModel(String quiz_id, String nama, String deskripsi, String gambar, String visibility, String level, long pertanyaan) {
        this.quiz_id = quiz_id;
        this.nama = nama;
        this.deskripsi = deskripsi;
        this.gambar = gambar;
        this.visibility = visibility;
        this.level = level;
        this.pertanyaan = pertanyaan;
    }

    public long getPertanyaan() {
        return pertanyaan;
    }

    public void setPertanyaan(long pertanyaan) {
        this.pertanyaan = pertanyaan;
    }

    public String getQuiz_id() {
        return quiz_id;
    }

    public void setQuiz_id(String quiz_id) {
        this.quiz_id = quiz_id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public String getGambar() {
        return gambar;
    }

    public void setGambar(String gambar) {
        this.gambar = gambar;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}
