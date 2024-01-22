package com.example.javaf.ui.about;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.javaf.R;
import com.example.javaf.databinding.FragmentAboutBinding;


public class AboutFragment extends Fragment {

    private FragmentAboutBinding binding;
    private LayoutInflater Inflater;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        
        binding = FragmentAboutBinding.inflate(Inflater,container,false);
        View root = binding.getRoot();
        
        Button mail_button = binding.mailButton;
        mail_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i1 = new Intent(Intent.ACTION_SEND);
                i1.setType("text/plain");
                i1.putExtra(Intent.EXTRA_EMAIL,new String[]{"nisanurpiskin@gmail.com"});
                if(i1.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivity(i1);
                }
            }
        });

        Button github_button = binding.githubButton;
        github_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://github.com";
                Intent i2 = new Intent(Intent.ACTION_VIEW);
                i2.setData(Uri.parse(url));
                startActivity(i2);
            }
        });
        
        return root;
    }
    
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}