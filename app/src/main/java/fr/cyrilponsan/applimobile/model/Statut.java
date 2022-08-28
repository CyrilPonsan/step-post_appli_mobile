package fr.cyrilponsan.applimobile.model;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Statut {

     private final Date date;
     private final int etat;
     private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

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
          String value = "";
          switch (etat) {
               case 1:
                    value = "pas encore collecté";
                    break;
               case 2:
                    value = "pris en charge";
                    break;
               case 3:
                    value = "avisé";
                    break;
               case 4:
                    value = "mis en instance";
                    break;
               case 5:
                    value = "distribué";
                    break;
               case 6:
                    value = "NPAI";
                    break;
               case 7:
                    value = "retour à l'expéditeur";
                    break;
          }
          return value;
     }
}
