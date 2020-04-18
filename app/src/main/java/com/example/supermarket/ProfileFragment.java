package com.example.supermarket;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nex3z.notificationbadge.NotificationBadge;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private BottomNavigationView bottomNavigationView;
    private DatabaseReference userRefs,notifRefs;
    private int counter;
    private NotificationBadge mBadge;

    private TextView tv_profile, tv_name, tv_balance, tv_email, tv_notifikasi;
    private Button btnLogout;
    private CircleImageView imageView;
    private ProgressBar progressBar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile,container,false);
    }

    @Override
    public void onStart() {
        super.onStart();
        initialize();
    }

    private void initialize(){
        bottomNavigationView = getActivity().findViewById(R.id.bottomNavBar);
        bottomNavigationView.setVisibility(View.VISIBLE);

        userRefs = FirebaseDatabase.getInstance().getReference().child("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        tv_profile = getActivity().findViewById(R.id.profile);
        tv_name = getActivity().findViewById(R.id.tv_nama_akun);
        tv_email = getActivity().findViewById(R.id.tv_email_akun);
        tv_balance = getActivity().findViewById(R.id.tv_balance_akun);
        imageView = getActivity().findViewById(R.id.iv_akun_picture);
        progressBar = getActivity().findViewById(R.id.pb_akun);
        btnLogout = getActivity().findViewById(R.id.btn_logout);
        mBadge = getActivity().findViewById(R.id.notif_badge);
        tv_notifikasi = getActivity().findViewById(R.id.notification);

        tv_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DetailProfileDragment detailProfileDragment = new DetailProfileDragment();
                setFragment(detailProfileDragment);
            }
        });

        tv_notifikasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(new NotifikasiFragment());
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertsignout();
            }
        });

        userRefs.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {

                Picasso.get().load(dataSnapshot.child("displayPicture").getValue().toString()).into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        progressBar.setVisibility(View.GONE);

                        tv_name.setText(dataSnapshot.child("name").getValue().toString());
                        tv_email.setText(dataSnapshot.child("email").getValue().toString());
                        tv_balance.setText("MarketPay: $"+dataSnapshot.child("saldo").getValue().toString());
                    }

                    @Override
                    public void onError(Exception e) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        notifRefs = FirebaseDatabase.getInstance().getReference().child("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Notif");

        notifRefs.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount()!=0){
                    counter=0;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        if (!snapshot.hasChild("read")){
                            counter+=1;
                        }
                    }
                    mBadge.setNumber(counter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    public void alertsignout(){ // fungsi untuk membuat alert dialog ketika ingin logout
        AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(getActivity());

        // Setting Dialog Title
        alertDialog2.setTitle("Exit Confirmation");

        alertDialog2.setCancelable(false);

        // Setting Dialog Message
        alertDialog2.setMessage("Are you sure want to log out?");

        // Setting Positive "Yes" Btn
        alertDialog2.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog
                FirebaseAuth.getInstance().signOut();
                closeActivity(LoginActivity.class);
            }
        });

        alertDialog2.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog2.show();

    }

    private void closeActivity(Class activity) { // fungsi untuk kelarin activity terakhir, dan diganti ke activity baru trus dikirim ke halaman login
        Intent mainIntent = new Intent(getActivity(), activity);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        getActivity().finish();
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameFragment,fragment).addToBackStack(null);
        fragmentTransaction.commit();
    }
}
