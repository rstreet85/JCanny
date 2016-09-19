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

public class ImgIO {
    
    /*
     * Accepts BufferedImage, returns int[][][] array of RGB (0-255) values.
     */
    public static int[][][] RGBArray(BufferedImage img) {
        if (img == null) {
            throw new IllegalArgumentException("ERROR: Source image is null!");
        }
        
        int[][][] rgb = new int[img.getHeight()][img.getWidth()][3];
        
        for (int i = 0; i < rgb.length; i++) {
            for (int j = 0; j < rgb[0].length; j++) {
                rgb[i][j] = intRGB(img.getRGB(j, i));
            }
        }
        
        return rgb;
    }
    
    /*
     * Accepts int[][][] array of RGB values, returns BufferedImage
     */
    public static BufferedImage RGBImg(int[][][] raw) {
        if (raw.length < 1 || raw[0].length < 1 || raw[0][0].length != 3) {
            throw new IllegalArgumentException("ERROR: Malformed RGB array!");
        }
        
        BufferedImage img = new BufferedImage(raw[0].length, raw.length, BufferedImage.TYPE_INT_RGB);
        
        for (int i = 0; i < raw.length; i++) {
            for (int j = 0; j < raw[0].length; j++) {
                img.setRGB(j, i, (raw[i][j][0] << 16) | (raw[i][j][1] << 8) | (raw[i][j][2]));
            }
        }
        
        return img;
    }
    
    /*
     * Accepts BufferedImage, returns int[][] array of grayscale (0-255) values
     */
    public static int[][] GSArray(BufferedImage img) {
        if (img == null) {
            throw new IllegalArgumentException("ERROR: Source image is null!");
        }
        
        int[][] gs = new int[img.getHeight()][img.getWidth()];
        
        for (int i = 0; i < gs.length; i++) {
            for (int j = 0; j < gs[0].length; j++) {
                gs[i][j] = intGS(img.getRGB(j, i));
            }
        }
        
        return gs;
    }
    
    /*
     * Accepts int[][] array of grayscale values, returns BufferedImage
     */
    public static BufferedImage GSImg(int[][] raw) {
        if (raw.length < 1 || raw[0].length < 1) {
            throw new IllegalArgumentException("ERROR: Malformed grayscale array!");
        }
        
        BufferedImage img = new BufferedImage(raw[0].length, raw.length, BufferedImage.TYPE_INT_RGB);
        
        for (int i = 0; i < raw.length; i++) {
            for (int j = 0; j < raw[0].length; j++) {
                img.setRGB(j, i, (raw[i][j] << 16) | (raw[i][j] << 8) | (raw[i][j]));
            }
        }
        
        return img;
    }
    
    /*
     * Accepts BufferedImage, returns double[][][] array of HSV values
     */
    public static double[][][] HSVArray(BufferedImage img) {
        if (img == null) {
            throw new IllegalArgumentException("ERROR: Source image is null!");
        }
        
        double[][][] hsv = new double[img.getHeight()][img.getWidth()][];
        
        for (int r = 0; r < hsv.length; r++) {
            for (int c = 0; c < hsv[0].length; c++) {
                int[] rgb = intRGB(img.getRGB(c, r));
                double cmax, cmin;                  //HSV based on range between highest & lowest intensity
                double[] rgbprime = new double[3];  //RGB values scaled from 0-255 to 0-1
                double[] pixelHSV = new double[3];
             
                
                for (int i = 0; i < rgb.length; i++) {
                    rgbprime[i] = (double) rgb[i] / 255.;
                }
                
                //IF r' > g' AND r' > b'
                if (rgbprime[0] > rgbprime[1] && rgbprime[0] > rgbprime[2]) {
                    cmax = rgbprime[0];
                    cmin = (rgbprime[1] < rgbprime[2]) ? rgbprime[1] : rgbprime[2];
                    pixelHSV[0] = 60 * (((rgbprime[1] - rgbprime[2]) / (cmax - cmin)) % 6);
                //IF g' > r' AND g' > b'
                } else if (rgbprime[1] > rgbprime[0] && rgbprime[1] > rgbprime[2]) {
                    cmax = rgbprime[1];
                    cmin = (rgbprime[0] < rgbprime[2]) ? rgbprime[0] : rgbprime[2];
                    pixelHSV[0] = 60 * (((rgbprime[2] - rgbprime[0]) / (cmax - cmin))  + 2);
                //IF b' > g' AND b' > r'
                } else if (rgbprime[2] > rgbprime[1] && rgbprime[2] > rgbprime[0]){
                    cmax = rgbprime[2];
                    cmin = (rgbprime[0] < rgbprime[1]) ? rgbprime[0] : rgbprime[1];
                    pixelHSV[0] = 60 * (((rgbprime[0] - rgbprime[1]) / (cmax - cmin))  + 4);
                //If the pixel's RGB value was 0,0,0 then HSV is 0,0,0
                } else {
                    cmax = 0;
                    cmin = 0;
                    pixelHSV[0] = 0;
                }
                
                pixelHSV[1] = (cmax == 0) ? 0 : (cmax - cmin) / cmax;
                pixelHSV[2] = cmax;
                hsv[r][c] = pixelHSV;
            }
        }
        
        return hsv;
    }
    
    /*
     * Accepts BufferedImage, returns double[][][] array of HSI values
     */
    public static double[][][] HSIArray(BufferedImage img) {
        if (img == null) {
            throw new IllegalArgumentException("ERROR: Source image is null!");
        }
        
        double[][][] hsi = new double[img.getHeight()][img.getWidth()][];
        
        for (int r = 0; r < hsi.length; r++) {
            for (int c = 0; c < hsi[0].length; c++) {
                int[] rgb = intRGB(img.getRGB(c, r));
                double[] pixelHSI = new double[3];
                double cos1 = rgb[0] - 0.5 * rgb[1] - 0.5 * rgb[2];
                double cos2 = Math.sqrt(rgb[0] * rgb[0] + rgb[1] * rgb[1] + rgb[2] * rgb[2]
                        - rgb[0] * rgb[1] - rgb[0] * rgb[2] - rgb[1] * rgb[2]);
                pixelHSI[0] = Math.acos(cos1 / cos2);
                pixelHSI[0] *= (180 / Math.PI);
                
                if (rgb[1] < rgb[2]) {
                    pixelHSI[0] = 360 - pixelHSI[0];
                }
                
                pixelHSI[2] = (rgb[0] + rgb[1] + rgb[2]) / 3.0;
                pixelHSI[1] = 1 - Min(rgb[0], rgb[1], rgb[2]) / pixelHSI[2];
                hsi[r][c] = pixelHSI;
            }
        }
        
        return hsi;
    }
    
    /*
     * Accepts BufferedImage, returns double[][][] array of TSL values
     */
    public static double[][][] TSLArray(BufferedImage img) {
        if (img == null) {
            throw new IllegalArgumentException("ERROR: Source image is null!");
        }
        
        double[][][] tsl = new double[img.getHeight()][img.getWidth()][];
        
        for (int r = 0; r < tsl.length; r++) {
            for (int c = 0; c < tsl[0].length; c++) {
                int[] rgb = intRGB(img.getRGB(c, r));
                double sum = rgb[0] + rgb[1] + rgb[2];
                double gPrime = (double) rgb[1] / sum;
                double[] pixelTSL = new double[3];
                
                if (gPrime > 0.1) {
                    pixelTSL[0] = (Math.atan2((rgb[0] / sum), gPrime) / (2 * Math.PI)) + 0.25;
                } else if (gPrime < -0.1) {
                    pixelTSL[0] = (Math.atan2((rgb[0] / sum), gPrime) / (2 * Math.PI)) + 0.75;
                } else {    //gPrime == 0
                    pixelTSL[0] = 0;
                }
                
                pixelTSL[1] = Math.sqrt(1.8 * ((rgb[0] / sum) - .3333) * ((rgb[0] / sum) - .3333) + 
                        ((rgb[1] / sum) - .3333) * ((rgb[1] / sum) - .3333));
                pixelTSL[2] = 0.299 * rgb[0] + 0.587 * rgb[1] + 0.114 * rgb[2];
                tsl[r][c] = pixelTSL;
            }
        }
        
        return tsl;
    }
    
    /*
     * Accepts double[][][] array of HSV values, returns BufferedImage
     */
    public static BufferedImage HSVImg(double[][][] raw) {
        if (raw.length < 1 || raw[0].length < 1 || raw[0][0].length != 3) {
            throw new IllegalArgumentException("ERROR: Malformed RGB array!");
        }
        
        BufferedImage img = new BufferedImage(raw[0].length, raw.length, BufferedImage.TYPE_INT_RGB);
        
        for (int r = 0; r < raw.length; r++) {
            for (int c = 0; c < raw[0].length; c++) {
                int[] rgb = new int [3];
                
                rgb[0] = (int) raw[r][c][0] * 255;
                rgb[1] = (int) raw[r][c][1] * 255;
                rgb[2] = (int) ((raw[r][c][2] / 360) * raw[r][c][2]);
                
                img.setRGB(c, r, (rgb[0] << 16) | (rgb[1] << 8) | (rgb[2]));
            }
        }
        
        return img;
    }
    
    /*
     * Accepts double[][][] array of HSI values, returns BufferedImage
     */
    public static BufferedImage HSIImg(double[][][] raw) {
        if (raw.length < 1 || raw[0].length < 1 || raw[0][0].length != 3) {
            throw new IllegalArgumentException("ERROR: Malformed RGB array!");
        }
        
        BufferedImage img = new BufferedImage(raw[0].length, raw.length, BufferedImage.TYPE_INT_RGB);
        
        for (int r = 0; r < raw.length; r++) {
            for (int c = 0; c < raw[0].length; c++) {
                int[] rgb = new int [3];
                
                rgb[0] = (int) raw[r][c][0] * 255;
                rgb[1] = (int) raw[r][c][1] * 255;
                rgb[2] = (int) ((raw[r][c][2] / 360) * raw[r][c][2]);
                
                img.setRGB(c, r, (rgb[0] << 16) | (rgb[1] << 8) | (rgb[2]));
            }
        }
        
        return img;
    }    
    
    /*
     * Accepts BufferedImage, returns double[][][] array of YCbCr values
     */
    public static double[][][] YCbCrArray(BufferedImage img) {
        if (img == null) {
            throw new IllegalArgumentException("ERROR: Source image is null!");
        }
        
        double[][][] out = new double[img.getHeight()][img.getWidth()][3];
        
        for (int i = 0; i < out.length; i++) {
            for (int j = 0; j < out[0].length; j++) {
                int[] rgb = intRGB(img.getRGB(j, i));
                //Y
                out[i][j][0] = 16 + (0.2568 * rgb[0] + 0.5022 * rgb[1] + 0.0975 * rgb[2]);
                //Cb
                out[i][j][1] = 128 + (-0.1476 * rgb[0] + -0.2899 * rgb[1] + 0.4375 * rgb[2]);
                //Cr
                out[i][j][2] = 128 + (0.4375 * rgb[0] + -0.3663 * rgb[1] + -0.0711 * rgb[2]);
            }
        }       
        
        return out;
    }
    
    /*
     * Accepts 32-bit pixel value from BufferedImage, returns int[][][] array of RGB values
     */
    private static int[] intRGB(int bits) {
        int[] out = { (bits >> 16) & 0xff, (bits >> 8) & 0xff, bits & 0xff };
        return out;
    }
    
    /*
     * Accepts 32-bit pixel value from BufferedImage, returns int[][] array of grayscale values
     */
    private static int intGS(int bits) {
        return (((bits >> 16) & 0xff) + ((bits >> 8) & 0xff) + (bits & 0xff)) / 3;
    }
    
    /*
     * Accepts 3 integer values, returns double of lowest value
     */
    private static double Min(int a, int b, int c) {
        if (a <= b && a <= c) {
            return (double) a;
        } else if (b <= c && b <= a) {
            return (double) b;
        } else {
            return (double) c;
        }
    }
}
