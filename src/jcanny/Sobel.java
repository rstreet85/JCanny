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
 * @author Robert Streetman
 */
package jcanny;

/**
 * Class has instructions for performing horizontal & vertical Sobel convolutions
 * on a grayscale image array
 * 
 */

public class Sobel {
    private static final int[][] MASKHORI = { {-1, 0, 1}, {-2, 0, 2}, {-1, 0, 1} };
    private static final int[][] MASKVERT = { {-1, -2, -1}, {0, 0, 0}, {1, 2, 1} };
    private static int[][] out;
    private static int height;
    private static int width;
    
    /*
     * Accepts int[][] array of pixel values, convolves with horizontal Sobel,
     * and returns int[][] array of pixel values.
     */
    public static int[][] Horizontal(int[][] raw) {
        height = raw.length;
        width = raw[0].length;
        
        if (height < 3 || width < 3) {
            throw new IllegalArgumentException("ERROR: Image too small for Sobel Mask!");
        }
        
        out = new int[height - 2][width - 2];
        
        for (int r = 1; r < height - 1; r++) {
            for (int c = 1; c < width - 1; c++) {
                int sum = 0;
                
                for (int kr = -1; kr < 2; kr++) {
                    for (int kc = -1; kc < 2; kc++) {
                        sum += (MASKHORI[kr + 1][kc + 1] * raw[r + kr][c + kc]);
                    }
                }
                
                out[r - 1][c - 1] = sum;
            }
        }
        
        return out;
    }
    
    /*
     * Accepts int[][] array of pixel values, convolves with vertical Sobel,
     * and returns int[][] array of pixel values.
     */
    public static int[][] Vertical(int[][] raw) {
        height = raw.length;
        width = raw[0].length;
        
        if (height < 3 || width < 3) {
            throw new IllegalArgumentException("ERROR: Image too small for Sobel Mask!");
        }
        
        out = new int[height - 2][width - 2];
        
        for (int r = 1; r < height - 1; r++) {
            for (int c = 1; c < width - 1; c++) {
                int sum = 0;
                
                for (int kr = -1; kr < 2; kr++) {
                    for (int kc = -1; kc < 2; kc++) {
                        sum += (MASKVERT[kr + 1][kc + 1] * raw[r + kr][c + kc]);
                    }
                }
                
                out[r - 1][c - 1] = sum;
            }
        }
        
        return out;
    }
}
