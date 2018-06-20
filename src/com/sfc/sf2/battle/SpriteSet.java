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
public class SpriteSet {
    private Ally[] allies;
    private Enemy[] enemies;
    private AIRegion[] aiRegions;
    private AIPoint[] aiPoints;

    public Ally[] getAllies() {
        return allies;
    }

    public void setAllies(Ally[] allies) {
        this.allies = allies;
    }

    public Enemy[] getEnemies() {
        return enemies;
    }

    public void setEnemies(Enemy[] enemies) {
        this.enemies = enemies;
    }

    public AIRegion[] getAiRegions() {
        return aiRegions;
    }

    public void setAiRegions(AIRegion[] aiRegions) {
        this.aiRegions = aiRegions;
    }

    public AIPoint[] getAiPoints() {
        return aiPoints;
    }

    public void setAiPoints(AIPoint[] aiPoints) {
        this.aiPoints = aiPoints;
    }
    
    
}
