package com.example.javaf;

import static androidx.fragment.app.FragmentManager.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUp extends AppCompatActivity {
    
    EditText isim, soyisim, mail,sifre;
    
    private Button signup;
   private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mAuth = FirebaseAuth.getInstance();
        isim = findViewById(R.id.isim);
        soyisim = findViewById(R.id.soyisim);
        mail = findViewById(R.id.mail);
        sifre = findViewById(R.id.sifre);
        signup = findViewById(R.id.signup);
      

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = isim.getText().toString().trim();
                String lastname = soyisim.getText().toString().trim();
                String email = mail.getText().toString().trim();
                String password = sifre.getText().toString().trim();
                
                if (name.isEmpty()) {
                    isim.setError("Lütfen bir isim giriniz.");
                    return;
                }
                if(email.isEmpty()) {
                    mail.setError("Lütfen e-mail adresinizi giriniz.");
                    return;
                }
                if(lastname.isEmpty()) {
                    soyisim.setError("Lütfen bir soyisim giriniz.");
                    return;
                }
                if(password.isEmpty()) {
                    sifre.setError("Lütfen bir şifre giriniz.");
                    return;
                }
                
                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(SignUp.this, task -> {
                    if(task.isSuccessful()) {
                       Toast.makeText(SignUp.this, "Üyeliğiniz başarıyla oluşturuldu.", Toast.LENGTH_SHORT).show();
                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        Map<String,Object> user = new HashMap<>();
                        user.put("name",isim);
                        user.put("lastname",soyisim);
                        db.collection("users").document(mAuth.getUid()).set(user).addOnSuccessListener(new OnSuccessListener<Void>() {

                            @SuppressLint("RestrictedApi")
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d(TAG, "Başarılı bir şekilde oluşturuldu.");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @SuppressLint("RestrictedApi")
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Oluşturma başarısız.");
                            }
                        });
                    }
                    else {
                        Exception e = task.getException();
                        if(e != null) {
                            Toast.makeText(SignUp.this, "Üyelik başarısız" +e.getMessage(), Toast.LENGTH_SHORT ).show();
                        }
                    }



                });
            }
        });

        Button login = (Button) findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignUp.this, Login.class);
                startActivity(intent);
            }
        });


    }
}