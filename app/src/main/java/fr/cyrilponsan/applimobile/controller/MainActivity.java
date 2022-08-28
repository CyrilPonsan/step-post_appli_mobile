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

public class MainActivity extends AppCompatActivity {

    private Button mButton;
    private EditText mEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mButton = findViewById(R.id.main_activity_button);
        mEditText = findViewById(R.id.main_activity_bordereau);

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

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, UpdateStatutActivity.class);
                intent.putExtra("bordereau", mEditText.getText().toString());
                startActivity(intent);
            }
        });
    }
}