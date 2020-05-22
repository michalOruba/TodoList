package pl.michaloruba.todolist;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Size;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class AttachMedia extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private long todoId;
    private GridView gridview;
    private ArrayList<Bitmap> images;
    private ArrayList<String> imagePaths;
    int index;
    MyImageAdapter myImageAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attach_media);

        todoId = getIntent().getLongExtra("todoID", 0L);

        gridview = findViewById(R.id.gridview);
        gridview.setOnItemClickListener(this);

        gridview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(AttachMedia.this)
                        .setMessage(getString(R.string.doDelete))
                        .setCancelable(true)
                        .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") int id) {
                                File file = new File (imagePaths.get(position));
                                if (file.delete()) {
                                    TodoHelper.generateToast(AttachMedia.this, R.string.fileDeleted);
                                    getAttachmentsForTodo();
                                }
                                else {
                                    TodoHelper.generateToast(AttachMedia.this, R.string.errorOccurs);
                                }
                            }
                        })
                        .setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, @SuppressWarnings("unused") int id) {

                            }
                        });
                final AlertDialog alert = dialogBuilder.create();
                alert.show();
                return true;
            }
        });

        getAttachmentsForTodo();

        FloatingActionButton fab = findViewById(R.id.fabAttach);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(AttachMedia.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(AttachMedia.this, new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                }
                else {
                    createHelpDialog();
                }
            }
        });
    }

    private void getAttachmentsForTodo() {
        try {
            File sdPictureFiles = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File[] files = sdPictureFiles.listFiles();
            images = new ArrayList<>();
            imagePaths = new ArrayList<>();

            File file;
            for (index = 0; index < files.length; index++) {
                file = files[index];
                if (file.getName().charAt(0) == String.valueOf(todoId).charAt(0)) {
                    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                    bmOptions.inSampleSize = 16; // one-to-eight scale
                    if (file.getAbsolutePath().contains(".jpg")) {
                        images.add(BitmapFactory.decodeFile(file.getAbsolutePath(), bmOptions));
                    }
                    else if (file.getAbsolutePath().contains(".mp4")){
                        images.add(ThumbnailUtils.createVideoThumbnail(file.getAbsolutePath(), MediaStore.Video.Thumbnails.MINI_KIND));
                    }
                    imagePaths.add(file.getAbsolutePath());
                }
            }

            myImageAdapter = new MyImageAdapter(this, images);
            gridview.setAdapter(myImageAdapter);
            myImageAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        showBigScreen(position);
    }

    private void showBigScreen(int position) {

        if (imagePaths.get(position).contains(".jpg")) {
            Intent intent = new Intent(this, BigScreen.class);
            intent.putExtra("position", position);
            intent.putExtra("bitmap", images.get(position));
            startActivity(intent);
        }
        else {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(imagePaths.get(position)));
            intent.setDataAndType(Uri.parse(imagePaths.get(position)), "video/mp4");
            startActivity(intent);
        }
    }

    private File createImageFile(String type) throws IOException {
        String timeStamp = TodoHelper.formatDateToString(new Date());
        String imageFileName = todoId + "_" +  (type.equals(MediaStore.ACTION_IMAGE_CAPTURE) ? "JPEG_" : "VID_") + timeStamp + "_";
        String suffix = type.equals(MediaStore.ACTION_IMAGE_CAPTURE) ? ".jpg" : ".mp4";
        File storageDir = getExternalFilesDir(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  // prefix
                suffix,         // suffix
                storageDir      // directory
        );
        return image;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                TodoHelper.generateToast(AttachMedia.this, R.string.permissionGranted);
            }
            else
            {
                TodoHelper.generateToast(AttachMedia.this, R.string.permissionDenied);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK)
        {
            getAttachmentsForTodo();
        }
    }

    public void createHelpDialog(){
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this)
                .setMessage(getString(R.string.userAttachAction))
                .setCancelable(true)
                .setPositiveButton(getString(R.string.photo), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") int id) {
                        openCameraApplication(MediaStore.ACTION_IMAGE_CAPTURE);
                    }
                })
                .setNeutralButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setNegativeButton(getString(R.string.movie), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, @SuppressWarnings("unused") int id) {
                        openCameraApplication(MediaStore.ACTION_VIDEO_CAPTURE);
                    }
                });
        final AlertDialog alert = dialogBuilder.create();
        alert.show();
    }

    public void openCameraApplication(String cameraType){
        Intent cameraIntent = new Intent(cameraType);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile(cameraType);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            if (photoFile != null) {
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        }
    }
}
