package com.uconnekt.adapter.listing;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.uconnekt.R;
import com.uconnekt.model.HelpandSupport;

import java.util.ArrayList;

public class HandSAdapter extends RecyclerView.Adapter<HandSAdapter.VewHolder> {
    private ArrayList<HelpandSupport> helpandSupports ;

    public HandSAdapter(ArrayList<HelpandSupport> helpandSupports){
        this.helpandSupports = helpandSupports;
    }

    @NonNull
    @Override
    public HandSAdapter.VewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        @SuppressLint("InflateParams") View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.handp_list_layout,null);
        return new HandSAdapter.VewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HandSAdapter.VewHolder holder, int position) {
        HelpandSupport helpandSupport = helpandSupports.get(position);
        holder.question.setText(helpandSupport.question);
        holder.answer.setText(helpandSupport.answer);
        if (helpandSupport.isSelected){
            holder.answer.setVisibility(View.VISIBLE);
            holder.image.setImageResource(R.drawable.ic_minus);
        }else {
            holder.answer.setVisibility(View.GONE);
            holder.image.setImageResource(R.drawable.ic_addition);
        }
    }

    @Override
    public int getItemCount() {
        return helpandSupports.size();
    }

    class VewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView question,answer;
        private ImageView image;
        VewHolder(View itemView) {
            super(itemView);
            question = itemView.findViewById(R.id.question);
            answer = itemView.findViewById(R.id.answer);
            image = itemView.findViewById(R.id.image);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (helpandSupports.get(getAdapterPosition()).isSelected) {
                helpandSupports.get(getAdapterPosition()).isSelected = false;
                image.setImageResource(R.drawable.ic_addition);
                notifyItemChanged(getAdapterPosition());
                //gone here
            }else{
                helpandSupports.get(getAdapterPosition()).isSelected = true;
                image.setImageResource(R.drawable.ic_minus);
                notifyItemChanged(getAdapterPosition());
                //visible here
            }
        }
    }
}
