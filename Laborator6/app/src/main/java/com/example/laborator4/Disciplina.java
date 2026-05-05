package com.example.laborator4;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Disciplina implements Parcelable {

    public enum Semestru {
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

    protected Disciplina(Parcel in) {
        numeDisciplina = in.readString();
        estePromovata = in.readByte() != 0;
        valoareNota = in.readInt();
        punctajExamen = in.readDouble();
        semestru = Semestru.values()[in.readInt()];
        long dateMillis = in.readLong();
        dataAdaugare = dateMillis == -1 ? null : new Date(dateMillis);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(numeDisciplina);
        dest.writeByte((byte) (estePromovata ? 1 : 0));
        dest.writeInt(valoareNota);
        dest.writeDouble(punctajExamen);
        dest.writeInt(semestru.ordinal());
        dest.writeLong(dataAdaugare != null ? dataAdaugare.getTime() : -1);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Disciplina> CREATOR = new Creator<Disciplina>() {
        @Override
        public Disciplina createFromParcel(Parcel in) {
            return new Disciplina(in);
        }

        @Override
        public Disciplina[] newArray(int size) {
            return new Disciplina[size];
        }
    };

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
