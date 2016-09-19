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

public class Gaussian {
    
    /*
     * Accepts int[][][] array of RGB values, int blur radius, and double blur intensity.
     * Returns int[][][] array of RGB values.
     */
    public static int[][][] BlurRGB(int[][][] raw, int rad, double intens) {
        double[] mask = new double[2 * rad + 1];
        double norm = 0.;
        int[][][] outRGB = new int[raw.length - 2 * rad][raw[0].length - 2 * rad][3];
        
        for (int x = -rad; x < rad + 1; x++) {   //Create Gaussian mask
            double exp = Math.exp(-((x * x) / (2 * intens * intens)));
            
            mask[x + rad] = (1 / (Math.sqrt(2 * Math.PI) * intens)) * exp;
            norm += mask[x + rad];
        }
        
        for (int r = rad; r < raw.length - rad; r++) {   //Convolve image with mask horizontally
            for (int c = rad; c < raw[0].length - rad; c++) {
                double[] sum = new double[3];
                
                for (int mr = -rad; mr < rad + 1; mr++) {
                    for (int chan = 0; chan < 3; chan++) {
                        sum[chan] += (mask[mr + rad] * raw[r][c + mr][chan]);
                    }
                }
                
                for (int chan = 0; chan < 3; chan++) {
                    sum[chan] /= norm;
                    outRGB[r - rad][c - rad][chan] = (int) sum[chan];
                }
            }
        }
        
        for (int r = rad; r < raw.length - rad; r++) {   //Convolve image with mask vertically
            for (int c = rad; c < raw[0].length - rad; c++) {
                double[] sum = new double[3];
                
                for (int mr = -rad; mr < rad + 1; mr++) {
                    for(int chan = 0; chan < 3; chan++) {
                        sum[chan] += (mask[mr + rad] * raw[r + mr][c][chan]);
                    }
                }
                
                for (int chan = 0; chan < 3; chan++) {
                    sum[chan] /= norm;
                    outRGB[r - rad][c - rad][chan] = (int) sum[chan];
                }
            }
        }
        
        return outRGB;
    }
    
    /*
     * Accepts int[][] array of GS values, int blur radius, and double blur intensity.
     * Returns int[][] array of GS values.
     */
    public static int[][] BlurGS (int[][] raw, int rad, double intens) {
        if (raw.length < 2 * rad + 1 || raw[0].length < 2 * rad + 1) {
            throw new IllegalArgumentException("ERROR: Image size too small for Gaussian blur!");
        }
        
        if (rad <= 0 || intens <= 0) {
            throw new IllegalArgumentException("ERROR: Image size too small for Gaussian blur!");
        }
        
        double[] mask = new double[2 * rad + 1];
        double norm = 0.;
        int[][] outGS = new int[raw.length - 2 * rad][raw[0].length - 2 * rad];
        
        for (int x = -rad; x < rad + 1; x++) {   //Create Gaussian mask
            double exp = Math.exp(-((x * x) / (2 * intens * intens)));
            
            mask[x + rad] = (1 / (Math.sqrt(2 * Math.PI) * intens)) * exp;
            norm += mask[x + rad];
        }
        
        for (int r = rad; r < raw.length - rad; r++) {   //Convolve image with mask horizontally
            for (int c = rad; c < raw[0].length - rad; c++) {
                double sum = 0.;
                
                for (int mr = -rad; mr < rad + 1; mr++) {
                    sum += (mask[mr + rad] * raw[r][c + mr]);
                }
                
                sum /= norm;
                outGS[r - rad][c - rad] = (int) sum;
            }
        }
        
        for (int r = rad; r < raw.length - rad; r++) {   //Convolve image with mask vertically
            for (int c = rad; c < raw[0].length - rad; c++) {
                double sum = 0.;
                
                for(int mr = -rad; mr < rad + 1; mr++) {
                    sum += (mask[mr + rad] * raw[r + mr][c]);
                }
                
                sum /= norm;
                outGS[r - rad][c - rad] = (int) sum;
            }
        }
        
        return outGS;
    }
}
