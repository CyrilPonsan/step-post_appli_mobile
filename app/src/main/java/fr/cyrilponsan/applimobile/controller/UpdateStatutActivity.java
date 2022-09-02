package fr.cyrilponsan.applimobile.controller;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
     private Button mDeleteButton;
     private TextView mAdresse;
     private final ArrayList<Button> mButtons = new ArrayList<>();
     private LinearLayout mButtonLayout;
     private String mBordereau;
     private Courrier mCourrier;
     private User mUser;
     private final String mUrl = "https://step-post-nodejs.herokuapp.com";
     private RequestQueue mRequestQueue;
     private final ArrayList<String> mEtatsList = new ArrayList<>();
     private Statut mLastStatut;

     public UpdateStatutActivity() {
     }

     @Override
     protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.activity_update_statut);

          mRequestQueue = Volley.newRequestQueue(UpdateStatutActivity.this);

          mAdresse = findViewById(R.id.update_statut_activity_adresse);
          mButtonLayout = findViewById(R.id.update_statut_activity_button_layout);
          mButton2 = findViewById(R.id.update_statut_activity_button2);
          mButton3 = findViewById(R.id.update_statut_activity_button3);
          mButton4 = findViewById(R.id.update_statut_activity_button4);
          mButton5 = findViewById(R.id.update_statut_activity_button5);
          mButton6 = findViewById(R.id.update_statut_activity_button6);
          mButton7 = findViewById(R.id.update_statut_activity_button7);
          mDeleteButton = findViewById(R.id.update_statut_activity_delete_button);
          Button retourButton = findViewById(R.id.update_statut_activity_retour_button);
          //mAvatar = findViewById(R.id.update_statut_activity_avatar);

          mDeleteButton.setOnClickListener(view -> deleteLastStatut());
          retourButton.setOnClickListener(view -> finish());

          mButtons.add(mButton2);
          mButtons.add(mButton3);
          mButtons.add(mButton4);
          mButtons.add(mButton5);
          mButtons.add(mButton6);
          mButtons.add(mButton7);

          for (int i = 0; i < mButtons.size(); i++) {
               mButtons.get(i).setOnClickListener(this);
          }

          Intent intent = getIntent();
          mBordereau = intent.getStringExtra("bordereau");
          mUser = intent.getParcelableExtra("user");

          chercherCourrier();
          getEtatsList();
     }

     private void chercherCourrier() {
          String url = mUrl + "/facteur/recherche-bordereau?bordereau=" + mBordereau;
          ArrayList<Statut> statutsList = new ArrayList<>();
          @SuppressLint("SetTextI18n") JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                  Request.Method.GET,
                  url,
                  null,
                  response -> {
               JSONObject courrier;
               JSONArray statuts;
               try {
                    courrier = response.getJSONObject("courrier");
                    mCourrier = new Courrier(courrier);
                    mAdresse.setText(("Bordereau n° : " + mBordereau + "\n").toUpperCase());
                    mAdresse.append("\n" + mCourrier.getFullName() + mCourrier.getFullAdresse());
               } catch (JSONException e) {
                    e.printStackTrace();
               }
               try {
                    statuts = response.getJSONArray("statuts");
                    for (int i = 0; i < statuts.length(); i++) {
                         statutsList.add(new Statut(statuts.getJSONObject(i)));
                    }
               } catch (JSONException | ParseException e) {
                    e.printStackTrace();
               }
               if (statutsList.size() != 0) {
                    mLastStatut = statutsList.get(statutsList.size() - 1);
                    mAdresse.append(mLastStatut.getStatutMessage());
               }
               if (mLastStatut.getEtat() == 1) {
                         mButton2.setVisibility(View.VISIBLE);
                         for (int i = 1; i < 6; i++) {
                              mButtons.get(i).setVisibility(View.GONE);
                         }
               } else {
                    mButton2.setVisibility(View.GONE);
                    for (int i = 1; i < 6; i++) {
                         mButtons.get(i).setVisibility(View.VISIBLE);
                    }

               }

          }, this::handleError) {
               @Override
               public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<>();
                    params.put("Authorization", "Bearer " + mUser.getToken());

                    return params;
               }
          };
          mRequestQueue.add(jsonObjectRequest);
     }

     private void updateStatut(int etat) {
          if (etat == mLastStatut.getEtat()) {
               toaster("Ce statut existe déjà !");
               return; }
          String url = mUrl + "/courriers/update-statut?state=" + etat + "&bordereau=" + mBordereau;
          JsonObjectRequest updateStatutRequest = new JsonObjectRequest(
                  Request.Method.GET,
                  url,
                  null,
                  response -> {
                       toaster("Statut mis à jour");
                       chercherCourrier();
                       mDeleteButton.setVisibility(View.VISIBLE);
                  },
                  this::handleError) {
               @Override
               public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<>();
                    params.put("Authorization", "Bearer " + mUser.getToken());

                    return params;
               }
          };
          mRequestQueue.add(updateStatutRequest);
     }

     @SuppressLint("SetTextI18n")
     private void handleError(VolleyError error) {
          System.out.println(error.networkResponse.statusCode);
          if(error.networkResponse.statusCode == 404) {
               mAdresse.setText("Courrier inexistant...");
               mButtonLayout.setVisibility(View.GONE);
               toaster("Courrier non trouvé ...");
          }
     }

     private void getEtatsList() {
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
                                 mEtatsList.add(tmp.optString("etat"));
                            } catch (JSONException e) {
                                 e.printStackTrace();
                            }
                       }
                  },
                  this::handleError) {
               @Override
               public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<>();
                    params.put("Authorization", "Bearer " + mUser.getToken());

                    return params;
               }
          };
          mRequestQueue.add(jsonArrayRequest);
     }

     @Override
     public void onClick(View view) {
          int etat = 1;
          if (view == mButton2) {
               etat = 2;
          } else if (view == mButton3) {
               etat = 3;
          } else if (view == mButton4) {
               etat = 4;
          } else if (view == mButton5) {
               etat = 5;
          } else if (view == mButton6) {
               etat = 6;
          } else if (view == mButton7) {
               etat = 7;
          }
          final int tmp = etat;
          if (etat == 2) {
               updateStatut(etat);
          } else {
               AlertDialog.Builder builder = new AlertDialog.Builder(this);
               builder.setTitle("Confirmez :")
                       .setMessage("Statut : " + mEtatsList.get(tmp - 2))
                       .setPositiveButton("OK", (dialog, which) -> updateStatut(tmp))
                       .setNegativeButton("ANNULER", (dialogInterface, i) -> {
                       })
                       .create()
                       .show();
          }
     }

     private void deleteLastStatut() {
          String url = mUrl + "/facteur/delete?bordereau=" + mBordereau;
          JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                  Request.Method.GET,
                  url,
                  null,
                  response -> {
                       toaster("Statut mis à jour !");
                       chercherCourrier();
                       mDeleteButton.setVisibility(View.GONE);
                  },
                  this::handleError)
          {
               @Override
               public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<>();
                    params.put("Authorization", "Bearer " + mUser.getToken());

                    return params;
               }
          };
          mRequestQueue.add(jsonObjectRequest);
     }

     private void toaster(String msg) {
          Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
     }
}