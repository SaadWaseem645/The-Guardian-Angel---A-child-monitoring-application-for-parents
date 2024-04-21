package com.infernalbitsoft.guardianangel.Services;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.ImageFormat;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.hardware.display.DisplayManager;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.infernalbitsoft.guardianangel.Activities.ProjectionActivity;
import com.infernalbitsoft.guardianangel.Notifications.MirrorNotification;
import com.infernalbitsoft.guardianangel.R;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static android.app.Activity.RESULT_OK;
import static com.infernalbitsoft.guardianangel.Activities.ProjectionActivity.mediaProjectionIntent;
import static com.infernalbitsoft.guardianangel.Services.CompoundService.currentApp;
import static com.infernalbitsoft.guardianangel.Utilities.LogAndToast.Log;
import static com.infernalbitsoft.guardianangel.Utilities.LogAndToast.Toast;

public class ScreenCaptureService extends Service {

    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference database = FirebaseDatabase.getInstance().getReference();
    private SharedPreferences preferences;
    private String username;
    private static int count = 0;

    private Handler handler = new Handler();


    private MediaProjection projection;
    private MediaProjectionManager projectionManager;

    private int mWidth;
    private int mHeight;

    private static int getVirtualDisplayFlags() {
        return DisplayManager.VIRTUAL_DISPLAY_FLAG_OWN_CONTENT_ONLY | DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC;
    }

    private class ImageAvailableListener implements ImageReader.OnImageAvailableListener {
        @Override
        public void onImageAvailable(ImageReader reader) {
            ByteArrayOutputStream bos = null;
            Bitmap bitmap = null;
            try (Image image = reader.acquireLatestImage()) {
                if (image != null) {
                    projection.stop();

                    if (count > 0)
                        return;
                    count++;
//                    Toast(ScreenCaptureService.this, "MirrorServiceRuns - " + count);
                    Log("ImageLoader", "Loaded Image");
                    Image.Plane[] planes = image.getPlanes();
                    ByteBuffer buffer = planes[0].getBuffer();
                    int pixelStride = planes[0].getPixelStride();
                    int rowStride = planes[0].getRowStride();
                    int rowPadding = rowStride - pixelStride * mWidth;

                    // create bitmap
                    bitmap = Bitmap.createBitmap(mWidth + rowPadding / pixelStride, mHeight, Bitmap.Config.ARGB_8888);
                    bitmap.copyPixelsFromBuffer(buffer);

                    // write bitmap to a file
                    bos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                    byte[] bytes = bos.toByteArray();

                    FirebaseStorage storage = FirebaseStorage.getInstance();

                    StorageReference storageRef = storage.getReference();
                    String referencePath = user.getUid() + "/" + username + "/";
                    StorageReference imageRef = storageRef.child(referencePath + UUID.randomUUID() + ".jpg");

                    UploadTask uploadTask = imageRef.putBytes(bytes);
                    uploadTask.addOnFailureListener(exception -> stopSelf()).addOnSuccessListener(taskSnapshot -> {
                        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {

                            count--;

                            String appName = "";
                            if (!currentApp.equals("NULL") && !currentApp.equals("")) {
                                try {
                                    PackageManager pm = getPackageManager();
                                    appName = pm.getApplicationLabel(pm.getApplicationInfo(currentApp, PackageManager.GET_META_DATA)).toString();
                                } catch (PackageManager.NameNotFoundException e) {
                                    return;
                                }
                            }

                            Map<String, Object> metadata = new HashMap<>();
                            metadata.put("url", uri.toString());
                            metadata.put("timestamp", ServerValue.TIMESTAMP);
                            metadata.put("appName", appName);

                            database.child(user.getUid())
                                    .child(preferences.getString(getString(R.string.sharedpreferences_child_profile_name_key), ""))
                                    .child("Mirror").push().setValue(metadata).addOnSuccessListener(aVoid -> {
                                stopSelf();
                            }).addOnFailureListener(e -> {
                                stopSelf();
                            });
                        }).addOnFailureListener(e -> {
                            stopSelf();
                            count--;
                        });
                    });
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (bos != null) {
                    try {
                        bos.close();
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                }

                if (bitmap != null) {
                    bitmap.recycle();
                }

            }
        }
    }

    public ScreenCaptureService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log("MirrorService", "Runs");

        preferences = getSharedPreferences(getString(R.string.app_preferences), MODE_PRIVATE);
        username = getSharedPreferences(getString(R.string.app_preferences), MODE_PRIVATE)
                .getString(getString(R.string.sharedpreferences_child_profile_name_key), "");

        if (mediaProjectionIntent == null) {
//            Toast(this, "Mirror After Kill Runs");
            Intent projectionIntent = new Intent(this, ProjectionActivity.class);
            projectionIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(projectionIntent);
            stopSelf();
        } else {
            Pair<Integer, Notification> notification = MirrorNotification.getNotification(this);
            startForeground(notification.first, notification.second);
            projectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
            DisplayMetrics metrics = getApplicationContext().getResources().getDisplayMetrics();
            mWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
            mHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
            int dpi = metrics.densityDpi;
            projection = projectionManager.getMediaProjection(RESULT_OK, mediaProjectionIntent);

            @SuppressLint("WrongConstant") ImageReader reader = ImageReader.newInstance(mWidth, mHeight, PixelFormat.RGBA_8888, 1);
            projection.createVirtualDisplay("Screen_Mirror", mWidth, mHeight, dpi, getVirtualDisplayFlags(), reader.getSurface(), null, null);

            reader.setOnImageAvailableListener(new ImageAvailableListener(), handler);
//            Toast(this, "Image Uploaded");
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Intent restartServiceTask = new Intent(getApplicationContext(), this.getClass());
        restartServiceTask.setPackage(getPackageName());
        PendingIntent restartPendingIntent = PendingIntent.getService(getApplicationContext(), 1, restartServiceTask, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager myAlarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        myAlarmService.set(
                AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + 1000,
                restartPendingIntent);

        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}