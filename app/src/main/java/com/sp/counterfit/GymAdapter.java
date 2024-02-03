package com.sp.counterfit;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class GymAdapter extends RecyclerView.Adapter<GymAdapter.GymViewHolder> {
    private List<GymItem> gymList;
    private OnGymItemClickListener listener;

    public interface OnGymItemClickListener {
        void onGymItemClick(GymItem gymItem);
    }

    public static class GymViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageViewGym;
        public TextView textViewGymName;
        public TextView textViewGymAddress;

        public GymViewHolder(View v) {
            super(v);
            imageViewGym = v.findViewById(R.id.imageViewGym);
            textViewGymName = v.findViewById(R.id.textViewGymName);
            textViewGymAddress = v.findViewById(R.id.textViewGymAddress);
        }
    }

    public GymAdapter(List<GymItem> gymList, OnGymItemClickListener listener) {
        this.gymList = gymList;
        this.listener = listener;
    }

    @Override
    public GymViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_gym, parent, false);
        return new GymViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(GymViewHolder holder, int position) {
        final GymItem gym = gymList.get(position);
        holder.imageViewGym.setImageResource(gym.getImageResourceId());
        holder.textViewGymName.setText(gym.getName());
        holder.textViewGymAddress.setText(gym.getAddress());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onGymItemClick(gym);
            }
        });
    }

    @Override
    public int getItemCount() {
        return gymList.size();
    }
}
