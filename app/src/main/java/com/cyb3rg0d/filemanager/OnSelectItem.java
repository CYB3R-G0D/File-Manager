package com.cyb3rg0d.filemanager;

import java.io.File;

public interface OnSelectItem {
    void onFileClick(File file);
    void onFileLongClick(File file, int position);
}