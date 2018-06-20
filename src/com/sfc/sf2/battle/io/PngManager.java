/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.battle.io;

import com.sfc.sf2.map.layout.MapLayout;
import com.sfc.sf2.map.layout.layout.MapLayoutLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 *
 * @author wiz
 */
public class PngManager {
    
    
    public static void exportPng(MapLayout mapLayout, String filepath){
        try {
            //System.out.println("com.sfc.sf2.maplayout.io.PngManager.exportPng() - Exporting PNG files ...");
            //writePngFile(maplayout.getTiles(),filepath);    
            //System.out.println("com.sfc.sf2.maplayout.io.PngManager.exportPng() - PNG files exported.");
        } catch (Exception ex) {
            Logger.getLogger(PngManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        
                
    }    
    
    public static void writePngFile(MapLayout mapLayout, String filepath){
        try {
            //System.out.println("com.sfc.sf2.maplayout.io.PngManager.exportPng() - Exporting PNG file ...");
            BufferedImage image = new MapLayoutLayout().buildImage(mapLayout, 8, true);
            File outputfile = new File(filepath);
            //System.out.println("File path : "+outputfile.getAbsolutePath());
            ImageIO.write(image, "png", outputfile);
            System.out.println("PNG file exported : " + outputfile.getAbsolutePath());
        } catch (Exception ex) {
            Logger.getLogger(PngManager.class.getName()).log(Level.SEVERE, null, ex);
        }       
    }
       
    
}
