/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.battle.gui;

import com.sfc.sf2.battle.Battle;
import com.sfc.sf2.battle.Enemy;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author wiz
 */
public class EnemyPropertiesTableModel extends AbstractTableModel {
    
    private Integer[][] tableData;
    private final String[] columns = {"Index", "X", "Y", "AI", "Item", "Order 1", "Region 1", "Order 2", "Region 2", "Byte10", "Spawn"};
    private Battle battle;
    private BattlePanel battlePanel;
    
    public EnemyPropertiesTableModel(Battle battle, BattlePanel battlePanel) {
        super();
        this.battle = battle;
        this.battlePanel = battlePanel;
        Enemy[] enemies = battle.getSpriteset().getEnemies();
        tableData = new Integer[enemies.length][];
        int i = 0;
        if(enemies!=null){
            while(i<enemies.length){
                tableData[i] = new Integer[11];
                tableData[i][0] = enemies[i].getIndex();
                tableData[i][1] = enemies[i].getX();
                tableData[i][2] = enemies[i].getY();
                tableData[i][3] = enemies[i].getAi();
                tableData[i][4] = enemies[i].getItem();
                tableData[i][5] = enemies[i].getMoveOrder1();
                tableData[i][6] = enemies[i].getTriggerRegion();
                tableData[i][7] = enemies[i].getByte8();
                tableData[i][8] = enemies[i].getByte9();
                tableData[i][9] = enemies[i].getByte10();
                tableData[i][10] = enemies[i].getSpawnParams();
                i++;
            }
        }
        while(i<tableData.length){
            tableData[i] = new Integer[11];
            i++;
        }
    }
    
    public void updateProperties() {
        List<Enemy> entries = new ArrayList<>();
        for(Integer[] entry : tableData){
            if(entry[0] != null && entry[1] != null
                    && entry[2] != null && entry[3] != null
                    && entry[4] != null && entry[5] != null
                    && entry[6] != null && entry[7] != null
                    && entry[8] != null && entry[9] != null
                    && entry[10] != null){
                Enemy enemy = new Enemy();
                enemy.setIndex(entry[0]);
                enemy.setX(entry[1]);
                enemy.setY(entry[2]);
                enemy.setAi(entry[3]); 
                enemy.setItem(entry[4]);
                enemy.setMoveOrder1(entry[5]);
                enemy.setTriggerRegion(entry[6]);
                enemy.setByte8(entry[7]); 
                enemy.setByte9(entry[8]);
                enemy.setByte10(entry[9]);
                enemy.setSpawnParams(entry[10]);         
                entries.add(enemy);
            }
        }
        Enemy[] enemies = new Enemy[entries.size()];
        battle.getSpriteset().setEnemies(entries.toArray(enemies));
    }
    
    public void addRow(){
        Integer[][] newTable = new Integer[tableData.length+1][];
        for(int i=0;i<tableData.length;i++){
            newTable[i] = tableData[i];
        }
        newTable[newTable.length-1] = new Integer[11];
        newTable[newTable.length-1][0] = tableData[tableData.length-1][0];
        newTable[newTable.length-1][1] = 0;
        newTable[newTable.length-1][2] = 0;
        newTable[newTable.length-1][3] = tableData[tableData.length-1][3];
        newTable[newTable.length-1][4] = tableData[tableData.length-1][4];
        newTable[newTable.length-1][5] = tableData[tableData.length-1][5];
        newTable[newTable.length-1][6] = tableData[tableData.length-1][6];
        newTable[newTable.length-1][7] = tableData[tableData.length-1][7];
        newTable[newTable.length-1][8] = tableData[tableData.length-1][8];
        newTable[newTable.length-1][9] = tableData[tableData.length-1][9];
        newTable[newTable.length-1][10] = tableData[tableData.length-1][10];
        tableData = newTable;
        updateProperties();
        battlePanel.updateSpriteDisplay();
        battlePanel.revalidate();
        battlePanel.repaint();
    }
    
    public void removeRow(){
        if(tableData.length>1){
            Integer[][] newTable = new Integer[tableData.length-1][];
            for(int i=0;i<newTable.length;i++){
                newTable[i] = tableData[i];
            }
            tableData = newTable;
            updateProperties();
            battlePanel.updateSpriteDisplay();
            battlePanel.revalidate();
            battlePanel.repaint();
        }
    }
    
    @Override
    public Class getColumnClass(int column) {
        return Integer.class;
    }    
    
    @Override
    public Object getValueAt(int row, int col) {
        return tableData[row][col];
    }
    @Override
    public void setValueAt(Object value, int row, int col) {
        tableData[row][col] = (Integer)value;
        updateProperties();
        battlePanel.updateSpriteDisplay();
        battlePanel.revalidate();
        battlePanel.repaint();
    }    
 
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }    
    
    @Override
    public int getRowCount() {
        return tableData.length;
    }
 
    @Override
    public int getColumnCount() {
        return columns.length;
    }
 
    @Override
    public String getColumnName(int columnIndex) {
        return columns[columnIndex];
    }

    public Battle getBattle() {
        return battle;
    }

    public void setBattle(Battle battle) {
        this.battle = battle;
    }
    
}
