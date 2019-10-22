package com.tdtruong.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SignInActivity extends AppCompatActivity {
    private TextView txtSignUp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        addControls();
        addEvents();
    }

    private void addEvents() {
        txtSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent iOpenSignUpScreen = new Intent(SignInActivity.this, SignUpActivity.class);
                startActivity(iOpenSignUpScreen);
            }
        });
    }

    private void addControls() {
        txtSignUp = findViewById(R.id.register_btn);
    }
}
