package com.cyb3rg0d.filemanager;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class FileViewHolder extends RecyclerView.ViewHolder {

    public TextView dir_filename, dir_filesize, dir_date;
    public CardView container;
    public ImageView fileIcon;

    public FileViewHolder(@NonNull View itemView) {
        super(itemView);

        dir_filename = itemView.findViewById(R.id.dir_filename);
        dir_filesize = itemView.findViewById(R.id.dir_filesize);
        dir_date = itemView.findViewById(R.id.dir_date);
        container = itemView.findViewById(R.id.file_container);
        fileIcon = itemView.findViewById(R.id.img_fileIcon);


    }
}
