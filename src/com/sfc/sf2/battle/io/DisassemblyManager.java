/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.battle.io;

import com.sfc.sf2.battle.AIPoint;
import com.sfc.sf2.battle.AIRegion;
import com.sfc.sf2.battle.Ally;
import com.sfc.sf2.battle.Enemy;
import com.sfc.sf2.battle.SpriteSet;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author wiz
 */
public class DisassemblyManager {
    

    public static SpriteSet importSpriteset(String spritesetPath){
        System.out.println("com.sfc.sf2.battle.io.DisassemblyManager.importAreas() - Importing disassembly ...");
        SpriteSet spriteset = new SpriteSet();
        try {
            int cursor = 0;
            Path spritesetpath = Paths.get(spritesetPath);
            byte[] data = Files.readAllBytes(spritesetpath);
            
            int alliesNumber = data[0];
            int enemiesNumber = data[1];
            int aiRegionsNumber = data[2];
            int aiPointsNumber = data[3];
            
            List<Ally> allyList = new ArrayList();
            for(int i=0;i<alliesNumber;i++){
                Ally newAlly = new Ally();
                newAlly.setIndex(data[4+i*12+0]);
                newAlly.setX(data[4+i*12+1]);
                newAlly.setY(data[4+i*12+2]);
                allyList.add(newAlly);
            }
            Ally[] allies = new Ally[allyList.size()];
            allies = allyList.toArray(allies);
            spriteset.setAllies(allies);
            
            List<Enemy> enemyList = new ArrayList();
            for(int i=0;i<enemiesNumber;i++){
                Enemy newEnemy = new Enemy();
                newEnemy.setIndex(data[4+alliesNumber*12+i*12+0]);
                newEnemy.setX(data[4+alliesNumber*12+i*12+1]);
                newEnemy.setY(data[4+alliesNumber*12+i*12+2]);
                newEnemy.setAi(data[4+alliesNumber*12+i*12+3]);
                newEnemy.setItem(getNextWord(data,4+alliesNumber*12+i*12+4));
                newEnemy.setMoveOrder1(data[4+alliesNumber*12+i*12+6]);
                newEnemy.setTriggerRegion(data[4+alliesNumber*12+i*12+7]);
                newEnemy.setByte8(data[4+alliesNumber*12+i*12+8]);
                newEnemy.setByte9(data[4+alliesNumber*12+i*12+9]);
                newEnemy.setByte10(data[4+alliesNumber*12+i*12+10]);
                newEnemy.setSpawnParams(data[4+alliesNumber*12+i*12+11]);
                enemyList.add(newEnemy);
            }
            Enemy[] enemies = new Enemy[enemyList.size()];
            enemies = enemyList.toArray(enemies);
            spriteset.setEnemies(enemies);

            List<AIRegion> aiRegionList = new ArrayList();
            for(int i=0;i<aiRegionsNumber;i++){
                AIRegion newAIRegion = new AIRegion();
                newAIRegion.setType(data[4+alliesNumber*12+enemiesNumber*12+i*12+0]);
                newAIRegion.setX1(data[4+alliesNumber*12+enemiesNumber*12+i*12+2]);
                newAIRegion.setY1(data[4+alliesNumber*12+enemiesNumber*12+i*12+3]);
                newAIRegion.setX2(data[4+alliesNumber*12+enemiesNumber*12+i*12+4]);
                newAIRegion.setY2(data[4+alliesNumber*12+enemiesNumber*12+i*12+5]);
                newAIRegion.setX3(data[4+alliesNumber*12+enemiesNumber*12+i*12+6]);
                newAIRegion.setY3(data[4+alliesNumber*12+enemiesNumber*12+i*12+7]);
                newAIRegion.setX4(data[4+alliesNumber*12+enemiesNumber*12+i*12+8]);
                newAIRegion.setY4(data[4+alliesNumber*12+enemiesNumber*12+i*12+9]);
                aiRegionList.add(newAIRegion);
            }
            AIRegion[] aiRegions = new AIRegion[aiRegionList.size()];
            aiRegions = aiRegionList.toArray(aiRegions);
            spriteset.setAiRegions(aiRegions);
            
            List<AIPoint> aiPointList = new ArrayList();
            for(int i=0;i<aiPointsNumber;i++){
                AIPoint newAIPoint = new AIPoint();
                newAIPoint.setX(data[4+alliesNumber*12+enemiesNumber*12+aiRegionsNumber*12+i*2+0]);
                newAIPoint.setY(data[4+alliesNumber*12+enemiesNumber*12+aiRegionsNumber*12+i*2+1]);
                aiPointList.add(newAIPoint);
            }
            AIPoint[] aiPoints = new AIPoint[aiPointList.size()];
            aiPoints = aiPointList.toArray(aiPoints);
            spriteset.setAiPoints(aiPoints);
            
        } catch (IOException ex) {
            Logger.getLogger(DisassemblyManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("com.sfc.sf2.battle.io.DisassemblyManager.importAreas() - Disassembly imported.");  
        return spriteset;
    }
    
    private static short getNextWord(byte[] data, int cursor){
        ByteBuffer bb = ByteBuffer.allocate(2);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.put(data[cursor+1]);
        bb.put(data[cursor]);
        short s = bb.getShort(0);
        //System.out.println("Next input word = $"+Integer.toString(s, 16)+" / "+Integer.toString(s, 2));
        return s;
    }    
    
    public static void exportSpriteSet(SpriteSet spriteset, String spritesetPath){
        System.out.println("com.sfc.sf2.battle.io.DisassemblyManager.exportSpriteSet() - Exporting disassembly ...");
        try { 
            byte[] spritesetBytes = produceSpriteSetBytes(spriteset);
            Path spritesetFilepath = Paths.get(spritesetPath);
            Files.write(spritesetFilepath,spritesetBytes);
            System.out.println(spritesetBytes.length + " bytes into " + spritesetFilepath);
        } catch (Exception ex) {
            Logger.getLogger(com.sfc.sf2.battle.io.DisassemblyManager.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
            System.out.println(ex);
        }            
        System.out.println("com.sfc.sf2.battle.io.DisassemblyManager.exportSpriteSet() - Disassembly exported.");     
    }
    
    private static byte[] produceSpriteSetBytes(SpriteSet spriteset){
        Ally[] allies = spriteset.getAllies();
        Enemy[] enemies = spriteset.getEnemies();
        AIRegion[] aiRegions = spriteset.getAiRegions();
        AIPoint[] aiPoints = spriteset.getAiPoints();
        
        int alliesNumber = allies.length;
        int enemiesNumber = enemies.length;
        int aiRegionsNumber = aiRegions.length;
        int aiPointsNumber = aiPoints.length;
        
        byte[] spritesetBytes = new byte[4+alliesNumber*12+enemiesNumber*12+aiRegionsNumber*12+aiPointsNumber*2];
        
        spritesetBytes[0] = (byte)alliesNumber;
        spritesetBytes[1] = (byte)enemiesNumber;
        spritesetBytes[2] = (byte)aiRegionsNumber;
        spritesetBytes[3] = (byte)aiPointsNumber;
        
        for(int i=0;i<alliesNumber;i++){
            Ally ally = allies[i];
            spritesetBytes[4+i*12+0] = (byte)ally.getIndex();
            spritesetBytes[4+i*12+1] = (byte)ally.getX();
            spritesetBytes[4+i*12+2] = (byte)ally.getY();
        }
        
        for(int i=0;i<enemiesNumber;i++){
            Enemy enemy = enemies[i];
            spritesetBytes[4+alliesNumber*12+i*12+0] = (byte)enemy.getIndex();
            spritesetBytes[4+alliesNumber*12+i*12+1] = (byte)enemy.getX();
            spritesetBytes[4+alliesNumber*12+i*12+2] = (byte)enemy.getY();
            spritesetBytes[4+alliesNumber*12+i*12+3] = (byte)enemy.getAi();
            spritesetBytes[4+alliesNumber*12+i*12+4] = (byte)(enemy.getItem()>>8);
            spritesetBytes[4+alliesNumber*12+i*12+5] = (byte)(enemy.getItem()&0xFF);
            spritesetBytes[4+alliesNumber*12+i*12+6] = (byte)enemy.getMoveOrder1();
            spritesetBytes[4+alliesNumber*12+i*12+7] = (byte)enemy.getTriggerRegion();
            spritesetBytes[4+alliesNumber*12+i*12+8] = (byte)enemy.getByte8();
            spritesetBytes[4+alliesNumber*12+i*12+9] = (byte)enemy.getByte9();
            spritesetBytes[4+alliesNumber*12+i*12+10] = (byte)enemy.getByte10();
            spritesetBytes[4+alliesNumber*12+i*12+11] = (byte)enemy.getSpawnParams();
        }
        
        for(int i=0;i<aiRegionsNumber;i++){
            AIRegion aiRegion = aiRegions[i];
            spritesetBytes[4+alliesNumber*12+enemiesNumber*12+i*12+0] = (byte)aiRegion.getType();
            spritesetBytes[4+alliesNumber*12+enemiesNumber*12+i*12+2] = (byte)aiRegion.getX1();
            spritesetBytes[4+alliesNumber*12+enemiesNumber*12+i*12+3] = (byte)aiRegion.getY1();
            spritesetBytes[4+alliesNumber*12+enemiesNumber*12+i*12+4] = (byte)aiRegion.getX2();
            spritesetBytes[4+alliesNumber*12+enemiesNumber*12+i*12+5] = (byte)aiRegion.getY2();
            spritesetBytes[4+alliesNumber*12+enemiesNumber*12+i*12+6] = (byte)aiRegion.getX3();
            spritesetBytes[4+alliesNumber*12+enemiesNumber*12+i*12+7] = (byte)aiRegion.getY3();
            spritesetBytes[4+alliesNumber*12+enemiesNumber*12+i*12+8] = (byte)aiRegion.getX4();
            spritesetBytes[4+alliesNumber*12+enemiesNumber*12+i*12+9] = (byte)aiRegion.getY4();
        }
        
        for(int i=0;i<aiPointsNumber;i++){
            AIPoint aiPoint = aiPoints[i];
            spritesetBytes[4+alliesNumber*12+enemiesNumber*12+aiRegionsNumber*12+i*2+0] = (byte)aiPoint.getX();
            spritesetBytes[4+alliesNumber*12+enemiesNumber*12+aiRegionsNumber*12+i*2+1] = (byte)aiPoint.getY();
        }

        return spritesetBytes;
    }
    
    public static byte[] importEnemySriteIDs(String mapspriteEnumPath, String filepath){
        System.out.println("com.sfc.sf2.battle.io.DisassemblyManager.importEnemySriteIDs() - Importing disassembly ...");
        byte[] data = null;
        try {
            if(filepath.endsWith(".bin")){
                Path filePath = Paths.get(filepath);
                data = Files.readAllBytes(filePath); 
            }else{
                Map<String, Integer> mapspriteEnum = new HashMap();
                File enumFile = new File(mapspriteEnumPath);
                Scanner enumScan = new Scanner(enumFile);
                while(enumScan.hasNext()){
                    String line = enumScan.nextLine();
                    if(line.trim().startsWith("; enum Mapsprites")){
                        line = enumScan.nextLine();
                        while(!line.startsWith("; enum")){
                            if(line.startsWith("MAPSPRITE")){
                                String key = line.substring(0,line.indexOf(":"));
                                Integer value = Integer.valueOf(line.substring(line.indexOf("$")+1).trim(), 16);
                                mapspriteEnum.put(key, value);
                            }
                            line = enumScan.nextLine();
                        }
                    }
                } 
                File file = new File(filepath);
                Scanner scan = new Scanner(file);
                List<Integer> values = new ArrayList();
                while(scan.hasNext()){
                    String line = scan.nextLine();
                    if(line.trim().startsWith("tbl_EnemyMapSprites:")){
                        while(scan.hasNext()){
                            line = scan.nextLine();
                            if(line.trim().startsWith("mapSprite")){
                                if(line.contains(";")){
                                    line = line.substring(0,line.indexOf(";"));
                                }
                                String value = line.trim().substring("mapSprite".length()).trim();
                                Integer val;
                                if(value.contains("$")||value.matches("[0-9]+")){
                                    val = valueOf(value);
                                }else{
                                    val = mapspriteEnum.get("MAPSPRITE_"+value);
                                }
                                values.add(val);
                            }
                        }
                    }
                } 
                data = new byte[values.size()];
                for(int i=0;i<data.length;i++){
                    data[i] = (byte)(values.get(i)&0xFF);
                }
            }           
        } catch (IOException ex) {
            Logger.getLogger(DisassemblyManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("com.sfc.sf2.battle.io.DisassemblyManager.importEnemySriteIDs() - Disassembly imported.");  
        return data;        
    }
    
    private static int valueOf(String s){
        s = s.trim();
        if(s.startsWith("$")){
            return Integer.valueOf(s.substring(1),16);
        }else{
            return Integer.valueOf(s);
        }
    }
}
