package fr.cyrilponsan.applimobile.controller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.cyrilponsan.applimobile.R;
import fr.cyrilponsan.applimobile.model.Courrier;
import fr.cyrilponsan.applimobile.model.Statut;

public class UpdateStatutActivity extends AppCompatActivity {

     private TextView mAdresse;
     private TextView mBordereauTextView;
     private ArrayList<Button> mButtons;
     private String mBordereau;
     private Courrier mCourrier;
     private ArrayList<Statut> mStatuts = new ArrayList<>();
     private String mUrl = "https://step-post-nodejs.herokuapp.com/recherchecourrier/bordereau?bordereau=";

     @Override
     protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.activity_update_statut);

          mAdresse = findViewById(R.id.updatestatut_activity_adresse);
          mBordereauTextView = findViewById(R.id.updatestatut_activity_bordereau);

          Intent intent = getIntent();
          mBordereau = intent.getStringExtra("bordereau");
          mBordereauTextView.setText(mBordereau);
          mUrl = mUrl + mBordereau;

          JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, mUrl, null, new Response.Listener<JSONObject>() {
               @Override
               public void onResponse(JSONObject response) {
                    JSONObject courrier = null;
                    JSONArray statuts = null;
                    try {
                         courrier = response.getJSONObject("courrier");
                         mCourrier = new Courrier(courrier);
                         String nom = mCourrier.getCivilite() + " " + mCourrier.getPrenom() + " " + mCourrier.getNom() + "\n";
                         String adresse = mCourrier.getAdresse() + "\n";
                         String ville = mCourrier.getCodePostal() + " " + mCourrier.getVille();
                         mAdresse.setText(nom + adresse + ville);
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
                         String text = mStatuts.get(mStatuts.size() - 1).getEtatMessage();
                         String date = mStatuts.get(mStatuts.size() - 1).getDate();
                         mAdresse.append("\n\n" + text + " : " + date);
                    }

                    switch ( mStatuts.get(mStatuts.size() - 1).getEtat()) {
                         case 1:
                              caseNonCollecte(1, "pas encore collecté");
                              break;
                    }

               }
          }, new Response.ErrorListener() {
               @Override
               public void onErrorResponse(VolleyError error) {
                    System.out.println("Dans le cul lulu.");
               }
          }) {
               @Override
               public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Authorization", "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOjEsImlhdCI6MTY2MTYyNzEwNSwiZXhwIjoxNjYyMjMxOTA1fQ.XVlT5exmDCvEaSMXoGAXOQ8B4na0VtZu7qoo2J9-2JM");

                    return params;
               }
          };
          RequestQueue mRequestQueue = Volley.newRequestQueue(UpdateStatutActivity.this);
          mRequestQueue.add(jsonObjectRequest);

     }

     private void caseNonCollecte(int etat, String text) {
          Button button = new Button(this);
          button.setText(text);
     }
}