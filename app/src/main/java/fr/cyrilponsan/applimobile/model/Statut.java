package fr.cyrilponsan.applimobile.model;

import org.json.JSONObject;

public class Statut {

     private final String date;
     private int etat;

     public Statut(JSONObject statut) {
          date = statut.optString("date");
          etat = statut.optInt("statut_id");
     }

     public String getDate() {
          return date;
     }

     public String getEtat() {
          String value = "";
          System.out.println("get etat " + etat);
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
