package com.example.supermarket.Home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.supermarket.Home.Buy.BuyFragment;
import com.example.supermarket.Home.MarketPay.ConfirmationFragment;
import com.example.supermarket.Home.MarketPay.EnterAmountFragment;
import com.example.supermarket.Home.Sell.SellFragment;
import com.example.supermarket.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeFragment extends Fragment {

    private TextView tv_hai,tv_balance;
    private LinearLayout add_balance;
    private RelativeLayout buy,sell,atas;
    private ProgressBar progressBar;

    private BottomNavigationView bottomNavigationView;
    private DatabaseReference userRefs,marketRefs;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home,container,false);
    }

    @Override
    public void onStart() {
        super.onStart();
        initialize();
    }

    //menginisiasi semua objek yang ada pada halaman ini
    private void initialize(){

        bottomNavigationView = getActivity().findViewById(R.id.bottomNavBar);
        bottomNavigationView.setVisibility(View.VISIBLE);

        tv_hai = getActivity().findViewById(R.id.hai_user);
        tv_balance = getActivity().findViewById(R.id.tv_balance);
        add_balance = getActivity().findViewById(R.id.add_balance);
        buy = getActivity().findViewById(R.id.layout_buy);
        sell = getActivity().findViewById(R.id.layout_sell);
        atas = getActivity().findViewById(R.id.layout_home_marketpay);
        progressBar = getActivity().findViewById(R.id.pb_home);

        userRefs = FirebaseDatabase.getInstance().getReference().child("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        //proses mengambil data user dari database utk ditampilkan di textview
        userRefs.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                tv_hai.setText("Hai, "+dataSnapshot.child("name").getValue().toString()+"!");
                tv_balance.setText("Balance: $"+dataSnapshot.child("saldo").getValue().toString());
                atas.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        marketRefs = FirebaseDatabase.getInstance().getReference().child("MarketPay").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        //proses utk mengecek apakah user memiliki transaksi yang sedang berlangsung atau tidak
        marketRefs.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dsp : dataSnapshot.getChildren()){
                    if(dsp.child("status").getValue().toString().equals("Waiting")){
                        add_balance.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ConfirmationFragment confirmationFragment =new ConfirmationFragment();
                                setFragment(confirmationFragment);
                            }
                        });
                    }else{
                        add_balance.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                EnterAmountFragment enterAmountFragment = new EnterAmountFragment();
                                setFragment(enterAmountFragment);
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //kondisi ketika textview add balance ditekan, halaman pindah ke halaman enter amount fragment
        add_balance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EnterAmountFragment enterAmountFragment = new EnterAmountFragment();
                setFragment(enterAmountFragment);
            }
        });

        sell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SellFragment sellFragment = new SellFragment();
                setFragment(sellFragment);
            }
        });

        buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BuyFragment buyFragment = new BuyFragment();
                setFragment(buyFragment);
            }
        });
    }

    //method utk pindah-pindah halaman dalam bentuk fragment
    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameFragment,fragment).addToBackStack(null);
        fragmentTransaction.commit();
    }
}
