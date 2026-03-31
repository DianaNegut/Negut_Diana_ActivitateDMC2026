package com.example.laborator4;

import android.content.Context;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class FileHelper {

    private static final SimpleDateFormat SDF =
            new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

    public static void salveazaDisciplina(Context context, String numeFisier, Disciplina disciplina) {
        File file = new File(context.getFilesDir(), numeFisier);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
            bw.write(inText(disciplina));
            bw.newLine();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Disciplina> incarcaDiscipline(Context context, String numeFisier) {
        ArrayList<Disciplina> lista = new ArrayList<>();
        File file = new File(context.getFilesDir(), numeFisier);
        if (!file.exists()) return lista;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String linie;
            while ((linie = br.readLine()) != null) {
                if (!linie.trim().isEmpty()) {
                    Disciplina d = dinText(linie);
                    if (d != null) lista.add(d);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    private static String inText(Disciplina d) {
        String data = d.getDataAdaugare() != null ? SDF.format(d.getDataAdaugare()) : "";
        return d.getNumeDisciplina() + "|" +
                d.isEstePromovata() + "|" +
                d.getValoareNota() + "|" +
                d.getPunctajExamen() + "|" +
                d.getSemestru().name() + "|" +
                data;
    }

    private static Disciplina dinText(String linie) {
        String[] parts = linie.split("\\|");
        if (parts.length < 6) return null;
        try {
            String nume = parts[0];
            boolean promovata = Boolean.parseBoolean(parts[1]);
            int nota = Integer.parseInt(parts[2]);
            double punctaj = Double.parseDouble(parts[3]);
            Disciplina.Semestru semestru = Disciplina.Semestru.valueOf(parts[4]);
            Date data = SDF.parse(parts[5]);
            return new Disciplina(nume, promovata, nota, punctaj, semestru, data);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
