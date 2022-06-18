package com.example.weather;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class DefaultFragment extends Fragment {

    public DefaultFragment() {
        super(R.layout.fragment_default);
    }

    private TextView result_info;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        result_info = view.findViewById(R.id.result_info);
        result_info.setText("sssssssss");
    }
}
