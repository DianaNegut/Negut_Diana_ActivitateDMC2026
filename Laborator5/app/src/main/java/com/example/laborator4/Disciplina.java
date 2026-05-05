package com.example.laborator4;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Disciplina implements Serializable {

    public enum Semestru implements Serializable {
        SEMESTRUL_1,
        SEMESTRUL_2,
        RESTANTA
    }

    private String numeDisciplina;
    private boolean estePromovata;
    private int valoareNota;
    private double punctajExamen;
    private Semestru semestru;
    private Date dataAdaugare;

    public Disciplina() {
        this.numeDisciplina = "Necunoscuta";
        this.estePromovata = false;
        this.valoareNota = 1;
        this.punctajExamen = 0.0;
        this.semestru = Semestru.SEMESTRUL_1;
        this.dataAdaugare = new Date();
    }

    public Disciplina(String numeDisciplina, boolean estePromovata,
                      int valoareNota, double punctajExamen, Semestru semestru, Date dataAdaugare) {
        this.numeDisciplina = numeDisciplina;
        this.estePromovata = estePromovata;
        this.valoareNota = valoareNota;
        this.punctajExamen = punctajExamen;
        this.semestru = semestru;
        this.dataAdaugare = dataAdaugare;
    }

    public String getNumeDisciplina() { return numeDisciplina; }
    public void setNumeDisciplina(String numeDisciplina) { this.numeDisciplina = numeDisciplina; }

    public boolean isEstePromovata() { return estePromovata; }
    public void setEstePromovata(boolean estePromovata) { this.estePromovata = estePromovata; }

    public int getValoareNota() { return valoareNota; }
    public void setValoareNota(int valoareNota) { this.valoareNota = valoareNota; }

    public double getPunctajExamen() { return punctajExamen; }
    public void setPunctajExamen(double punctajExamen) { this.punctajExamen = punctajExamen; }

    public Semestru getSemestru() { return semestru; }
    public void setSemestru(Semestru semestru) { this.semestru = semestru; }

    public Date getDataAdaugare() { return dataAdaugare; }
    public void setDataAdaugare(Date dataAdaugare) { this.dataAdaugare = dataAdaugare; }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        String dataStr = dataAdaugare != null ? sdf.format(dataAdaugare) : "-";
        return numeDisciplina +
                " | Nota: " + valoareNota +
                " | Sem: " + semestru +
                " | Promovata: " + (estePromovata ? "Da" : "Nu") +
                " | Punctaj: " + punctajExamen +
                " | Data: " + dataStr;
    }
}