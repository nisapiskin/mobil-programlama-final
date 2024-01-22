package com.example.javaf.ui.photo;

import static android.app.Activity.RESULT_OK;

import static androidx.fragment.app.FragmentManager.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.net.http.UploadDataProvider;
import android.os.Bundle;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.OptIn;
import androidx.fragment.app.Fragment;
import androidx.media3.common.util.Log;
import androidx.media3.common.util.UnstableApi;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.javaf.R;
import com.example.javaf.databinding.FragmentPhotoBinding;
import com.example.javaf.ui.gallery.GalleryViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PhotoFragment extends Fragment {

    private FragmentPhotoBinding binding;
    private int ResultImage = 1;

    List<String> firebaseLabelList = new ArrayList<>();
    private GalleryViewModel checkBox;
    private int resultCode;
    private int requestCode;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentPhotoBinding.inflate(inflater, container,false);
        View root = binding.getRoot();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference labelref = db.collection("label");
        labelref.addSnapshotListener((snapshots, e) -> {
            if(e != null) {
                return;
            }
            firebaseLabelList.clear();

            LinearLayout layout = binding.LinearLayout;

            for(QueryDocumentSnapshot document: snapshots){
                Label label = document.toObject(Label.class);
                CheckBox checkBox = new CheckBox(getActivity());
                checkBox.setText(label.getLabelText());
                layout.addView(checkBox);
                firebaseLabelList.add(label.getLabelText());
            }
        });

        Button select_button = binding.select;
        select_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i,ResultImage);
            }
        });
        return root;
    }
    private List<String> getSelectedLabels() {
        List<String> selectedLabels = new ArrayList<>();
        LinearLayout layout = binding.LinearLayout;

        for(int i = 0; i < layout.getChildCount(); i++){
            View view = layout.getChildAt(i);
            if(view instanceof CheckBox) {
                selectedLabels.add(checkBox.getText().toString());
            }
        }
        return selectedLabels;
    }
     public void onActivityResult(int RequestCode, int ResultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == ResultImage && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String [] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getActivity().getContentResolver().query(selectedImage,filePathColumn,null,null,null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            ImageView imageView = binding.imageView3;
            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));

            Button share = binding.share;
            share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseStorage storage = FirebaseStorage.getInstance();
                    StorageReference storageRef = storage.getReference();
                    StorageReference photoRef = storageRef.child("posts/" + UUID.randomUUID().toString());

                    UploadTask uploadTask = photoRef.putFile(selectedImage);

                    FirebaseAuth auth = FirebaseAuth.getInstance();

                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while(!uriTask.isSuccessful());
                            Uri downloadUrl = uriTask.getResult();
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            DocumentReference userRef = db.collection("users").document(auth.getUid());
                            userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    String name = documentSnapshot.getString("name");

                                    Map<String,Object> post = new HashMap<>();
                                    post.put("imageUrl", downloadUrl.toString());
                                    post.put("name", name);

                                    List<String> selectedLabels = getSelectedLabels();
                                    post.put("label",selectedLabels);

                                    db.collection("posts").document().set(post).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @SuppressLint("RestrictedApi")
                                        @OptIn(markerClass = UnstableApi.class) @Override
                                        public void onSuccess(Void unused) {
                                            Log.d(TAG, "Başarılı bir şekilde yuklendi.");
                                            Toast.makeText(getActivity(),"Başarılı işlem.",Toast.LENGTH_SHORT).show();

                                        }
                                    });
                                }
                            });
                        }
                    });
                }
            });
        }
     }
     public void OndestroyView() {
        super.onDestroyView();
        binding = null;
     }
}