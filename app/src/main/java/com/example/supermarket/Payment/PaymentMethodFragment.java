package com.example.supermarket.Payment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.supermarket.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class PaymentMethodFragment extends Fragment {

    private BottomNavigationView bottomNavigationView;
    private Toolbar toolbar;

    private Spinner spinner;
    private EditText editText;
    private TextView textView;
    private Button button;

    private String totalPayment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_payment_method, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        initialize();
    }

    private void initialize(){

        bottomNavigationView = getActivity().findViewById(R.id.bottomNavBar);
        bottomNavigationView.setVisibility(View.GONE);
        toolbar = getActivity().findViewById(R.id.toolbar_cart);
        setToolbar();

        spinner = getActivity().findViewById(R.id.spin_method);
        editText = getActivity().findViewById(R.id.et_payment_method);
        textView = getActivity().findViewById(R.id.payment);
        button = getActivity().findViewById(R.id.btnContinue);

        if (!TextUtils.isEmpty(totalPayment)){
            textView.setText("Amount payable on delivery: "+totalPayment);
        }

        String[] entriesKategori = new String[]{
                "Cash on Delivery"
        };

        ArrayAdapter<String> spinnerAdapterKategori = new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, entriesKategori);
        spinnerAdapterKategori.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(spinnerAdapterKategori);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String tipe = (String) parent.getItemAtPosition(position);
                editText.setText(tipe);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(new PaymentBillFragment(totalPayment,editText.getText().toString()));
            }
        });

    }

    private void setToolbar() {
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("");
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
    }

    private void setFragment(Fragment fragment) // fungsi buat pindah - pindah fragment
    {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameFragment,fragment).addToBackStack(null);
        fragmentTransaction.commit();
    }

    public PaymentMethodFragment(String totalPayment) {
        this.totalPayment = totalPayment;
    }
}
