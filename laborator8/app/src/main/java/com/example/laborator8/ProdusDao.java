package com.example.laborator8;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ProdusDao {

    @Insert
    void inserare(Produs produs);

    @Query("SELECT * FROM produse")
    List<Produs> selectareTot();

    @Query("SELECT * FROM produse WHERE nume = :numeParam")
    List<Produs> selectareDupaNume(String numeParam);

    @Query("SELECT * FROM produse WHERE cantitate BETWEEN :min AND :max")
    List<Produs> selectareDupaCantitate(int min, int max);

    @Query("DELETE FROM produse WHERE pret > :pretParam")
    void stergerePretMaiMare(double pretParam);

    @Query("DELETE FROM produse WHERE pret < :pretParam")
    void stergerePretMaiMic(double pretParam);

    @Query("UPDATE produse SET cantitate = cantitate + 1 WHERE UPPER(nume) LIKE :pattern")
    void crescCantitate(String pattern);
}
