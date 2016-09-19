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
import java.io.File;
import javax.imageio.ImageIO;

public class Tester {
    
    private static final double CANNY_THRESHOLD_RATIO = 2.0;
    
    private static String imgFileName;
    private static String imgOutFile = "";
    private static String imgExt;

    public static void main(String[] args) {
        //Read input file name and create output file name
        imgFileName = args[0];
        imgExt = args[1];
        String[] arr = imgFileName.split("\\.");
        
        for (int i = 0; i < arr.length - 1; i++) {
            imgOutFile += arr[i];
        }
        
        imgOutFile += "_canny.";
        imgOutFile += imgExt;
        
        //Sample JCanny usage
        try {
            BufferedImage input = ImageIO.read(new File(imgFileName));
            BufferedImage output = JCanny.CannyEdges(input, CANNY_THRESHOLD_RATIO);
            ImageIO.write(output, imgExt, new File(imgOutFile));
        } catch (Exception ex) {
            System.out.println("ERROR ACCESING IMAGE FILE:\n" + ex.getMessage());
        }
    }
    
}
