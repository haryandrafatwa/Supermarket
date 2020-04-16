package com.example.supermarket;

import android.content.Context;
import android.graphics.Color;
import android.icu.text.NumberFormat;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import it.sephiroth.android.library.rangeseekbar.RangeSeekBar;

public class SearchBarFragment extends Fragment {

    private EditText et_search, et_minimum,et_maximum;
    private ImageButton ib_search, ib_notif;
    private TextView electronic, furniture, vegetable, fruit, snack, beverage, appliance, sport, new_,used;
    private RangeSeekBar rangeSeekBar;

    private BottomNavigationView bottomNavigationView;
    private Toolbar toolbar;

    private DatabaseReference productRefs;

    private List<String> category = new ArrayList<>(), condition = new ArrayList<>();

    private Boolean elec = true,furn = true,veg = true,fru = true,sna = true,bev = true,app = true,spo = true,nw = true,prl = true;
    private int min = 0,max = 0,minSearch = 0,maxSearch = 0;

    private DatabaseReference searchRefs;

    private ArrayList<SearchModel> mList = new ArrayList<>();
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter adapter;
    private RecyclerView rv_search;

    private Boolean status = false;
    private String id;;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        initialize();
    }



    private void initialize(){

        bottomNavigationView = getActivity().findViewById(R.id.bottomNavBar);
        bottomNavigationView.setVisibility(View.GONE);

        toolbar = getActivity().findViewById(R.id.toolbar_search);
        setToolbar();

        initRecyclerViewItemKategori();

        et_search = getActivity().findViewById(R.id.et_search_buy);
        ib_search = getActivity().findViewById(R.id.ib_search_buy);
        ib_notif = getActivity().findViewById(R.id.ib_notification_buy);

        electronic = getActivity().findViewById(R.id.electronic);
        furniture = getActivity().findViewById(R.id.furniture);
        vegetable = getActivity().findViewById(R.id.vegetable);
        fruit = getActivity().findViewById(R.id.fruit);
        snack = getActivity().findViewById(R.id.snack);
        beverage = getActivity().findViewById(R.id.beverage);
        appliance = getActivity().findViewById(R.id.appliance);
        sport = getActivity().findViewById(R.id.sport);
        new_ = getActivity().findViewById(R.id.newCondition);
        used = getActivity().findViewById(R.id.used);

        searchRefs = FirebaseDatabase.getInstance().getReference().child("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Search");
        searchRefs.limitToLast(5).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount()!= 0){
                    rv_search.setVisibility(View.VISIBLE);
                    mList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        mList.add(new SearchModel(snapshot.child("recentSearch").getValue().toString(),snapshot.getKey()));
                        adapter.notifyDataSetChanged();
                    }
                }else {
                    rv_search.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        String recent = ((MainActivity)getActivity()).getSearch();

        et_search.setText(recent);

        ib_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BuyFragment buyFragment = new BuyFragment();
                if (!TextUtils.isEmpty(et_search.getText().toString())){
                    buyFragment.setSearchText(et_search.getText().toString());
                    searchRefs.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getChildrenCount()==0){
                                HashMap hashMap = new HashMap();
                                hashMap.put("id",dataSnapshot.getChildrenCount()+"");
                                hashMap.put("recentSearch",et_search.getText().toString());
                                dataSnapshot.child(dataSnapshot.getChildrenCount()+"").getRef().updateChildren(hashMap);
                            }else{
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                                    id = dataSnapshot.getChildrenCount()+"";
                                    if (!snapshot.child("recentSearch").getValue().toString().equalsIgnoreCase(et_search.getText().toString())){
                                        status = false;
                                    }else{
                                        status = true;
                                        break;
                                    }
                                }
                                if (!status){
                                    HashMap hashMap = new HashMap();
                                    hashMap.put("id",id);
                                    hashMap.put("recentSearch",et_search.getText().toString());
                                    dataSnapshot.child(id).getRef().updateChildren(hashMap);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                if (category != null){
                    buyFragment.setCategoryArray(category);
                }
                if (condition != null){
                    buyFragment.setConditionArray(condition);
                }
                if (minSearch >= 0 && maxSearch != 0 ){
                    buyFragment.setMax(maxSearch);
                    buyFragment.setMin(minSearch);
                }
                setFragment(buyFragment);
            }
        });

        et_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        event != null &&
                                event.getAction() == KeyEvent.ACTION_DOWN &&
                                event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    if (event == null || !event.isShiftPressed()) {
                        // the user is done typing.
                        ib_search.callOnClick();
                        return true; // consume.
                    }
                }
                return false; // pass on to other listeners.
            }
        });

        et_minimum = getActivity().findViewById(R.id.et_minimum);
        et_maximum = getActivity().findViewById(R.id.et_maximum);
        rangeSeekBar = getActivity().findViewById(R.id.rs_price);

        et_search.requestFocus();
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(et_search, InputMethodManager.SHOW_IMPLICIT);

        productRefs = FirebaseDatabase.getInstance().getReference().child("Product");
        productRefs.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount()!=0){
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        if (snapshot.getKey().equalsIgnoreCase("0")){
                            max = Integer.valueOf(snapshot.child("harga").getValue().toString());
                            min = Integer.valueOf(snapshot.child("harga").getValue().toString());
                        }else{
                            if (max < Integer.valueOf(snapshot.child("harga").getValue().toString())){
                                max = Integer.valueOf(snapshot.child("harga").getValue().toString());
                            }

                            if (min > Integer.valueOf(snapshot.child("harga").getValue().toString())){
                                min = Integer.valueOf(snapshot.child("harga").getValue().toString());
                            }
                        }
                    }
                    et_maximum.setText(max+"");
                    et_minimum.setText(min+"");
                    rangeSeekBar.setMax(max);
                    rangeSeekBar.setProgress(0,max);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        rangeSeekBar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener() {
            @Override
            public void onProgressChanged(final RangeSeekBar seekBar, final int progressStart, final int progressEnd, final boolean fromUser) {
                et_maximum.setText(progressEnd+"");
                et_minimum.setText(progressStart+"");
                maxSearch = progressEnd;
                minSearch = progressStart;
            }

            @Override
            public void onStartTrackingTouch(final RangeSeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(final RangeSeekBar seekBar) { }
        });

        et_maximum.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            private String current = "";
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals(current)) {
                    et_maximum.removeTextChangedListener(this);

                    Locale local = new Locale("en", "US");
                    String replaceable = String.format("[$,.]",
                            NumberFormat.getCurrencyInstance().getCurrency()
                                    .getSymbol(local));
                    String cleanString = s.toString().replaceAll(replaceable,
                            "");

                    double parsed;
                    try {
                        parsed = Double.parseDouble(cleanString);
                        if (parsed > max){
                            parsed = max;
                        }
                        maxSearch = (int) parsed;
                        rangeSeekBar.setProgress(rangeSeekBar.getProgressStart(),(int) parsed);
                    } catch (NumberFormatException e) {
                        parsed = 0.00;
                    }

                    NumberFormat formatter = NumberFormat
                            .getCurrencyInstance(local);
                    formatter.setMaximumFractionDigits(0);
                    formatter.setParseIntegerOnly(true);
                    String formatted = formatter.format((parsed));

                    current = formatted;
                    et_maximum.setText(formatted);
                    et_maximum.setSelection(formatted.length());
                    et_maximum.addTextChangedListener(this);
                }
            }
        });

        et_minimum.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            private String current = "";
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals(current)) {
                    et_minimum.removeTextChangedListener(this);

                    Locale local = new Locale("en", "US");
                    String replaceable = String.format("[$,.]",
                            NumberFormat.getCurrencyInstance().getCurrency()
                                    .getSymbol(local));
                    String cleanString = s.toString().replaceAll(replaceable,
                            "");

                    double parsed;
                    try {
                        parsed = Double.parseDouble(cleanString);
                        if (parsed > (max-10)){
                            parsed = (max-10);
                        }
                        minSearch = (int) parsed;
                        rangeSeekBar.setProgress((int) parsed,rangeSeekBar.getProgressEnd());
                    } catch (NumberFormatException e) {
                        parsed = 0.00;
                    }

                    NumberFormat formatter = NumberFormat
                            .getCurrencyInstance(local);
                    formatter.setMaximumFractionDigits(0);
                    formatter.setParseIntegerOnly(true);
                    String formatted = formatter.format((parsed));

                    current = formatted;
                    et_minimum.setText(formatted);
                    et_minimum.setSelection(formatted.length());
                    et_minimum.addTextChangedListener(this);
                }
            }
        });

        electronic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!elec){
                    electronic.setBackground(getActivity().getResources().getDrawable(R.drawable.bg_filter_off));
                    electronic.setTextColor(Color.BLACK);
                    elec = true;
                    category.remove("Electronic");
                }else{
                    electronic.setBackground(getActivity().getResources().getDrawable(R.drawable.bg_filter_on));
                    electronic.setTextColor(Color.WHITE);
                    elec = false;
                    category.add("Electronic");
                }
            }
        });

        sport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!spo){
                    sport.setBackground(getActivity().getResources().getDrawable(R.drawable.bg_filter_off));
                    sport.setTextColor(Color.BLACK);
                    spo = true;
                    category.remove("Sport");
                }else{
                    sport.setBackground(getActivity().getResources().getDrawable(R.drawable.bg_filter_on));
                    sport.setTextColor(Color.WHITE);
                    spo = false;
                    category.add("Sport");
                }
            }
        });

        appliance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!app){
                    appliance.setBackground(getActivity().getResources().getDrawable(R.drawable.bg_filter_off));
                    appliance.setTextColor(Color.BLACK);
                    app = true;
                    category.remove("Appliance");
                }else{
                    appliance.setBackground(getActivity().getResources().getDrawable(R.drawable.bg_filter_on));
                    appliance.setTextColor(Color.WHITE);
                    app = false;
                    category.add("Appliance");
                }
            }
        });

        beverage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!bev){
                    beverage.setBackground(getActivity().getResources().getDrawable(R.drawable.bg_filter_off));
                    beverage.setTextColor(Color.BLACK);
                    bev = true;
                    category.remove("Beverage");
                }else{
                    beverage.setBackground(getActivity().getResources().getDrawable(R.drawable.bg_filter_on));
                    beverage.setTextColor(Color.WHITE);
                    bev = false;
                    category.add("Beverage");
                }
            }
        });

        snack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!sna){
                    snack.setBackground(getActivity().getResources().getDrawable(R.drawable.bg_filter_off));
                    snack.setTextColor(Color.BLACK);
                    sna = true;
                    category.remove("Snack");
                }else{
                    snack.setBackground(getActivity().getResources().getDrawable(R.drawable.bg_filter_on));
                    snack.setTextColor(Color.WHITE);
                    sna = false;
                    category.add("Snack");
                }
            }
        });

        fruit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!fru){
                    fruit.setBackground(getActivity().getResources().getDrawable(R.drawable.bg_filter_off));
                    fruit.setTextColor(Color.BLACK);
                    fru = true;
                    category.remove("Fruit");
                }else{
                    fruit.setBackground(getActivity().getResources().getDrawable(R.drawable.bg_filter_on));
                    fruit.setTextColor(Color.WHITE);
                    fru = false;
                    category.add("Fruit");
                }
            }
        });

        vegetable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!veg){
                    vegetable.setBackground(getActivity().getResources().getDrawable(R.drawable.bg_filter_off));
                    vegetable.setTextColor(Color.BLACK);
                    veg = true;
                    category.remove("Vegetable");
                }else{
                    vegetable.setBackground(getActivity().getResources().getDrawable(R.drawable.bg_filter_on));
                    vegetable.setTextColor(Color.WHITE);
                    veg = false;
                    category.add("Vegetable");
                }
            }
        });

        furniture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!furn){
                    furniture.setBackground(getActivity().getResources().getDrawable(R.drawable.bg_filter_off));
                    furniture.setTextColor(Color.BLACK);
                    furn = true;
                    category.remove("Furniture");
                }else{
                    furniture.setBackground(getActivity().getResources().getDrawable(R.drawable.bg_filter_on));
                    furniture.setTextColor(Color.WHITE);
                    furn = false;
                    category.add("Furniture");
                }
            }
        });

        new_.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!nw){
                    new_.setBackground(getActivity().getResources().getDrawable(R.drawable.bg_filter_off));
                    new_.setTextColor(Color.BLACK);
                    nw = true;
                    condition.remove("New");
                }else{
                    new_.setBackground(getActivity().getResources().getDrawable(R.drawable.bg_filter_on));
                    new_.setTextColor(Color.WHITE);
                    nw = false;
                    condition.add("New");
                }
            }
        });

        used.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!prl){
                    used.setBackground(getActivity().getResources().getDrawable(R.drawable.bg_filter_off));
                    used.setTextColor(Color.BLACK);
                    prl = true;
                    condition.remove("Preloved");
                }else{
                    used.setBackground(getActivity().getResources().getDrawable(R.drawable.bg_filter_on));
                    used.setTextColor(Color.WHITE);
                    prl = false;
                    condition.add("Preloved");
                }
            }
        });

    }

    private void initRecyclerViewItemKategori(){ // fungsi buat bikin object list artikel
        rv_search = getActivity().findViewById(R.id.rv_recent_search);
        if (getActivity() != null){
            adapter = new SearchAdapter(mList,getActivity().getApplicationContext(),getActivity(), new SearchAdapter.onItemClickListner(){
                @Override
                public void onClick(int position, String str) {
                    et_search.setText(str);
                }
            });
        }
        mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,true);
        rv_search.setLayoutManager(mLayoutManager);
        rv_search.setAdapter(adapter);
    }

    private void setFragment(Fragment fragment) // fungsi buat pindah - pindah fragment
    {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        HomeFragment homeFragment = new HomeFragment();
        fragmentTransaction.replace(R.id.frameFragment,homeFragment);
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
}
