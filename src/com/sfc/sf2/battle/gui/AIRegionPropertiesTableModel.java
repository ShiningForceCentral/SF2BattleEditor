/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.battle.gui;

import com.sfc.sf2.battle.AIRegion;
import com.sfc.sf2.battle.Battle;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author wiz
 */
public class AIRegionPropertiesTableModel extends AbstractTableModel {
    
    private final Integer[][] tableData;
    private final String[] columns = {"Type", "X1", "Y1", "X2", "Y2", "X3", "Y3", "X4", "Y4"};
    private Battle battle;
    private BattlePanel battlePanel;
    
    public AIRegionPropertiesTableModel(Battle battle, BattlePanel battlePanel) {
        super();
        this.battle = battle;
        this.battlePanel = battlePanel;
        tableData = new Integer[16][];
        int i = 0;
        AIRegion[] regions = battle.getSpriteset().getAiRegions();
        if(regions!=null){
            while(i<regions.length){
                tableData[i] = new Integer[9];
                tableData[i][0] = regions[i].getType();
                tableData[i][1] = regions[i].getX1();
                tableData[i][2] = regions[i].getY1();
                tableData[i][3] = regions[i].getX2();
                tableData[i][4] = regions[i].getY2();
                tableData[i][5] = regions[i].getX3();
                tableData[i][6] = regions[i].getY3();
                tableData[i][7] = regions[i].getX4();
                tableData[i][8] = regions[i].getY4();
                i++;
            }
        }
        while(i<tableData.length){
            tableData[i] = new Integer[9];
            i++;
        }
    }
    
    public void updateProperties() {
        List<AIRegion> entries = new ArrayList<>();
        for(Integer[] entry : tableData){
            if(entry[0] != null && entry[1] != null
                    && entry[2] != null && entry[3] != null
                    && entry[4] != null && entry[5] != null
                    && entry[6] != null && entry[7] != null
                    && entry[8] != null){
                AIRegion aiRegion = new AIRegion();
                aiRegion.setType(entry[0]);
                aiRegion.setX1(entry[1]);
                aiRegion.setY1(entry[2]);
                aiRegion.setX2(entry[3]);
                aiRegion.setY2(entry[4]);
                aiRegion.setX3(entry[5]);
                aiRegion.setY3(entry[6]);
                aiRegion.setX4(entry[7]);
                aiRegion.setY4(entry[8]);        
                entries.add(aiRegion);
            }
        }
        AIRegion[] regions = new AIRegion[entries.size()];
        battle.getSpriteset().setAiRegions(entries.toArray(regions));
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
        //battlePanel.updateAIRegionDisplay();
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
