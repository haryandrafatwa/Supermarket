package com.example.supermarket.Cart;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.supermarket.Payment.PaymentMethodFragment;
import com.example.supermarket.R;
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
    private Button btnPay;

    private ArrayList<CartModel> mList = new ArrayList<>();
    private ArrayList<CartModel> mList2 = new ArrayList<>();
    private RecyclerView recyclerViewDetail, recyclerView;
    private RecyclerView.LayoutManager mLayoutManager,mLayoutManager2;
    private RecyclerView.Adapter adapter,adapter2;

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

    //method utk menginisiasi objek2 yang ada pada halaman ini
    private void initialize(){

        bottomNavigationView = getActivity().findViewById(R.id.bottomNavBar);
        bottomNavigationView.setVisibility(View.VISIBLE);
        bottomNavigationView.setSelectedItemId(R.id.menuCart);

        tv_estimated_price = getActivity().findViewById(R.id.tv_estimated_price);
        tv_cart_empty = getActivity().findViewById(R.id.tv_cart_empty);
        progressBar  =getActivity().findViewById(R.id.pb_cart);
        relativeLayout = getActivity().findViewById(R.id.rl_cart);
        btnPay = getActivity().findViewById(R.id.btn_pay_now);
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
        //proses utk mengambil produk yang ada pada cart user
        cartRefs.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() != 0){
                    mList.clear();
                    relativeLayout.setVisibility(View.VISIBLE);
                    int totalPay = 0;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        mList.add(new CartModel(snapshot.getKey(),snapshot.child("id").getValue().toString(),snapshot.child("nama_produk").getValue().toString(),snapshot.child("category").getValue().toString(),
                                snapshot.child("imageURL").getValue().toString(),Integer.valueOf(snapshot.child("amount").getValue().toString()),Integer.valueOf(snapshot.child("price").getValue().toString()),Integer.valueOf(snapshot.child("totalPrice").getValue().toString())));
                        adapter.notifyDataSetChanged();
                        adapter2.notifyDataSetChanged();
                        totalPay+=Integer.valueOf(snapshot.child("totalPrice").getValue().toString());

                        recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                            @Override
                            public void onGlobalLayout() {
                                if(recyclerViewReadyCallback != null){
                                    recyclerViewReadyCallback.onLayoutReady();
                                }
                                recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            }
                        });
                        recyclerViewDetail.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                            @Override
                            public void onGlobalLayout() {
                                if(recyclerViewReadyCallback != null){
                                    recyclerViewReadyCallback.onLayoutReady();
                                }
                                recyclerViewDetail.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            }
                        });
                    }
                    tv_estimated_price.setText("$"+(totalPay+2));
                }else{
                    tv_cart_empty.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFragment(new PaymentMethodFragment(tv_estimated_price.getText().toString()));
            }
        });

    }

    private void setFragment(Fragment fragment) // fungsi buat pindah - pindah fragment
    {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameFragment,fragment).addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void initRecyclerViewItemDetail(){ // fungsi buat bikin object list item detail
        recyclerViewDetail = getActivity().findViewById(R.id.rv_cart);
        adapter = new CartDetailAdapter(mList,getActivity().getApplicationContext(),getActivity());
        mLayoutManager = new LinearLayoutManager(getActivity(),RecyclerView.VERTICAL,true);
        ((LinearLayoutManager) mLayoutManager).setStackFromEnd(true);
        recyclerViewDetail.setLayoutManager(mLayoutManager);
        recyclerViewDetail.setAdapter(adapter);
    }

    private void initRecyclerViewItem(){ // fungsi buat bikin object list item
        recyclerView = getActivity().findViewById(R.id.rv_cartItem);
        adapter2 = new CartAdapter(mList,getActivity().getApplicationContext(),getActivity());
        mLayoutManager2 = new LinearLayoutManager(getActivity(),RecyclerView.VERTICAL,true);
        ((LinearLayoutManager) mLayoutManager2).setStackFromEnd(true);
        recyclerView.setLayoutManager(mLayoutManager2);
        recyclerView.setAdapter(adapter2);
    }
}
