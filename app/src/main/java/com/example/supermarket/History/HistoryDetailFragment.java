package com.example.supermarket.History;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.supermarket.Cart.CartModel;
import com.example.supermarket.Ongoing.OngoingModel;
import com.example.supermarket.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HistoryDetailFragment extends Fragment {

    private BottomNavigationView bottomNavigationView;
    private Toolbar toolbar;
    private TextView tv_ongoing_name, tv_name, tv_date, tv_address, tv_payMethod;

    private ArrayList<String> productId = new ArrayList<>();
    private ArrayList<CartModel> mList = new ArrayList<>();
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter adapter;
    private RecyclerView  rv_item;
    private ProgressBar progressBar;

    private OngoingModel ongoingModel;
    private DatabaseReference ongoingRefs,notifRefs;
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
        return inflater.inflate(R.layout.fragment_history_detail, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        initialize();
    }

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
        ongoingRefs.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    mList.add(new CartModel(snapshot.getKey(),snapshot.child("id").getValue().toString(),snapshot.child("nama_produk").getValue().toString(),snapshot.child("category").getValue().toString(),
                            snapshot.child("imageURL").getValue().toString(),Integer.valueOf(snapshot.child("amount").getValue().toString()),Integer.valueOf(snapshot.child("price").getValue().toString()),Integer.valueOf(snapshot.child("totalPrice").getValue().toString())));
                    adapter.notifyDataSetChanged();
                    productId.add(snapshot.child("id").getValue().toString());
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
    }

    private void initRecyclerViewItem(){ // fungsi buat bikin object list artikel
        rv_item = getActivity().findViewById(R.id.rv_cart);
        adapter = new HistoryDetailAdapter(mList,getActivity().getApplicationContext(),getActivity(), ongoingModel.getId());
        mLayoutManager = new LinearLayoutManager(getActivity(),RecyclerView.VERTICAL,true);
        ((LinearLayoutManager) mLayoutManager).setStackFromEnd(true);
        rv_item.setLayoutManager(mLayoutManager);
        rv_item.setAdapter(adapter);
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
        FragmentManager fm = getActivity().getSupportFragmentManager();
        for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameFragment,fragment).addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void setOngoingModel(OngoingModel ongoingModel) {
        this.ongoingModel = ongoingModel;
    }
}
