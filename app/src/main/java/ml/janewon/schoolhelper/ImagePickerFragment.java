package ml.janewon.schoolhelper;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.io.IOException;

import static android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

public class ImagePickerFragment extends DialogFragment {

    public ImagePickerFragment() {
        // Required empty public constructor
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String[] methods = {"From Gallery", "From Camera"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose a Photo")
                .setItems(methods, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                Intent chooseImage;
                                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                    chooseImage = new Intent(Intent.ACTION_OPEN_DOCUMENT, EXTERNAL_CONTENT_URI);
                                } else {
                                    chooseImage = new Intent(Intent.ACTION_PICK, EXTERNAL_CONTENT_URI);
                                }
                                chooseImage.setType("image/*");
                                getActivity().startActivityForResult(chooseImage, AddTeacherActivity.PICK_IMAGE_FROM_GALLERY);
                                break;
                            case 1:
                                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                if(takePicture.resolveActivity(getActivity().getPackageManager()) != null) {
                                    try {
                                        File photoFile = ((AddTeacherActivity) getActivity()).createImageFile();
                                        if(photoFile != null) {
                                            Uri photoUri = FileProvider.getUriForFile(getActivity(),
                                                    "ml.janewon.schoolhelper.fileprovider",
                                                    photoFile);
                                            takePicture.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                                            getActivity().startActivityForResult(takePicture, AddTeacherActivity.PICK_IMAGE_FROM_CAMERA);
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                        }
                    }
                });
        return builder.create();
    }
}
