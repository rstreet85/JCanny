/**
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
 * 
 */
package jcanny;

import java.io.File;
import javax.imageio.ImageIO;

/**
 * This class contains methods for masking an image array with Gaussian masks.
 * 
 * @author robert
 */

public class Gaussian {
    private static final double SQRT2PI = Math.sqrt(2 * Math.PI);
    private static double[] mask;
    private static int height;
    private static int width;
    
    /**
     * 
     * @param raw       int[][][], an array of RGB values to be blurred
     * @param rad       int, the radius of the Gaussian filter (filter width = 2 * r + 1)
     * @param intens    double, the intensity of the Gaussian blur
     * @return outRGB   int[][][], an array of RGB values from blurring input image with Gaussian filter
     */
    public static int[][][] BlurRGB(int[][][] raw, int rad, double intens) {
        double intensSquared2 = 2 * intens * intens;
        double invIntensSqrPi = 1 / (SQRT2PI * intens);
        double norm = 0.;
        mask = new double[2 * rad + 1];
        height = raw.length;
        width = raw[0].length;
        int[][][] outRGB = new int[height - 2 * rad][width - 2 * rad][3];
        
        //Create Gaussian kernel
        for (int x = -rad; x < rad + 1; x++) {
            double exp = Math.exp(-((x * x) / intensSquared2));
            
            mask[x + rad] = invIntensSqrPi * exp;
            norm += mask[x + rad];
        }
        
        //Convolve image with kernel horizontally
        for (int r = rad; r < height - rad; r++) {
            for (int c = rad; c < width - rad; c++) {
                double[] sum = new double[3];
                
                for (int mr = -rad; mr < rad + 1; mr++) {
                    for (int chan = 0; chan < 3; chan++) {
                        sum[chan] += (mask[mr + rad] * raw[r][c + mr][chan]);
                    }
                }
                
                //Normalize channels after blur
                for (int chan = 0; chan < 3; chan++) {
                    sum[chan] /= norm;
                    outRGB[r - rad][c - rad][chan] = (int) Math.round(sum[chan]);
                }
            }
        }
        
        //Convolve image with kernel vertically
        for (int r = rad; r < height - rad; r++) {
            for (int c = rad; c < width - rad; c++) {
                double[] sum = new double[3];
                
                for (int mr = -rad; mr < rad + 1; mr++) {
                    for(int chan = 0; chan < 3; chan++) {
                        sum[chan] += (mask[mr + rad] * raw[r + mr][c][chan]);
                    }
                }
                
                //Normalize channels after blur
                for (int chan = 0; chan < 3; chan++) {
                    sum[chan] /= norm;
                    outRGB[r - rad][c - rad][chan] = (int) Math.round(sum[chan]);
                }
            }
        }
        
        return outRGB;
    }
    
    /**
     * 
     * @param raw       int[][], an array of grayscale values to be blurred
     * @param rad       int, the radius of the Gaussian filter (filter width = 2 * r + 1)
     * @param intens    double, the intensity of the Gaussian blur
     * @return outRGB   int[][], an array of grayscale values from blurring input image with Gaussian filter
     */
    public static int[][] BlurGS (int[][] raw, int rad, double intens) {
        height = raw.length;
        width = raw[0].length;
        
        //Bounds checking
        if (height < 2 * rad + 1 || width < 2 * rad + 1) {
            throw new IllegalArgumentException("ERROR: Image size too small for Gaussian blur!");
        }
        
        if (rad <= 0 || intens <= 0) {
            throw new IllegalArgumentException("ERROR: Illegal Gaussian filter parameters!");
        }
        
        mask = new double[2 * rad + 1];
        double norm = 0.;
        double invIntensSqrPi = 1 / (SQRT2PI * intens);
        int[][] outGS = new int[height - 2 * rad][width - 2 * rad];
        
        //Create Gaussian kernel
        for (int x = -rad; x < rad + 1; x++) {
            double exp = Math.exp(-((x * x) / (2 * intens * intens)));
            
            mask[x + rad] = invIntensSqrPi * exp;
            norm += mask[x + rad];
        }
        
        //Convolve image with kernel horizontally
        for (int r = rad; r < height - rad; r++) {
            for (int c = rad; c < width - rad; c++) {
                double sum = 0.;
                
                for (int mr = -rad; mr < rad + 1; mr++) {
                    sum += (mask[mr + rad] * raw[r][c + mr]);
                }
                
                //Normalize channel after blur
                sum /= norm;
                outGS[r - rad][c - rad] = (int) Math.round(sum);
            }
        }
        
        //Convolve image with kernel vertically
        for (int r = rad; r < height - rad; r++) {
            for (int c = rad; c < width - rad; c++) {
                double sum = 0.;
                
                for(int mr = -rad; mr < rad + 1; mr++) {
                    sum += (mask[mr + rad] * raw[r + mr][c]);
                }
                
                //Normalize channel after blur
                sum /= norm;
                outGS[r - rad][c - rad] = (int) Math.round(sum);
            }
        }
        
        return outGS;
    }
}
