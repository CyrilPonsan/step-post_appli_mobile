package fr.cyrilponsan.applimobile.model;

import org.json.JSONObject;

public class Courrier {

     private final int id;
     private final String bordereau;
     private final String civilite;
     private final String prenom;
     private final String nom;
     private final String adresse;
     private final String complement;
     private final String codePostal;
     private final String ville;


     public Courrier(JSONObject courrier) {
          id = courrier.optInt("id");
          bordereau = courrier.optString("bordereau");
          civilite = courrier.optString("civilite");
          prenom = courrier.optString("prenom");
          nom = courrier.optString("nom");
          adresse = courrier.optString("adresse");
          if (courrier.opt("complement") != null) {
               complement = courrier.optString("complement") + "\n";
          } else {
               complement = "";
          }
          codePostal = courrier.optString("codePostal");
          ville = courrier.optString("ville");
     }

     public String getFullName() {
          return (prenom + " " + nom + "\n").toUpperCase();
     }

     public String getFullAdresse() {
          return (adresse + "\n" + complement + codePostal + " " + ville + "\n\n").toUpperCase();
     }

     public int getId() { return id; }

     public String getCivilite() {
          return civilite;
     }

     public String getPrenom() {
          return prenom;
     }

     public String getNom() {
          return nom;
     }

     public String getAdresse() {
          return adresse;
     }

     public String getCodePostal() {
          return codePostal;
     }

     public String getVille() {
          return ville;
     }
}
