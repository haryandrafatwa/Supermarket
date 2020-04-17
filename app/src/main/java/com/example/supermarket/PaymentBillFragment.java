package com.example.supermarket;

import android.content.DialogInterface;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class PaymentBillFragment extends Fragment {

    private BottomNavigationView bottomNavigationView;
    private Toolbar toolbar;

    private EditText et_name, et_username, et_address, et_city, et_phone;
    private Button button;

    private String totalPayment, payMethod;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_payment_bill, container, false);
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

        et_name = getActivity().findViewById(R.id.et_bill_name);
        et_username = getActivity().findViewById(R.id.et_bill_username);
        et_address = getActivity().findViewById(R.id.et_bill_address);
        et_city = getActivity().findViewById(R.id.et_bill_city);
        et_phone = getActivity().findViewById(R.id.et_bill_phone);
        button = getActivity().findViewById(R.id.btnContinue);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(et_name.getText().toString()) && !TextUtils.isEmpty(et_username.getText().toString())  && !TextUtils.isEmpty(et_address.getText().toString())
                        && !TextUtils.isEmpty(et_city.getText().toString()) && !TextUtils.isEmpty(et_phone.getText().toString())){
                    alertFillData();
                }else{
                    Toast.makeText(getActivity(), "Please fill out your information first", Toast.LENGTH_SHORT).show();
                }
            }
        });

        DatabaseReference userRefs = FirebaseDatabase.getInstance().getReference().child("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        userRefs.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                et_name.setText(dataSnapshot.child("name").getValue().toString());
                et_username.setText(dataSnapshot.child("username").getValue().toString());
                et_phone.setText(dataSnapshot.child("phonenumber").getValue().toString().substring(1, dataSnapshot.child("phonenumber").getValue().toString().length()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void alertFillData(){ // fungsi untuk membuat alert dialog ketika ingin logout
        AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(getActivity());

        // Setting Dialog Title
        alertDialog2.setTitle("Confirmation");

        alertDialog2.setCancelable(false);

        // Setting Dialog Message
        alertDialog2.setMessage("the information that you inputed is valid information?");

        // Setting Positive "Yes" Btn
        alertDialog2.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog

                DatabaseReference notifRefs = FirebaseDatabase.getInstance().getReference().child("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Notif");
                notifRefs.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        HashMap hashMap = new HashMap();
                        hashMap.put("name",et_name.getText().toString());
                        hashMap.put("username",et_username.getText().toString());
                        hashMap.put("address",et_address.getText().toString());
                        hashMap.put("city",et_city.getText().toString());
                        hashMap.put("phoneNumber",et_phone.getText().toString());
                        hashMap.put("totalPayment",totalPayment);
                        hashMap.put("payMethod",payMethod);
                        hashMap.put("status","Active");
                        hashMap.put("report",false);

                        Date c = Calendar.getInstance().getTime();
                        Locale local = new Locale("id", "ID");
                        SimpleDateFormat format = new SimpleDateFormat("dd MMMM yyyy", local);

                        String date = format.format(c);
                        hashMap.put("date",date);

                        dataSnapshot.child(dataSnapshot.getChildrenCount()+"").getRef().updateChildren(hashMap);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                final DatabaseReference cartRefs = FirebaseDatabase.getInstance().getReference().child("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Cart");
                DatabaseReference onGoingRefs = FirebaseDatabase.getInstance().getReference().child("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Order");
                onGoingRefs.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                        final String id = dataSnapshot.getChildrenCount()+"";
                        cartRefs.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                dataSnapshot.child(id).getRef().setValue(snapshot.getValue());
                                cartRefs.removeValue();
                                setFragment(new PaymentCheckoutFragment());
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        alertDialog2.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog2.show();

    }

    private void setFragment(Fragment fragment) // fungsi buat pindah - pindah fragment
    {
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

    public PaymentBillFragment(String totalPayment, String payMethod) {
        this.totalPayment = totalPayment;
        this.payMethod = payMethod;
    }
}
