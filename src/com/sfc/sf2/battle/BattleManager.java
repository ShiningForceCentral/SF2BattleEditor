/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.battle;

import com.sfc.sf2.battle.io.DisassemblyManager;
import com.sfc.sf2.battle.io.PngManager;
import com.sfc.sf2.battle.mapcoords.BattleMapCoords;
import com.sfc.sf2.battle.mapcoords.BattleMapCoordsManager;
import com.sfc.sf2.battle.mapterrain.BattleMapTerrainManager;
import com.sfc.sf2.map.layout.MapLayoutManager;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author wiz
 */
public class BattleManager {
       
    private MapLayoutManager mapLayoutManager = new MapLayoutManager();
    private BattleMapCoordsManager mapCoordsManager = new BattleMapCoordsManager();
    private BattleMapTerrainManager mapTerrainManager = new BattleMapTerrainManager();
    private Battle battle;
    private String[][] mapEntries = null;
    private BattleMapCoords coords = null;
    
    public void importDisassembly(String mapPalettesPath, String mapTilesetsPath, String incbinPath, String mapEntriesPath, String mapCoordsPath, String mapspriteEntriesPath, String enemySpritesPath, String neutralEntitiesPath,
                                    int battleIndex, String terrainPath, String spritesetPath){
        System.out.println("com.sfc.sf2.battle.BattleManager.importDisassembly() - Importing disassembly ...");
        mapCoordsManager.importDisassembly(mapCoordsPath);
        BattleMapCoords[] coordsArray = mapCoordsManager.getCoords();
        battle = new Battle();
        battle.setIndex(battleIndex);
        battle.setMapCoords(coordsArray[battleIndex]);
        mapTerrainManager.importDisassembly(terrainPath);
        battle.setTerrain(mapTerrainManager.getTerrain());
        battle.setSpriteset(DisassemblyManager.importSpriteset(spritesetPath));
        mapEntries = importMapEntryFile(incbinPath, mapEntriesPath);
        /* TODO :
        - Import mapsprite entries
        - Import enemy sprite IDs
        - Import neutral entites */
        System.out.println("com.sfc.sf2.battle.BattleManager.importDisassembly() - Disassembly imported.");
    }
    
    public void exportDisassembly(String mapcoordsPath, String terrainPath, String spritesetPath){
        System.out.println("com.sfc.sf2.battle.BattleManager.importDisassembly() - Exporting disassembly ...");

        System.out.println("com.sfc.sf2.battle.BattleManager.importDisassembly() - Disassembly exported.");        
    }      
    
    public void exportPng(String filepath){
        System.out.println("com.sfc.sf2.battle.BattleManager.exportPng() - Exporting PNG ...");
        //PngManager.exportPng(map, filepath);
        System.out.println("com.sfc.sf2.battle.BattleManager.exportPng() - PNG exported.");       
    }
    
    private String[][] importMapEntryFile(String basePath, String mapEntriesFilePath){
        String[][] entries = null;
        List<String> tilesetsPaths = new ArrayList();
        List<String> blocksPaths = new ArrayList();
        List<String> layoutPaths = new ArrayList();
        try{
            File entryFile = new File(mapEntriesFilePath);
            Scanner scan = new Scanner(entryFile);
            while(scan.hasNext()){
                String line = scan.nextLine();
                if(line.contains("pt_MapData:")){
                    System.out.println("pt_MapData found");
                    while(scan.hasNext()&&line.contains("dc.l")){
                        String mapPointer = line.substring(line.indexOf("dc.l")+5).trim();
                        System.out.println(mapPointer+" : ");
                        Scanner mapScan = new Scanner(entryFile);
                        while(mapScan.hasNext()){
                            String mapline = mapScan.nextLine();
                            if(mapline.startsWith(mapPointer)){
                                while(mapScan.hasNext()&&!mapline.contains("incbin")){
                                    mapline = mapScan.nextLine();
                                }
                                String tilesetsPath = mapline.substring(mapline.indexOf("\"")+1, mapline.lastIndexOf("\""));
                                System.out.println("  tilesetsPath : "+tilesetsPath);
                                tilesetsPaths.add(tilesetsPath);
                                mapline = mapScan.nextLine();
                                while(mapScan.hasNext()&&!mapline.contains("dc.l")){
                                    mapline = mapScan.nextLine();
                                }
                                String blocksPointer = mapline.substring(mapline.indexOf("dc.l")+5).trim();
                                Scanner blocksScan = new Scanner(entryFile);
                                while(blocksScan.hasNext()){
                                    String blocksLine = blocksScan.nextLine();
                                    if(blocksLine.startsWith(blocksPointer)){
                                        while(blocksScan.hasNext()&&!blocksLine.contains("incbin")){
                                            blocksLine = blocksScan.nextLine();
                                        }
                                        String blocksPath = blocksLine.substring(blocksLine.indexOf("\"")+1, blocksLine.lastIndexOf("\""));
                                        System.out.println("  blocksPath : "+blocksPath);                                        
                                        blocksPaths.add(blocksPath);
                                        break;
                                    }
                                }
                                mapline = mapScan.nextLine();
                                while(mapScan.hasNext()&&!mapline.contains("dc.l")){
                                    mapline = mapScan.nextLine();
                                }
                                String layoutPointer = mapline.substring(mapline.indexOf("dc.l")+5).trim();
                                Scanner layoutsScan = new Scanner(entryFile);
                                while(layoutsScan.hasNext()){
                                    String layoutLine = layoutsScan.nextLine();
                                    if(layoutLine.startsWith(layoutPointer)){
                                        while(layoutsScan.hasNext()&&!layoutLine.contains("incbin")){
                                            layoutLine = layoutsScan.nextLine();
                                        }
                                        String layoutPath = layoutLine.substring(layoutLine.indexOf("\"")+1, layoutLine.lastIndexOf("\""));
                                        System.out.println("  layoutPath : "+layoutPath); 
                                        layoutPaths.add(layoutPath);
                                        break;
                                    }
                                }
                                break;
                            }
                        }
                        line = scan.nextLine();
                    }
                    break;
                }
            }         
            entries = new String[tilesetsPaths.size()][];
            for(int i=0;i<entries.length;i++){
                entries[i] = new String[3];
                entries[i][0] = basePath + tilesetsPaths.get(i);
                entries[i][1] = basePath + blocksPaths.get(i);
                entries[i][2] = basePath + layoutPaths.get(i);
                System.out.println(entries[i][0]+" / "+entries[i][1]+" / "+entries[i][2]);
            }
        }catch(Exception e){
             System.err.println("com.sfc.sf2.mapsprite.io.PngManager.importPng() - Error while parsing map entries data : "+e);
        }         
        return entries;
    }
    
    

    public String[][] getMapEntries() {
        return mapEntries;
    }

    public void setMapEntries(String[][] mapEntries) {
        this.mapEntries = mapEntries;
    }

    public BattleMapCoords getCoords() {
        return coords;
    }

    public void setCoords(BattleMapCoords coords) {
        this.coords = coords;
    }

    public Battle getBattle() {
        return battle;
    }

    public void setBattle(Battle battle) {
        this.battle = battle;
    }
    
    
    
}
