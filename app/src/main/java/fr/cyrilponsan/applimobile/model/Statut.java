package fr.cyrilponsan.applimobile.model;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Statut {

     private final Date date;
     private final int etat;
     private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy", Locale.FRANCE);

     public Statut(JSONObject statut) throws ParseException {
          etat = statut.optInt("statut_id");
          System.out.println(statut.optString("date"));
          SimpleDateFormat tmpDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss", Locale.FRANCE);
          date = tmpDateFormat.parse(statut.optString("date"));
     }

     public String getDate() {
          return dateFormat.format(date);
     }

     private String getTime() {
          SimpleDateFormat timeDateFormat = new SimpleDateFormat("HH'h'mm", Locale.FRANCE);
          return  timeDateFormat.format(date);
     }

     public int getEtat() {
          return etat;
     }

     public String getEtatMessage() {
          String[] etats = {
                  "pas encore collecté",
                  "pris en charge",
                  "avisé",
                  "mis en instance",
                  "distribué",
                  "NPAI",
                  "retour à l'expéditeur"
          };
          return etats[etat - 1];
     }

     public String getStatutMessage() {
          return ("\n" + getEtatMessage() + " le :\n" + getDate()).toUpperCase() + " à " + getTime();
     }
}
