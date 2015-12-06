/**
 * Simple shape detector program.
 * It loads a video stream and tries to find simple shapes (rectangle, triangle, circle) in it.
 * This program is a modified version of `shape-detect.cpp` - http://opencv-code.com/tutorials/detecting-simple-shapes-in-an-image/.
 * Author: Bang Dede
 * Modified and written in Java by: Gasimov Aydin
 */

package com.video;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class Mat2Image extends Mat{
        Mat mat = new Mat();
        BufferedImage img;
          byte[] dat;
          static Mat imageGray = new Mat();
		  static Mat imageCny = new Mat();
		  static Mat hierarchy = new Mat();
		  static Mat lines = new Mat();
		  
    public Mat2Image() {
    	
    }
   
        public static Image toBufferedImage(Mat m){
  	         int type = BufferedImage.TYPE_BYTE_GRAY;
  	         if ( m.channels() > 1 ) {
  	              type = BufferedImage.TYPE_3BYTE_BGR;
  	         }
  	        int bufferSize = m.channels()*m.cols()*m.rows();
  	        byte [] b = new byte[bufferSize];
  	      m.get(0,0,b); // get all the pixels
  	               BufferedImage image = new BufferedImage(m.cols(),m.rows(), type);
  	        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
  	           System.arraycopy(b, 0, targetPixels, 0, b.length);  
  	        
  	        return image;

  	  }
        
        
        
          @SuppressWarnings("unused")
		public static <Bitmap> Image EdgeDetect(Mat m) {
        	  
        	    Imgproc.cvtColor(m, imageGray, Imgproc.COLOR_BGR2GRAY);
 	            Imgproc.Canny(imageGray, imageGray, 250, 150, 3 , true);
 	         
 	                List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
 	                Imgproc.threshold(imageGray, imageGray, 190, 220, Imgproc.THRESH_BINARY);
 	                Imgproc.adaptiveThreshold(imageGray, imageGray, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY_INV, 11, 5);
 	                Imgproc.findContours(imageGray.clone(), contours, imageGray, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
 	                 
				     List<MatOfPoint2f> approx = new ArrayList<MatOfPoint2f>();
				     Mat dst = imageGray.clone();
				       
				        for (int i=0; i<contours.size(); i++){
				        	   
				        	   MatOfPoint2f myPt = new MatOfPoint2f();
				        	   contours.get(i).convertTo(myPt, CvType.CV_32FC2);
				        	   approx.add(myPt);
				        	   
				        	   MatOfPoint2f myPt2 = new MatOfPoint2f();
				        	   contours.get(i).convertTo(myPt2, CvType.CV_32FC2);

				        	   Imgproc.approxPolyDP(myPt2,  myPt, Imgproc.arcLength(myPt2, true)*0.02, true);
				        	   
				        	if (Math.abs(Imgproc.contourArea(myPt2)) < 4000)
				    			continue;
				        	
				        	  MatOfPoint points = new MatOfPoint( myPt.toArray() );  
				        	  MatOfPoint points2 = new MatOfPoint( myPt2.toArray() );
				        	   if (approx.size() == 3)
				    		       {
				    			        setLabel(m, "TRI",  points);    // Triangles
				    			        Imgproc.drawContours(m, contours, i, new Scalar(0,0,255), 1);
				    		       } 
				        	   
				        	   else if (approx.size() == 4)
				       		        {
				       			         int vtc = approx.size();
				       			         Vector<Double> cos = new Vector<Double>();
			       			             Point[] pArray = points.toArray(); 
			       			        
				       			             for (int j = 2; j < vtc+1; j++) {
				       			            	
				       			                 cos.add(Angle(pArray[j%vtc], pArray[j-2], pArray[j-1]));
				       			              Collections.sort(cos);
				       			           System.out.println("cos:  " + cos + " cos.size: " + cos.size());
				       		                    double mincos = cos.firstElement();
				       			                double maxcos = cos.lastElement();
				       			             
				       			             System.out.println("mincos:  " + mincos + " maxcos: " + maxcos);    
				       			                   if ((vtc == 4) && (mincos >= -0.1) && (maxcos <= 0.3)) 
				       			                             { setLabel(m, "RECT", points); Imgproc.drawContours(m, contours, i, new Scalar(255,0,0), 1);}
				       			                       
				       		       }      
				       		    }  
				        	   else {
	       			            	  double area = Imgproc.contourArea(points);
	       			            	  Rect r = Imgproc.boundingRect(points);
	       			            	  int radius = r.width / 2;

	       			   		             	if (Math.abs(1 - ((double)r.width / r.height)) <= 0.2 &&
	       			   			               Math.abs(1 - (area / (Math.PI * Math.pow(radius, 2)))) <= 0.2)
	       			   				             { setLabel(m, "CIR", points); Imgproc.drawContours(m, contours, i, new Scalar(0,255,0), 1);}
	       			               }
				        	  
				        	   
				        	   System.out.println("myPt: " + myPt.size() + " Imgproc.contourArea(myPt): " + Imgproc.contourArea(myPt));
				        	   
				        }
				     
        	    return Mat2Image.toBufferedImage(m);  
          }
                  
    

	private static void setLabel(Mat dst, String label, MatOfPoint contours) {
			
	         	int fontface = Core.FONT_HERSHEY_SIMPLEX;
		        double scale = 0.4;
		        int thickness = 1;
		        int[] baseline = {1};
		        
		           Size text =  Core.getTextSize(label, fontface, scale, thickness, baseline);
		           Rect r = Imgproc.boundingRect( contours);
                   Point pt = new Point((r.x + ((r.width - text.width) / 2)),(r.y + ((r.height + text.height) / 2)));
		            Core.rectangle(dst,new Point(0, 0), new Point(text.width, -text.height), new Scalar(255,255,255), Core.FILLED);
		            Core.putText(dst, label, pt, fontface, scale, new Scalar(0,0,255), thickness, 8, false);
		            
	}

     private static double Angle(Point pt1, Point pt2, Point pt0){
    	    double dx1 = pt1.x - pt0.x;
    		double dy1 = pt1.y - pt0.y;
    		double dx2 = pt2.x - pt0.x;
    		double dy2 = pt2.y - pt0.y;
    		return (dx1*dx2 + dy1*dy2)/Math.sqrt((dx1*dx1 + dy1*dy1)*(dx2*dx2 + dy2*dy2) + 1e-10);
     }

	static{
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
}
