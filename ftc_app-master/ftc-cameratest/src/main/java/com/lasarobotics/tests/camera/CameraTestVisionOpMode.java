/*
 * Copyright (c) 2015 LASA Robotics and Contributors
 * MIT licensed
 */

package com.lasarobotics.tests.camera;

import android.util.Log;

import org.lasarobotics.vision.opmode.TestableVisionOpMode;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Point;
import org.opencv.core.Size;

import java.io.IOException;

import static org.lasarobotics.vision.android.Util.getContext;
import static org.opencv.imgcodecs.Imgcodecs.CV_LOAD_IMAGE_COLOR;
import static org.opencv.imgcodecs.Imgcodecs.imread;

/**
 * Vision OpMode run by the Camera Test Activity
 * Use TestableVisionOpModes in testing apps ONLY (but you can easily convert between opmodes just by changingt t
 */
public class CameraTestVisionOpMode extends TestableVisionOpMode {
    final double FUNCTION_A_A = 8001.3;
    final double FUNCTION_A_B = 1.880;
    final double FUNCTION_A_C = 301.5;
    final double KERNEL_SCALER = 1;
    final float TAPE_THRESHOLD = 2000;
    final double FUNCTION_B_A = 1000.0;
    final double FUNCTION_B_B = 1.7695;
    final double FUNCTION_B_C = 1.1096;
    Mat image;

    @Override
    public void init() {
        super.init();
        this.setFrameSize(new Size(800, 720));


        try {
            image = Utils.loadResource(getContext(), R.drawable.hate, CV_LOAD_IMAGE_COLOR);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Imgproc.cvtColor(image, image, Imgproc.COLOR_BGR2RGBA);
    }

    @Override
    public void loop() {
        super.loop();
    }

    @Override
    public void stop() {
        super.stop();
    }

    @Override
    public Mat frame(Mat rgba, Mat gray) {
        //Imgproc.filter2D(rgba, rgba, -1, mkernel);
        rgba = super.frame(rgba, gray);
        Mat yo = image.clone();
        Point[] yeah = findTape(25, yo);
        Log.d("much" , Integer.toString(yeah.length));
        for (Point point : yeah) {
            Imgproc.circle(image, point, 2, new Scalar(0,255,0), -1);

        }
        //int yin = distToPos(25);
        //Imgproc.rectangle(image, new Point(0, yin-2), new Point(1280, yin+3), new Scalar(0, 0, 255));
        return image;
    }

    private int distToPos (double d) {
        return (int) (FUNCTION_A_A / (d - FUNCTION_A_B) + FUNCTION_A_C);
    }


    private int distToLength (double d) {
        return (int) (FUNCTION_B_A / (d - FUNCTION_B_B) + FUNCTION_B_C);
    }

    private Point[] findTape(double d, Mat in) { // , double[][] kernel) {
        int yin = distToPos(d);
        double[] result = new double[2];
        int width = in.width();
        int height = in.height();
        Rect crop = new Rect(0, yin-2, width, 5);
        Mat kmat = new Mat(height, width, CvType.CV_32F);

        Imgproc.cvtColor(in, in, Imgproc.COLOR_RGBA2GRAY);
        Imgproc.Sobel(in, kmat, CvType.CV_16S, 1, 0);
        Core.convertScaleAbs(kmat, kmat);

        kmat = new Mat(kmat, crop);

        Mat maybe = new Mat(1, width, CvType.CV_32S);
        Core.reduce(kmat, maybe, 0, Core.REDUCE_SUM, CvType.CV_32S);
        Core.compare(maybe, Scalar.all(1000), maybe, Core.CMP_GT);
        //maybe.setTo(Scalar.all(255.0));
        Core.findNonZero(maybe, maybe);
        MatOfPoint yeah = new MatOfPoint(maybe);
        return yeah.toArray();
    }
}
