package fr.cyrilponsan.applimobile.controller;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.DialogInterface;
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
     private Button mDeleteButton;
     private Button mRetourButton;
     private TextView mAdresse;
     private ArrayList<Button> mButtons = new ArrayList<>();
     private LinearLayout mButtonLayout;
     private String mBordereau;
     private Courrier mCourrier;
     private User mUser;
     private final ArrayList<Statut> mStatuts = new ArrayList<>();
     private final String mUrl = "https://step-post-nodejs.herokuapp.com";
     private ImageView mAvatar;
     private RequestQueue mRequestQueue;
     private ArrayList<String> mEtatsList = new ArrayList<>();

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
          mRetourButton = findViewById(R.id.update_statut_activity_retour_button);
          //mAvatar = findViewById(R.id.update_statut_activity_avatar);

          mDeleteButton.setOnClickListener(view -> deleteLastStatut(mRequestQueue));
          mRetourButton.setOnClickListener(view -> finish());

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

          chercherCourrier(mRequestQueue);
          getEtatsList(mRequestQueue);
     }

     private void chercherCourrier(RequestQueue requestQueue) {
          String url = mUrl + "/facteur/recherche-bordereau?bordereau=" + mBordereau;
          ArrayList<Statut> statutsList = new ArrayList<>();
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
                    mAdresse.append(statutsList.get(statutsList.size() - 1).getStatutMessage());
               }
               System.out.println("satutsList : " + statutsList.size() + " " + statutsList.get(statutsList.size() - 1).getEtat());
               if (statutsList.get(statutsList.size() - 1).getEtat() == 1) {
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

     private void updateStatut(int etat, RequestQueue requestQueue) {
          String url = mUrl + "/courriers/update-statut?state=" + etat + "&bordereau=" + mBordereau;
          JsonObjectRequest updateStatutRequest = new JsonObjectRequest(
                  Request.Method.GET,
                  url,
                  null,
                  response -> {
                       toaster("Statut mis à jour");
                       chercherCourrier(mRequestQueue);
                       mDeleteButton.setVisibility(View.VISIBLE);
                  },
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
               mButtonLayout.setVisibility(View.GONE);
               toaster("Courrier non trouvé ...");
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
                                 mEtatsList.add(tmp.optString("etat"));
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
          AlertDialog.Builder builder = new AlertDialog.Builder(this);
          builder.setTitle("Confirmez :")
                  .setMessage("Statut : " + mEtatsList.get(tmp - 2))
                  .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialog, int which) {
                            updateStatut(tmp, mRequestQueue );
                       }
                  })
                  .setNegativeButton("ANNULER", new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialogInterface, int i) {
                       }
                  })
                  .create()
                  .show();
     }

     private void deleteLastStatut(RequestQueue requestQueue) {
          String url = mUrl + "/facteur/delete?bordereau=" + mBordereau;
          JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                  Request.Method.GET,
                  url,
                  null,
                  response -> {
                       toaster("Statut mis à jour !");
                       chercherCourrier(mRequestQueue);
                       mDeleteButton.setVisibility(View.GONE);
                  },
                  error -> handleError(error))
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