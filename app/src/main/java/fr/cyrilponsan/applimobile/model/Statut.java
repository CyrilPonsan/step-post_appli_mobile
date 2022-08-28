package fr.cyrilponsan.applimobile.model;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Statut {

     private final Date date;
     private final int etat;
     private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");

     public Statut(JSONObject statut) throws ParseException {
          etat = statut.optInt("statut_id");
          SimpleDateFormat tmpDateFormat = new SimpleDateFormat("yyyy-MM-dd");
          date = tmpDateFormat.parse(statut.optString("date"));
     }

     public String getDate() {
          return dateFormat.format(date);
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
}
