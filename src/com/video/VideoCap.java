/**
 * Simple shape detector program.
 * It loads a video stream and tries to find simple shapes (rectangle, triangle, circle) in it.
 * This program is a modified version of `shape-detect.cpp` - http://opencv-code.com/tutorials/detecting-simple-shapes-in-an-image/.
 * Author: Bang Dede
 * Modified and written in Java by: Gasimov Aydin
 */

package com.video;

import java.awt.image.BufferedImage;
import org.opencv.core.Core;
import org.opencv.highgui.VideoCapture;

public class VideoCap {
    static{
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    VideoCapture cap;
    Mat2Image mat2Img = new Mat2Image();

    VideoCap(){
        cap = new VideoCapture();
        cap.open(0);
    } 
 
    BufferedImage getOneFrame() {
        cap.read(mat2Img.mat);
        //return mat2Img.getImage(mat2Img.mat);
         return (BufferedImage) Mat2Image.EdgeDetect(mat2Img.mat);
    }
}