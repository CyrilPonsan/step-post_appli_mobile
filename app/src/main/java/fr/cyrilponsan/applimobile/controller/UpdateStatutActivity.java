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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.cyrilponsan.applimobile.R;
import fr.cyrilponsan.applimobile.model.Courrier;
import fr.cyrilponsan.applimobile.model.Statut;

public class UpdateStatutActivity extends AppCompatActivity {

     private TextView mAdresse;
     private TextView mBordereauTextView;
     private Button mButton;
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
          mButton = findViewById(R.id.updatestatut_activity_button);

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
                              System.out.println("coucou " + s.getEtat());
                              mStatuts.add(s);
                         }
                    } catch (JSONException e) {
                         e.printStackTrace();
                    }
                    if (mStatuts.size() != 0) {
                         System.out.println("size " + mStatuts.size());
                         Statut s = mStatuts.get(mStatuts.size() - 1);
                         System.out.println("etat " + s.getEtat());
                         mButton.setText(s.getEtat());                    }
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
                    params.put("Accept-Language", "fr");

                    return params;
               }
          };
          RequestQueue mRequestQueue = Volley.newRequestQueue(UpdateStatutActivity.this);
          mRequestQueue.add(jsonObjectRequest);

     }
}