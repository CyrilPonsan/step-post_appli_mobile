package fr.cyrilponsan.applimobile.controller;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import fr.cyrilponsan.applimobile.R;
import fr.cyrilponsan.applimobile.model.User;

public class MainActivity extends AppCompatActivity {

     private EditText mEmail;
     private EditText mPassword;
     private Button mButton;
     private String mEmailValue;
     private String mPasswordValue;

     @Override
     protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          setContentView(R.layout.activity_main);

          mEmail = findViewById(R.id.main_activity_email);
          mPassword = findViewById(R.id.main_activity_password);
          mButton = findViewById(R.id.main_activity_button);
          //mButton.setEnabled(false);
          RequestQueue mRequestQueue = Volley.newRequestQueue(MainActivity.this);

          mEmail.addTextChangedListener(new TextWatcher() {
               @Override
               public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

               }

               @Override
               public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

               }

               @Override
               public void afterTextChanged(Editable editable) {
                    mEmailValue = mEmail.getText().toString();
                    //testFields();
               }
          });

          mPassword.addTextChangedListener(new TextWatcher() {
               @Override
               public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

               }

               @Override
               public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

               }

               @Override
               public void afterTextChanged(Editable editable) {
                    mPasswordValue = mPassword.getText().toString();
                    //testFields();
               }
          });

          mButton.setOnClickListener(view -> login(mRequestQueue));
     }

     private void testFields() {
          if (mEmailValue != null && mPasswordValue != null) {
               mButton.setEnabled(!mEmailValue.isEmpty() && !mPasswordValue.isEmpty());
          }
     }

     private void login(RequestQueue requestQueue) {
          Map<String, String>  params = new HashMap<>();
          params.put("username", "tata@toto.fr");
          params.put("password", "Abcd@1234");
          JSONObject jsonParams = new JSONObject(params);

          String url = "https://step-post-nodejs.herokuapp.com/auth/facteur/login";
          JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                  Request.Method.POST,
                  url,
                  jsonParams,
                  this::handleLoginResponse,
                  this::handleError
          );
          requestQueue.add(jsonObjectRequest);
     }

     private void handleLoginResponse(JSONObject response) {
          User user = new User(response);
          Intent intent = new Intent(MainActivity.this, RechercheCourrier.class);
          intent.putExtra("user", user);
          startActivity(intent);
     }

     private void handleError(VolleyError error) {
          String msg;
          if (error.networkResponse.statusCode == 401) {
               msg = "Identifiants incorrects !";
          } else {
               msg = "Impossible de se connecter !";
          }
          Toast toast = Toast.makeText(
                  MainActivity.this,
                  msg,
                  Toast.LENGTH_SHORT);
          toast.show();
     }
}