package com.example.supermarket;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
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
import com.nex3z.notificationbadge.NotificationBadge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BuyFragment extends Fragment {

    private RecyclerViewReadyCallback recyclerViewReadyCallback;

    private interface RecyclerViewReadyCallback {
        void onLayoutReady();
    }

    private BottomNavigationView bottomNavigationView;

    private TextView et_search;
    private ImageButton ib_search, ib_notif;
    private RelativeLayout rl_filter;
    private TextView tv_item_empty;
    private ProgressBar pb_item;
    private Toolbar toolbar;
    private NotificationBadge mBadge;

    private ArrayList<String> listKategori = new ArrayList<>();
    private ArrayList<ItemModel> mList = new ArrayList<>();
    private ArrayList<ItemModel> reverse = new ArrayList<>();
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter adapter;
    private RecyclerView rv_kategori, rv_item;

    private RecyclerView.LayoutManager mLayoutManagerK;
    private RecyclerView.Adapter adapterK;

    private DatabaseReference productRefs, notifRefs;

    private String searchText;
    private List<String> categoryArray,conditionArray;
    private int min,max,counter;

    private RelativeLayout buy;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_buy, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        };
        getActivity().getOnBackPressedDispatcher().addCallback(this,callback);
    }

    @Override
    public void onStart() {
        super.onStart();
        initiliaze();
    }

    private void initiliaze(){

        bottomNavigationView = getActivity().findViewById(R.id.bottomNavBar);
        bottomNavigationView.setVisibility(View.GONE);
        toolbar = getActivity().findViewById(R.id.toolbar_buy);
        setToolbar();
        buy = getActivity().findViewById(R.id.nested_buy);

        et_search = getActivity().findViewById(R.id.et_search_buy);
        ib_search = getActivity().findViewById(R.id.ib_search_buy);
        ib_notif = getActivity().findViewById(R.id.ib_notification_buy);
        rl_filter = getActivity().findViewById(R.id.layout_filter);
        rv_item = getActivity().findViewById(R.id.rv_item);
        rv_kategori = getActivity().findViewById(R.id.rv_kategori_buy);
        tv_item_empty = getActivity().findViewById(R.id.tv_item_empty);
        pb_item = getActivity().findViewById(R.id.pb_item);
        mBadge = getActivity().findViewById(R.id.notif_badge);

        initRecyclerViewItem();

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

        recyclerViewReadyCallback = new RecyclerViewReadyCallback() {
            @Override
            public void onLayoutReady() { // fungsi untuk mengecheck apakah recyclerview sudah siap untuk tampil semua item
                pb_item.setVisibility(View.GONE);
            }
        };

        productRefs = FirebaseDatabase.getInstance().getReference().child("Product");

        if (!TextUtils.isEmpty(searchText)){
            et_search.setHint(searchText);
            productRefs.orderByChild("nama_produk").startAt(searchText).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getChildrenCount()!=0){
                        tv_item_empty.setVisibility(View.GONE);
                        mList.clear();
                        reverse.clear();
                        for (DataSnapshot dats:dataSnapshot.getChildren()){
                            mList.add(new ItemModel(dats.getKey(),dats.child("alamat").getValue().toString(),dats.child("imageURL").getValue().toString(),dats.child("kategori").getValue().toString(),
                                    dats.child("kondisi").getValue().toString() ,dats.child("nama_produk").getValue().toString(),dats.child("by").getValue().toString() ,dats.child("uid").getValue().toString(),Integer.valueOf(dats.child("stok").getValue().toString()),Integer.valueOf(dats.child("harga").getValue().toString()),Float.valueOf(dats.child("rating").getValue().toString()),
                                    Integer.valueOf(dats.child("numOfRating").getValue().toString()),dats.child("deskripsi").getValue().toString()));
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
                        Collections.reverse(mList);
                        reverse.addAll(mList);
                    }else{
                        tv_item_empty.setVisibility(View.VISIBLE);
                        pb_item.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }else{
            if (conditionArray == null && categoryArray == null && min == 0 && max == 0){
                productRefs.orderByKey().addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getChildrenCount()!=0){
                            tv_item_empty.setVisibility(View.GONE);
                            mList.clear();
                            reverse.clear();
                            for (DataSnapshot dats:dataSnapshot.getChildren()){
                                mList.add(new ItemModel(dats.getKey(),dats.child("alamat").getValue().toString(),dats.child("imageURL").getValue().toString(),dats.child("kategori").getValue().toString(),
                                        dats.child("kondisi").getValue().toString() ,dats.child("nama_produk").getValue().toString(),dats.child("by").getValue().toString() ,dats.child("uid").getValue().toString(),Integer.valueOf(dats.child("stok").getValue().toString()),Integer.valueOf(dats.child("harga").getValue().toString()),Float.valueOf(dats.child("rating").getValue().toString()),
                                        Integer.valueOf(dats.child("numOfRating").getValue().toString()),dats.child("deskripsi").getValue().toString()));
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
                            Collections.reverse(mList);
                            reverse.addAll(mList);
                        }else{
                            tv_item_empty.setVisibility(View.VISIBLE);
                            pb_item.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }else if (categoryArray.size() != 0 && conditionArray.size() == 0){
                filterCategory();
            }else if (conditionArray.size() != 0 && categoryArray.size() == 0){
                filterCondition();
            }else if(min >= 0 && max != 0 && conditionArray.size() == 0 && categoryArray.size() == 0){
                filterPrice();
            }else if (categoryArray.size() != 0 && conditionArray.size() != 0){
                if(categoryArray.size() == 8 && conditionArray.size() == 2){
                    for (int i = 0; i < categoryArray.size(); i++) {
                        reverse.clear();
                        productRefs.orderByChild("kategori").equalTo(categoryArray.get(i)).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getChildrenCount()!=0){
                                    tv_item_empty.setVisibility(View.GONE);
                                    mList.clear();
                                    for (DataSnapshot dats:dataSnapshot.getChildren()){
                                        for (int j = 0; j < conditionArray.size(); j++) {
                                            if (dats.child("kondisi").getValue().toString().equalsIgnoreCase(conditionArray.get(j))){
                                                mList.add(new ItemModel(dats.getKey(),dats.child("alamat").getValue().toString(),dats.child("imageURL").getValue().toString(),dats.child("kategori").getValue().toString(),
                                                        dats.child("kondisi").getValue().toString() ,dats.child("nama_produk").getValue().toString(),dats.child("by").getValue().toString() ,dats.child("uid").getValue().toString(),Integer.valueOf(dats.child("stok").getValue().toString()),Integer.valueOf(dats.child("harga").getValue().toString()),Float.valueOf(dats.child("rating").getValue().toString()),
                                                        Integer.valueOf(dats.child("numOfRating").getValue().toString()),dats.child("deskripsi").getValue().toString()));
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
                                    }
                                    Collections.reverse(mList);
                                    reverse.addAll(mList);
                                }else{
                                    pb_item.setVisibility(View.GONE);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                    if (reverse.size()==0){
                        tv_item_empty.setVisibility(View.VISIBLE);
                    }else{
                        tv_item_empty.setVisibility(View.GONE);
                    }
                }else{
                    for (int i = 0; i < categoryArray.size(); i++) {
                        reverse.clear();
                        productRefs.orderByChild("kategori").equalTo(categoryArray.get(i)).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getChildrenCount()!=0){
                                    tv_item_empty.setVisibility(View.GONE);
                                    mList.clear();
                                    for (DataSnapshot dats:dataSnapshot.getChildren()){
                                        for (int j = 0; j < conditionArray.size(); j++) {
                                            if (dats.child("kondisi").getValue().toString().equalsIgnoreCase(conditionArray.get(j))){
                                                mList.add(new ItemModel(dats.getKey(),dats.child("alamat").getValue().toString(),dats.child("imageURL").getValue().toString(),dats.child("kategori").getValue().toString(),
                                                        dats.child("kondisi").getValue().toString() ,dats.child("nama_produk").getValue().toString(),dats.child("by").getValue().toString() ,dats.child("uid").getValue().toString(),Integer.valueOf(dats.child("stok").getValue().toString()),Integer.valueOf(dats.child("harga").getValue().toString()),Float.valueOf(dats.child("rating").getValue().toString()),
                                                        Integer.valueOf(dats.child("numOfRating").getValue().toString()),dats.child("deskripsi").getValue().toString()));
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
                                                pb_item.setVisibility(View.GONE);
                                                tv_item_empty.setVisibility(View.VISIBLE);
                                            }
                                        }
                                    }
                                    Collections.reverse(mList);
                                    reverse.addAll(mList);
                                }else{
                                    pb_item.setVisibility(View.GONE);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                    if (reverse.size()==0){
                        tv_item_empty.setVisibility(View.VISIBLE);
                    }else{
                        tv_item_empty.setVisibility(View.GONE);
                    }
                }
            }
        }

        et_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchBarFragment searchBarFragment = new SearchBarFragment();
                setFragment(searchBarFragment);
            }
        });

        ib_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchBarFragment searchBarFragment = new SearchBarFragment();
                setFragment(searchBarFragment);
            }
        });

    }

    private void filterCondition(){
        for (int i = 0; i < conditionArray.size(); i++) {
            reverse.clear();
            productRefs.orderByChild("kondisi").equalTo(conditionArray.get(i)).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getChildrenCount()!=0){
                        tv_item_empty.setVisibility(View.GONE);
                        mList.clear();
                        for (DataSnapshot dats:dataSnapshot.getChildren()){
                            mList.add(new ItemModel(dats.getKey(),dats.child("alamat").getValue().toString(),dats.child("imageURL").getValue().toString(),dats.child("kategori").getValue().toString(),
                                    dats.child("kondisi").getValue().toString() ,dats.child("nama_produk").getValue().toString(),dats.child("by").getValue().toString() ,dats.child("uid").getValue().toString(),Integer.valueOf(dats.child("stok").getValue().toString()),Integer.valueOf(dats.child("harga").getValue().toString()),Float.valueOf(dats.child("rating").getValue().toString()),
                                    Integer.valueOf(dats.child("numOfRating").getValue().toString()),dats.child("deskripsi").getValue().toString()));
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
                        Collections.reverse(mList);
                        reverse.addAll(mList);
                    }else{
                        pb_item.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        if (reverse.size()==0){
            tv_item_empty.setVisibility(View.VISIBLE);
        }else{
            tv_item_empty.setVisibility(View.GONE);
        }
    }

    private void filterCategory(){
        for (int i = 0; i < categoryArray.size(); i++) {
            reverse.clear();
            productRefs.orderByChild("kategori").equalTo(categoryArray.get(i)).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.getChildrenCount()!=0){
                        tv_item_empty.setVisibility(View.GONE);
                        mList.clear();
                        for (DataSnapshot dats:dataSnapshot.getChildren()){
                            mList.add(new ItemModel(dats.getKey(),dats.child("alamat").getValue().toString(),dats.child("imageURL").getValue().toString(),dats.child("kategori").getValue().toString(),
                                    dats.child("kondisi").getValue().toString() ,dats.child("nama_produk").getValue().toString(),dats.child("by").getValue().toString() ,dats.child("uid").getValue().toString(),Integer.valueOf(dats.child("stok").getValue().toString()),Integer.valueOf(dats.child("harga").getValue().toString()),Float.valueOf(dats.child("rating").getValue().toString()),
                                    Integer.valueOf(dats.child("numOfRating").getValue().toString()),dats.child("deskripsi").getValue().toString()));
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
                        Collections.reverse(mList);
                        reverse.addAll(mList);
                    }else{
                        pb_item.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        if (reverse.size()==0){
            tv_item_empty.setVisibility(View.VISIBLE);
        }else{
            tv_item_empty.setVisibility(View.GONE);
        }
    }

    private void filterPrice(){
        productRefs.orderByChild("harga").startAt(min).endAt(max).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount()!=0){
                    tv_item_empty.setVisibility(View.GONE);
                    mList.clear();
                    reverse.clear();
                    for (DataSnapshot dats:dataSnapshot.getChildren()){
                        mList.add(new ItemModel(dats.getKey(),dats.child("alamat").getValue().toString(),dats.child("imageURL").getValue().toString(),dats.child("kategori").getValue().toString(),
                                dats.child("kondisi").getValue().toString() ,dats.child("nama_produk").getValue().toString(),dats.child("by").getValue().toString() ,dats.child("uid").getValue().toString(),Integer.valueOf(dats.child("stok").getValue().toString()),Integer.valueOf(dats.child("harga").getValue().toString()),Float.valueOf(dats.child("rating").getValue().toString()),
                                Integer.valueOf(dats.child("numOfRating").getValue().toString()),dats.child("deskripsi").getValue().toString()));
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
                    Collections.reverse(mList);
                    reverse.addAll(mList);
                }else{
                    tv_item_empty.setVisibility(View.VISIBLE);
                    pb_item.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initRecyclerViewItem(){ // fungsi buat bikin object list artikel
        rv_item = getActivity().findViewById(R.id.rv_item);
        adapter = new ItemAdapter(reverse,getActivity().getApplicationContext(),getActivity());
        mLayoutManager = new GridLayoutManager(getActivity(),2);
        rv_item.setLayoutManager(mLayoutManager);
        rv_item.setAdapter(adapter);
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

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public void setCategoryArray(List<String> categoryArray) {
        this.categoryArray = categoryArray;
    }

    public void setConditionArray(List<String> conditionArray) {
        this.conditionArray = conditionArray;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public void setMax(int max) {
        this.max = max;
    }
}
