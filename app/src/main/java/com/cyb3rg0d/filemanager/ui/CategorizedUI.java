package com.cyb3rg0d.filemanager.ui;

import android.Manifest;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.cyb3rg0d.filemanager.FileAdapter;
import com.cyb3rg0d.filemanager.OnSelectItem;
import com.cyb3rg0d.filemanager.OpenFile;
import com.cyb3rg0d.filemanager.R;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class CategorizedUI extends Fragment implements OnSelectItem {

    private RecyclerView recyclerView;
    private List<File> fileList;
    private FileAdapter fileAdapter;

    SwipeRefreshLayout swipeRefreshLayout;
    File storage;
    View view;
    String pathData;
    String[] longPressItems = {"Details","Open in another app","Rename","Delete","Send"};
    File path;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_categorized, container, false);

        Bundle bundle = this.getArguments();
        if (bundle.getString("fileType").equals("downloads")){
            path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        } else {
            path = Environment.getExternalStorageDirectory();
        }

        runtimePermission();
        return view;
    }

    private void runtimePermission() {
        Dexter.withContext(getContext()).withPermissions(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                filesDisplay();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }

    public ArrayList<File> findFiles(File file){
        ArrayList<File> arrayList = new ArrayList<>();
        File[] files = file.listFiles();

        for (File singleFile : files){
            if (singleFile.isDirectory() && !singleFile.isHidden()){
                arrayList.addAll(findFiles(singleFile));
            } else {
                switch (getArguments().getString("fileType")){
                    case "image":
                        if (singleFile.getName().toLowerCase().endsWith(".jpeg")||
                                singleFile.getName().toLowerCase().endsWith(".jpg") ||
                                singleFile.getName().toLowerCase().endsWith(".png") ||
                                singleFile.getName().toLowerCase().endsWith(".gif")||
                                singleFile.getName().toLowerCase().endsWith(".bpm")||
                                singleFile.getName().toLowerCase().endsWith(".webp")){
                            arrayList.add(singleFile);
                        }
                        break;

                    case "music":
                        if (singleFile.getName().toLowerCase().endsWith(".mp3") ||
                                singleFile.getName().toLowerCase().endsWith(".mp4a") ||
                                singleFile.getName().toLowerCase().endsWith(".pcm") ||
                                singleFile.getName().toLowerCase().endsWith(".aiff") ||
                                singleFile.getName().toLowerCase().endsWith(".aac") ||
                                singleFile.getName().toLowerCase().endsWith(".ogg") ||
                                singleFile.getName().toLowerCase().endsWith(".wma") ||
                                singleFile.getName().toLowerCase().endsWith(".flac") ||
                                singleFile.getName().toLowerCase().endsWith(".alac") ||
                                singleFile.getName().toLowerCase().endsWith(".wma") ||
                                singleFile.getName().toLowerCase().endsWith(".m4a") ||
                                singleFile.getName().toLowerCase().endsWith(".wav")){
                            arrayList.add(singleFile);
                        }
                        break;

                    case "video":
                        if (singleFile.getName().toLowerCase().endsWith(".mp4")||
                                singleFile.getName().toLowerCase().endsWith(".mov")||
                                singleFile.getName().toLowerCase().endsWith(".avi")||
                                singleFile.getName().toLowerCase().endsWith(".flv")||
                                singleFile.getName().toLowerCase().endsWith(".mvk")||
                                singleFile.getName().toLowerCase().endsWith(".wmv")||
                                singleFile.getName().toLowerCase().endsWith(".avchd")||
                                singleFile.getName().toLowerCase().endsWith(".webm")||
                                singleFile.getName().toLowerCase().endsWith(".mpeg-4")){
                            arrayList.add(singleFile);
                        }
                        break;

                    case "docs":
                        if (singleFile.getName().toLowerCase().endsWith(".pdf")||
                                singleFile.getName().toLowerCase().endsWith(".doc") ||
                                singleFile.getName().toLowerCase().endsWith(".docx") ||
                                singleFile.getName().toLowerCase().endsWith(".odt")||
                                singleFile.getName().toLowerCase().endsWith(".xls")||
                                singleFile.getName().toLowerCase().endsWith(".xlsx")||
                                singleFile.getName().toLowerCase().endsWith(".ods")||
                                singleFile.getName().toLowerCase().endsWith(".ppt")||
                                singleFile.getName().toLowerCase().endsWith(".pptx")||
                                singleFile.getName().toLowerCase().endsWith(".csv")||
                                singleFile.getName().toLowerCase().endsWith(".txt")||
                                singleFile.getName().toLowerCase().endsWith(".rft")||
                                singleFile.getName().toLowerCase().endsWith(".odt")){
                            arrayList.add(singleFile);
                        }
                        break;

                    case "apk":
                        if (singleFile.getName().toLowerCase().endsWith(".apk")){
                            arrayList.add(singleFile);
                        }
                        break;

                    case "downloads":
                        if (!singleFile.isDirectory()){
                            arrayList.add(singleFile);
                        }
                        break;
                }
            }
        }
        return arrayList;
    }

    private void filesDisplay() {
        swipeRefreshLayout = view.findViewById(R.id.refresh);
        recyclerView = view.findViewById(R.id.recycler_internal);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        fileList = new ArrayList<>();
        fileList.addAll(findFiles(path));
        fileAdapter = new FileAdapter(getContext(),fileList,this);
        recyclerView.setAdapter(fileAdapter);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                // User defined method to shuffle the array list items
                refreshListItems();
            }
        });
    }


    private void refreshListItems() {
        fileAdapter.notifyDataSetChanged();
        recyclerView.refreshDrawableState();
        recyclerView.invalidate();
        getFragmentManager().beginTransaction().detach(this).attach(this).commit();
    }

    @Override
    public void onFileClick(File file) {
        if (file.isDirectory()){
            Bundle bundle = new Bundle();
            bundle.putString("path",file.getAbsolutePath());
            CategorizedUI internalUI = new CategorizedUI();
            internalUI.setArguments(bundle);
            getFragmentManager().beginTransaction().replace(R.id.fragment_container,internalUI).addToBackStack(null).commit();
        } else {
            try {
                OpenFile.openFile(getContext(),file);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    @Override
    public void onFileLongClick(File file, int i) {
        final Dialog optionDialog = new Dialog(getContext());
        optionDialog.setContentView(R.layout.option_dialog);
        optionDialog.setTitle("Options");
        ListView options = (ListView) optionDialog.findViewById(R.id.optionList);
        CustomAdapter customAdapter = new CustomAdapter();
        options.setAdapter(customAdapter);
        optionDialog.show();

        options.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                switch (selectedItem){
                    case "Details":
                        Dialog detailsDialog = new Dialog(getContext());
                        detailsDialog.setContentView(R.layout.details_dialog);
                        TextView detailsTxt = detailsDialog.findViewById(R.id.detailsTxt);
                        TextView detailsTitle = detailsDialog.findViewById(R.id.detailsTitle);
                        Button detailsBtn = detailsDialog.findViewById(R.id.detailsBtn);

                        detailsTitle.setText(file.getName());
                        detailsTitle.setSelected(true);

                        Date lastModDate = new Date(file.lastModified());
                        SimpleDateFormat formater = new SimpleDateFormat("dd/MM/yyy HH:mm:ss a");
                        String formattedDate = formater.format(lastModDate);

                        detailsTxt.setText("Location: "+ file.getAbsolutePath() + "\n" +
                                "Size: " + Formatter.formatShortFileSize(getContext(),file.length()) + "\n" +
                                "Date: " +formattedDate+ "\n" +
                                "Readable:" +file.canRead()+ "\n" +
                                "Writable:" +file.canWrite()+ "\n" +
                                "Hidden:" +file.isHidden());

                        detailsBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                detailsDialog.cancel();
                            }
                        });

                        detailsDialog.show();
                        optionDialog.cancel();
                        break;

                    case "Open in another app":
                        File openOtherApp = file;
                        if (file.isDirectory()){
                            Toast.makeText(getContext(), "This is a folder", Toast.LENGTH_SHORT).show();
                        } else {
                            Uri uri = FileProvider.getUriForFile(getContext(), getContext().getApplicationContext().getPackageName() + ".provider", file);
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setDataAndType(uri, "*/*");
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            getContext().startActivity(intent);
                            optionDialog.cancel();
                        }
                        break;

                    case "Rename":
                        Dialog renameDialog = new Dialog(getContext());
                        renameDialog.setContentView(R.layout.rename_dialog);
                        EditText renameInput = renameDialog.findViewById(R.id.renameInput);
                        Button renameButtonOk = renameDialog.findViewById(R.id.renameBtnOk);
                        Button renameButtonCancel = renameDialog.findViewById(R.id.renameBtnCancel);
                        renameInput.setText(file.getName());

                        renameButtonCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                renameDialog.cancel();
                            }
                        });

                        renameButtonOk.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                File currentFile = new File(file.getPath()+"/");
                                File newFile = new File(file.getPath()+renameInput.getText().toString());
                                if(currentFile.exists()){
                                    File from = new File(currentFile,file.getName());
                                    File to = new File(currentFile,renameInput.getText().toString());
                                    if(from.exists())
                                        from.renameTo(to);
                                    Toast.makeText(getContext(), "Rename", Toast.LENGTH_SHORT).show();
                                }
                                fileList.clear();
                                fileList.addAll(findFiles(path));
                                fileAdapter.notifyItemRemoved(position);
                                recyclerView.invalidate();
                                fileAdapter.notifyDataSetChanged();
                                renameDialog.cancel();
                            }
                        });

                        renameDialog.show();
                        optionDialog.cancel();
                        break;

                    case "Delete":
                        Dialog deleteDialog = new Dialog(getContext());
                        deleteDialog.setContentView(R.layout.delete_dialog);
                        TextView deleteTitle = deleteDialog.findViewById(R.id.deleteTitle);
                        Button deleteOk = deleteDialog.findViewById(R.id.deleteOk);
                        Button deleteCancel = deleteDialog.findViewById(R.id.deleteCancel);

                        deleteTitle.setText("Delete " +file.getName()+ " ?");

                        deleteCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                deleteDialog.cancel();
                            }
                        });

                        deleteOk.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (file.isDirectory()){
                                    deleteRecursive(file);
                                    fileList.clear();
                                    fileList.addAll(findFiles(path));
                                    fileAdapter.notifyItemRemoved(position);
                                    recyclerView.invalidate();
                                    fileAdapter.notifyDataSetChanged();
                                    deleteDialog.cancel();
                                    Toast.makeText(getContext(), "Deleted", Toast.LENGTH_SHORT).show();
                                }else{
                                    file.delete();
                                    fileList.clear();
                                    fileList.addAll(findFiles(path));
                                    fileAdapter.notifyItemRemoved(position);
                                    recyclerView.invalidate();
                                    fileAdapter.notifyDataSetChanged();
                                    deleteDialog.cancel();
                                    Toast.makeText(getContext(), "Deleted", Toast.LENGTH_SHORT).show();
                                }
                            }

                            private void deleteRecursive(File x) {
                                if (x.isDirectory())
                                    for (File child : x.listFiles())
                                        deleteRecursive(child);

                                x.delete();
                            }
                        });

                        deleteDialog.show();
                        optionDialog.cancel();
                    break;

                    case "Send":
                        File selectedFile = file;
                        Uri uri = FileProvider.getUriForFile(getContext(),getContext().getApplicationContext().getPackageName()+".provider",file);

                        Intent intent = new Intent(Intent.ACTION_SEND);
                        if (uri.toString().contains(".pdf")){
                            intent.setDataAndType(uri,"application/pdf");
                        }
                        else if (uri.toString().contains(".doc")||uri.toString().contains(".docs")) {
                            intent.setDataAndType(uri,"application/msword");
                        }
                        else if (uri.toString().contains(".odt")) {
                            intent.setDataAndType(uri,"application/vnd.oasis.opendocument.text");
                        }
                        else if (uri.toString().contains(".xls")||uri.toString().contains(".xlsx")||
                                uri.toString().contains(".ods")) {
                            intent.setDataAndType(uri,"application/vnd.ms-excel");
                        }
                        else if (uri.toString().contains(".ppt")||uri.toString().contains(".pptx")) {
                            intent.setDataAndType(uri,"application/vnd.ms-powerpoint");
                        }
                        else if (uri.toString().contains(".txt")) {
                            intent.setDataAndType(uri,"text/plain");
                        }
                        else if (uri.toString().contains(".odt")) {
                            intent.setDataAndType(uri,"application/vnd.oasis.opendocument.text");
                        }
                        else if (uri.toString().contains(".html")) {
                            intent.setDataAndType(uri,"text/html");
                        }
                        else if (uri.toString().contains(".rar")) {
                            intent.setDataAndType(uri,"application/x-rar-compressed");
                        }
                        else if (uri.toString().contains(".tar")) {
                            intent.setDataAndType(uri,"application/tar+gzip");
                        }
                        else if (uri.toString().contains(".zip")) {
                            intent.setDataAndType(uri,"application/zip");
                        }
                        else if (uri.toString().contains(".epub")||
                                uri.toString().contains(".cbz")||
                                uri.toString().contains(".cbr")||
                                uri.toString().contains(".f2b")||
                                uri.toString().contains(".mobi")) {
                            intent.setDataAndType(uri,"application/*+zip");
                        }
                        else if (uri.toString().contains(".wav")||uri.toString().contains(".mp3")||
                                uri.toString().contains(".mp4a")||uri.toString().contains(".pcm")||
                                uri.toString().contains(".aiff")||uri.toString().contains(".aac")||
                                uri.toString().contains(".ogg")||uri.toString().contains(".wma")
                                ||uri.toString().contains(".flac")||uri.toString().contains(".alac")
                                ||uri.toString().contains(".wma")||uri.toString().contains(".m4a")){
                            intent.setDataAndType(uri,"audio/*");
                        }
                        else if (uri.toString().contains(".mp4")||uri.toString().contains(".mov")||
                                uri.toString().contains(".mp4a")||uri.toString().contains(".avi")||
                                uri.toString().contains(".flv")||uri.toString().contains(".mvk")
                                ||uri.toString().contains(".wmv")||uri.toString().contains(".avchd")
                                ||uri.toString().contains("webm")) {
                            intent.setDataAndType(uri,"video/*");
                        }
                        else if (uri.toString().contains(".jpeg")||uri.toString().contains(".png")||
                                uri.toString().contains(".jpg")||
                                uri.toString().contains(".gif")||uri.toString().contains(".bpm")||
                                uri.toString().contains(".webp")) {
                            intent.setDataAndType(uri,"image/*");
                        }
                        else if (uri.toString().contains(".html")) {
                            intent.setDataAndType(uri,"text/html");
                        }
                        else if (uri.toString().contains(".json")) {
                            intent.setDataAndType(uri,"text/html");
                        }
                        else if (uri.toString().contains(".txt")||
                                uri.toString().contains(".rft")||
                                uri.toString().contains(".odt")) {
                            intent.setDataAndType(uri,"text/plain");
                        }
                        else {
                            intent.setDataAndType(uri,"*/*");
                        }
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        getContext().startActivity(intent);
                        optionDialog.cancel();
                        break;
                }
            }
        });
    }


    class CustomAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return longPressItems.length;
        }

        @Override
        public Object getItem(int position) {
            return longPressItems[position];
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View optionView = getLayoutInflater().inflate(R.layout.option_layout,null);
            TextView optionTxt = optionView.findViewById(R.id.optionTxt);
            ImageView optionImg = optionView.findViewById(R.id.optionImg);
            optionTxt.setText(longPressItems[position]);

            if (longPressItems[position].equals("Details")) {
                optionImg.setImageResource(R.drawable.ic_details);
            } else if (longPressItems[position].equals("Open in another app")) {
                optionImg.setImageResource(R.drawable.open_in_another_app);
            } else if (longPressItems[position].equals("Rename")) {
                optionImg.setImageResource(R.drawable.ic_rename);
            } else if (longPressItems[position].equals("Delete")) {
                optionImg.setImageResource(R.drawable.ic_delete );
            } else if (longPressItems[position].equals("Send")) {
                optionImg.setImageResource(R.drawable.ic_send);
            }
            return optionView;
        }
    }
}

