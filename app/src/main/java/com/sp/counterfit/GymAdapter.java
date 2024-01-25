package com.sp.counterfit;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class GymAdapter extends RecyclerView.Adapter<GymAdapter.GymViewHolder> {

    private List<GymItem> gymList;

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

    public GymAdapter(List<GymItem> gymList) {
        this.gymList = gymList;
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

        // Set the click listener for the CardView
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an AlertDialog.Builder
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setMessage("Do you want to view the location on Google Maps?");

                // Set up the buttons
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked Yes button
                        Uri gmmIntentUri = Uri.parse("geo:" + gym.getLatitude() + "," + gym.getLongitude());
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);

                        mapIntent.setPackage("com.google.android.apps.maps");

                        if (mapIntent.resolveActivity(view.getContext().getPackageManager()) != null) {
                            view.getContext().startActivity(mapIntent);
                        }
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        // Nothing to do here
                    }
                });

                // Create and show the AlertDialog
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

    }


    @Override
    public int getItemCount() {
        return gymList.size();
    }
}
