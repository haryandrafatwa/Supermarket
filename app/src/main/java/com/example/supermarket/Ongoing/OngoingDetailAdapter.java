package com.example.supermarket.Ongoing;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.supermarket.Cart.CartModel;
import com.example.supermarket.R;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

//adapter berfungsi utk menampilkan item-item pada recycler view
public class OngoingDetailAdapter extends RecyclerView.Adapter<OngoingDetailAdapter.ViewHolder> {

    private List<CartModel> mList = new ArrayList<>();
    private Context mContext;
    private FragmentActivity mActivity;

    private DatabaseReference cartRefs, productRefs;

    //adapter konstruktor
    public OngoingDetailAdapter(List<CartModel> mList, Context mContext, FragmentActivity mActivity) {
        this.mList = mList;
        this.mContext = mContext;
        this.mActivity = mActivity;
    }

    //method utk menentukan layout mana yg akan dijadikan layout item pada recycler view
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        mContext = viewGroup.getContext();

        View v = LayoutInflater.from(mContext).inflate(R.layout.item_ongoing_detail,viewGroup,false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    //method utk mengassign objek2 sesuai dengan model item
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        final int position = i;
        final CartModel model = mList.get(i);

        viewHolder.tv_nama_produk.setText(model.getNama_produk());
        viewHolder.tv_category.setText(model.getCategory());
        viewHolder.tv_amount.setText(model.getAmount()+" Pcs");
        Picasso.get().load(model.getImageURL()).fit().centerCrop().into(viewHolder.imageView);

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    //class utk menampung objek2 yang ada pada halaman item list recycler view
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
