package com.example.supermarket;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nex3z.notificationbadge.NotificationBadge;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class BuyDetailFragment extends Fragment {

    private BottomNavigationView bottomNavigationView;
    private Toolbar toolbar;

    private TextView et_search, tv_product_name, tv_description, tv_rating, tv_stock, tv_price,tv_seller_name, tv_seller_phone, tv_condition, tv_category,tv_chat;
    private ImageButton ib_search, ib_notif,ib_chat;
    private ImageView imageView;
    private RatingBar ratingBar;
    private Button button;
    private CircleImageView circleImageView;
    private LinearLayout layoutChat;
    private RelativeLayout relativeLayout;
    private ProgressBar progressBar;

    private String id,name, desc, uid, imageURL, condition, category, sellerPhone;
    private int price, numOfRating, stock;
    private float rating;

    private DatabaseReference sellerRefs,notifRefs;
    private int counter;
    private NotificationBadge mBadge;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_buy_detail, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        initialize();
    }

    private void initialize(){

        bottomNavigationView = getActivity().findViewById(R.id.bottomNavBar);
        bottomNavigationView.setVisibility(View.GONE);
        toolbar = getActivity().findViewById(R.id.toolbar_buy);
        setToolbar();

        relativeLayout = getActivity().findViewById(R.id.buy_detail);
        progressBar = getActivity().findViewById(R.id.pb_buy_detail);
        et_search = getActivity().findViewById(R.id.et_search_buy);
        tv_product_name = getActivity().findViewById(R.id.tv_name_product);
        tv_description = getActivity().findViewById(R.id.tv_description_product);
        tv_rating = getActivity().findViewById(R.id.tv_produk_sell_item_rating);
        tv_stock = getActivity().findViewById(R.id.tv_stock_product);
        tv_seller_name = getActivity().findViewById(R.id.tv_seller_name_product);
        tv_seller_phone = getActivity().findViewById(R.id.tv_seller_phone_product);
        tv_price = getActivity().findViewById(R.id.tv_price_product);
        ib_search = getActivity().findViewById(R.id.ib_search_buy);
        ib_notif = getActivity().findViewById(R.id.ib_notification_buy);
        imageView = getActivity().findViewById(R.id.iv_image_product);
        ratingBar = getActivity().findViewById(R.id.rb_rating_produk_sell);
        button = getActivity().findViewById(R.id.btn_add_cart);
        circleImageView = getActivity().findViewById(R.id.iv_seller_product);
        layoutChat = getActivity().findViewById(R.id.chat);
        tv_category = getActivity().findViewById(R.id.tv_category_product);
        tv_condition = getActivity().findViewById(R.id.tv_condition_product);
        ib_chat = getActivity().findViewById(R.id.ib_chat);
        tv_chat = getActivity().findViewById(R.id.tvchat);
        mBadge = getActivity().findViewById(R.id.notif_badge);

        sellerRefs = FirebaseDatabase.getInstance().getReference().child("User").child(uid);
        sellerRefs.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Picasso.get().load(imageURL).fit().centerCrop().into(imageView);
                tv_product_name.setText(name);
                tv_description.setText(desc);
                tv_stock.setText(stock+"");
                tv_condition.setText(condition);
                tv_category.setText(category);
                tv_price.setText("$"+price);

                final float mean = (rating/numOfRating);
                ratingBar.setRating(mean);
                tv_rating.setText(mean+"");

                if(mean > 0){
                    ratingBar.setVisibility(View.VISIBLE);
                    tv_rating.setVisibility(View.VISIBLE);
                }else{
                    ratingBar.setVisibility(View.GONE);
                    tv_rating.setVisibility(View.GONE);
                }
                sellerPhone = dataSnapshot.child("phonenumber").getValue().toString();
                tv_seller_name.setText(dataSnapshot.child("name").getValue().toString());
                tv_seller_phone.setText(sellerPhone);
                Picasso.get().load(dataSnapshot.child("displayPicture").getValue().toString()).fit().centerCrop().into(circleImageView);

                relativeLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        ib_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchBarFragment searchBarFragment =new SearchBarFragment();
                setFragment(searchBarFragment);
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
                    Toast.makeText(getActivity(), counter+"", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        et_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchBarFragment searchBarFragment =new SearchBarFragment();
                setFragment(searchBarFragment);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (stock > 0){
                    final HashMap hashMap = new HashMap();
                    hashMap.put("id",id);
                    hashMap.put("nama_produk",name);
                    hashMap.put("price",price);
                    hashMap.put("totalPrice",price);
                    hashMap.put("amount",1);
                    hashMap.put("category",category);
                    hashMap.put("imageURL",imageURL);
                    final DatabaseReference cartRefs = FirebaseDatabase.getInstance().getReference().child("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Cart");
                    cartRefs.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (!dataSnapshot.hasChild(id)){

                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Product").child(id);
                                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        int stok = Integer.valueOf(dataSnapshot.child("stok").getValue().toString());
                                        stock = stok-1;
                                        tv_stock.setText(stock+"");
                                        dataSnapshot.child("stok").getRef().setValue(stok-1);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                                cartRefs.child(id).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener() {
                                    @Override
                                    public void onComplete(@NonNull Task task) {
                                        initializeDialogSuccess();
                                    }
                                });
                            }else{
                                dataSnapshot.child(id).getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Product").child(id);
                                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                int stok = Integer.valueOf(dataSnapshot.child("stok").getValue().toString());
                                                stock = stok-1;
                                                tv_stock.setText(stock+"");
                                                dataSnapshot.child("stok").getRef().setValue(stok-1);
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });

                                        int amount, price, newAmount, newPrice;
                                        price = Integer.valueOf(dataSnapshot.child("price").getValue().toString());
                                        amount = Integer.valueOf(dataSnapshot.child("amount").getValue().toString());
                                        newAmount = amount+1;
                                        newPrice = price*newAmount;
                                        Toast.makeText(getActivity(), "New Amount: "+newAmount+"; Total Price: "+newPrice, Toast.LENGTH_SHORT).show();
                                        cartRefs.child(id).child("amount").setValue(newAmount);
                                        cartRefs.child(id).child("totalPrice").setValue(newPrice);

                                        initializeDialogSuccess();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }else{
                    initializeDialogFailed();
                }
            }
        });

        layoutChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.putExtra("sms_body", "Hai, apakah barang tersedia?");
                    intent.setData(Uri.parse("sms:"+sellerPhone));
                    getActivity().startActivity(intent);
                } catch (android.content.ActivityNotFoundException anfe) {
                    Log.d("Error" , "Error");
                }
            }
        });

        ib_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.putExtra("sms_body", "Hai, apakah barang tersedia?");
                    intent.setData(Uri.parse("sms:"+sellerPhone));
                    getActivity().startActivity(intent);
                } catch (android.content.ActivityNotFoundException anfe) {
                    Log.d("Error" , "Error");
                }
            }
        });

        tv_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.putExtra("sms_body", "Hai, apakah barang tersedia?");
                    intent.setData(Uri.parse("sms:"+sellerPhone));
                    getActivity().startActivity(intent);
                } catch (android.content.ActivityNotFoundException anfe) {
                    Log.d("Error" , "Error");
                }
            }
        });
    }

    private void initializeDialogSuccess(){
        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(getActivity(),R.style.CustomAlertDialog);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_cart,null);
        dialogView.setBackgroundColor(Color.TRANSPARENT);
        dialog.setView(dialogView);
        dialog.setCancelable(false);
        dialog.setTitle(null);

        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                dialog.cancel();
                CartFragment cartFragment = new CartFragment();
                setFragment(cartFragment);
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

    private void initializeDialogFailed(){
        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(getActivity(),R.style.CustomAlertDialog);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_cart_failed,null);
        dialogView.setBackgroundColor(Color.TRANSPARENT);
        dialog.setView(dialogView);
        dialog.setCancelable(false);
        dialog.setTitle(null);

        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        dialog.show();
    }

    private void setFragment(Fragment fragment) // fungsi buat pindah - pindah fragment
    {
        FragmentManager fm = getActivity().getSupportFragmentManager();
        for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        CartFragment cartFragment = new CartFragment();
        fragmentTransaction.replace(R.id.frameFragment,cartFragment);
        fragmentTransaction.replace(R.id.frameFragment,fragment);
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

    public void setId(String id) {
        this.id = id;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public void setNumOfRating(int numOfRating) {
        this.numOfRating = numOfRating;
    }
}
