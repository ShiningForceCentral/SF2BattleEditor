/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.battle;

import com.sfc.sf2.mapsprite.MapSprite;

/**
 *
 * @author TiMMy
 */
public class EnemyData {
    private String name;
    private int id;
    private MapSprite mapSprite;
        
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getID() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    }

    public MapSprite getMapSprite() {
        return mapSprite;
    }

    public void setMapSprite(MapSprite mapSprite) {
        this.mapSprite = mapSprite;
    }
}
