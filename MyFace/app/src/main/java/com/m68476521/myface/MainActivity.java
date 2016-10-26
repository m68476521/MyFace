package com.m68476521.myface;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.provider.Settings;
import android.support.v4.util.SparseArrayCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.MultiDetector;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.Landmark;

public class MainActivity extends AppCompatActivity {

    private ImageView mImageView;
    private Button mButton;
    private Canvas canvas;
    private Paint rectPaint;
    private Bitmap defaultBitmap;
    private Bitmap temporaryBitmap;

//    @Override
//    protected void attachBaseContext(Context base) {
//        super.attachBaseContext(base);
//        MultiDex
//        //
//        // MultiDex.install(MainActivity.this);
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImageView = (ImageView) findViewById(R.id.imageView);

        mButton = (Button) findViewById(R.id.button2);
        mButton.setOnClickListener(new View.OnClickListener() {
//            @LinearLayoutCompat.OrientationMode
            public void onClick(View v) {
                System.out.println("Mike on click ");




                BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                bitmapOptions.inMutable = true;


                initializeBitmap(bitmapOptions);
                createRectanglePaint();


                canvas = new Canvas(temporaryBitmap);
                canvas.drawBitmap(defaultBitmap, 0 , 0, null);

                FaceDetector faceDetector = new FaceDetector.Builder(v.getContext())
                        .setTrackingEnabled(false)
                        .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                        .build();

                if (!faceDetector.isOperational()) {
                    new AlertDialog.Builder(v.getContext())
                            .setMessage("Face detector could not be set up" +
                                    "on your device")
                            .show();
                            return;
                } else {

                    Frame frame = new Frame.Builder().setBitmap(defaultBitmap).build();
                    SparseArray<Face> sparseArray = faceDetector.detect(frame);

                    detectFaces(sparseArray);

                    mImageView.setImageDrawable(new BitmapDrawable(getResources(), temporaryBitmap));

                    faceDetector.release();
                }

//                Frame frame = new Frame.Builder().setBitmap(defaultBitmap).build();
//                SparseArray<Face> sparseArray = faceDetector.detect(frame);
//
//                mImageView.setImageDrawable(new BitmapDrawable(getResources(), temporaryBitmap));
//
//                faceDetector.release();


            }
        });


    }

    private void detectFaces(SparseArray<Face> sparseArray) {
        for (int i = 0; i < sparseArray.size(); i++) {
            Face face = sparseArray.valueAt(i);

            float left = face.getPosition().x;
            float top = face.getPosition().y;
            float right = left + face.getWidth();
            float bottom =  right + face.getHeight();

            float cornerRadius = 2.0f;
            RectF rectF = new RectF(left, top, right, bottom);

            canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, rectPaint);

            detectLandmarks(face);
        }
    }

    private void detectLandmarks(Face face) {
        for (Landmark landmark : face.getLandmarks()) {
            int x = (int) (landmark.getPosition().x);
            int y = (int) (landmark.getPosition().y);

            float radius = 10.0f;

            canvas.drawCircle(x, y, radius, rectPaint);

        }
    }

    private void createRectanglePaint() {
        rectPaint = new Paint();
        rectPaint.setStrokeWidth(5);
        rectPaint.setColor(Color.CYAN);
        rectPaint.setStyle(Paint.Style.STROKE);
    }

    private void initializeBitmap(BitmapFactory.Options bitmapOptions) {
        defaultBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.image,
                bitmapOptions);
        temporaryBitmap = Bitmap.createBitmap(defaultBitmap.getWidth(), defaultBitmap
                .getHeight(), Bitmap.Config.RGB_565);
//        eyePatchBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.eye_patch,
//                bitmapOptions);
    }

    }
