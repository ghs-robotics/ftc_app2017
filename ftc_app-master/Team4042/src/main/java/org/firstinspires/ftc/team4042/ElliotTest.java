package org.firstinspires.ftc.team4042;

import android.graphics.Camera;
import android.app.Activity;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class ElliotTest extends Activity implements PictureCallback{
    public static Camera getCameraInstance(){
        Camera camera = android.graphics.Camera.open();
        try {
            camera
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        // TODO Auto-generated method stub

    }
}