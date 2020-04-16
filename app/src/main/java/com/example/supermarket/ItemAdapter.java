package com.example.supermarket;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    private List<ItemModel> mList = new ArrayList<>();
    private Context mContext;
    private FragmentActivity mActivity;

    public ItemAdapter(List<ItemModel> mList, Context mContext, FragmentActivity mActivity) {
        this.mList = mList;
        this.mContext = mContext;
        this.mActivity = mActivity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        mContext = viewGroup.getContext();

        View v = LayoutInflater.from(mContext).inflate(R.layout.item_produk_sell,viewGroup,false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        final int position = i;
        final ItemModel model = mList.get(i);

        Picasso.get().load(model.getImageURL()).fit().centerCrop().into(viewHolder.imageView);
        viewHolder.tv_nama_produk.setText(model.getNama_produk());
        viewHolder.tv_stock.setText("In stock: "+model.getStok());
        viewHolder.tv_harga.setText("$"+model.getHarga());

        float rating = model.getRating();
        float num = Float.intBitsToFloat(model.getNumOfRating());
        final float mean = (rating/num);

        viewHolder.ratingBar.setRating(mean);
        viewHolder.tv_rating.setText(mean+"");
        if(mean > 0){
            viewHolder.ratingBar.setVisibility(View.VISIBLE);
            viewHolder.tv_rating.setVisibility(View.VISIBLE);
        }else{
            viewHolder.ratingBar.setVisibility(View.GONE);
            viewHolder.tv_rating.setVisibility(View.GONE);
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BuyDetailFragment buyDetailFragment = new BuyDetailFragment();
                buyDetailFragment.setDesc(model.getDeskripsi());
                buyDetailFragment.setImageURL(model.getImageURL());
                buyDetailFragment.setName(model.getNama_produk());
                buyDetailFragment.setNumOfRating(model.getNumOfRating());
                buyDetailFragment.setPrice(model.getHarga());
                buyDetailFragment.setRating(model.getRating());
                buyDetailFragment.setStock(model.getStok());
                buyDetailFragment.setUid(model.getUid());
                buyDetailFragment.setCategory(model.getKategori());
                buyDetailFragment.setCondition(model.getKondisi());
                buyDetailFragment.setId(model.getId());
                setFragment(buyDetailFragment);
            }
        });

    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = mActivity.getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameFragment,fragment).addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView tv_nama_produk,tv_stock, tv_harga, tv_rating;
        private RatingBar ratingBar;
        private ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_nama_produk = itemView.findViewById(R.id.tv_produk_sell_item_name);
            tv_harga = itemView.findViewById(R.id.tv_produk_sell_item_harga);
            tv_stock = itemView.findViewById(R.id.tv_produk_sell_item_stock);
            tv_rating = itemView.findViewById(R.id.tv_produk_sell_item_rating);
            imageView = itemView.findViewById(R.id.iv_produk_sell_item);
            ratingBar = itemView.findViewById(R.id.rb_rating_produk_sell);
        }
    }
}
