package com.example.laborator8;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "produse")
public class Produs {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "nume")
    public String nume;

    @ColumnInfo(name = "categorie")
    public String categorie;

    @ColumnInfo(name = "pret")
    public double pret;

    @ColumnInfo(name = "cantitate")
    public int cantitate;

    public Produs(String nume, String categorie, double pret, int cantitate) {
        this.nume = nume;
        this.categorie = categorie;
        this.pret = pret;
        this.cantitate = cantitate;
    }

    @Override
    public String toString() {
        return "[" + id + "] " + nume
                + " | Cat: " + categorie
                + " | Pret: " + String.format("%.2f", pret) + " lei"
                + " | Cant: " + cantitate;
    }
}
