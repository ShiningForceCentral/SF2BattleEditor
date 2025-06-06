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
import com.sfc.sf2.battle.EnemyData;
import com.sfc.sf2.battle.EnemyEnums;
import com.sfc.sf2.battle.SpriteSet;
import com.sfc.sf2.mapsprite.MapSprite;
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
    
    private static final String MACRO_HEADER = "BattleSpriteset";
    private static final String MACRO_ALLIES = "allyCombatant";
    private static final String MACRO_ENEMIES = "enemyCombatant";
    private static final String MACRO_DCB = "dc.b";
    private static final String MACRO_REGIONS = "; AI Regions";
    private static final String MACRO_ENEMY_LINE2 = "combatantAiAndItem";
    private static final String MACRO_ENEMY_LINE3 = "combatantBehavior";
    
    private static String header;
    
    public static SpriteSet importSpriteset(String spritesetPath, EnemyData[] enemyData, EnemyEnums enemyEnums){
        System.out.println("com.sfc.sf2.battle.io.DisassemblyManager.importAreas() - Importing disassembly ...");
        SpriteSet spriteset = null;
        if(spritesetPath.endsWith(".asm")){
            spriteset = importSpritesetAsm(spritesetPath, enemyData, enemyEnums);
        }else{
            spriteset = importSpritesetBin(spritesetPath, enemyData, enemyEnums);
        }
        System.out.println("com.sfc.sf2.battle.io.DisassemblyManager.importAreas() - Disassembly imported.");  
        return spriteset;
    }    

    public static SpriteSet importSpritesetAsm(String spritesetPath, EnemyData[] enemyData, EnemyEnums enemyEnums){
        System.out.println("com.sfc.sf2.battle.io.DisassemblyManager.importAreas() - Importing disassembly ...");
        SpriteSet spriteset = new SpriteSet();
        
        List<Ally> allyList = new ArrayList();            
        List<Enemy> enemyList = new ArrayList();
        List<AIRegion> aiRegionList = new ArrayList();
        List<AIPoint> aiPointList = new ArrayList();
        
        try{
            File file = new File(spritesetPath);
            Scanner scan = new Scanner(file);
            boolean inHeader = true;
            boolean parsedSizes = false;
            boolean parsingRegions = false;
            header = "";
            while(scan.hasNext()){
                String line = scan.nextLine();
                if(parsedSizes && line.trim().startsWith(MACRO_DCB)){
                    if (parsingRegions){
                        String[] params = line.trim().substring(MACRO_DCB.length()).trim().split(",");
                        
                        AIRegion newAIRegion = new AIRegion();
                        //Line 1
                        newAIRegion.setType(Integer.valueOf(params[0].trim()));
                        //Line 2 (Ignore)
                        if (scan.hasNext()){ scan.nextLine(); }
                        //Line 3
                        if (scan.hasNext()){
                            line = scan.nextLine();
                            params = line.trim().substring(MACRO_DCB.length()).trim().split(",");
                            newAIRegion.setX1(Integer.valueOf(params[0].trim()));
                            newAIRegion.setY1(Integer.valueOf(params[1].trim()));
                        }
                        //Line 4
                        if (scan.hasNext()){
                            line = scan.nextLine();
                            params = line.trim().substring(MACRO_DCB.length()).trim().split(",");
                            newAIRegion.setX2(Integer.valueOf(params[0].trim()));
                            newAIRegion.setY2(Integer.valueOf(params[1].trim()));
                        }
                        //Line 5
                        if (scan.hasNext()){
                            line = scan.nextLine();
                            params = line.trim().substring(MACRO_DCB.length()).trim().split(",");
                            newAIRegion.setX3(Integer.valueOf(params[0].trim()));
                            newAIRegion.setY3(Integer.valueOf(params[1].trim()));
                        }
                        //Line 6
                        if (scan.hasNext()){
                            line = scan.nextLine();
                            params = line.trim().substring(MACRO_DCB.length()).trim().split(",");
                            newAIRegion.setX4(Integer.valueOf(params[0].trim()));
                            newAIRegion.setY4(Integer.valueOf(params[1].trim()));
                        }
                        //Line 7 & 8 (Ignore)
                        if (scan.hasNext()){ scan.nextLine(); }
                        if (scan.hasNext()){ scan.nextLine(); }
                        
                        aiRegionList.add(newAIRegion);
                    }
                    else
                    {
                        String[] params = line.trim().substring(MACRO_DCB.length()).trim().split(",");
                        if (params.length == 2){
                            AIPoint newPoint = new AIPoint();
                            newPoint.setX(Integer.valueOf(params[0].trim()));
                            newPoint.setY(Integer.valueOf(params[1].trim()));
                            aiPointList.add(newPoint);
                        }
                    }
                }
                else if(line.trim().startsWith(MACRO_ALLIES)){
                    inHeader = false;
                    parsedSizes = true;
                    
                    /*
                    index (0 to $B), X, Y
                    Unused, Unused, Unused
                    Unused, Unused, Unused, Unused, Unused, Unused, Unused
                    */
                    
                    String[] params = line.trim().substring(MACRO_ALLIES.length()).trim().split(",");
                    int index = Integer.valueOf(params[0].trim());
                    int x = Integer.valueOf(params[1].trim());
                    int y = Integer.valueOf(params[2].trim());
                    Ally newAlly = new Ally();
                    newAlly.setIndex(index);
                    newAlly.setX(x);
                    newAlly.setY(y);
                    allyList.add(newAlly);
                    
                    //AI and behaviour lines not relevant for allies, so skip them
                    if (scan.hasNext()){ scan.nextLine(); }
                    if (scan.hasNext()){ scan.nextLine(); }
                }
                else if(line.trim().startsWith(MACRO_ENEMIES)){
                    inHeader = false;
                    parsedSizes = true;
                    
                    /*
                    index, X, Y
                    aiType, extraItem
                    moveOrder1, region1, moveOrder2, region2, unknown, spawnParams
                    */
                    
                    String[] params = line.trim().substring(MACRO_ENEMIES.length()).trim().split(",");
                    int x = 0, y = 0, region1 = 0, region2 = 0, unknownParam = 0;
                    String name, aiCommand = null, item = null, moveOrder1 = null, moveOrder2 = null, spawnParams = null;
                                          
                    //Line 1
                    name = params[0].trim();
                    x = Integer.valueOf(params[1].trim());
                    y = Integer.valueOf(params[2].trim());
                    
                    //Line 2
                    if (scan.hasNext()){
                        scan.nextLine();
                        
                        //TODO new datatype
                        params = line.trim().substring("MACRO_ENEMY_LINE2".length()).trim().split(",");
                        aiCommand = EnemyEnums.toEnumString(params[0].trim(), enemyEnums.getCommandSets());
                        item = EnemyEnums.stringToItemString(params[1].trim(), enemyEnums.getItems());
                    }
                          
                    //Line 3
                    if (scan.hasNext()){
                        scan.nextLine();
                        
                        //TODO new datatype
                        params = line.trim().substring("MACRO_ENEMY_LINE3".length()).trim().split(",");
                        moveOrder1 = EnemyEnums.stringToAiOrderString(params[0].trim(), enemyEnums.getOrders());
                        region1 = Integer.valueOf(params[1].trim());
                        moveOrder2 = EnemyEnums.stringToAiOrderString(params[2].trim(), enemyEnums.getOrders());
                        region2 = Integer.valueOf(params[3].trim());
                        unknownParam = Integer.valueOf(params[4].trim());
                        spawnParams = EnemyEnums.toEnumString(params[5].trim(), enemyEnums.getSpawnParams());
                    }
                    
                    Enemy newEnemy = new Enemy();
                    
                    boolean matchFound = false;
                    for (int e = 0; e < enemyData.length; e++) {
                        if (enemyData[e] != null && enemyData[e].getName().equals(name)){
                            newEnemy.setEnemyData(enemyData[e]);
                            matchFound = true;
                            break;
                        }
                    }
                    if (!matchFound){
                        EnemyData placeholder = new EnemyData();
                        placeholder.setName(name);
                        placeholder.setID(-1);
                        newEnemy.setEnemyData(placeholder);
                    }
                    
                    newEnemy.setX(x);
                    newEnemy.setY(y);
                    newEnemy.setAi(aiCommand);
                    newEnemy.setItem(item);
                    newEnemy.setMoveOrder1(moveOrder1);
                    newEnemy.setTriggerRegion1(region1);
                    newEnemy.setMoveOrder2(moveOrder2);
                    newEnemy.setTriggerRegion2(region2);
                    newEnemy.setByte10(unknownParam);
                    newEnemy.setSpawnParams(spawnParams);
                    enemyList.add(newEnemy);
                }
                else if(inHeader){
                    header+=line;
                    header+="\n";

                    if (line.trim().startsWith(MACRO_HEADER))
                        inHeader = false;
                }
                else if(line.trim().startsWith(";")){
                    parsingRegions = (line.trim().equals(MACRO_REGIONS));
                }
            }
            
            Ally[] allies = new Ally[allyList.size()];
            allies = allyList.toArray(allies);
            spriteset.setAllies(allies);
            
            Enemy[] enemies = new Enemy[enemyList.size()];
            enemies = enemyList.toArray(enemies);
            spriteset.setEnemies(enemies);
            
            AIRegion[] aiRegions = new AIRegion[aiRegionList.size()];
            aiRegions = aiRegionList.toArray(aiRegions);
            spriteset.setAiRegions(aiRegions);
            
            AIPoint[] aiPoints = new AIPoint[aiPointList.size()];
            aiPoints = aiPointList.toArray(aiPoints);
            spriteset.setAiPoints(aiPoints);
            
        }catch(Exception e){
             System.err.println("com.sfc.sf2.battle.mapcoords.io.DisassemblyManager.importDisassembly() - Error while parsing graphics data : "+e);
             e.printStackTrace();
        }
        
        System.out.println("com.sfc.sf2.battle.io.DisassemblyManager.importAreas() - Disassembly imported.");  
        return spriteset;
    }

    public static SpriteSet importSpritesetBin(String spritesetPath, EnemyData[] enemyData, EnemyEnums enemyEnums){
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
                
                Integer id = Integer.valueOf(data[4+alliesNumber*12+i*12+0]&0xFF);
                if (enemyData[id] != null)
                    newEnemy.setEnemyData(enemyData[id]);                
                newEnemy.setX(data[4+alliesNumber*12+i*12+1]);
                newEnemy.setY(data[4+alliesNumber*12+i*12+2]);
                newEnemy.setAi(enemyEnums.toEnumString(data[4+alliesNumber*12+i*12+3], enemyEnums.getCommandSets()));
                newEnemy.setItem(EnemyEnums.itemNumToString(getNextWord(data,4+alliesNumber*12+i*12+4), enemyEnums.getItems()));
                newEnemy.setMoveOrder1(EnemyEnums.aiOrderNumToString(data[4+alliesNumber*12+i*12+6], enemyEnums.getOrders()));
                newEnemy.setTriggerRegion1(data[4+alliesNumber*12+i*12+7]);
                newEnemy.setMoveOrder2(EnemyEnums.aiOrderNumToString(data[4+alliesNumber*12+i*12+8], enemyEnums.getOrders()));
                newEnemy.setTriggerRegion2(data[4+alliesNumber*12+i*12+9]);
                newEnemy.setByte10(data[4+alliesNumber*12+i*12+10]);
                newEnemy.setSpawnParams(enemyEnums.toEnumString(data[4+alliesNumber*12+i*12+11], enemyEnums.getSpawnParams()));
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
    
    public static void exportSpriteSet(SpriteSet spriteset, String spritesetPath, EnemyEnums enemyEnums){
        System.out.println("com.sfc.sf2.battle.io.DisassemblyManager.exportSpriteSet() - Exporting disassembly ...");
        try { 
            if(spritesetPath.endsWith(".asm")){
                StringBuilder asm = new StringBuilder();
                asm.append(header);
                asm.append(produceSpriteSetBytesAsm(spriteset, enemyEnums));
                Path spritesetFilepath = Paths.get(spritesetPath);
                Files.write(spritesetFilepath, asm.toString().getBytes());
                System.out.println(asm);
            }else{
                byte[] spritesetBytes = produceSpriteSetBytesBin(spriteset, enemyEnums);
                Path spritesetFilepath = Paths.get(spritesetPath);
                Files.write(spritesetFilepath, spritesetBytes);
                System.out.println(spritesetBytes.length + " bytes into " + spritesetFilepath);
            }
        } catch (Exception ex) {
            Logger.getLogger(DisassemblyManager.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
            System.out.println(ex);
        }            
        System.out.println("com.sfc.sf2.battle.io.DisassemblyManager.exportSpriteSet() - Disassembly exported.");         
    }
    
    private static String produceSpriteSetBytesAsm(SpriteSet spriteset, EnemyEnums enemyEnums){
                
        Ally[] allies = spriteset.getAllies();
        Enemy[] enemies = spriteset.getEnemies();
        AIRegion[] aiRegions = spriteset.getAiRegions();
        AIPoint[] aiPoints = spriteset.getAiPoints();
        
        StringBuilder asm = new StringBuilder();
        
        //Sizes
        asm.append("                ; # Allies");
        asm.append("                "+MACRO_DCB+" "+allies.length+"\n");
        asm.append("                ; # Enemies");
        asm.append("                "+MACRO_DCB+" "+enemies.length+"\n");
        asm.append("                ; # AI Regions");
        asm.append("                "+MACRO_DCB+" "+aiRegions.length+"\n");
        asm.append("                ; # AI Points");
        asm.append("                "+MACRO_DCB+" "+aiPoints.length+"\n");
        asm.append("\n");
        
        //Allies
        asm.append("                ; Allies");
        for(int i=0;i<allies.length;i++){
            Ally ally = allies[i];
            asm.append("                "+MACRO_ALLIES+" "+ally.getIndex()+", "+ally.getX()+", "+ally.getY()+"\n");
            asm.append("                "+MACRO_ENEMY_LINE2+" HEALER1, NOTHING\n");
            asm.append("                "+MACRO_ENEMY_LINE3+" NONE, 0, NONE, 0, 0, STARTING\n");
        }
        asm.append("\n");
        
        //Enemies
        asm.append("                ; Enemies");
        for(int i=0;i<enemies.length;i++){
            Enemy enemy = enemies[i];
            
            String name = enemy.getEnemyData().getName();
            String command = EnemyEnums.toEnumString(enemy.getAi(), enemyEnums.getCommandSets());
            String item = EnemyEnums.stringToItemString(enemy.getItem(), enemyEnums.getItems());
            String moveOrder1 = EnemyEnums.stringToAiOrderString(enemy.getMoveOrder1(), enemyEnums.getOrders());
            String moveOrder2 = EnemyEnums.stringToAiOrderString(enemy.getMoveOrder2(), enemyEnums.getOrders());
            String spawnParams = EnemyEnums.toEnumString(enemy.getSpawnParams(), enemyEnums.getSpawnParams());
            
            asm.append("                "+MACRO_ENEMIES+" "+name+", "+enemy.getX()+", "+enemy.getY()+"\n");
            asm.append("                "+MACRO_ENEMY_LINE2+" "+command+", "+item+"\n");
            asm.append("                "+MACRO_ENEMY_LINE3+" "+moveOrder1+", "+enemy.getTriggerRegion1()+", "+moveOrder2+", "
                    +enemy.getTriggerRegion2()+", "+enemy.getByte10()+", "+spawnParams+"\n");
        }
        asm.append("\n");
        
        //Regions
        asm.append("                ; AI Regions");
        for(int i=0;i<aiRegions.length;i++){
            AIRegion region = aiRegions[i];
            asm.append("                "+MACRO_DCB+" "+region.getType()+"\n");
            asm.append("                "+MACRO_DCB+" 0\n");
            asm.append("                "+MACRO_DCB+" "+region.getX1()+", "+region.getY1()+"\n");
            asm.append("                "+MACRO_DCB+" "+region.getX2()+", "+region.getY2()+"\n");
            asm.append("                "+MACRO_DCB+" "+region.getX3()+", "+region.getY3()+"\n");
            asm.append("                "+MACRO_DCB+" "+region.getX4()+", "+region.getY4()+"\n");
            asm.append("                "+MACRO_DCB+" 0\n");
            asm.append("                "+MACRO_DCB+" 0\n");
        }
        asm.append("\n");
        
        //AI Points
        asm.append("                ; AI Points");
        for(int i=0;i<aiPoints.length;i++){
            AIPoint point = aiPoints[i];
            asm.append("                "+MACRO_DCB+" "+point.getX()+", "+point.getY()+"\n");
        }
        asm.append("\n");
        
        return asm.toString();
    }
    
    //Lagacy? Do spritesets need binary format anymore?
    private static byte[] produceSpriteSetBytesBin(SpriteSet spriteset, EnemyEnums enemyEnums){
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
            short item = EnemyEnums.itemStringToNum(header, enemyEnums.getItems());
            spritesetBytes[4+alliesNumber*12+i*12+0] = (byte)(enemy.getEnemyData().getID()&0xFF);
            spritesetBytes[4+alliesNumber*12+i*12+1] = (byte)enemy.getX();
            spritesetBytes[4+alliesNumber*12+i*12+2] = (byte)enemy.getY();
            spritesetBytes[4+alliesNumber*12+i*12+3] = EnemyEnums.toEnumByte(enemy.getAi(), enemyEnums.getCommandSets());
            spritesetBytes[4+alliesNumber*12+i*12+4] = (byte)(item>>8);
            spritesetBytes[4+alliesNumber*12+i*12+5] = (byte)(item&0xFF);
            spritesetBytes[4+alliesNumber*12+i*12+6] = EnemyEnums.aiOrderStringToNum(enemy.getMoveOrder1(), enemyEnums.getOrders());
            spritesetBytes[4+alliesNumber*12+i*12+7] = (byte)enemy.getTriggerRegion1();
            spritesetBytes[4+alliesNumber*12+i*12+8] = EnemyEnums.aiOrderStringToNum(enemy.getMoveOrder2(), enemyEnums.getOrders());
            spritesetBytes[4+alliesNumber*12+i*12+9] = (byte)enemy.getTriggerRegion2();
            spritesetBytes[4+alliesNumber*12+i*12+10] = (byte)enemy.getByte10();
            spritesetBytes[4+alliesNumber*12+i*12+11] = EnemyEnums.toEnumByte(enemy.getSpawnParams(), enemyEnums.getSpawnParams());
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
    
    public static EnemyData[] importEnemyData(String mapspriteEnumPath, String filepath, MapSprite[] mapsprites){
        System.out.println("com.sfc.sf2.battle.io.DisassemblyManager.importEnemyData() - Importing disassembly ...");
        List<EnemyData> enemyDataList = new ArrayList();
            
        try {

            if(filepath.endsWith(".bin")){
        
                Path filePath = Paths.get(filepath);
                byte[] data = Files.readAllBytes(filePath);
                
                for (int i = 0; i < data.length; i++) {                        
                    EnemyData enemy = new EnemyData();
                    enemy.setName("enemy_"+String.format("%03x", i));
                    enemy.setID(Integer.valueOf(data[i]));
                    if (data[i]*3 < mapsprites.length)
                        enemy.setMapSprite(mapsprites[data[i]*3+2]);
                    else
                        enemy.setMapSprite(null);
                    
                    while (enemyDataList.size() < data[i]){
                        enemyDataList.add(null);
                    }
                    enemyDataList.set(data[i], enemy);
                }
                
            }else{ 
                Map<String, Integer> map = new HashMap<String, Integer>();
                
                File file = new File(filepath);
                Scanner scan = new Scanner(file);
                while(scan.hasNext()){
                    String line = scan.nextLine();
                    if(line.trim().startsWith("mapsprite")){
                        if(line.contains(";")){
                            line = line.substring(0,line.indexOf(";"));
                        }
                        String key = line.trim().substring("mapsprite".length()).trim();
                        Integer value;
                        if(key.contains("$")||key.matches("[0-9]+")){
                            value = valueOf(key);
                        }else{
                            value = enemyDataList.size();
                        }    
                        
                        map.put(key, value);
                        EnemyData enemy = new EnemyData();
                        enemy.setName(key);
                        enemy.setID(value);
                        enemyDataList.add(enemy);
                    }
                }
                
                File enumFile = new File(mapspriteEnumPath);
                Scanner enumScan = new Scanner(enumFile);
                while(enumScan.hasNext()){
                    String line = enumScan.nextLine();
                    if(line.trim().startsWith("; enum Mapsprites")){
                        line = enumScan.nextLine();
                        while(!line.startsWith("; enum")){
                            if(line.startsWith("MAPSPRITE")){
                                String key = line.substring(10,line.indexOf(":"));
                                Integer value = line.indexOf("$")+1;
                                if (value <= 0){
                                    value = line.indexOf("equ")+4;
                                }
                                Integer comment = line.indexOf(";");
                                if (comment == -1){
                                    value = valueOf(line.substring(value).trim());
                                }
                                else{
                                    value = valueOf(line.substring(value, comment).trim());
                                }
                                
                                if (map.containsKey(key)){
                                    Integer index = map.get(key);
                                    if (index < enemyDataList.size() && enemyDataList.get(index) != null && value*3 < mapsprites.length){
                                        enemyDataList.get(index).setMapSprite(mapsprites[value*3+2]);
                                    }
                                }                                    
                            }
                            line = enumScan.nextLine();
                        }
                    }
                }
            }

        } catch (IOException ex) {
            Logger.getLogger(DisassemblyManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("com.sfc.sf2.battle.io.DisassemblyManager.importEnemyData() - Disassembly imported.");
        
        EnemyData[] enemyData = new EnemyData[enemyDataList.size()];
        enemyData = enemyDataList.toArray(enemyData);
        return enemyData;
    }
    
    
    
    public static EnemyEnums importEnemyEnums(String mapspriteEnumPath){
        System.out.println("com.sfc.sf2.battle.io.DisassemblyManager.importEnemyEnums() - Importing disassembly ...");
        Map<String, Integer> items = new HashMap<>();
        Map<String, Integer> aiCommandSets = new HashMap<>();
        Map<String, Integer> aiOrders = new HashMap<>();
        Map<String, Integer> spawnParams = new HashMap<>();
            
        try {
            if(mapspriteEnumPath.endsWith(".bin")){
        
                //TODO Handle .bin enums (is this necessary?)
                
            }else{ 
                Map<String, Integer> map = new HashMap<String, Integer>();
                
                File file = new File(mapspriteEnumPath);
                Scanner scan = new Scanner(file);
                while(scan.hasNext()){
                    String line = scan.nextLine();
                    if(line.trim().startsWith("; enum AiCommandsets")){
                        while(scan.hasNext()){
                            line = scan.nextLine();

                            if(line.startsWith("AICOMMANDSET_")){
                                line = line.substring(line.indexOf("_")+1);
                                String command = line.substring(0, line.indexOf(":"));
                                int value = valueOf(line.substring(line.indexOf("equ")+4));
                                aiCommandSets.put(command, value);
                            }
                            else if (line.startsWith("; --")){
                                break;
                            }
                        }
                    }
                    else if(line.trim().startsWith("; enum AiOrders")){
                        while(scan.hasNext()){
                            line = scan.nextLine();

                            if(line.startsWith("AIORDER_")){
                                line = line.substring(line.indexOf("_")+1);
                                String order = line.substring(0, line.indexOf(":"));
                                int value = valueOf(line.substring(line.indexOf("equ")+4));
                                aiOrders.put(order, value);
                            }
                            else if (line.startsWith("; --")){
                                break;
                            }
                        }
                    }
                    else if(line.trim().startsWith("; enum SpawnSettings")){
                        while(scan.hasNext()){
                            line = scan.nextLine();

                            if(line.startsWith("SPAWN_")){
                                line = line.substring(line.indexOf("_")+1);
                                String spawn = line.substring(0, line.indexOf(":"));
                                int value = valueOf(line.substring(line.indexOf("equ")+4));
                                spawnParams.put(spawn, value);
                            }
                            else if (line.startsWith("; --")){
                                break;
                            }
                        }
                    }
                    else if(line.trim().startsWith("; enum Items")){
                        while(scan.hasNext()){
                            line = scan.nextLine();

                            if(line.startsWith("ITEM_")){
                                line = line.substring(line.indexOf("_")+1);
                                String item = line.substring(0, line.indexOf(":"));
                                int value = valueOf(line.substring(line.indexOf("equ")+4));
                                items.put(item, value);
                            }
                            else if (line.startsWith("; --")){
                                break;
                            }
                        }
                    }
                }
            }

        } catch (IOException ex) {
            Logger.getLogger(DisassemblyManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("com.sfc.sf2.battle.io.DisassemblyManager.importEnemyEnums() - Disassembly imported.");
        
            
        EnemyEnums enemyEnums = new EnemyEnums();
        enemyEnums.setItems(items);
        enemyEnums.setCommandSets(aiCommandSets);
        enemyEnums.setOrders(aiOrders);
        enemyEnums.setSpawnParams(spawnParams);
        return enemyEnums;
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
