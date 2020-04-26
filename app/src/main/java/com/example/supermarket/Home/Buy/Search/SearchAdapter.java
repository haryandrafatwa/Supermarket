package com.example.supermarket.Home.Buy.Search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.supermarket.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private List<SearchModel> mList = new ArrayList<>();
    private Context mContext;
    private FragmentActivity mActivity;

    private onItemClickListner onItemClickListner;

    private DatabaseReference searchRefs;

    public interface onItemClickListner{
        void onClick(int position, String str);//pass your object types.
    }

    public SearchAdapter(List<SearchModel> mList, Context mContext, FragmentActivity mActivity, onItemClickListner onItemClickListner) {
        this.mList = mList;
        this.mContext = mContext;
        this.mActivity = mActivity;
        this.onItemClickListner = onItemClickListner ;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        mContext = viewGroup.getContext();

        View v = LayoutInflater.from(mContext).inflate(R.layout.item_kategori,viewGroup,false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        final int position = i;
        final SearchModel model = mList.get(i);

        viewHolder.tv_nama_kategori.setText(model.getRecent());
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListner.onClick(position,model.getRecent());
            }
        });

        viewHolder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchRefs = FirebaseDatabase.getInstance().getReference().child("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Search").child(model.getId());
                searchRefs.removeValue();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView tv_nama_kategori;
        private ImageButton imageButton;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_nama_kategori = itemView.findViewById(R.id.tv_item_kategori);
            imageButton = itemView.findViewById(R.id.ib_delete_recent);
        }
    }

}
