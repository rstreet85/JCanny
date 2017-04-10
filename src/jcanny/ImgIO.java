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

import java.awt.image.BufferedImage;

/**
 * This class contains utility methods for transforming image data.
 * 
 * @author robert
 */

public class ImgIO {
    
    /**
     * Send this method a BufferedImage to get an RGB array (int, value 0-255).
     * 
     * @param img   BufferedImage, the input image from which to extract RGB
     * @return rgb  int[][][], a 3-dimension array of RGB values from image
     * 
     */
    public static int[][][] RGBArray(BufferedImage img) {
        int[][][] rgb = null;
        int height = img.getHeight();
        int width = img.getWidth();
        
        if (height > 0 && width > 0) {
            rgb = new int[height][width][3];

            for (int row = 0; row < height; row++) {
                for (int column = 0; column < width; column++) {
                    rgb[row][column] = intRGB(img.getRGB(column, row));
                }
            }
        }
        
        return rgb;
    }
    
    /**
     * Send this method an array of RGB pixels (int) to get a BufferedImage.
     * 
     * @param raw   int[][][] representing RGB pixels of image.
     * @return img  BufferedImage built from RGB array
     */
    public static BufferedImage RGBImg(int[][][] raw) {
        BufferedImage img = null;
        int height = raw.length;
        int width = raw[0].length;
        
        if (height > 0 && width > 0 || raw[0][0].length == 3) {
            img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

            for (int row = 0; row < height; row++) {
                for (int column = 0; column < width; column++) {
                    img.setRGB(column, row, (raw[row][column][0] << 16) | (raw[row][column][1] << 8) | (raw[row][column][2]));
                }
            }
        }
        
        return img;
    }
    
    /**
     * Send this method a BufferedImage to get a grayscale array (int, value 0-255.
     * 
     * @param img   BufferedImage, the input image from which to extract grayscale
     * @return gs   int[][] array of grayscale pixel values from image.
     */
    public static int[][] GSArray(BufferedImage img) {
        int[][] gs = null;
        int height = img.getHeight();
        int width = img.getWidth();
        
        if (height > 0 && width > 0) {
            gs = new int[height][width];

            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    int bits = img.getRGB(j, i);
                    //Not sure if precision is needed, but adding for now
                    long avg = Math.round((((bits >> 16) & 0xff) + ((bits >> 8) & 0xff) + (bits & 0xff)) / 3.0);
                    gs[i][j] = (int) avg;
                }
            }
        }
        
        return gs;
    }
    
    /**
     * Send this method an array of grayscale pixels (int) to get a BufferedImage
     * 
     * @param raw   int[][] representing grayscale pixels of image.
     * @return img  BufferedImage built from grayscale array 
     */
    public static BufferedImage GSImg(int[][] raw) {
        BufferedImage img = null;
        int height = raw.length;
        int width = raw[0].length;
        
        if (height > 0 && width > 0) {
            img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    img.setRGB(j, i, (raw[i][j] << 16) | (raw[i][j] << 8) | (raw[i][j]));
                }
            }
        }
        
        return img;
    }
    
    /*
     * Accepts BufferedImage, returns double[][][] array of HSV values
     */
    /**
     * Currently unsupported - Send this method a BufferedImage to get a double[][][] HSV array.
     * 
     * @deprecated
     */
    public static double[][][] HSVArray(BufferedImage img) {
        if (img == null) {
            throw new IllegalArgumentException("ERROR: Source image is null!");
        }
        
        int height = img.getHeight();
        int width = img.getWidth();
        double[][][] hsv = new double[height][width][];
        
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                int[] rgb = intRGB(img.getRGB(c, r));
                double cmax, cmin;                  //Min & Max intensity values
                double[] rgbprime = new double[3];  //RGB values scaled from 0-255 to 0-1
                double[] pixelHSV = new double[3];  //HSV value for pixel
                
                for (int i = 0; i < 3; i++) {
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
    /**
     * Currently unsupported - Send this method a BufferedImage to get a double[][][] HSI array.
     * 
     * @deprecated
     */
    public static double[][][] HSIArray(BufferedImage img) {
        if (img == null) {
            throw new IllegalArgumentException("ERROR: Source image is null!");
        }
        
        int height = img.getHeight();
        int width = img.getWidth();
        double[][][] hsi = new double[height][width][];
        double piRad = 180 / Math.PI;
        
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                int[] rgb = intRGB(img.getRGB(c, r));
                double[] pixelHSI = new double[3];
                double cos1 = rgb[0] - 0.5 * rgb[1] - 0.5 * rgb[2];
                double cos2 = Math.sqrt(rgb[0] * rgb[0] + rgb[1] * rgb[1] + rgb[2] * rgb[2]
                        - rgb[0] * rgb[1] - rgb[0] * rgb[2] - rgb[1] * rgb[2]);
                
                pixelHSI[0] = Math.acos(cos1 / cos2) * piRad;
                
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
    /**
     * Currently unsupported - Send this method a BufferedImage to get a double[][][] TSL array.
     * 
     * @deprecated
     */
    public static double[][][] TSLArray(BufferedImage img) {
        if (img == null) {
            throw new IllegalArgumentException("ERROR: Source image is null!");
        }
        
        int height = img.getHeight();
        int width = img.getWidth();
        double[][][] tsl = new double[height][width][];
        
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
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
    /**
     * Currently unsupported - Send this method a double[][][] HSV array to get an RGB BufferedImage.
     * 
     * @deprecated
     */
    public static BufferedImage HSVImg(double[][][] raw) {
        int height = raw.length;
        int width = raw[0].length;
        
        if (height < 1 || width < 1 || raw[0][0].length != 3) {
            throw new IllegalArgumentException("ERROR: Malformed RGB array!");
        }
        
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
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
    /**
     * Currently unsupported - Send this method a double[][][] HSI array to get an RGB BufferedImage.
     * 
     * @deprecated
     */
    public static BufferedImage HSIImg(double[][][] raw) {
        int height = raw.length;
        int width = raw[0].length;
        
        if (height < 1 || width < 1 || raw[0][0].length != 3) {
            throw new IllegalArgumentException("ERROR: Malformed RGB array!");
        }
        
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
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
    /**
     * Currently unsupported - Send this method a BufferedImage to get a double[][][] YCbCr array.
     * 
     * @deprecated
     */
    public static double[][][] YCbCrArray(BufferedImage img) {
        if (img == null) {
            throw new IllegalArgumentException("ERROR: Source image is null!");
        }
        
        int height = img.getHeight();
        int width = img.getWidth();
        double[][][] out = new double[height][width][3];
        
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
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
    
    /**
     * Send this method a 32-bit pixel value from BufferedImage to get the RGB
     * 
     * @param bits  int, 32-bit BufferedImage pixel value
     * @return rgb  int[], RGB values extracted from pixel  
     */
    private static int[] intRGB(int bits) {
        int[] rgb = { (bits >> 16) & 0xff, (bits >> 8) & 0xff, bits & 0xff };
        
        //Don't propagate bad pixel values
        for (int i = 0; i < 3; i++) {
            if (rgb[i] < 0) {
                rgb[i] = 0;
            } else if (rgb[i] > 255) {
                rgb[i] = 255;
            }
        }
        
        return rgb;
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
