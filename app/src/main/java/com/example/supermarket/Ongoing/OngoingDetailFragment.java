package com.example.supermarket.Ongoing;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.supermarket.Cart.CartModel;
import com.example.supermarket.MainActivity;
import com.example.supermarket.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OngoingDetailFragment extends Fragment {

    private BottomNavigationView bottomNavigationView;
    private Toolbar toolbar;
    private Button btnComplete;
    private TextView btnCancel,tv_ongoing_name, tv_name, tv_date, tv_address, tv_payMethod;

    private ArrayList<CartModel> mList = new ArrayList<>();
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter adapter;
    private RecyclerView  rv_item;
    private ProgressBar progressBar;

    private OngoingModel ongoingModel;
    private DatabaseReference ongoingRefs;
    private android.app.AlertDialog.Builder dialog;

    private RecyclerViewReadyCallback recyclerViewReadyCallback;

    private interface RecyclerViewReadyCallback {
        void onLayoutReady();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ongoing_detail, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        initialize();
    }

    //method utk menginisiasi seluruh objek yang ada pada halaman ini 
    private void initialize(){

        bottomNavigationView = getActivity().findViewById(R.id.bottomNavBar);
        bottomNavigationView.setVisibility(View.GONE);
        toolbar = getActivity().findViewById(R.id.toolbar_ongoing);
        setToolbar();

        tv_ongoing_name = getActivity().findViewById(R.id.tv_item_name);
        tv_name = getActivity().findViewById(R.id.tv_item_name_bill);
        tv_date = getActivity().findViewById(R.id.tv_item_date);
        tv_payMethod = getActivity().findViewById(R.id.tv_item_payMethod);
        tv_address = getActivity().findViewById(R.id.tv_item_address);
        progressBar = getActivity().findViewById(R.id.pb_item);
        btnCancel = getActivity().findViewById(R.id.btnCancel);
        btnComplete = getActivity().findViewById(R.id.btnComplete);

        initRecyclerViewItem();

        recyclerViewReadyCallback = new RecyclerViewReadyCallback() {
            @Override
            public void onLayoutReady() { // fungsi untuk mengecheck apakah recyclerview sudah siap untuk tampil semua item
                progressBar.setVisibility(View.GONE);
            }
        };

        tv_ongoing_name.setText("Order No. "+(Integer.valueOf(ongoingModel.getId())+1));
        tv_name.setText(ongoingModel.getName());
        tv_date.setText(ongoingModel.getDate());
        tv_payMethod.setText(ongoingModel.getPayMethod());
        tv_address.setText(ongoingModel.getAddress());

        ongoingRefs = FirebaseDatabase.getInstance().getReference().child("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Order").child(ongoingModel.getId());
        //mengambil data ongoing pada user tsb dari firebase database
        ongoingRefs.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    mList.add(new CartModel(snapshot.getKey(),snapshot.child("id").getValue().toString(),snapshot.child("nama_produk").getValue().toString(),snapshot.child("category").getValue().toString(),
                            snapshot.child("imageURL").getValue().toString(),Integer.valueOf(snapshot.child("amount").getValue().toString()),Integer.valueOf(snapshot.child("price").getValue().toString()),Integer.valueOf(snapshot.child("totalPrice").getValue().toString())));
                    adapter.notifyDataSetChanged();
                    rv_item.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            if(recyclerViewReadyCallback != null){
                                recyclerViewReadyCallback.onLayoutReady();
                            }
                            rv_item.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initializeDialogCancel();
            }
        });
        btnComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertCompleted();
            }
        });
    }

    //method utk menampilkan pesan dialog ketika pesanan atau transaksi dibatalkan
    private void initializeDialogCancel() {
        dialog = new android.app.AlertDialog.Builder(getActivity(), R.style.CustomAlertDialog);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_cancel_order, null);
        dialogView.setBackgroundColor(Color.TRANSPARENT);
        dialog.setView(dialogView);
        dialog.setCancelable(false);
        dialog.setTitle(null);

        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Notif").child(ongoingModel.getId());
                reference.child("status").setValue("Cancel");
                closeActivity(MainActivity.class);
            }
        });

        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        dialog.show();

    }

    //method utk menampilkan pesan dialog bahwa pesanan telah selesai dibayar
    private void initializeDialogCompleted() {
        dialog = new android.app.AlertDialog.Builder(getActivity(), R.style.CustomAlertDialog);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_order_completed, null);
        dialogView.setBackgroundColor(Color.TRANSPARENT);
        dialog.setView(dialogView);
        dialog.setCancelable(false);
        dialog.setTitle(null);

        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Notif").child(ongoingModel.getId());
                reference.child("status").setValue("Done");
                closeActivity(MainActivity.class);
            }
        });

        dialog.show();

    }

    public void alertCompleted(){ // fungsi untuk membuat alert dialog ketika ingin menyelesaikan pesanan
        AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(getActivity());

        // Setting Dialog Title
        alertDialog2.setTitle("Confirmation");

        alertDialog2.setCancelable(false);

        // Setting Dialog Message
        alertDialog2.setMessage("Are you sure want to completed this order?");

        // Setting Positive "Yes" Btn
        alertDialog2.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog
                initializeDialogCompleted();
                dialog.cancel();
            }
        });

        alertDialog2.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog2.show();

    }

    private void initRecyclerViewItem(){ // fungsi buat bikin object list ongoing
        rv_item = getActivity().findViewById(R.id.rv_cart);
        adapter = new OngoingDetailAdapter(mList,getActivity().getApplicationContext(),getActivity());
        mLayoutManager = new LinearLayoutManager(getActivity(),RecyclerView.VERTICAL,true);
        ((LinearLayoutManager) mLayoutManager).setStackFromEnd(true);
        rv_item.setLayoutManager(mLayoutManager);
        rv_item.setAdapter(adapter);
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

    private void closeActivity(Class activity) { // fungsi untuk kelarin activity terakhir, dan diganti ke activity baru trus dikirim ke halaman login
        Intent mainIntent = new Intent(getActivity(), activity);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        getActivity().finish();
    }

    public void setOngoingModel(OngoingModel ongoingModel) {
        this.ongoingModel = ongoingModel;
    }
}
