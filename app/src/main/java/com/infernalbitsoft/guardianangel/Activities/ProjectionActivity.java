package com.infernalbitsoft.guardianangel.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.infernalbitsoft.guardianangel.R;
import com.infernalbitsoft.guardianangel.Services.ScreenCaptureService;

import static com.infernalbitsoft.guardianangel.Utilities.GlobalVariables.SCREEN_MIRROR_PERMISSION_CODE;
import static com.infernalbitsoft.guardianangel.Utilities.LogAndToast.Toast;

public class ProjectionActivity extends AppCompatActivity {

    public static Intent mediaProjectionIntent = null;

    public static void requestProjectionIntentActivity(Context ctx) {
        Intent pIntent = new Intent(ctx, ProjectionActivity.class);
        ctx.startActivity(pIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projection);
        MediaProjectionManager projectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        startActivityForResult(projectionManager.createScreenCaptureIntent(), SCREEN_MIRROR_PERMISSION_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        Toast(this, "Projection Activity");
        if (requestCode == SCREEN_MIRROR_PERMISSION_CODE) {
            if (resultCode == RESULT_OK) {
                mediaProjectionIntent = data;
            }
            this.finish();
        }
    }
}