/*
 * Copyright 2016 Robert Streetman
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package jcanny;

import java.awt.image.BufferedImage;

public class JCanny {
    private static final double GAUSSIAN_INTENSITY = 2.5;
    private static final int GAUSSIAN_RADIUS = 10;
    
    private static int tHi;         //Hysteresis high threshold; Definitely edge pixels, do not examine
    private static int tLo;         //Hysteresis low threshold; possible edge pixel, examine further.
    private static int stDev;       //Standard deviation in magnitude of image's pixels
    private static int mean;        //Mean of magnitude in image's pixels
    private static int numDev;      //Number of standard deviations above mean for high threshold
    private static double tFract;   //Low threshold is this fraction of high threshold
    private static int[][] dir;     //Gradient direction mask. Equals Math.atan2(gy/gx)
    private static int[][] gx;      //Mask resulting from horizontal 3x3 Sobel mask
    private static int[][] gy;      //Mask resulting from vertical 3x3 Sobel mask
    private static double[][] mag;  //Direction mask. Equals Math.sqrt(gx^2 * gy^2)
    
    /*
     * This function accepts a single-channel (grayscale, red, blue, Y, etc) image and returns an image with detected edges.
     * Currently computes hysteresis thresholds based on an a given ratio, but in the future all parameters will be passed 
     * in from an external source to allow another program to optimize them.
     */
    public static BufferedImage CannyEdges(BufferedImage img, int numberDeviations, double fract) throws Exception {
        if (fract <= 0 || fract >= 1) {  
            throw new IllegalArgumentException("ERROR: Hysteresis threshold ratio in range 0 - 1.0!");
        }
        
        int[][] raw = ImgIO.GSArray(img);
        int[][] blurred = Gaussian.BlurGS(raw, GAUSSIAN_RADIUS, GAUSSIAN_INTENSITY);
        numDev = numberDeviations;
        tFract = fract;
        
        gx = Sobel.Horizontal(blurred);  //Convolved with 3x3 horizontal Sobel mask
        gy = Sobel.Vertical(blurred);    //Convolved with 3x3 vertical Sobel mask
        
        Magnitude(gx, gy);    //Find the gradient magnitude at each pixel
        Direction(gx, gy);    //Find the gradient direction at each pixel
        Suppression();              //Using the direction and magnitude images, identify candidate points
        
        BufferedImage edges = ImgIO.GSImg(Hysteresis());
        
        return edges;
    }
    
    private static void Magnitude(int[][] gx, int[][] gy) {
        int sum = 0;
        int var = 0;
        mag = new double[gx.length][gx[0].length];
        
        for (int r = 0; r < gx.length; r++) {
            for (int c = 0; c < gx[0].length; c++) {
                mag[r][c] = Math.sqrt(gx[r][c] * gx[r][c] + gy[r][c] * gy[r][c]);
                sum += mag[r][c];
            }
        }
        
        mean = sum / (mag.length * mag[0].length);
        
        //Get variance
        for (int r = 0; r < mag.length; r++) {
            for (int c = 0; c < mag[0].length; c++) {
                var += (mag[r][c] - mean) * (mag[r][c] - mean);
            }
        }
        
        stDev = (int) Math.sqrt(var / (mag.length * mag[0].length));
    }
    
    private static void Direction(int[][] gx, int[][] gy) {
        dir = new int[gx.length][gx[0].length];
        
        for (int r = 0; r < gx.length; r++) {
            for (int c = 0; c < gx[0].length; c++) {
                double angle = Math.atan2(gy[r][c], gx[r][c]) * (180 / Math.PI);    //Convert radians to degrees
                
                if (angle < 0) {
                    angle += 360.;  //Check for negative angles
                }
                
                //Each pixels ACTUAL angle is examined and placed in 1 of four groups (for the four searched 45-degree neighbors)
                if (angle <= 22.5 || (angle >= 157.5 && angle <= 202.5) || angle >= 337.5) {
                    dir[r][c] = 0;      //Check left and right neighbors
                } else if ((angle >= 22.5 && angle <= 67.5) || (angle >= 202.5 && angle <= 247.5)) {
                    dir[r][c] = 45;     //Check diagonal (upper right and lower left) neighbors
                } else if ((angle >= 67.5 && angle <= 112.5) || (angle >= 247.5 && angle <= 292.5)) {
                    dir[r][c] = 90;     //Check top and bottom neighbors
                } else {
                    dir[r][c] = 135;    //Check diagonal (upper left and lower right) neighbors
                }
            }
        }
    }
    
    private static void Suppression() throws Exception {
        for (int r = 1; r < mag.length - 1; r++) {
            for (int c = 1; c < mag[0].length - 1; c++) {
                int direction = dir[r][c];
                
                if (direction == 0) {
                    if (mag[r][c] < mag[r][c - 1] && mag[r][c] < mag[r][c + 1]) {
                        mag [r - 1][c - 1] = 0;
                    }
                } else if (direction == 45) {
                    if (mag[r][c] < mag[r - 1][c + 1] && mag[r][c] < mag[r + 1][c - 1]) {
                        mag [r - 1][c - 1] = 0;
                    }
                } else if (direction == 90) {
                    if (mag[r][c] < mag[r - 1][c] && mag[r][c] < mag[r + 1][c]) {
                        mag [r - 1][c - 1] = 0;
                    }
                } else if (direction == 135) {
                    if (mag[r][c] < mag[r - 1][c - 1] && mag[r][c] < mag[r + 1][c + 1]) {
                        mag [r - 1][c - 1] = 0;
                    }
                } else {
                    throw new Exception("ERROR: Illegal edge direction!");
                }
            }
        }
    }
    
    private static int[][] Hysteresis() {
        int[][] bin = new int[mag.length - 2][mag[0].length - 2];
        tHi = mean + (numDev * stDev);  //Magnitude greater than or equal to high threshold is an edge pixel
        tLo = (int) (tHi * tFract);     //Magnitude less than low threshold not an edge, equal or greater possible edge
        
        for (int r = 1; r < mag.length - 1; r++) {
            for (int c = 1; c < mag[0].length - 1; c++) {
                if (mag[r][c] >= tHi) {
                    bin[r - 1][c - 1] = 255;
                } else if (mag[r][c] >= tLo) {
                    boolean connected = false;
                    
                    for (int nr = -1; nr < 2; nr++) {
                        for (int nc = -1; nc < 2; nc++) {
                            if (mag[r + nr][c + nc] >= tHi) {
                                connected = true;
                            }
                        }
                    }
                    
                    bin[r - 1][c - 1] = (connected) ? 255 : 0;
                } else {
                    bin[r - 1][c - 1] = 0;
                }
            }
        }
        
        return bin;
    }
}
