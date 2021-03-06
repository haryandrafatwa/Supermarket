package com.example.supermarket.Profile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.supermarket.History.HistoryDetailFragment;
import com.example.supermarket.Ongoing.OngoingDetailFragment;
import com.example.supermarket.Ongoing.OngoingModel;
import com.example.supermarket.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

//adapter berfungsi utk menampilkan item-item pada recycler view
public class NotifikasiAdapter extends RecyclerView.Adapter<NotifikasiAdapter.ViewHolder> {

    private List<OngoingModel> mList = new ArrayList<>();
    private Context mContext;
    private FragmentActivity mActivity;

    //adapter konstruktor
    public NotifikasiAdapter(List<OngoingModel> mList, Context mContext, FragmentActivity mActivity) {
        this.mList = mList;
        this.mContext = mContext;
        this.mActivity = mActivity;
    }

    //method utk menentukan layout mana yg akan dijadikan layout item pada recycler view
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        mContext = viewGroup.getContext();

        View v = LayoutInflater.from(mContext).inflate(R.layout.item_notifikasi,viewGroup,false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    //method utk mengassign objek2 sesuai dengan model item
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int i) {
        final int position = i;
        final OngoingModel model = mList.get(i);

        viewHolder.tv_nama_ongoing.setText("Order No. "+(Integer.valueOf(model.getId())+1));
        viewHolder.tv_date.setText(model.getDate());

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Notif").child(model.getId());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("status").getValue().toString().equalsIgnoreCase("done")){
                    viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            HistoryDetailFragment historyDetailFragment = new HistoryDetailFragment();
                            historyDetailFragment.setOngoingModel(model);
                            setFragment(historyDetailFragment);
                        }
                    });
                }else if (dataSnapshot.child("status").getValue().toString().equalsIgnoreCase("active")){
                    viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            OngoingDetailFragment ongoingDetailFragment = new OngoingDetailFragment();
                            ongoingDetailFragment.setOngoingModel(model);
                            setFragment(ongoingDetailFragment);
                        }
                    });
                }else{
                    viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(mActivity, "Your order has been canceled!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        viewHolder.ib_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Notif").child(model.getId());
                reference.child("delete").setValue(true);

            }
        });
    }

    //fungsi buat pindah - pindah fragment
    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = mActivity.getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameFragment,fragment).addToBackStack(null);
        fragmentTransaction.commit();
    }

    //class utk menampung objek2 yang ada pada halaman item list recycler view
    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView tv_nama_ongoing, tv_date;
        private ImageButton ib_delete;

        public ViewHolder(View itemView) {
            super(itemView);
            tv_nama_ongoing = itemView.findViewById(R.id.tv_item_name);
            tv_date = itemView.findViewById(R.id.tv_item_date);
            ib_delete = itemView.findViewById(R.id.ib_delete);
        }
    }
}
