package com.example.supermarket.Profile;

import android.os.Bundle;
import android.util.Log;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class NotifikasiFragment extends Fragment {

    private BottomNavigationView bottomNavigationView;
    private Toolbar toolbar;
    private TextView tv_notif_empty, tv_delete_all;
    private ProgressBar progressBar;
    private Boolean status = true;

    private DatabaseReference notifRefs;

    private ArrayList<OngoingModel> mList = new ArrayList<>();
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter adapter;
    private RecyclerView  rv_item;

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
        return inflater.inflate(R.layout.fragment_notifikasi, container, false);
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
        toolbar = getActivity().findViewById(R.id.toolbar_notifikasi);
        setToolbar();

        tv_notif_empty = getActivity().findViewById(R.id.tv_item_empty);
        tv_delete_all = getActivity().findViewById(R.id.tv_delete_notif);
        progressBar = getActivity().findViewById(R.id.pb_item);

        recyclerViewReadyCallback = new RecyclerViewReadyCallback() {
            @Override
            public void onLayoutReady() { // fungsi untuk mengecheck apakah recyclerview sudah siap untuk tampil semua item
                progressBar.setVisibility(View.GONE);
            }
        };
        initRecyclerViewItem();

        //proses pengambilan notifikasi yang belum didelete
        notifRefs = FirebaseDatabase.getInstance().getReference().child("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Notif");
        notifRefs.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount()!=0){
                    tv_notif_empty.setVisibility(View.GONE);
                    mList.clear();
                    for (DataSnapshot dats:dataSnapshot.getChildren()){
                        if (!dats.hasChild("delete")){
                            status=true;
                            tv_notif_empty.setVisibility(View.GONE);
                            mList.add(new OngoingModel(dats.getKey(),dats.child("date").getValue().toString(),dats.child("name").getValue().toString(),
                                    dats.child("payMethod").getValue().toString(),dats.child("address").getValue().toString()));

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
                            dats.child("read").getRef().setValue(true);
                            tv_notif_empty.setVisibility(View.GONE);
                        }else{
                            tv_notif_empty.setVisibility(View.VISIBLE);
                            tv_delete_all.setVisibility(View.GONE);
                            Log.d("iapdnipanjiqeq","ILANG DONG!"+dats.getKey());
                            status=false;
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
                }else{
                    status=false;
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if (status){
            tv_notif_empty.setVisibility(View.GONE);
        }else{
            tv_notif_empty.setVisibility(View.VISIBLE);
        }

        //proses melakukan delete all notif
        tv_delete_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference firebaseDatabase = FirebaseDatabase.getInstance().getReference().child("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Notif");
                firebaseDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getChildrenCount()!=0){
                            for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                                snapshot.child("delete").getRef().setValue(true);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

    }

    private void initRecyclerViewItem(){ // fungsi buat bikin object list artikel
        rv_item = getActivity().findViewById(R.id.rv_item);
        adapter = new NotifikasiAdapter(mList,getActivity().getApplicationContext(),getActivity());
        mLayoutManager = new LinearLayoutManager(getActivity(),RecyclerView.VERTICAL,false);
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
}
