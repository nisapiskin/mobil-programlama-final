package com.example.javaf.ui.label;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.javaf.R;
import com.example.javaf.databinding.FragmentLabelBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class LabelFragment extends Fragment {

    private FragmentLabelBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLabelBinding.inflate(inflater,container,false);
        View root = binding.getRoot();

        LinearLayout linearLayout = binding.LinearLayout;

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference labelref = db.collection("label");

        labelref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()){
                        Label label = document.toObject(Label.class);
                        CheckBox checkBox = new CheckBox(getActivity());
                        checkBox.setText(label.getLabelText());
                        linearLayout.addView(checkBox);
                    }
                } else {
                    Log.d("TAG", "Dosya alınırken hata oldu.", task.getException());
                }

            }
        });
        Button add = binding.add;
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText label = binding.label;
                String labelEditText = label.getText().toString();
                labelref.whereEqualTo("labelEditText",labelEditText).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful() && task.getResult().size() > 0) {
                            Toast.makeText(getActivity(), "Zaten mevcut", Toast.LENGTH_SHORT).show();
                        }else {
                            labelref.add(new Label(labelEditText)).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Toast.makeText(getActivity(),"Label eklendi.",Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getActivity(), "Label eklenemedi",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });
            }
        });
        return root;
    }
}