package com.example.javaf;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {

    EditText email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        Button login = (Button) findViewById(R.id.giris_yap);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email_giris = email.getText().toString();
                String password_giris = password.getText().toString();

                if(email_giris.isEmpty() || password_giris.isEmpty()) {
                  Toast.makeText(Login.this,"Lutfen bir mail ve şifre giriniz.", Toast.LENGTH_SHORT).show();
                  return;
                }

                mAuth.signInWithEmailAndPassword(email_giris, password_giris).addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(Login.this, "Giriş başarılı", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Login.this, MainActivity.class);
                            startActivity(intent);
                        }
                        else {
                            Toast.makeText(Login.this, "Giriş başarısız", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

        Button signup = (Button) findViewById(R.id.kayit_ol);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, SignUp.class);
                startActivity(intent);
            }
        });
    }
}