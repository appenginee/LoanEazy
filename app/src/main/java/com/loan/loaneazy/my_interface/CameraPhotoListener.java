package com.loan.loaneazy.my_interface;

public interface CameraPhotoListener {
    public void onCameraPrevClick(int screenNo);

    public void onCameraCaptureClick();

    public void onSaveAllClick(String path1, String path2, String path3);
}
