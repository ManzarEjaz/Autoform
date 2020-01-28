package com.example.autoform;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class login_activity extends AppCompatActivity {
    EditText ed1,ed2;
    Button b;
    private FirebaseAuth firebaseAuth ;
    private FirebaseAuth.AuthStateListener authStateListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_activity);
        firebaseAuth = FirebaseAuth.getInstance();
        b = findViewById(R.id.button);
        ed1 = (EditText)findViewById(R.id.editText);
        ed2 = (EditText)findViewById(R.id.editText2);
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser()!=null){
                    startActivity(new Intent(login_activity.this,MainActivity.class));
                }
            }
        };
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signin();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    private void signin(){
        String email = ed1.getText().toString().trim();
        String pass = ed2.getText().toString().trim();
        if (TextUtils.isEmpty(email)||TextUtils.isEmpty(pass)) {
            Toast.makeText(getApplicationContext(), "Fields are empty", Toast.LENGTH_SHORT).show();

        }else {
            firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Sign in problem", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }
}
