package com.example.rionay.antitigerpoaching;
//created by Da Huo 791094
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.os.Bundle;
import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.view.MotionEvent;
import android.widget.Toast;
import com.example.rionay.antitigerpoaching.ImageControl.ICustomMethod;
import com.example.rionay.antitigerpoaching.R.id;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class MainActivity extends Activity implements OnClickListener {

    private TextView textView1;
    private TextView textView2;
    private TextView textView3;
    private TextView textView4;
    private TextView textView5;
    private ImageView dstimage;
    private Bitmap srcBitmap;
    private String pathName;
    private SeekBar SaturationseekBar = null;
    private SeekBar BrightnessseekBar = null;
    private SeekBar ContrastseekBar = null;
    private int imgHeight, imgWidth;
    private Button Button1;

    private DatabaseReference mDatabase;

    private StorageReference mStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView1 = (TextView) findViewById(R.id.id);
        textView2 = (TextView) findViewById(R.id.name);
        textView3 = (TextView) findViewById(R.id.version);
        textView4 = (TextView) findViewById(R.id.EpochTime);
        textView5 = (TextView)findViewById(id.Longtitude);
        Button1 = (Button) findViewById(R.id.Button1);
        dstimage = (ImageView) findViewById(R.id.Tmage);


        mDatabase = FirebaseDatabase.getInstance().getReference();


        mDatabase.child("dayKey").addValueEventListener(new ValueEventListener() {        //get the daykey value of JSON message
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String dayKey = dataSnapshot.getValue().toString();
                textView1.setText("dayKey is: "+dayKey);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabase.child("captureTime").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String captureTime = dataSnapshot.getValue().toString();
                String RealTime = EpochTimeTransfer(captureTime);           //transfer the Epoch time to Real time
                textView2.setText("captureTime is: "+RealTime);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabase.child("cameraId").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String cameraId = dataSnapshot.getValue().toString();
                textView3.setText("cameraId is: "+cameraId);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabase.child("latitude").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String latitude = dataSnapshot.getValue().toString();
                textView4.setText("latitude is: "+latitude);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        mDatabase.child("longitude").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String longitude = dataSnapshot.getValue().toString();
                textView5.setText("longitude is: "+longitude);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        mStorage = FirebaseStorage.getInstance().getReference();



        StorageReference islandRef = mStorage.child("Photos/test.jpg");   //get the photo stored in the server

        try {
            File localFile = File.createTempFile("images", "jpg");
            pathName = localFile.getPath();
            islandRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {

                    Toast.makeText(MainActivity.this, "Download done!!!", Toast.LENGTH_LONG).show();
                    parseJSONWithJSONObject();

                    // Local temp file has been created
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        ///////////////////////////
        Button1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,SecondActivity.class);
                startActivity(intent);
            }
        });      //jump to the secondActivity class and layout interface to send the judgement
    }








    private void parseJSONWithJSONObject() {

        findView();
        try {
            SaturationseekBar = (SeekBar) findViewById(R.id.Saturationseekbar);
            BrightnessseekBar = (SeekBar) findViewById(R.id.Brightnessseekbar);
            ContrastseekBar = (SeekBar) findViewById(R.id.Contrastseekbar);
            srcBitmap = BitmapFactory.decodeFile(pathName);
            dstimage.setImageBitmap(srcBitmap);
            imgHeight = srcBitmap.getHeight();
            imgWidth = srcBitmap.getWidth();




            SaturationseekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                //invoke this method when the position of sliding block changes
                public void onProgressChanged(SeekBar arg0, int progress, boolean fromUser) {
                    Bitmap SaturBmp = Bitmap.createBitmap(imgWidth, imgHeight, Bitmap.Config.ARGB_8888);
                    ColorMatrix ColorMatrix = new ColorMatrix();
                    // set the saturation
                    ColorMatrix.setSaturation((float) (progress / 100.0));   //set the saturation of the image, use the float as argument
                    //0 is gray，1 is primary image
                    Paint SaturaPaint = new Paint();
                    SaturaPaint.setColorFilter(new ColorMatrixColorFilter(ColorMatrix));

                    Canvas SaturaCanvas = new Canvas(SaturBmp);
                    SaturaCanvas.drawBitmap(srcBitmap, 0, 0, SaturaPaint);

                    dstimage.setImageBitmap(SaturBmp);

                }

                public void onStartTrackingTouch(SeekBar bar) {
                }

                public void onStopTrackingTouch(SeekBar bar) {
                }
            });    //set the saturation of the image




            BrightnessseekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                public void onProgressChanged(SeekBar arg0, int progress, boolean fromUser) {
                    Bitmap BrightBmp = Bitmap.createBitmap(imgWidth, imgHeight, Bitmap.Config.ARGB_8888);
                    int brightness = progress - 127;
                    ColorMatrix ColorMatrix = new ColorMatrix();
                    ColorMatrix.set(new float[] { 1, 0, 0, 0, brightness, 0, 1, 0, 0, brightness, 0, 0, 1, 0, brightness, 0, 0, 0, 1, 0 });// change the brightness
                    //reset the value of the matrix
                    Paint BrightPaint = new Paint();
                    BrightPaint.setColorFilter(new ColorMatrixColorFilter(ColorMatrix));

                    Canvas BrightCanvas = new Canvas(BrightBmp);
                    BrightCanvas.drawBitmap(srcBitmap, 0, 0, BrightPaint);
                    dstimage.setImageBitmap(BrightBmp);

                }

                public void onStartTrackingTouch(SeekBar bar) {
                }

                public void onStopTrackingTouch(SeekBar bar) {
                }
            });  //set the Brightness of the image




            ContrastseekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                public void onProgressChanged(SeekBar arg0, int progress, boolean fromUser) {
                    Bitmap ContrBmp = Bitmap.createBitmap(imgWidth, imgHeight, Bitmap.Config.ARGB_8888);
                    // int brightness = progress - 127;
                    float contrast = (float) ((progress + 64) / 128.0);
                    ColorMatrix ColorMatrix = new ColorMatrix();
                    ColorMatrix.set(new float[] { contrast, 0, 0, 0, 0, 0, contrast, 0, 0, 0, 0, 0, contrast, 0, 0, 0, 0, 0, 1, 0 });

                    Paint ContrPaint = new Paint();
                    ContrPaint.setColorFilter(new ColorMatrixColorFilter(ColorMatrix));

                    Canvas ContrCanvas = new Canvas(ContrBmp);
                    ContrCanvas.drawBitmap(srcBitmap, 0, 0, ContrPaint);

                    dstimage.setImageBitmap(ContrBmp);
                }

                public void onStartTrackingTouch(SeekBar arg0) {
                    // TODO Auto-generated method stub

                }

                public void onStopTrackingTouch(SeekBar seekBar) {
                    // TODO Auto-generated method stub
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }









    public String EpochTimeTransfer(String EpochTime) {     //change the Epoch time into Real time
        String RealTime = "";
        try {

            long l = Long.parseLong(EpochTime);
            l = l * 1000;

            Date CapturedDate = new Date(l);

            DateFormat TimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

            TimeFormat.setTimeZone(TimeZone.getTimeZone("Etc/UTC"));
            RealTime = TimeFormat.format(CapturedDate);
        } catch (Exception e) {
            e.printStackTrace();

        }
        return RealTime;
    }







    ImageControl imgControl;
    @Override
    public void onClick(View arg0) {
        // TODO Auto-generated method stub

    }







    private void findView() {
        imgControl = (ImageControl) findViewById(id.Tmage);
        srcBitmap = BitmapFactory.decodeFile(pathName);
        imgControl.setImageBitmap(srcBitmap);
        init();
    }

    private void init() {
        Bitmap bitmap;
        if (imgControl.getDrawingCache() != null) {
            bitmap = Bitmap.createBitmap(imgControl.getDrawingCache());
        } else {
            bitmap = ((BitmapDrawable) imgControl.getDrawable()).getBitmap();
        }
        Rect frame = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;
        int screenWidth = this.getWindowManager().getDefaultDisplay().getWidth();       //set the width of the picture the same with the Window
        int screenHidth = this.getWindowManager().getDefaultDisplay().getHeight() - statusBarHeight*11;//
        if (bitmap != null) {
            imgControl.imageInitial(bitmap, screenWidth, screenHidth, statusBarHeight, new ICustomMethod(){

                @Override
                public void customMethod(Boolean currentStatus) {
                    if (currentStatus) {
                    } else {
                    }
                }
            });
        }                  //successfully get the downloaded image
        else
        {
            Toast.makeText(MainActivity.this, "load failed", Toast.LENGTH_SHORT).show();
        }                  //show the failed information to the user

    }




    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {

            case MotionEvent.ACTION_DOWN:                    //the event of mouse click down
                imgControl.mouseClickDown(event);
                break;

            case MotionEvent.ACTION_POINTER_DOWN:            //the event ot multiple click down
                imgControl.mousePointDown(event);
                break;

            case MotionEvent.ACTION_MOVE:                    // the event of mouse moving
                imgControl.mouseMovement(event);
                break;

            case MotionEvent.ACTION_UP:
                imgControl.mouseClickUp();
                break;

        }

        return super.onTouchEvent(event);
    }
}
