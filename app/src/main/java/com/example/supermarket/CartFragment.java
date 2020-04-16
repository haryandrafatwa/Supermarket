package com.example.supermarket;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CartFragment extends Fragment {

    private RecyclerViewReadyCallback recyclerViewReadyCallback;

    private interface RecyclerViewReadyCallback {
        void onLayoutReady();
    }

    private BottomNavigationView bottomNavigationView;
    private TextView tv_estimated_price, tv_cart_empty;
    private ProgressBar progressBar;
    private RelativeLayout relativeLayout;

    private ArrayList<CartModel> mList = new ArrayList<>();
    private RecyclerView recyclerViewDetail, recyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter adapter;

    private DatabaseReference cartRefs;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cart,container,false);
    }

    @Override
    public void onStart() {
        super.onStart();
        initialize();
    }

    private void initialize(){

        bottomNavigationView = getActivity().findViewById(R.id.bottomNavBar);
        bottomNavigationView.setVisibility(View.VISIBLE);

        tv_estimated_price = getActivity().findViewById(R.id.tv_estimated_price);
        tv_cart_empty = getActivity().findViewById(R.id.tv_cart_empty);
        progressBar  =getActivity().findViewById(R.id.pb_cart);
        relativeLayout = getActivity().findViewById(R.id.rl_cart);
        initRecyclerViewItem();
        initRecyclerViewItemDetail();

        recyclerViewReadyCallback = new RecyclerViewReadyCallback() {
            @Override
            public void onLayoutReady() { // fungsi untuk mengecheck apakah recyclerview sudah siap untuk tampil semua item
                progressBar.setVisibility(View.GONE);
                relativeLayout.setVisibility(View.VISIBLE);
            }
        };

        cartRefs = FirebaseDatabase.getInstance().getReference().child("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Cart");
        cartRefs.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() != 0){
                    mList.clear();
                    relativeLayout.setVisibility(View.VISIBLE);
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        mList.add(new CartModel(snapshot.getKey(),snapshot.child("id").getValue().toString(),snapshot.child("nama_produk").getValue().toString(),snapshot.child("category").getValue().toString(),
                                snapshot.child("imageURL").getValue().toString(),Integer.valueOf(snapshot.child("amount").getValue().toString()),Integer.valueOf(snapshot.child("price").getValue().toString())));
                        adapter.notifyDataSetChanged();

                        recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                            @Override
                            public void onGlobalLayout() {
                                if(recyclerViewReadyCallback != null){
                                    recyclerViewReadyCallback.onLayoutReady();
                                }
                                recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            }
                        });
                    }
                }else{
                    tv_cart_empty.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void initRecyclerViewItemDetail(){ // fungsi buat bikin object list artikel
        recyclerView = getActivity().findViewById(R.id.rv_cart);
        adapter = new CartDetailAdapter(mList,getActivity().getApplicationContext(),getActivity());
        mLayoutManager = new LinearLayoutManager(getActivity(),RecyclerView.VERTICAL,true);
        ((LinearLayoutManager) mLayoutManager).setStackFromEnd(true);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);
    }

    private void initRecyclerViewItem(){ // fungsi buat bikin object list artikel
        recyclerView = getActivity().findViewById(R.id.rv_cartItem);
        adapter = new CartAdapter(mList,getActivity().getApplicationContext(),getActivity());
        mLayoutManager = new LinearLayoutManager(getActivity(),RecyclerView.VERTICAL,true);
        ((LinearLayoutManager) mLayoutManager).setStackFromEnd(true);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);
    }
}
