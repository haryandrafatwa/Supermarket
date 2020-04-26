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

public class HistoryFragment extends Fragment {

    private interface RecyclerViewReadyCallback {
        void onLayoutReady();
    }

    private RecyclerViewReadyCallback recyclerViewReadyCallback;

    private ArrayList<OngoingModel> mList = new ArrayList<>();
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter adapter;
    private RecyclerView  rv_item;

    private BottomNavigationView bottomNavigationView;
    private TextView tv_item_empty;
    private ProgressBar progressBar;

    private DatabaseReference notifRefs;

    private Boolean status = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history,container,false);
    }

    @Override
    public void onStart() {
        super.onStart();
        initialize();
    }

    private void initialize(){

        bottomNavigationView = getActivity().findViewById(R.id.bottomNavBar);
        bottomNavigationView.setVisibility(View.VISIBLE);

        initRecyclerViewItem();
        recyclerViewReadyCallback = new RecyclerViewReadyCallback() {
            @Override
            public void onLayoutReady() { // fungsi untuk mengecheck apakah recyclerview sudah siap untuk tampil semua item
                progressBar.setVisibility(View.GONE);
            }
        };
        tv_item_empty = getActivity().findViewById(R.id.tv_item_empty);
        progressBar = getActivity().findViewById(R.id.pb_item);

        notifRefs = FirebaseDatabase.getInstance().getReference().child("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Notif");
        notifRefs.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount()!=0){
                    tv_item_empty.setVisibility(View.GONE);
                    mList.clear();
                    for (DataSnapshot dats:dataSnapshot.getChildren()){
                        if (dats.child("status").getValue().toString().equalsIgnoreCase("done")){
                            status = true;
                            tv_item_empty.setVisibility(View.GONE);
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
                        }else{
                            status = false;
//                            progressBar.setVisibility(View.GONE);
//                            tv_item_empty.setVisibility(View.VISIBLE);
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
                    tv_item_empty.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if (status){
            tv_item_empty.setVisibility(View.GONE);
        }else{
            tv_item_empty.setVisibility(View.VISIBLE);

        }

    }

    private void initRecyclerViewItem(){ // fungsi buat bikin object list artikel
        rv_item = getActivity().findViewById(R.id.rv_item);
        adapter = new HistoryAdapter(mList,getActivity().getApplicationContext(),getActivity());
        mLayoutManager = new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL,false);
        ((LinearLayoutManager) mLayoutManager).setStackFromEnd(true);
        rv_item.setLayoutManager(mLayoutManager);
        rv_item.setAdapter(adapter);
    }
}
