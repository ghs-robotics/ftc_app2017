/*
 * Copyright (c) 2015 LASA Robotics and Contributors
 * MIT licensed
 */

package com.lasarobotics.tests.camera;

import android.os.Environment;

import org.lasarobotics.vision.image.Drawing;
import org.lasarobotics.vision.opmode.TestableVisionOpMode;
import org.lasarobotics.vision.util.color.Color;
import org.lasarobotics.vision.util.color.ColorRGBA;
import org.opencv.android.Utils;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.DMatch;
import org.opencv.core.KeyPoint;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import static org.lasarobotics.vision.android.Util.getContext;
import static org.opencv.core.Core.CMP_GT;
import static org.opencv.imgcodecs.Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE;
import static org.opencv.imgcodecs.Imgcodecs.imread;

import org.lasarobotics.vision.detection.ColorBlobDetector;

/**
 * Vision OpMode run by the Camera Test Activity
 * Use TestableVisionOpModes in testing apps ONLY (but you can easily convert between opmodes just by changingt t
 */
public class CameraTestVisionOpMode extends TestableVisionOpMode {
    final double FUNCTION_A_A = 8001.3;
    final double FUNCTION_A_B = 1.880;
    final double FUNCTION_A_C = 301.5;
    int[] kernel = { -1, 0, 1, -2, 0, 2, -1, 0, 1};
    Mat mkernel;
    final double KERNEL_SCALER = 1;
    final float TAPE_THRESHOLD = 2000;
    final double FUNCTION_B_A = 1000.0;
    final double FUNCTION_B_B = 1.7695;
    final double FUNCTION_B_C = 1.1096;

    @Override
    public void init() {
        super.init();
        this.setFrameSize(new Size(3264, 1836));
        Mat mkernel = new Mat( 3, 3, CvType.CV_32S );
        mkernel.put(3, 3, kernel);


        /*try {
            this.image = Utils.loadResource(getContext(), R.drawable.legos, CV_LOAD_IMAGE_GRAYSCALE);
        } catch (IOException e) {
            e.printStackTrace();
        }*/
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
        return rgba;
    }

    private int distToPos (double d) {
        return (int) (FUNCTION_A_A / (d - FUNCTION_A_B) + FUNCTION_A_C);
    }


    private int distToLength (double d) {
        return (int) (FUNCTION_B_A / (d - FUNCTION_B_B) + FUNCTION_B_C);
    }

    private Mat edgeDetection (Mat bad) {
        Imgproc.filter2D(bad, bad, -1, mkernel);
        return bad;
    }

    private double[] findTape(double d, Mat in, double[][] kernel) {
        int yin = distToPos(d);
        double[] result = new double[2];
        int width = in.width();
        int height = in.height();
        Rect crop = new Rect(0, yin-2, width, 5);
        Mat kmat = new Mat(height, width, CvType.CV_32S);

        Imgproc.cvtColor(in, in, Imgproc.COLOR_RGBA2GRAY);
        Imgproc.filter2D(in, kmat, -1, mkernel);
        kmat = new Mat(kmat, crop);
        Core.absdiff(kmat, Scalar.all(0), kmat);
        Mat maybe = new Mat(1, width, CvType.CV_32S);
        Core.reduce(kmat, maybe, 0, Core.REDUCE_SUM, CvType.CV_32S);
        Core.compare(maybe, Scalar.all(TAPE_THRESHOLD), maybe, Core.CMP_GT);
        Core.findNonZero(maybe, maybe);
    }
}
