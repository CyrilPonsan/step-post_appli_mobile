package fr.cyrilponsan.applimobile.controller;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import fr.cyrilponsan.applimobile.R;
import fr.cyrilponsan.applimobile.model.Courrier;
import fr.cyrilponsan.applimobile.model.Statut;

public class UpdateStatutActivity extends AppCompatActivity {

     private TextView mAdresse;
     private ArrayList<Button> mButtons;
     private LinearLayout mButtonLayout;
     private String mBordereau;
     private Courrier mCourrier;
     private final ArrayList<Statut> mStatuts = new ArrayList<>();
     private final String mUrl = "https://step-post-nodejs.herokuapp.com/";

     @Override
     protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.activity_update_statut);

          RequestQueue mRequestQueue = Volley.newRequestQueue(UpdateStatutActivity.this);

          mAdresse = findViewById(R.id.updatestatut_activity_adresse);
          TextView bordereauTextView = findViewById(R.id.updatestatut_activity_bordereau);
          mButtonLayout = findViewById(R.id.updatestatut_activity_button_layout);

          Intent intent = getIntent();
          mBordereau = intent.getStringExtra("bordereau");
          bordereauTextView.setText(mBordereau);
          String url = mUrl + "recherchecourrier/bordereau?bordereau=" + mBordereau;

          @SuppressLint("SetTextI18n") JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
               JSONObject courrier;
               JSONArray statuts;
               try {
                    courrier = response.getJSONObject("courrier");
                    mCourrier = new Courrier(courrier);
                    String nom = mCourrier.getCivilite() + " " + mCourrier.getPrenom() + " " + mCourrier.getNom() + "\n";
                    String adresse = mCourrier.getAdresse() + "\n";
                    String ville = mCourrier.getCodePostal() + " " + mCourrier.getVille();
                    mAdresse.setText((nom + adresse + ville).toUpperCase());
               } catch (JSONException e) {
                    e.printStackTrace();
               }
               try {
                    statuts = response.getJSONArray("statuts");
                    for (int i = 0; i < statuts.length(); i++) {
                         Statut s = new Statut(statuts.getJSONObject(i));
                         mStatuts.add(s);
                    }
               } catch (JSONException | ParseException e) {
                    e.printStackTrace();
               }
               if (mStatuts.size() != 0) {
                    String text = mStatuts.get(mStatuts.size() - 1).getEtatMessage().toUpperCase();
                    String date = mStatuts.get(mStatuts.size() - 1).getDate();
                    mAdresse.append("\n\n" + text + " : " + date);
               }

               switch ( mStatuts.get(mStatuts.size() - 1).getEtat()) {
                    case 1:
                         caseNonCollecte(mRequestQueue);
                         break;
               }

          }, error -> System.out.println("Dans le cul lulu.")) {
               @Override
               public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<>();
                    params.put("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOjEsImlhdCI6MTY2MTYyNzEwNSwiZXhwIjoxNjYyMjMxOTA1fQ.XVlT5exmDCvEaSMXoGAXOQ8B4na0VtZu7qoo2J9-2JM");

                    return params;
               }
          };
          mRequestQueue.add(jsonObjectRequest);
     }

     @SuppressLint("SetTextI18n")
     private void caseNonCollecte(RequestQueue requestQueue) {
          Button button = new Button(this);
          button.setText("pris en charge");
          mButtonLayout.addView(button);

          button.setOnClickListener(view -> updateStatut(2, requestQueue));
     }

     private void updateStatut(int etat, RequestQueue requestQueue) {
          String url = mUrl + "courriers/update-statut?state=" + etat + "&bordereau=" + mBordereau;
          System.out.println("url " + url);
          JsonObjectRequest updateStatutRequest = new JsonObjectRequest(
                  Request.Method.GET,
                  url,
                  null,
                  response -> System.out.println("coucou"),
                  error -> System.out.println("dans le cul lulu")) {
               @Override
               public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<>();
                    params.put("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOjEsImlhdCI6MTY2MTYyNzEwNSwiZXhwIjoxNjYyMjMxOTA1fQ.XVlT5exmDCvEaSMXoGAXOQ8B4na0VtZu7qoo2J9-2JM");

                    return params;
               }
          };
          requestQueue.add(updateStatutRequest);
     }
}