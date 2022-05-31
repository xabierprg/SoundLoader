package com.example.soundloader.Adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.soundloader.R;
import com.example.soundloader.Models.Song;
import java.util.ArrayList;

public class AdapterData
        extends RecyclerView.Adapter<AdapterData.ViewHolderData>
        implements View.OnClickListener, View.OnLongClickListener {

    ArrayList<Song> listSongs;

    private View.OnClickListener onClickListener;
    private View.OnLongClickListener onLongClickListener;

    public AdapterData(ArrayList<Song> listSongs) {
        this.listSongs = listSongs;
    }

    @NonNull
    @Override
    public ViewHolderData onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        @SuppressLint("InflateParams") View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list,null,false);

        view.setOnClickListener(this);
        view.setOnLongClickListener(this);

        return new ViewHolderData(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderData holder, int position) {
        holder.data.setText(listSongs.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return listSongs.size();
    }


    @Override
    public void onClick(View view) {
        if(onClickListener != null) {
            onClickListener.onClick(view);
        }

    }

    @Override
    public boolean onLongClick(View view) {
        if(onLongClickListener != null) {
            onLongClickListener.onLongClick(view);
        }
        return false;
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public void setOnLongClickListener(View.OnLongClickListener onLongClickListener) {
        this.onLongClickListener = onLongClickListener;
    }

    public static class ViewHolderData extends RecyclerView.ViewHolder {

        TextView data;
        ImageView image;

        public ViewHolderData(@NonNull View itemView) {
            super(itemView);
            data = itemView.findViewById(R.id.idTexto);
            image = itemView.findViewById(R.id.idImagen);
        }

    }

}
