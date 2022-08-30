package fr.cyrilponsan.applimobile.controller;

import android.annotation.SuppressLint;
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

public class UpdateStatutActivity extends AppCompatActivity {

     private Button mCancelButton;
     private Button mConfirmButton;
     private TextView mAdresse;
     private ArrayList<Button> mButtons;
     private LinearLayout mButtonLayout;
     private LinearLayout mButtonsLayout;
     private String mBordereau;
     private Courrier mCourrier;
     private User mUser;
     private final ArrayList<Statut> mStatuts = new ArrayList<>();
     private final String mUrl = "https://step-post-nodejs.herokuapp.com/";
     private ImageView mAvatar;

     @Override
     protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.activity_update_statut);

          RequestQueue mRequestQueue = Volley.newRequestQueue(UpdateStatutActivity.this);

          mAdresse = findViewById(R.id.update_statut_activity_adresse);
          mCancelButton = findViewById(R.id.update_statut_activity_cancel_button);
          mConfirmButton = findViewById(R.id.update_statut_activity_confirm_button);
          mButtonLayout = findViewById(R.id.update_statut_activity_button_layout);
          mButtonsLayout = findViewById(R.id.update_status_activity_buttons_layout);
          //mAvatar = findViewById(R.id.update_statut_activity_avatar);
          mButtonsLayout.setVisibility(View.GONE);

          Intent intent = getIntent();
          mBordereau = intent.getStringExtra("bordereau");
          mUser = intent.getParcelableExtra("user");

          mAdresse.setText(("Bordereau n° : " + mBordereau + "\n").toUpperCase());

          chercherCourrier(mRequestQueue);

          mCancelButton.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                    mButtonsLayout.setVisibility(View.GONE);
               }
          });

          mConfirmButton.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                    updateStatut(2, mRequestQueue);
                    chercherCourrier(mRequestQueue);
                    mButtonsLayout.setVisibility(View.GONE);
               }
          });
     }

     private void chercherCourrier(RequestQueue requestQueue) {
          String url = mUrl + "recherchecourrier/bordereau?bordereau=" + mBordereau;
          JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, response -> {
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

               switch ( mStatuts.get(mStatuts.size() - 1).getEtat()) {
                    case 1:
                         caseNonCollecte(requestQueue);
                         break;
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
               public void onClick(View view) {
                    mButtonsLayout.setVisibility(View.VISIBLE);
               }
          });
     }

     private void updateStatut(int etat, RequestQueue requestQueue) {
          String url = mUrl + "courriers/update-statut?state=" + etat + "&bordereau=" + mBordereau;
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
}