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

/**
 * This class contains methods for masking an image array with horizontal and vertical Sobel masks.
 * 
 * @author robert
 */

public class Sobel {
    //The masks for each Sobel convolution
    private static final int[][] MASK_H = { {-1, 0, 1}, {-2, 0, 2}, {-1, 0, 1} };
    private static final int[][] MASK_V = { {-1, -2, -1}, {0, 0, 0}, {1, 2, 1} };
    
    /**
     * Send this method an int[][] array of grayscale pixel values to get a an image resulting
     * from the convolution of this image with the horizontal Sobel mask.
     * 
     * @param raw   int[][], array of grayscale pixel values 0-255
     * @return out  int[][], output array of convolved image.
     */
    public static int[][] Horizontal(int[][] raw) {
        int[][] out = null;
        int height = raw.length;
        int width = raw[0].length;
        
        if (height > 2 && width > 2) {
            out = new int[height - 2][width - 2];
        
            for (int r = 1; r < height - 1; r++) {
                for (int c = 1; c < width - 1; c++) {
                    int sum = 0;

                    for (int kr = -1; kr < 2; kr++) {
                        for (int kc = -1; kc < 2; kc++) {
                            sum += (MASK_H[kr + 1][kc + 1] * raw[r + kr][c + kc]);
                        }
                    }

                    out[r - 1][c - 1] = sum;
                }
            }
        }
        
        return out;
    }
    
    /**
     * Send this method an int[][] array of grayscale pixel values to get a an image resulting
     * from the convolution of this image with the vertical Sobel mask.
     * 
     * @param raw   int[][], array of grayscale pixel values 0-255
     * @return out  int[][], output array of convolved image.
     */
    public static int[][] Vertical(int[][] raw) {
        int[][] out = null;
        int height = raw.length;
        int width = raw[0].length;
        
        if (height > 2 || width > 2) {
            out = new int[height - 2][width - 2];
        
            for (int r = 1; r < height - 1; r++) {
                for (int c = 1; c < width - 1; c++) {
                    int sum = 0;

                    for (int kr = -1; kr < 2; kr++) {
                        for (int kc = -1; kc < 2; kc++) {
                            sum += (MASK_V[kr + 1][kc + 1] * raw[r + kr][c + kc]);
                        }
                    }

                    out[r - 1][c - 1] = sum;
                }
            }
        }
        
        return out;
    }
}
