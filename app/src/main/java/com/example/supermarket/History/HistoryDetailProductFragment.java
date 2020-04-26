package com.example.supermarket.History;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.supermarket.Cart.CartModel;
import com.example.supermarket.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class HistoryDetailProductFragment extends Fragment {

    private BottomNavigationView bottomNavigationView;
    private Toolbar toolbar;
    private EditText etRating;
    private RatingBar ratingBar;

    private TextView tv_nama_produk, tv_category, tv_amount;
    private ImageView imageView;
    private Button btnReport;

    private CartModel cartModel;
    private String idOrder;
    private float numOfRating, rating, nowRating;
    private Boolean status;

    private DatabaseReference productRefs, orderRefs;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history_detail_product, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        initialize();;
    }

    private void initialize(){

        bottomNavigationView = getActivity().findViewById(R.id.bottomNavBar);
        bottomNavigationView.setVisibility(View.GONE);
        toolbar = getActivity().findViewById(R.id.toolbar_ongoing);
        setToolbar();

        tv_nama_produk = getActivity().findViewById(R.id.tv_item_name);
        tv_category = getActivity().findViewById(R.id.tv_item_category);
        imageView = getActivity().findViewById(R.id.iv_product_cart);
        tv_amount = getActivity().findViewById(R.id.tv_amount_item);
        btnReport = getActivity().findViewById(R.id.btnReport);
        etRating = getActivity().findViewById(R.id.etRating);
        ratingBar = getActivity().findViewById(R.id.rb_rating_history);

        Picasso.get().load(cartModel.getImageURL()).fit().centerCrop().into(imageView);
        tv_nama_produk.setText(cartModel.getNama_produk());
        tv_category.setText(cartModel.getCategory());
        tv_amount.setText(cartModel.getAmount()+" Pcs");

        productRefs = FirebaseDatabase.getInstance().getReference().child("Product").child(cartModel.getId_produk());
        productRefs.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                numOfRating = (float) Integer.valueOf(dataSnapshot.child("numOfRating").getValue().toString());
                rating = Float.valueOf(dataSnapshot.child("rating").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        orderRefs = FirebaseDatabase.getInstance().getReference().child("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Order").child(idOrder).child(cartModel.getId_produk());
        orderRefs.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("rating")){
                    nowRating = Float.valueOf(dataSnapshot.child("rating").getValue().toString());
                    ratingBar.setRating(nowRating);
                    etRating.setText(nowRating+"");
                    status = false;
                }else{
                    status = true;
                }

                if (!dataSnapshot.hasChild("report")){
                    btnReport.setVisibility(View.VISIBLE);
                }else{
                    btnReport.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        etRating.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        event != null &&
                                event.getAction() == KeyEvent.ACTION_DOWN &&
                                event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    if (event == null || !event.isShiftPressed()) {
                        // the user is done typing.
                        if (status){
                            ratingBar.setRating(Float.valueOf(etRating.getText().toString()));
                            orderRefs.child("rating").setValue(Float.valueOf(etRating.getText().toString()));
                            productRefs.child("rating").setValue(rating+Float.valueOf(etRating.getText().toString()));
                            productRefs.child("numOfRating").setValue(numOfRating+1);
                        }else{
                            float selisih = rating-nowRating;
                            ratingBar.setRating(Float.valueOf(etRating.getText().toString()));
                            orderRefs.child("rating").setValue(Float.valueOf(etRating.getText().toString()));
                            productRefs.child("rating").setValue(selisih+Float.valueOf(etRating.getText().toString()));
                        }
                        return true; // consume.
                    }
                }
                return false; // pass on to other listeners.
            }
        });

        btnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertReport();
            }
        });
    }

    private void initializeDialogReport() {

        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(getActivity(), R.style.CustomAlertDialog);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_report_order, null);
        dialogView.setBackgroundColor(Color.TRANSPARENT);
        dialog.setView(dialogView);
        dialog.setCancelable(false);
        dialog.setTitle(null);

        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                productRefs.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int report = Integer.valueOf(dataSnapshot.child("report").getValue().toString());
                        productRefs.child("report").setValue(report+1);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                orderRefs.child("report").setValue(true);
            }
        });

        dialog.show();

    }

    public void alertReport(){ // fungsi untuk membuat alert dialog ketika ingin logout
        AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(getActivity());

        // Setting Dialog Title
        alertDialog2.setTitle("Confirmation");

        alertDialog2.setCancelable(false);

        // Setting Dialog Message
        alertDialog2.setMessage("Are you sure want to report that product?");

        // Setting Positive "Yes" Btn
        alertDialog2.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Write your code here to execute after dialog
                initializeDialogReport();
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

    public void setCartModel(CartModel cartModel) {
        this.cartModel = cartModel;
    }

    public void setIdOrder(String idOrder) {
        this.idOrder = idOrder;
    }
}
