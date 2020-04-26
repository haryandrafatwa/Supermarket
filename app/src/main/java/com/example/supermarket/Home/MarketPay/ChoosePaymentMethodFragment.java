package com.example.supermarket.Home.MarketPay;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.supermarket.Home.HomeFragment;
import com.example.supermarket.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ChoosePaymentMethodFragment extends Fragment {

    private RelativeLayout mandiri_title, mandiri_content,rv_mandiri, bca_title, bca_content, rv_bca, bni_title, bni_content, rv_bni;
    private ImageButton ib_mandiri, ib_bca, ib_bni;
    private BottomNavigationView bottomNavigationView;
    private EditText editText,mandiri,bca,bni;
    private String method = "",amount, rekening;
    private Button button;
    private Toolbar toolbar;
    private DatabaseReference userRefs, marketRefs;
    private String email, name;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_choose_payment_add_balance, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        initialize();
    }

    //method utk menginisiasi semua objek yang ada pada halaman ini
    private void initialize(){

        bottomNavigationView = getActivity().findViewById(R.id.bottomNavBar);
        bottomNavigationView.setVisibility(View.GONE);
        editText = getActivity().findViewById(R.id.et_amount);
        toolbar = getActivity().findViewById(R.id.toolbar_add_balance);
        setToolbar();

        mandiri_title = getActivity().findViewById(R.id.layout_title_mandiri);
        mandiri_content = getActivity().findViewById(R.id.layout_content_mandiri);
        rv_mandiri = getActivity().findViewById(R.id.layout_mandiri);
        ib_mandiri = getActivity().findViewById(R.id.ib_mandiri);
        mandiri = getActivity().findViewById(R.id.rekening_mandiri);

        bca_title = getActivity().findViewById(R.id.layout_title_bca);
        bca_content = getActivity().findViewById(R.id.layout_content_bca);
        rv_bca = getActivity().findViewById(R.id.layout_bca);
        ib_bca = getActivity().findViewById(R.id.ib_bca);
        bca = getActivity().findViewById(R.id.rekening_bca);

        bni_title = getActivity().findViewById(R.id.layout_title_bni);
        bni_content = getActivity().findViewById(R.id.layout_content_bni);
        rv_bni = getActivity().findViewById(R.id.layout_bni);
        ib_bni = getActivity().findViewById(R.id.ib_bni);
        bni = getActivity().findViewById(R.id.rekening_bni);

        button = getActivity().findViewById(R.id.et_add_balance_proceed_2);

        //proses mengambil objek yang dikirimkan dari halaman enter amount dan ditaruh di edit text dan divariabel amount local
        final Bundle bundle = getArguments();
        amount = bundle.getString("amount");
        editText.setText(amount+"");

        //proses pemilihan payment method, dan mengassign variabel method sesuai dengan bank yg dipilih
        mandiri_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rv_mandiri.setBackground(getActivity().getDrawable(R.drawable.bg_btn_solid_green));
                rv_bni.setBackground(getActivity().getDrawable(R.drawable.bg_btn_solid_white));
                rv_bca.setBackground(getActivity().getDrawable(R.drawable.bg_btn_solid_white));
                method = "Mandiri";
                rekening = mandiri.getText().toString();
            }
        });

        rv_mandiri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rv_mandiri.setBackground(getActivity().getDrawable(R.drawable.bg_btn_solid_green));
                rv_bni.setBackground(getActivity().getDrawable(R.drawable.bg_btn_solid_white));
                rv_bca.setBackground(getActivity().getDrawable(R.drawable.bg_btn_solid_white));
                method = "Mandiri";
                rekening = mandiri.getText().toString();
            }
        });

        mandiri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rv_mandiri.setBackground(getActivity().getDrawable(R.drawable.bg_btn_solid_green));
                rv_bni.setBackground(getActivity().getDrawable(R.drawable.bg_btn_solid_white));
                rv_bca.setBackground(getActivity().getDrawable(R.drawable.bg_btn_solid_white));
                method = "Mandiri";
                rekening = mandiri.getText().toString();
            }
        });

        ib_mandiri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mandiri_content.getVisibility() == View.GONE){
                    ib_mandiri.setBackground(getActivity().getDrawable(R.drawable.ic_expand_less));
                    mandiri_content.setVisibility(View.VISIBLE);
                }else{
                    ib_mandiri.setBackground(getActivity().getDrawable(R.drawable.ic_expand_more));
                    mandiri_content.setVisibility(View.GONE);
                }
            }
        });

        bca_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rv_bca.setBackground(getActivity().getDrawable(R.drawable.bg_btn_solid_green));
                rv_mandiri.setBackground(getActivity().getDrawable(R.drawable.bg_btn_solid_white));
                rv_bni.setBackground(getActivity().getDrawable(R.drawable.bg_btn_solid_white));
                method = "BCA";
                rekening = bca.getText().toString();
            }
        });

        rv_bca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rv_bca.setBackground(getActivity().getDrawable(R.drawable.bg_btn_solid_green));
                rv_mandiri.setBackground(getActivity().getDrawable(R.drawable.bg_btn_solid_white));
                rv_bni.setBackground(getActivity().getDrawable(R.drawable.bg_btn_solid_white));
                method = "BCA";
                rekening = bca.getText().toString();
            }
        });

        bca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rv_bca.setBackground(getActivity().getDrawable(R.drawable.bg_btn_solid_green));
                rv_mandiri.setBackground(getActivity().getDrawable(R.drawable.bg_btn_solid_white));
                rv_bni.setBackground(getActivity().getDrawable(R.drawable.bg_btn_solid_white));
                method = "BCA";
                rekening = bca.getText().toString();
            }
        });

        ib_bca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bca_content.getVisibility() == View.GONE){
                    ib_bca.setBackground(getActivity().getDrawable(R.drawable.ic_expand_less));
                    bca_content.setVisibility(View.VISIBLE);
                }else{
                    ib_bca.setBackground(getActivity().getDrawable(R.drawable.ic_expand_more));
                    bca_content.setVisibility(View.GONE);
                }
            }
        });

        bni_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rv_bni.setBackground(getActivity().getDrawable(R.drawable.bg_btn_solid_green));
                rv_mandiri.setBackground(getActivity().getDrawable(R.drawable.bg_btn_solid_white));
                rv_bca.setBackground(getActivity().getDrawable(R.drawable.bg_btn_solid_white));
                method = "BNI";
                rekening = bni.getText().toString();
            }
        });

        rv_bni.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rv_bni.setBackground(getActivity().getDrawable(R.drawable.bg_btn_solid_green));
                rv_mandiri.setBackground(getActivity().getDrawable(R.drawable.bg_btn_solid_white));
                rv_bca.setBackground(getActivity().getDrawable(R.drawable.bg_btn_solid_white));
                method = "BNI";
                rekening = bni.getText().toString();
            }
        });

        bni.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rv_bni.setBackground(getActivity().getDrawable(R.drawable.bg_btn_solid_green));
                rv_mandiri.setBackground(getActivity().getDrawable(R.drawable.bg_btn_solid_white));
                rv_bca.setBackground(getActivity().getDrawable(R.drawable.bg_btn_solid_white));
                method = "BNI";
                rekening = bni.getText().toString();
            }
        });

        ib_bni.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bni_content.getVisibility() == View.GONE){
                    ib_bni.setBackground(getActivity().getDrawable(R.drawable.ic_expand_less));
                    bni_content.setVisibility(View.VISIBLE);
                }else{
                    ib_bni.setBackground(getActivity().getDrawable(R.drawable.ic_expand_more));
                    bni_content.setVisibility(View.GONE);
                }
            }
        });
        userRefs = FirebaseDatabase.getInstance().getReference().child("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        marketRefs = FirebaseDatabase.getInstance().getReference().child("MarketPay").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        //proses mengambil data user dari firebase database
        userRefs.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                email = dataSnapshot.child("email").getValue().toString();
                name = dataSnapshot.child("name").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //kondisi utk mengecek apakah user telah memilih bank atau belum
                if (TextUtils.isEmpty(method)){
                    Toast.makeText(getActivity(), "Please choose payment method first", Toast.LENGTH_SHORT).show();
                }else{
                    alertChoose(bundle);
                }
            }
        });

    }

    //method utk menampilkan pesan dialog ketika menekan tombol pay now
    public void alertChoose(final Bundle bundle){
        AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(getActivity());

        // Setting Dialog Title
        alertDialog2.setTitle("Payment Confirmation");

        alertDialog2.setCancelable(false);

        // Setting Dialog Message
        alertDialog2.setMessage("You can't change payment method later, are you sure?");

        // Setting Positive "Yes" Btn
        alertDialog2.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog
                bundle.putString("rekening",rekening);
                bundle.putString("method",method);
                final HashMap marketMap = new HashMap();
                marketMap.put("name",name);
                marketMap.put("email",email);
                marketMap.put("rekening",rekening);
                marketMap.put("method",method);
                marketMap.put("amount",Integer.valueOf(amount.replaceAll("[$,]","")));
//                marketMap.put("millis",5000);
                marketMap.put("millis",300000);
                marketMap.put("status","Waiting");
                marketRefs.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        marketRefs.child(dataSnapshot.getChildrenCount()+"").updateChildren(marketMap).addOnCompleteListener(new OnCompleteListener() {
                            @Override
                            public void onComplete(@NonNull Task task) {
                                ConfirmationFragment confirmationFragment = new ConfirmationFragment();
                                setFragment(confirmationFragment);
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

    //method utk pindah-pindah halaman menggunakan fragment
    private void setFragment(Fragment fragment) // fungsi buat pindah - pindah fragment
    {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
        //dilakukan loop pop stack fragment agar fragment tidak tumpah tindih
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        HomeFragment homeFragment = new HomeFragment();
        fragmentTransaction.replace(R.id.frameFragment,homeFragment).addToBackStack(null);
        fragmentTransaction.replace(R.id.frameFragment,fragment);
        fragmentTransaction.commit();
    }

    //method utk mensetting toolbar agar tidak ada title dan juga agar ada tombol back
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
