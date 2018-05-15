package com.uconnekt.resume_picker.utils;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.uconnekt.resume_picker.cursors.DocScannerTask;
import com.uconnekt.resume_picker.cursors.loadercallbacks.FileMapResultCallback;
import com.uconnekt.resume_picker.cursors.loadercallbacks.FileResultCallback;
import com.uconnekt.resume_picker.cursors.loadercallbacks.PhotoDirLoaderCallbacks;
import com.uconnekt.resume_picker.models.Document;
import com.uconnekt.resume_picker.models.FileType;
import com.uconnekt.resume_picker.models.PhotoDirectory;
import com.uconnekt.resume_picker.view.FilePickerConst;

import java.util.Comparator;
import java.util.List;


public class MediaStoreHelper {

  public static void getPhotoDirs(FragmentActivity activity, Bundle args, FileResultCallback<PhotoDirectory> resultCallback) {
    if(activity.getSupportLoaderManager().getLoader(FilePickerConst.MEDIA_TYPE_IMAGE)!=null)
      activity.getSupportLoaderManager().restartLoader(FilePickerConst.MEDIA_TYPE_IMAGE, args, new PhotoDirLoaderCallbacks(activity, resultCallback));
    else
      activity.getSupportLoaderManager().initLoader(FilePickerConst.MEDIA_TYPE_IMAGE, args, new PhotoDirLoaderCallbacks(activity, resultCallback));
  }

  public static void getVideoDirs(FragmentActivity activity, Bundle args, FileResultCallback<PhotoDirectory> resultCallback) {
    if(activity.getSupportLoaderManager().getLoader(FilePickerConst.MEDIA_TYPE_VIDEO)!=null)
      activity.getSupportLoaderManager().restartLoader(FilePickerConst.MEDIA_TYPE_VIDEO, args, new PhotoDirLoaderCallbacks(activity, resultCallback));
    else
      activity.getSupportLoaderManager().initLoader(FilePickerConst.MEDIA_TYPE_VIDEO, args, new PhotoDirLoaderCallbacks(activity, resultCallback));
  }

  public static void getDocs(FragmentActivity activity,
                             List<FileType> fileTypes,
                             Comparator<Document> comparator,
                             FileMapResultCallback fileResultCallback)
  {
    new DocScannerTask(activity, fileTypes, comparator, fileResultCallback).execute();
  }
}