/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.battle;

/**
 *
 * @author wiz
 */
public class Enemy {
    private int index;
    private int x;
    private int y;
    private int ai;
    private int item;
    private int moveOrder1;
    private int triggerRegion;
    private int byte8;
    private int byte9;
    private int byte10;
    private int spawnParams;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getAi() {
        return ai;
    }

    public void setAi(int ai) {
        this.ai = ai;
    }

    public int getItem() {
        return item;
    }

    public void setItem(int item) {
        this.item = item;
    }

    public int getMoveOrder1() {
        return moveOrder1;
    }

    public void setMoveOrder1(int moveOrder1) {
        this.moveOrder1 = moveOrder1;
    }

    public int getTriggerRegion() {
        return triggerRegion;
    }

    public void setTriggerRegion(int triggerRegion) {
        this.triggerRegion = triggerRegion;
    }

    public int getByte8() {
        return byte8;
    }

    public void setByte8(int byte8) {
        this.byte8 = byte8;
    }

    public int getByte9() {
        return byte9;
    }

    public void setByte9(int byte9) {
        this.byte9 = byte9;
    }

    public int getByte10() {
        return byte10;
    }

    public void setByte10(int byte10) {
        this.byte10 = byte10;
    }

    public int getSpawnParams() {
        return spawnParams;
    }

    public void setSpawnParams(int spawnParams) {
        this.spawnParams = spawnParams;
    }
    
    
}
