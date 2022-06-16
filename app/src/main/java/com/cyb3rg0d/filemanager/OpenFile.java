package com.cyb3rg0d.filemanager;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;

public class OpenFile {
    public static void openFile(Context context, File file) throws IOException {

        File selectedFile = file;
        Uri uri = FileProvider.getUriForFile(context,context.getApplicationContext().getPackageName()+".provider",file);

        Intent intent = new Intent(Intent.ACTION_VIEW);
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
        context.startActivity(intent);
    }
}
