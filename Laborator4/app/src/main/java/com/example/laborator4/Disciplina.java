package com.example.laborator4;

import java.io.Serializable;

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

    public Disciplina() {
        this.numeDisciplina = "Necunoscuta";
        this.estePromovata = false;
        this.valoareNota = 1;
        this.punctajExamen = 0.0;
        this.semestru = Semestru.SEMESTRUL_1;
    }

    public Disciplina(String numeDisciplina, boolean estePromovata,
                      int valoareNota, double punctajExamen, Semestru semestru) {
        this.numeDisciplina = numeDisciplina;
        this.estePromovata = estePromovata;
        this.valoareNota = valoareNota;
        this.punctajExamen = punctajExamen;
        this.semestru = semestru;
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

    @Override
    public String toString() {
        return "Disciplina{" +
                "numeDisciplina='" + numeDisciplina + '\'' +
                ", estePromovata=" + estePromovata +
                ", valoareNota=" + valoareNota +
                ", punctajExamen=" + punctajExamen +
                ", semestru=" + semestru +
                '}';
    }
}