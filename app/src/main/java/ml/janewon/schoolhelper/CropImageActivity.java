package ml.janewon.schoolhelper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.steelkiwi.cropiwa.CropIwaView;
import com.steelkiwi.cropiwa.config.CropIwaSaveConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import id.zelory.compressor.Compressor;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class CropImageActivity extends AppCompatActivity {

    private CropIwaView mCropIwaView;
    private Uri mImageUri;
    private Uri mCompressedImageUri;
    private ProgressDialog mCroppingProgressDialog;
    private ProgressDialog mCompressingProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop_image);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();
        mImageUri = intent.getData();

        mCropIwaView = (CropIwaView) findViewById(R.id.activity_crop_image_crop_view);
        mCropIwaView.setImageUri(mImageUri);

        mCropIwaView.setCropSaveCompleteListener(new CropIwaView.CropSaveCompleteListener() {
            @Override
            public void onCroppedRegionSaved(Uri bitmapUri) {
                if(mCroppingProgressDialog != null) {
                    mCroppingProgressDialog.dismiss();
                }

                mCompressingProgressDialog = ProgressDialog.show(CropImageActivity.this, null, "Compressing image. Please wait.", true);
                final File croppedImage = new File(bitmapUri.getPath());
                new Compressor(CropImageActivity.this)
                        .compressToFileAsFlowable(croppedImage)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<File>() {
                            @Override
                            public void accept(File file) throws Exception {
                                if(file != null) {
                                    copyImage(file, croppedImage);
                                    if(mCompressingProgressDialog != null) {
                                        mCompressingProgressDialog.dismiss();
                                    }

                                    mCompressedImageUri = Uri.fromFile(croppedImage);

                                    file.delete();
                                    file.getParentFile().delete();

                                    Intent resultIntent = new Intent();
                                    resultIntent.setData(mCompressedImageUri);
                                    setResult(Activity.RESULT_OK, resultIntent);
                                    finish();
                                } else {
                                    Toast.makeText(CropImageActivity.this, "Error compress your image", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }, new Consumer<Throwable>() {

                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                throwable.printStackTrace();
                            }
                        });
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.button_save_data, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                mCropIwaView.crop(new CropIwaSaveConfig.Builder(mImageUri)
                        .setCompressFormat(Bitmap.CompressFormat.JPEG)
                        .setQuality(100)
                        .build());

                mCroppingProgressDialog = ProgressDialog.show(this, null, "Cropping image. Please wait.", true);
                break;
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void copyImage(File inputFile, File outputFile) {
        InputStream in;
        OutputStream out;

        try {
            in = new FileInputStream(inputFile);
            out = new FileOutputStream(outputFile);

            byte[] buffer = new byte[1024];
            int read;
            while((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }

            in.close();
            out.flush();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, "An error occurred", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "An error occurred", Toast.LENGTH_SHORT).show();
        }
    }
}
