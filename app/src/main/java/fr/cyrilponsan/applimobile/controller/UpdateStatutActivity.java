package fr.cyrilponsan.applimobile.controller;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import fr.cyrilponsan.applimobile.R;
import fr.cyrilponsan.applimobile.model.Courrier;
import fr.cyrilponsan.applimobile.model.Statut;
import fr.cyrilponsan.applimobile.model.User;

public class UpdateStatutActivity extends AppCompatActivity implements View.OnClickListener{

     private Button mButton2;
     private Button mButton3;
     private Button mButton4;
     private Button mButton5;
     private Button mButton6;
     private Button mButton7;
     private TextView mAdresse;
     private ArrayList<Button> mButtons = new ArrayList<>();
     private LinearLayout mButtonLayout;
     private String mBordereau;
     private Courrier mCourrier;
     private User mUser;
     private final ArrayList<Statut> mStatuts = new ArrayList<>();
     private final String mUrl = "https://step-post-nodejs.herokuapp.com";
     private ImageView mAvatar;

     @Override
     protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.activity_update_statut);

          RequestQueue mRequestQueue = Volley.newRequestQueue(UpdateStatutActivity.this);

          mAdresse = findViewById(R.id.update_statut_activity_adresse);
          mButtonLayout = findViewById(R.id.update_statut_activity_button_layout);
          mButton2 = findViewById(R.id.update_statut_activity_button2);
          mButton3 = findViewById(R.id.update_statut_activity_button3);
          mButton4 = findViewById(R.id.update_statut_activity_button4);
          mButton5 = findViewById(R.id.update_statut_activity_button5);
          mButton6 = findViewById(R.id.update_statut_activity_button6);
          mButton7 = findViewById(R.id.update_statut_activity_button7);
          //mAvatar = findViewById(R.id.update_statut_activity_avatar);
          mButtons.add(mButton2);
          mButtons.add(mButton3);
          mButtons.add(mButton4);
          mButtons.add(mButton5);
          mButtons.add(mButton6);
          mButtons.add(mButton7);

          Intent intent = getIntent();
          mBordereau = intent.getStringExtra("bordereau");
          mUser = intent.getParcelableExtra("user");

          mAdresse.setText(("Bordereau n° : " + mBordereau + "\n").toUpperCase());

          chercherCourrier(mRequestQueue);
          getEtatsList(mRequestQueue);
     }

     private void nonPrisEnCharge() {

     }

     private void chercherCourrier(RequestQueue requestQueue) {
          String url = mUrl + "/facteur/recherche-bordereau?bordereau=" + mBordereau;
          JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                  Request.Method.GET,
                  url,
                  null,
                  response -> {
               JSONObject courrier;
               JSONArray statuts;
               try {
                    courrier = response.getJSONObject("courrier");
                    mCourrier = new Courrier(courrier);
                    mAdresse.append("\n" + mCourrier.getFullName() + mCourrier.getFullAdresse());
               } catch (JSONException e) {
                    e.printStackTrace();
               }
               try {
                    statuts = response.getJSONArray("statuts");
                    for (int i = 0; i < statuts.length(); i++) {
                         mStatuts.add(new Statut(statuts.getJSONObject(i)));
                    }
               } catch (JSONException | ParseException e) {
                    e.printStackTrace();
               }
               if (mStatuts.size() != 0) {
                    mAdresse.append(mStatuts.get(mStatuts.size() - 1).getStatutMessage());
               }
          }, error -> handleError(error)) {
               @Override
               public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<>();
                    params.put("Authorization", "Bearer " + mUser.getToken());

                    return params;
               }
          };
          requestQueue.add(jsonObjectRequest);
     }

     @SuppressLint("SetTextI18n")
     private void caseNonCollecte(RequestQueue requestQueue) {
          Button button = new Button(this);
          button.setText("pris en charge");
          mButtonLayout.addView(button);

          button.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {}
          });
     }

     private void updateStatut(int etat, RequestQueue requestQueue) {
          String url = mUrl + "/courriers/update-statut?state=" + etat + "&bordereau=" + mBordereau;
          JsonObjectRequest updateStatutRequest = new JsonObjectRequest(
                  Request.Method.GET,
                  url,
                  null,
                  response -> System.out.println("coucou"),
                  error -> handleError(error)) {
               @Override
               public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<>();
                    params.put("Authorization", "Bearer " + mUser.getToken());

                    return params;
               }
          };
          requestQueue.add(updateStatutRequest);
     }

     private void handleError(VolleyError error) {
          String msg;
          System.out.println(error.networkResponse.statusCode);
          if(error.networkResponse.statusCode == 404) {
               mAdresse.setText("Courrier inexistant...");
               msg = "dans le cul lulu !";
               Toast toast = Toast.makeText(UpdateStatutActivity.this, msg, Toast.LENGTH_SHORT);
               toast.show();
          }
     }

     private void getEtatsList(RequestQueue requestQueue) {
          String url = mUrl + "/courriers/statuts";
          JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                  Request.Method.GET,
                  url,
                  null,
                  response -> {
                       for ( int i = 1; i < response.length(); i++) {
                            try {
                                 JSONObject tmp = (response.getJSONObject(i));
                                 mButtons.get(i - 1).setText(tmp.optString("etat"));
                            } catch (JSONException e) {
                                 e.printStackTrace();
                            }
                       }
                  },
                  error -> handleError(error)) {
               @Override
               public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<>();
                    params.put("Authorization", "Bearer " + mUser.getToken());

                    return params;
               }
          };
          requestQueue.add(jsonArrayRequest);
     }

     @Override
     public void onClick(View view) {

     }
}