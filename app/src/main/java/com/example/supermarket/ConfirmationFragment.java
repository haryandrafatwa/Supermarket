package com.example.supermarket;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

public class ConfirmationFragment extends Fragment {

    private BottomNavigationView bottomNavigationView;
    private Toolbar toolbar;
    private EditText editText,et_rekening;
    private TextView textView;
    private Button button;
    private String amount, method, rekening, id;
    private int saldo;
    private ImageView imageView;
    private CountDownTimer countDownTimer;
    private boolean timerStopped;

    private DatabaseReference marketRefs,userRefs;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_confirmation, container, false);
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

        editText = getActivity().findViewById(R.id.et_amount);
        et_rekening = getActivity().findViewById(R.id.rekening);
        textView = getActivity().findViewById(R.id.countdown);

        imageView = getActivity().findViewById(R.id.iv_bank);
        button = getActivity().findViewById(R.id.et_add_balance_proceed_3);

        userRefs = FirebaseDatabase.getInstance().getReference().child("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        userRefs.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                saldo = Integer.valueOf(dataSnapshot.child("saldo").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        marketRefs = FirebaseDatabase.getInstance().getReference().child("MarketPay").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        marketRefs.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dsp : dataSnapshot.getChildren()){
                    if(dsp.child("status").getValue().toString().equals("Waiting")) {
                        amount = "$" + dsp.child("amount").getValue().toString();
                        rekening = dsp.child("rekening").getValue().toString();
                        method = dsp.child("method").getValue().toString();
                        id = dsp.getKey();

                        if (method.equals("Mandiri")) {
                            imageView.setBackground(getActivity().getDrawable(R.drawable.ic_bank_mandiri));
                        } else if (method.equals("BCA")) {
                            imageView.setBackground(getActivity().getDrawable(R.drawable.ic_bank_central_asia));
                        } else if (method.equals("BNI")) {
                            imageView.setBackground(getActivity().getDrawable(R.drawable.ic_bank_negara_indonesia));
                        }

                        editText.setText(amount);
                        et_rekening.setText(rekening);

                        startTimer(Long.valueOf(dsp.child("millis").getValue().toString()));

                        marketRefs.child(id).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.child("status").getValue().toString().equals("Done")){
                                    stopTimer("success");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void setTimerStartListener(long millisinfuture) {
        // will be called at every 1500 milliseconds i.e. every 1.5 second.
        if (countDownTimer != null){
            countDownTimer.onTick(millisinfuture);
        }else{
            countDownTimer = new CountDownTimer(millisinfuture, 1000) {
                public void onTick(long millisUntilFinished) {
                    long menit = millisUntilFinished/1000/60;
                    long second = millisUntilFinished/1000%60;
                    marketRefs.child(id).child("millis").setValue(millisUntilFinished);
                    textView.setText(menit+"m"+second+"s");
                }

                public void onFinish() {
                    // Here do what you like...
                    initializeDialogSuccessandFailed("failed");
                }
            }.start();
        }

    }

    public void stopTimer(String status) {
        if (countDownTimer != null){
            countDownTimer.cancel();
            timerStopped = true;
        }
        initializeDialogSuccessandFailed(status);
    }

    public void startTimer(long i) {
        setTimerStartListener(i);
        timerStopped = false;
    }

    private void initializeDialogSuccessandFailed(String status){

        if (status.equals("failed")){
            android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(getActivity(),R.style.CustomAlertDialog);
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_payment_failed,null);
            dialogView.setBackgroundColor(Color.TRANSPARENT);
            dialog.setView(dialogView);
            dialog.setCancelable(false);
            dialog.setTitle(null);

            dialog.setPositiveButton("OKAY", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    HomeFragment homeFragment = new HomeFragment();
                    marketRefs.child(id).child("status").setValue("Canceled");
                    setFragment(homeFragment);
                }
            });

            dialog.show();
        }else{
            android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(getActivity(),R.style.CustomAlertDialog);
            LayoutInflater inflater = getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_payment_success,null);
            dialogView.setBackgroundColor(Color.TRANSPARENT);
            dialog.setView(dialogView);
            dialog.setCancelable(false);
            dialog.setTitle(null);

            TextView textView = dialogView.findViewById(R.id.balance_dialog);

            int newSaldo = saldo+Integer.valueOf(amount.replaceAll("[$]",""));
            userRefs.child("saldo").setValue(newSaldo);
            textView.setText("$"+newSaldo);

            dialog.setPositiveButton("OKAY", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    HomeFragment homeFragment = new HomeFragment();
                    setFragment(homeFragment);
                }
            });

            dialog.show();
        }
    }

    private void setFragment(Fragment fragment) // fungsi buat pindah - pindah fragment
    {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameFragment,fragment);
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
