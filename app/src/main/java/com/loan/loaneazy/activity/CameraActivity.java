package com.loan.loaneazy.activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.loan.loaneazy.MyCamera.AspectRatio;
import com.loan.loaneazy.MyCamera.AspectRatioFragment;
import com.loan.loaneazy.MyCamera.CameraConstants;
import com.loan.loaneazy.MyCamera.MyCameraFragment;
import com.loan.loaneazy.R;
import com.loan.loaneazy.my_interface.CameraListener;
import com.loan.loaneazy.utility.Constants;


public class CameraActivity extends AppCompatActivity implements View.OnClickListener, AspectRatioFragment.AspectRatioListener, CameraListener {
    private static final String FRAGMENT_DIALOG = "aspect_dialog";
    private static final String TAG = "CameraActivity";

    private static final int[] FLASH_OPTIONS = {
            CameraConstants.FLASH_AUTO,
            CameraConstants.FLASH_OFF,
            CameraConstants.FLASH_ON,
    };

    private static final int[] FLASH_ICONS = {
            R.drawable.ic_flash_auto,
            R.drawable.ic_flash_off,
            R.drawable.ic_flash_on,
    };

    MyCameraFragment mCameraFragment;

    /**
     * The button of take picture
     */
    private Button mPictureButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreen();
        setContentView(R.layout.activity_camera);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);
        if (null == savedInstanceState) {
            mCameraFragment = MyCameraFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, mCameraFragment)
                    .commit();
        } else {
            mCameraFragment = (MyCameraFragment) getSupportFragmentManager().findFragmentById(R.id.container);
        }

        mPictureButton = (Button) findViewById(R.id.picture);
        mPictureButton.setOnClickListener(this);
        /*int facing = mCameraFragment.getFacing();
        mCameraFragment.setFacing(facing == CameraConstants.FACING_FRONT ?
                CameraConstants.FACING_BACK : CameraConstants.FACING_FRONT);*/
        boolean booleanExtra = getIntent().getBooleanExtra(Constants.OPENWHICH, false);
        if (booleanExtra) {
            mCameraFragment.setFacing(CameraConstants.FACING_FRONT);
        } else {
            mCameraFragment.setFacing(CameraConstants.FACING_BACK);
        }


    }

    private void setFullScreen() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.picture:
                mCameraFragment.takePicture();
                mPictureButton.setEnabled(false);
                break;
        }
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.camera_menu, menu);
        return true;
    }*/

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.switch_camera:
                int facing = mCameraFragment.getFacing();
                mCameraFragment.setFacing(facing == CameraConstants.FACING_FRONT ?
                        CameraConstants.FACING_BACK : CameraConstants.FACING_FRONT);
                invalidateOptionsMenu();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }*/

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    public void onCameraCapturePerform(String picturePath, Uri uri) {
        Log.e(TAG, "onCameraCapturePerform: Camera Image Path Is  : " + picturePath);
        Intent intent = new Intent();
        intent.putExtra(Constants.IMAGE_PATH, picturePath);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onAspectRatioSelected(@NonNull AspectRatio ratio) {
        Toast.makeText(this, ratio.toString(), Toast.LENGTH_SHORT).show();
        mCameraFragment.setAspectRatio(ratio);
    }
}