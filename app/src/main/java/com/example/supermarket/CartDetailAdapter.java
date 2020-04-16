package com.example.supermarket;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CartDetailAdapter extends RecyclerView.Adapter<CartDetailAdapter.ViewHolder> {

    private List<CartModel> mList = new ArrayList<>();
    private Context mContext;
    private FragmentActivity mActivity;

    private android.app.AlertDialog.Builder dialog;

    private DatabaseReference cartRefs, productRefs;

    public CartDetailAdapter(List<CartModel> mList, Context mContext, FragmentActivity mActivity) {
        this.mList = mList;
        this.mContext = mContext;
        this.mActivity = mActivity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        mContext = viewGroup.getContext();

        View v = LayoutInflater.from(mContext).inflate(R.layout.item_cart_detail,viewGroup,false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        final int position = i;
        final CartModel model = mList.get(i);

        viewHolder.tv_nama_produk.setText(model.getNama_produk());
        viewHolder.tv_category.setText(model.getCategory());
        viewHolder.tv_amount.setText(model.getAmount()+" Pcs");
        Picasso.get().load(model.getImageURL()).fit().centerCrop().into(viewHolder.imageView);

        if (model.getAmount() == 0){
            cartRefs = FirebaseDatabase.getInstance().getReference().child("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Cart").child(model.getId_cart());
            cartRefs.removeValue();
            setFragment(new CartFragment());
        }

        viewHolder.ib_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (model.getAmount() > 1){
                    model.setAmount(model.getAmount()-1);
                    model.setTotalPrice(model.getTotalPrice()-model.getPrice());
                    viewHolder.tv_amount.setText(model.getAmount()+" Pcs");
                    cartRefs = FirebaseDatabase.getInstance().getReference().child("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Cart").child(model.getId_cart());
                    cartRefs.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            dataSnapshot.child("amount").getRef().setValue(model.getAmount());
                            dataSnapshot.child("totalPrice").getRef().setValue(model.getTotalPrice());
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    productRefs = FirebaseDatabase.getInstance().getReference().child("Product").child(model.getId_produk());
                    productRefs.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            int stok = Integer.valueOf(dataSnapshot.child("stok").getValue().toString());
                            dataSnapshot.child("stok").getRef().setValue(stok+1);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }else if (model.getAmount() < 2){
                    initializeDialogDelete();
                    dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialog, int which) {
                            model.setAmount(model.getAmount()-1);
                            model.setTotalPrice(model.getTotalPrice()-model.getPrice());
                            viewHolder.tv_amount.setText(model.getAmount()+" Pcs");
                            cartRefs = FirebaseDatabase.getInstance().getReference().child("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Cart").child(model.getId_cart());
                            cartRefs.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    dataSnapshot.child("amount").getRef().setValue(model.getAmount());
                                    dataSnapshot.child("totalPrice").getRef().setValue(model.getTotalPrice());
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                            productRefs = FirebaseDatabase.getInstance().getReference().child("Product").child(model.getId_produk());
                            productRefs.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    int stok = Integer.valueOf(dataSnapshot.child("stok").getValue().toString());
                                    dataSnapshot.child("stok").getRef().setValue(stok+1);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
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
            }
        });
    }

    private void initializeDialogDelete() {
        dialog = new android.app.AlertDialog.Builder(mActivity, R.style.CustomAlertDialog);
        LayoutInflater inflater = mActivity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_cart_delete, null);
        dialogView.setBackgroundColor(Color.TRANSPARENT);
        dialog.setView(dialogView);
        dialog.setCancelable(false);
        dialog.setTitle(null);
    }

    private void setFragment(Fragment fragment) // fungsi buat pindah - pindah fragment
    {
        FragmentManager fm = mActivity.getSupportFragmentManager();
        for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
        FragmentTransaction fragmentTransaction = mActivity.getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameFragment,fragment);
        fragmentTransaction.commit();
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView tv_nama_produk, tv_category, tv_amount;
        private ImageView imageView;
        private ImageButton ib_delete;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_nama_produk = itemView.findViewById(R.id.tv_item_name);
            tv_category = itemView.findViewById(R.id.tv_item_category);
            imageView = itemView.findViewById(R.id.iv_product_cart);
            tv_amount = itemView.findViewById(R.id.tv_amount_item);
            ib_delete = itemView.findViewById(R.id.ib_remove);
        }
    }
}
