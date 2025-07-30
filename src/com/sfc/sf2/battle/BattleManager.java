/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.battle;

import com.sfc.sf2.battle.io.DisassemblyManager;
import com.sfc.sf2.battle.mapcoords.BattleMapCoords;
import com.sfc.sf2.battle.mapcoords.BattleMapCoordsManager;
import com.sfc.sf2.battle.mapterrain.BattleMapTerrain;
import com.sfc.sf2.battle.mapterrain.BattleMapTerrainManager;
import com.sfc.sf2.mapsprite.MapSprite;
import com.sfc.sf2.mapsprite.MapSpriteManager;

/**
 *
 * @author wiz
 */
public class BattleManager {
    private final DisassemblyManager disassembly = new DisassemblyManager();
    private final BattleMapCoordsManager mapCoordsManager = new BattleMapCoordsManager();
    private final BattleMapTerrainManager mapTerrainManager = new BattleMapTerrainManager();
    private final MapSpriteManager mapspriteManager = new MapSpriteManager();
    private Battle battle;
    private BattleMapTerrain terrain;
    private String[][] mapEntries = null;
    private String[] spritesetEntries = null;
    private BattleMapCoords[] coordsArray = null;
    private EnemyData[] enemyData = null;
    private EnemyEnums enemyEnums = null;
        
    public void importDisassembly(String incbinPath, String mapEntriesPath, int battleIndex) {
        System.out.println("com.sfc.sf2.battle.BattleManager.importDisassembly() - Importing disassembly ...");
        mapEntries = disassembly.importMapEntryFile(incbinPath, mapEntriesPath);
        battle = new Battle();
        battle.setIndex(battleIndex);
        System.out.println("com.sfc.sf2.battle.BattleManager.importDisassembly() - Disassembly imported.");
    }
    
    public void importMapCoordsDisassembly(String basePath, String mapEntriesPath, String mapCoordsPath, int battleIndex) {
        System.out.println("com.sfc.sf2.battle.BattleManager.importMapCoordsDisassembly() - Importing Map Coords disassembly ...");
        mapCoordsManager.importDisassembly(basePath, mapEntriesPath, mapCoordsPath);
        coordsArray = mapCoordsManager.getCoords();
        battle.setMapCoords(coordsArray[battleIndex]);
        System.out.println("com.sfc.sf2.battle.BattleManager.importMapCoordsDisassembly() - Map Coords disassembly imported.");
    }
        
    public void importSpritesDataDisassembly(String incbinPath, String basePalettePath, String mapspriteEntriesPath, String mapspriteEnumPath, int battleIndex, String spritesetEntriesPath) {
        System.out.println("com.sfc.sf2.battle.BattleManager.importSpritesDataDisassembly() - Importing Sprites Data disassembly ...");
        spritesetEntries = disassembly.importSpritesetEntriesFile(spritesetEntriesPath);
        MapSprite[] mapsprites = mapspriteManager.importDisassemblyFromEntryFile(basePalettePath, mapspriteEntriesPath, incbinPath);
        enemyEnums = disassembly.importEnemyEnums(mapspriteEnumPath);
        enemyData = disassembly.importEnemyData(enemyEnums, mapsprites, mapspriteEnumPath);
        battle.setSpriteset(disassembly.importSpriteset(incbinPath+spritesetEntries[battleIndex], enemyData, enemyEnums));
        System.out.println("com.sfc.sf2.battle.BattleManager.importSpritesDataDisassembly() - Sprites Data disassembly imported.");
    }
    
    public void importTerrainDisassembly(String palettesPath, String tilesetsPath, String basePath, String mapEntriesPath, String terrainEntriesPath, String battleMapCoordsPath, int battleIndex) {
        System.out.println("com.sfc.sf2.battle.BattleManager.importTerrainDisassembly() - Importing Terrain disassembly ...");
        mapTerrainManager.importDisassembly(palettesPath, tilesetsPath, basePath, mapEntriesPath, terrainEntriesPath, battleMapCoordsPath, battleIndex);
        terrain = mapTerrainManager.getTerrain();
        battle.setTerrain(terrain);
        System.out.println("com.sfc.sf2.battle.BattleManager.importTerrainDisassembly() - Terrain disassembly imported.");
    }
    
    public void exportDisassembly(String mapcoordsPath, String terrainPath, String spritesetPath) {
        System.out.println("com.sfc.sf2.battle.BattleManager.importDisassembly() - Exporting disassembly ...");
        coordsArray[battle.getIndex()] = battle.getMapCoords();
        mapCoordsManager.exportDisassembly(coordsArray, mapcoordsPath);
        mapTerrainManager.setTerrain(battle.getTerrain());
        mapTerrainManager.exportDisassembly(terrainPath);
        disassembly.exportSpriteSet(battle.getSpriteset(), spritesetPath, enemyEnums);
        System.out.println("com.sfc.sf2.battle.BattleManager.importDisassembly() - Disassembly exported.");        
    }

    public String[][] getMapEntries() {
        return mapEntries;
    }

    public void setMapEntries(String[][] mapEntries) {
        this.mapEntries = mapEntries;
    }

    public Battle getBattle() {
        return battle;
    }

    public void setBattle(Battle battle) {
        this.battle = battle;
    }

    public EnemyData[] getEnemyData() {
        return enemyData;
    }    

    public EnemyEnums getEnemyEnums() {
        return enemyEnums;
    }
}
