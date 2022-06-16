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

public class DCIMUI extends Fragment implements OnSelectItem, Filterable {

    private RecyclerView recyclerView;
    private List<File> fileList;
    private List<File> allFileList;
    private ImageView img_background;
    private TextView dir_path;
    private FileAdapter fileAdapter;

    SwipeRefreshLayout swipeRefreshLayout;
    File storage;
    View view;
    String pathData;
    String[] longPressItems = {"Details","Open in another app","Rename","Delete","Send"};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.action_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.search_action);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                getFilter().filter(newText);
                return false;
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_internal, container, false);
        dir_path = view.findViewById(R.id.dir_path);
        img_background = view.findViewById(R.id.img_background);

        String internalStorage = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+"DCIM";
        File checkDir = new File(internalStorage);
        if (!checkDir.exists()) {
            internalStorage = Environment.getExternalStorageDirectory().getAbsolutePath();
            Toast.makeText(getContext(), "Directory doesn't exist", Toast.LENGTH_SHORT).show();
        }
        storage = new File(internalStorage);

        try {
            pathData = getArguments().getString("path");
            File file = new File(pathData);
            storage = file;
        } catch (Exception exception) {
            exception.printStackTrace();
        }



        dir_path.setText(storage.getAbsolutePath());
        dir_path.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipboardManager clipboardManager = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData file_dir_clip = ClipData.newPlainText("File Directory", dir_path.getText().toString());
                clipboardManager.setPrimaryClip(file_dir_clip);
                Toast.makeText(getContext(), "Copied to clipboard", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
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
                arrayList.add(singleFile);
            }
        }
        for (File singleFile : files){
            if (!singleFile.isDirectory()){
                arrayList.add(singleFile);
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
        fileList.addAll(findFiles(storage));
        allFileList = new ArrayList<>();
        allFileList.addAll(findFiles(storage));
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
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.mkdir:
                Dialog mkdirDialog = new Dialog(getContext());
                mkdirDialog.setContentView(R.layout.mkdir_dialog);
                EditText mkdirInput = mkdirDialog.findViewById(R.id.mkdirInput);
                Button mkdirButtonOk = mkdirDialog.findViewById(R.id.mkdirBtnOk);
                Button mkdirButtonCancel = mkdirDialog.findViewById(R.id.mkdirBtnCancel);

                mkdirInput.setText("New Folder");

                mkdirButtonOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            if (!mkdirInput.getText().toString().isEmpty()){
                                final File dir = new File(dir_path.getText().toString() + "/" + mkdirInput.getText());
                                if (!dir.exists()){
                                    dir.mkdirs();
                                    Toast.makeText(getContext(), "Created folder", Toast.LENGTH_SHORT).show();
                                    fileList.clear();
                                    fileList.addAll(findFiles(storage));
                                    fileAdapter.notifyDataSetChanged();
                                    recyclerView.invalidate();
                                } else {
                                    Toast.makeText(getContext(), "Already exist", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getContext(), "Folder name cannot be empty", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                        }
                        mkdirDialog.cancel();
                    }
                });

                mkdirButtonCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mkdirDialog.cancel();
                    }
                });

                mkdirDialog.show();

                return true;

            case R.id.sort_name:
                Collections.sort(fileList, new Comparator<File>() {
                    @Override
                    public int compare(File o1, File o2) {
                        return o1.getName().compareTo(o2.getName());
                    }
                });
                recyclerView.invalidate();
                fileAdapter.notifyDataSetChanged();
        }
        return false;
    }

    @Override
    public void onFileClick(File file) {
        if (file.isDirectory()){
            Bundle bundle = new Bundle();
            bundle.putString("path",file.getAbsolutePath());
            DCIMUI internalUI = new DCIMUI();
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
                                File currentFile = new File(dir_path.getText().toString());
                                File newFile = new File(file.getPath()+renameInput.getText().toString());
                                if(currentFile.exists()){
                                    File from = new File(currentFile,file.getName());
                                    File to = new File(currentFile,renameInput.getText().toString());
                                    if(from.exists())
                                        from.renameTo(to);
                                    Toast.makeText(getContext(), "Rename", Toast.LENGTH_SHORT).show();
                                }
                                fileList.clear();
                                fileList.addAll(findFiles(storage));
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
                                    fileList.addAll(findFiles(storage));
                                    fileAdapter.notifyItemRemoved(position);
                                    recyclerView.invalidate();
                                    fileAdapter.notifyDataSetChanged();
                                    deleteDialog.cancel();
                                    Toast.makeText(getContext(), "Deleted", Toast.LENGTH_SHORT).show();
                                }else{
                                    file.delete();
                                    fileList.clear();
                                    fileList.addAll(findFiles(storage));
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

    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<File> filteredFileList = new ArrayList<>();

            if (constraint.toString().isEmpty()){
                filteredFileList.addAll(allFileList);
            } else {
                    for (File filterFile: allFileList) {
                    if (filterFile.getName().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        filteredFileList.add(filterFile);
                    }
                }
            }
            FilterResults filterResults = new FilterResults();
            filterResults.values = filteredFileList;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            fileList.clear();
            fileList.addAll((Collection<? extends File>) results.values);
            fileAdapter.notifyDataSetChanged();
        }
    };


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

