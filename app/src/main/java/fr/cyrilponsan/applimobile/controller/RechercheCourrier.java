package fr.cyrilponsan.applimobile.controller;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import fr.cyrilponsan.applimobile.R;
import fr.cyrilponsan.applimobile.model.User;

public class RechercheCourrier extends AppCompatActivity {

    private Button mButton;
    private EditText mEditText;
    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recherche_courrier);

        mButton = findViewById(R.id.recherche_courrier_activity_button);
        mEditText = findViewById(R.id.recherche_courrier_activity_bordereau);

        Intent intent = getIntent();
        mUser = intent.getParcelableExtra("user");

        mButton.setEnabled(false);

        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                mButton.setEnabled(!mEditText.getText().toString().isEmpty());
            }
        });

        mButton.setOnClickListener(view -> {
            Intent intent1 = new Intent(RechercheCourrier.this, UpdateStatutActivity.class);
            intent1.putExtra("bordereau", mEditText.getText().toString());
            intent1.putExtra("user", mUser);
            startActivity(intent1);
        });
    }
}