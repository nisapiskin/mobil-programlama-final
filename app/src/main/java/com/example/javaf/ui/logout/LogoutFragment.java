package com.example.javaf.ui.logout;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.javaf.R;
import com.example.javaf.Splash;
import com.example.javaf.databinding.FragmentAboutBinding;
import com.example.javaf.databinding.FragmentLogoutBinding;

public class LogoutFragment extends Fragment {

    private FragmentLogoutBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLogoutBinding.inflate(inflater,container,false);
        View root = binding.getRoot();
        Button logout_button = binding.logoutButton;

        logout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Splash.class);
                startActivity(intent);
            }
        });

        return root;
    }
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}