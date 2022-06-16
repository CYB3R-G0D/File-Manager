package com.cyb3rg0d.filemanager;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.provider.MediaStore;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOError;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class FileAdapter extends RecyclerView.Adapter<FileViewHolder> {

    private Context context;
    private List<File> file;
    Bitmap bitmap = null;
    int pageNum = 0;
    private OnSelectItem selectItem;

    public FileAdapter(Context context, List<File> file, OnSelectItem selectItem) {
        this.context = context;
        this.file = file;
        this.selectItem = selectItem;
    }

    public FileAdapter(Context context, List<File> fileList, SwipeRefreshLayout.OnRefreshListener onRefreshListener) {
    }

    @Override
    public FileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FileViewHolder(LayoutInflater.from(context).inflate(R.layout.file_container,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull FileViewHolder holder, int position) {
        holder.dir_filename.setText(file.get(position).getName());
        holder.dir_filename.setSelected(true);

        Date lastModified = new Date(file.get(position).lastModified());
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm a");
        String datemodified = formatter.format(lastModified);
        holder.dir_date.setText(datemodified);

        int items = 0;
        if (file.get(position).isDirectory())
        {
            File[] files = file.get(position).listFiles();
            for (File singleFile : files)
            {
                if (!singleFile.isHidden())
                {
                    items += 1;
                }
            }
            holder.dir_filesize.setText(String.valueOf(items) + " Files");
        }
        else {
            holder.dir_filesize.setText(Formatter.formatShortFileSize(context,file.get(position).length()));
        }

        if (file.get(position).isDirectory()){
            holder.fileIcon.setImageResource(R.drawable.ic_folder);
        }
        else if (file.get(position).getName().toLowerCase().endsWith(".jpeg")||
                file.get(position).getName().toLowerCase().endsWith(".jpg") ||
                file.get(position).getName().toLowerCase().endsWith(".png") ||
                file.get(position).getName().toLowerCase().endsWith(".gif")||
                file.get(position).getName().toLowerCase().endsWith(".bpm")||
                file.get(position).getName().toLowerCase().endsWith(".webp")){
            try {
                bitmap = BitmapFactory.decodeFile(file.get(position).getPath());
                holder.fileIcon.setImageBitmap(bitmap);
            } catch (IOError ioError) {
                holder.fileIcon.setImageResource(R.drawable.ic_image);
            }
        }
        else if (file.get(position).getName().toLowerCase().endsWith(".pdf")){
            holder.fileIcon.setImageResource(R.drawable.ic_pdf);
        }
        else if (file.get(position).getName().toLowerCase().endsWith(".mp3") ||
                file.get(position).getName().toLowerCase().endsWith(".mp4a") ||
                file.get(position).getName().toLowerCase().endsWith(".pcm") ||
                file.get(position).getName().toLowerCase().endsWith(".aiff") ||
                file.get(position).getName().toLowerCase().endsWith(".aac") ||
                file.get(position).getName().toLowerCase().endsWith(".ogg") ||
                file.get(position).getName().toLowerCase().endsWith(".wma") ||
                file.get(position).getName().toLowerCase().endsWith(".flac") ||
                file.get(position).getName().toLowerCase().endsWith(".alac") ||
                file.get(position).getName().toLowerCase().endsWith(".wma") ||
                file.get(position).getName().toLowerCase().endsWith(".m4a") ||
        file.get(position).getName().toLowerCase().endsWith(".wav")){
            try {
                MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                mmr.setDataSource(file.get(position).getPath());
                InputStream inputStream = null;
                if (mmr.getEmbeddedPicture() != null) {
                    inputStream = new ByteArrayInputStream(mmr.getEmbeddedPicture());
                    holder.fileIcon.setImageBitmap(BitmapFactory.decodeStream(inputStream));
                } else {
                    holder.fileIcon.setImageResource(R.drawable.ic_audio);
                }
                mmr.release();
            } catch (IOError ioError){
                holder.fileIcon.setImageResource(R.drawable.ic_audio);
            }
        }
        else if (file.get(position).getName().toLowerCase().endsWith(".mp4")||
                file.get(position).getName().toLowerCase().endsWith(".mov")||
                file.get(position).getName().toLowerCase().endsWith(".avi")||
                file.get(position).getName().toLowerCase().endsWith(".flv")||
                file.get(position).getName().toLowerCase().endsWith(".mvk")||
                file.get(position).getName().toLowerCase().endsWith(".wmv")||
                file.get(position).getName().toLowerCase().endsWith(".avchd")||
                file.get(position).getName().toLowerCase().endsWith(".webm")||
                file.get(position).getName().toLowerCase().endsWith(".mpeg-4")){
            try {
                Bitmap thumVideo = ThumbnailUtils.createVideoThumbnail(file.get(position).getPath(), MediaStore.Video.Thumbnails.MICRO_KIND);
                holder.fileIcon.setImageBitmap(thumVideo);
            } catch (IOError ioError){
                holder.fileIcon.setImageResource(R.drawable.ic_video);
            }
        }
        else if (file.get(position).getName().toLowerCase().endsWith(".apk")){
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageArchiveInfo(file.get(position).getPath(), PackageManager.GET_ACTIVITIES);
            if(packageInfo != null) {
                ApplicationInfo appInfo = packageInfo.applicationInfo;
                if (Build.VERSION.SDK_INT >= 8) {
                    appInfo.sourceDir = file.get(position).getPath();
                    appInfo.publicSourceDir = file.get(position).getPath();
                }
                Drawable icon = appInfo.loadIcon(context.getPackageManager());
                Bitmap iconApk = ((BitmapDrawable) icon).getBitmap();
                holder.fileIcon.setImageBitmap(iconApk);
            } else {
                holder.fileIcon.setImageResource(R.drawable.ic_apk);
            }
        }
        else if (file.get(position).getName().toLowerCase().endsWith(".zip")){
            holder.fileIcon.setImageResource(R.drawable.ic_zip);
        }
        else if (file.get(position).getName().toLowerCase().endsWith(".rar")){
            holder.fileIcon.setImageResource(R.drawable.ic_rar);
        }
        else if (file.get(position).getName().toLowerCase().endsWith(".tar")){
            holder.fileIcon.setImageResource(R.drawable.ic_tar);
        }
        else if (file.get(position).getName().toLowerCase().endsWith(".doc")||
                file.get(position).getName().toLowerCase().endsWith(".docx")||
                file.get(position).getName().toLowerCase().endsWith(".odt")){
            holder.fileIcon.setImageResource(R.drawable.ic_doc);
        }
        else if (file.get(position).getName().toLowerCase().endsWith(".xls")||
                file.get(position).getName().toLowerCase().endsWith(".xlsx")||
                file.get(position).getName().toLowerCase().endsWith(".ods")){
            holder.fileIcon.setImageResource(R.drawable.ic_html);
        }
        else if (file.get(position).getName().toLowerCase().endsWith(".ppt")||
                file.get(position).getName().toLowerCase().endsWith(".pptx")){
            holder.fileIcon.setImageResource(R.drawable.ic_ppt);
        }
        else if (file.get(position).getName().toLowerCase().endsWith(".csv")){
            holder.fileIcon.setImageResource(R.drawable.ic_csv);
        }
        else if (file.get(position).getName().toLowerCase().endsWith(".txt")||
                file.get(position).getName().toLowerCase().endsWith(".rft")||
                file.get(position).getName().toLowerCase().endsWith(".odt")){
            holder.fileIcon.setImageResource(R.drawable.ic_txt);
        }
        else if (file.get(position).getName().toLowerCase().endsWith(".html")){
            holder.fileIcon.setImageResource(R.drawable.ic_html);
        }
        else if (file.get(position).getName().toLowerCase().endsWith(".json")){
            holder.fileIcon.setImageResource(R.drawable.ic_json);
        }
        else if (file.get(position).getName().toLowerCase().endsWith(".torrent")){
            holder.fileIcon.setImageResource(R.drawable.ic_torrent);
        }
        else if (file.get(position).getName().toLowerCase().endsWith(".cbr")){
            holder.fileIcon.setImageResource(R.drawable.ic_cbr);
        }
        else if (file.get(position).getName().toLowerCase().endsWith(".cbz")){
            holder.fileIcon.setImageResource(R.drawable.ic_cbz);
        }
        else if (file.get(position).getName().toLowerCase().endsWith(".epub")){
            holder.fileIcon.setImageResource(R.drawable.ic_epub);
        }
        else if (file.get(position).getName().toLowerCase().endsWith(".fb2")){
            holder.fileIcon.setImageResource(R.drawable.ic_fb2);
        }
        else if (file.get(position).getName().toLowerCase().endsWith(".mobi")){
            holder.fileIcon.setImageResource(R.drawable.ic_mobi);
        }
        else if (file.get(position).getName().toLowerCase().endsWith(".oeb")||
                file.get(position).getName().toLowerCase().endsWith(".etb")||
                file.get(position).getName().toLowerCase().endsWith(".lrs")||
                file.get(position).getName().toLowerCase().endsWith(".tpz")){
            holder.fileIcon.setImageResource(R.drawable.ic_ebook);
        }
        else if (file.get(position).getName().toLowerCase().endsWith(".info")){
            holder.fileIcon.setImageResource(R.drawable.ic_doc_info);
        }
        else if (file.get(position).getName().toLowerCase().endsWith(".java")){
            holder.fileIcon.setImageResource(R.drawable.ic_java);
        }
        else {
            holder.fileIcon.setImageResource(R.drawable.ic_unknown);
        }

        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectItem.onFileClick(file.get(position));
            }
        });

        holder.container.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                selectItem.onFileLongClick(file.get(position),position);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return file.size();
    }
}
