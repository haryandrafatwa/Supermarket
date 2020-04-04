package com.example.supermarket;

import android.graphics.Color;
import android.icu.text.NumberFormat;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class EnterAmountFragment extends Fragment {

    private BottomNavigationView bottomNavigationView;

    private RelativeLayout amount5, amount10, amount25, amount50, amount75, amount100;
    private TextView tvamount5, tvamount10, tvamount25, tvamount50, tvamount75, tvamount100;
    private EditText editText;
    private Toolbar toolbar;
    private Button button;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_choose_amount_add_balance, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        initialize();
    }

    private void initialize(){

        bottomNavigationView = getActivity().findViewById(R.id.bottomNavBar);
        bottomNavigationView.setVisibility(View.GONE);
        toolbar = getActivity().findViewById(R.id.toolbar_add_balance);
        setToolbar();

        editText = getActivity().findViewById(R.id.et_enter_amount);

        amount5 = getActivity().findViewById(R.id.amount5);
        amount10 = getActivity().findViewById(R.id.amount10);
        amount25 = getActivity().findViewById(R.id.amount25);
        amount50 = getActivity().findViewById(R.id.amount50);
        amount75 = getActivity().findViewById(R.id.amount75);
        amount100 = getActivity().findViewById(R.id.amount100);

        tvamount5 = getActivity().findViewById(R.id.tv_amount5);
        tvamount10 = getActivity().findViewById(R.id.tv_amount10);
        tvamount25 = getActivity().findViewById(R.id.tv_amount25);
        tvamount50 = getActivity().findViewById(R.id.tv_amount50);
        tvamount75 = getActivity().findViewById(R.id.tv_amount75);
        tvamount100 = getActivity().findViewById(R.id.tv_amount100);

        button = getActivity().findViewById(R.id.et_add_balance_proceed_1);

        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            private String current = "";
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                switch (s.toString()){
                    case "$5" :
                        amount5.setBackground(getActivity().getDrawable(R.drawable.bg_btn_solid_green));
                        amount10.setBackground(getActivity().getDrawable(R.drawable.bg_btn_solid_white));
                        amount25.setBackground(getActivity().getDrawable(R.drawable.bg_btn_solid_white));
                        amount50.setBackground(getActivity().getDrawable(R.drawable.bg_btn_solid_white));
                        amount75.setBackground(getActivity().getDrawable(R.drawable.bg_btn_solid_white));
                        amount100.setBackground(getActivity().getDrawable(R.drawable.bg_btn_solid_white));

                        tvamount5.setTextColor(Color.WHITE);
                        tvamount10.setTextColor(Color.BLACK);
                        tvamount25.setTextColor(Color.BLACK);
                        tvamount50.setTextColor(Color.BLACK);
                        tvamount75.setTextColor(Color.BLACK);
                        tvamount100.setTextColor(Color.BLACK);

                        break;

                    case "$10" :
                        amount5.setBackground(getActivity().getDrawable(R.drawable.bg_btn_solid_white));
                        amount25.setBackground(getActivity().getDrawable(R.drawable.bg_btn_solid_white));
                        amount50.setBackground(getActivity().getDrawable(R.drawable.bg_btn_solid_white));
                        amount75.setBackground(getActivity().getDrawable(R.drawable.bg_btn_solid_white));
                        amount100.setBackground(getActivity().getDrawable(R.drawable.bg_btn_solid_white));
                        amount10.setBackground(getActivity().getDrawable(R.drawable.bg_btn_solid_green));

                        tvamount5.setTextColor(Color.BLACK);
                        tvamount10.setTextColor(Color.WHITE);
                        tvamount25.setTextColor(Color.BLACK);
                        tvamount50.setTextColor(Color.BLACK);
                        tvamount75.setTextColor(Color.BLACK);
                        tvamount100.setTextColor(Color.BLACK);

                        break;

                    case "$25" :
                        amount5.setBackground(getActivity().getDrawable(R.drawable.bg_btn_solid_white));
                        amount10.setBackground(getActivity().getDrawable(R.drawable.bg_btn_solid_white));
                        amount50.setBackground(getActivity().getDrawable(R.drawable.bg_btn_solid_white));
                        amount75.setBackground(getActivity().getDrawable(R.drawable.bg_btn_solid_white));
                        amount100.setBackground(getActivity().getDrawable(R.drawable.bg_btn_solid_white));
                        amount25.setBackground(getActivity().getDrawable(R.drawable.bg_btn_solid_green));

                        tvamount5.setTextColor(Color.BLACK);
                        tvamount10.setTextColor(Color.BLACK);
                        tvamount25.setTextColor(Color.WHITE);
                        tvamount50.setTextColor(Color.BLACK);
                        tvamount75.setTextColor(Color.BLACK);
                        tvamount100.setTextColor(Color.BLACK);

                        break;

                    case "$50" :
                        amount5.setBackground(getActivity().getDrawable(R.drawable.bg_btn_solid_white));
                        amount25.setBackground(getActivity().getDrawable(R.drawable.bg_btn_solid_white));
                        amount10.setBackground(getActivity().getDrawable(R.drawable.bg_btn_solid_white));
                        amount75.setBackground(getActivity().getDrawable(R.drawable.bg_btn_solid_white));
                        amount100.setBackground(getActivity().getDrawable(R.drawable.bg_btn_solid_white));
                        amount50.setBackground(getActivity().getDrawable(R.drawable.bg_btn_solid_green));

                        tvamount5.setTextColor(Color.BLACK);
                        tvamount10.setTextColor(Color.BLACK);
                        tvamount25.setTextColor(Color.BLACK);
                        tvamount50.setTextColor(Color.WHITE);
                        tvamount75.setTextColor(Color.BLACK);
                        tvamount100.setTextColor(Color.BLACK);

                        break;

                    case "$75" :
                        amount5.setBackground(getActivity().getDrawable(R.drawable.bg_btn_solid_white));
                        amount25.setBackground(getActivity().getDrawable(R.drawable.bg_btn_solid_white));
                        amount50.setBackground(getActivity().getDrawable(R.drawable.bg_btn_solid_white));
                        amount10.setBackground(getActivity().getDrawable(R.drawable.bg_btn_solid_white));
                        amount100.setBackground(getActivity().getDrawable(R.drawable.bg_btn_solid_white));
                        amount75.setBackground(getActivity().getDrawable(R.drawable.bg_btn_solid_green));

                        tvamount5.setTextColor(Color.BLACK);
                        tvamount10.setTextColor(Color.BLACK);
                        tvamount25.setTextColor(Color.BLACK);
                        tvamount50.setTextColor(Color.BLACK);
                        tvamount75.setTextColor(Color.WHITE);
                        tvamount100.setTextColor(Color.BLACK);

                        break;

                    case "$100" :
                        amount5.setBackground(getActivity().getDrawable(R.drawable.bg_btn_solid_white));
                        amount25.setBackground(getActivity().getDrawable(R.drawable.bg_btn_solid_white));
                        amount50.setBackground(getActivity().getDrawable(R.drawable.bg_btn_solid_white));
                        amount75.setBackground(getActivity().getDrawable(R.drawable.bg_btn_solid_white));
                        amount10.setBackground(getActivity().getDrawable(R.drawable.bg_btn_solid_white));
                        amount100.setBackground(getActivity().getDrawable(R.drawable.bg_btn_solid_green));

                        tvamount5.setTextColor(Color.BLACK);
                        tvamount10.setTextColor(Color.BLACK);
                        tvamount25.setTextColor(Color.BLACK);
                        tvamount50.setTextColor(Color.BLACK);
                        tvamount75.setTextColor(Color.BLACK);
                        tvamount100.setTextColor(Color.WHITE);

                        break;

                    default:
                        amount5.setBackground(getActivity().getDrawable(R.drawable.bg_btn_solid_white));
                        amount25.setBackground(getActivity().getDrawable(R.drawable.bg_btn_solid_white));
                        amount50.setBackground(getActivity().getDrawable(R.drawable.bg_btn_solid_white));
                        amount75.setBackground(getActivity().getDrawable(R.drawable.bg_btn_solid_white));
                        amount10.setBackground(getActivity().getDrawable(R.drawable.bg_btn_solid_white));
                        amount100.setBackground(getActivity().getDrawable(R.drawable.bg_btn_solid_white));

                        tvamount5.setTextColor(Color.BLACK);
                        tvamount10.setTextColor(Color.BLACK);
                        tvamount25.setTextColor(Color.BLACK);
                        tvamount50.setTextColor(Color.BLACK);
                        tvamount75.setTextColor(Color.BLACK);
                        tvamount100.setTextColor(Color.BLACK);
                        break;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

                editText.removeTextChangedListener(this);

                Locale local = new Locale("en", "US");
                String replaceable = String.format("[$,.]",
                        NumberFormat.getCurrencyInstance().getCurrency()
                                .getSymbol(local));
                String cleanString = s.toString().replaceAll(replaceable,
                        "");

                double parsed;
                try {
                    parsed = Double.parseDouble(cleanString);
                } catch (NumberFormatException e) {
                    parsed = 0.00;
                }

                NumberFormat formatter = NumberFormat
                        .getCurrencyInstance(local);
                formatter.setMaximumFractionDigits(0);
                formatter.setParseIntegerOnly(true);
                String formatted = formatter.format((parsed));

                current = formatted;
                editText.setText(formatted);
                editText.setSelection(formatted.length());
                editText.addTextChangedListener(this);
            }

        });

        amount5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText("$5");
            }
        });
        amount10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText("$10");
            }
        });
        amount25.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText("$25");
            }
        });
        amount50.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText("$50");
            }
        });
        amount75.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText("$75");
            }
        });
        amount100.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText("$100");
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!editText.getText().toString().equals("")){
                    if(Integer.valueOf(editText.getText().toString().replaceAll("[$,]",""))<5){
                        Toast.makeText(getActivity(), "Amount must be above $5", Toast.LENGTH_SHORT).show();
                    }else{
                        ChoosePaymentMethodFragment choosePaymentMethodFragment = new ChoosePaymentMethodFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("amount",editText.getText().toString());
                        choosePaymentMethodFragment.setArguments(bundle);
                        setFragment(choosePaymentMethodFragment);
                    }
                }else{
                    Toast.makeText(getActivity(), "Please fill the amount first", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameFragment,fragment).addToBackStack(null);
        fragmentTransaction.commit();
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
}
