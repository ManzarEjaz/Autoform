package com.example.autoform;

import androidx.annotation.NonNull;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.io.IOException;


public class MainActivity extends AppCompatActivity {
    private Button snapBtn;
    private Button detectBtn;
    private Button gallery;
    private Button signout;
    private Button upload;
    private ImageView imageView;
    private TextView txtView;
    private Bitmap bitmap;
    private final int PICK_PHOTO = 1;
    private final int CAMERA_PHOTO = 2;
    String h;
    String name = "ty";
    String name1 = "";
    String surname = "yu";
    String surname1 = "";
    String phone = "iuu";
    String phone1 = "";
    member m;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        snapBtn = findViewById(R.id.snapBtn);
        detectBtn = findViewById(R.id.detectBtn);
        imageView = findViewById(R.id.imageView);
        txtView = findViewById(R.id.txtView);
        gallery = findViewById(R.id.gallery);
        signout = findViewById(R.id.signout);
        upload = findViewById(R.id.upload);
        m = new member();
        reference = FirebaseDatabase.getInstance().getReference().child("member");
        signout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, login_activity.class));
            }
        });
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPhoto();
            }
        });
        snapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCamera();
            }
        });

        detectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detectTxt();
            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                h = txtView.getText().toString();
                Log.e("hjhjhjgygyghghg", h);
                String[] words = h.split("\\s+");
                Log.e("name is", words[0]);
                name1 = "";
                surname1 = "";
                phone1 = "";
                for (int i = 0; i < words[0].length(); i++) {
                    name1 += words[0].charAt(i);
                }
                Log.e("name isssssssssssssss", name1);
                for (int i = 0; i < words[1].length(); i++) {
                    surname1 += words[1].charAt(i);
                }
                Log.e("surname isssssssssss", surname1);
                for (int i = 0; i < words[2].length(); i++) {
                    phone1 += words[2].charAt(i);
                }
                Log.e("phone isssssssssss", phone1);;
                m.setNa(name1);
                m.setSu(surname1);
                m.setPh(phone1);
                reference.push().setValue(m);

                Toast.makeText(getApplicationContext(), "Data inserted", Toast.LENGTH_LONG).show();
            }
        });

    }


    public void onPhoto() {
        try {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            startActivityForResult(intent, PICK_PHOTO);
        } catch (Exception e) {

        }
    }

    public void onCamera() {
        try {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, CAMERA_PHOTO);
        } catch (Exception e) {

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PICK_PHOTO:
                if (data == null) return;

                Uri uri = data.getData();

                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                imageView.setImageBitmap(imageProcess(bitmap));


                break;
            case CAMERA_PHOTO:
                bitmap = data.getParcelableExtra("data");
                if (bitmap == null) return;

                imageView.setImageBitmap(imageProcess(bitmap));

                break;
        }
    }

    private void detectTxt() {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(imageProcess(bitmap));
        FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
        Task<FirebaseVisionText> result =
                detector.processImage(image)
                        .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                            @Override
                            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                                processTxt(firebaseVisionText);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        });
    }

    private void processTxt(FirebaseVisionText firebaseVisionText) {
        String result = firebaseVisionText.getText().trim();
        String[] words = result.split("\\s+");


        Log.e("printede string is", words[1]);
        txtView.setText(result);
    }

    public Bitmap imageProcess(Bitmap image) {
        int width, height;
        height = image.getHeight();
        width = image.getWidth();
        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(image, 0, 0, paint);
        return bmpGrayscale;
    }
}
