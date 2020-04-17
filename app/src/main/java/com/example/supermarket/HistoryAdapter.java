package com.example.supermarket;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private List<OngoingModel> mList = new ArrayList<>();
    private Context mContext;
    private FragmentActivity mActivity;

    public HistoryAdapter(List<OngoingModel> mList, Context mContext, FragmentActivity mActivity) {
        this.mList = mList;
        this.mContext = mContext;
        this.mActivity = mActivity;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        mContext = viewGroup.getContext();

        View v = LayoutInflater.from(mContext).inflate(R.layout.item_ongoing,viewGroup,false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        final int position = i;
        final OngoingModel model = mList.get(i);

        viewHolder.tv_nama_ongoing.setText("Order No. "+(Integer.valueOf(model.getId())+1));
        viewHolder.tv_nama.setText(model.getName());
        viewHolder.tv_date.setText(model.getDate());
        viewHolder.tv_paymethod.setText(model.getPayMethod());
        viewHolder.tv_address.setText(model.getAddress());

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HistoryDetailFragment historyDetailFragment = new HistoryDetailFragment();
                historyDetailFragment.setOngoingModel(model);
                setFragment(historyDetailFragment);
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

        private TextView tv_nama_ongoing, tv_nama, tv_paymethod, tv_address,tv_date;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_nama_ongoing = itemView.findViewById(R.id.tv_item_name);
            tv_nama = itemView.findViewById(R.id.tv_item_name_bill);
            tv_paymethod = itemView.findViewById(R.id.tv_item_payMethod);
            tv_address = itemView.findViewById(R.id.tv_item_address);
            tv_date = itemView.findViewById(R.id.tv_item_date);
        }
    }
}
