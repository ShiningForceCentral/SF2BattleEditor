/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.battle.gui;

import com.sfc.sf2.battle.AIPoint;
import com.sfc.sf2.battle.Battle;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author wiz
 */
public class AIPointPropertiesTableModel extends AbstractTableModel {
    
    private final Integer[][] tableData;
    private final String[] columns = {"X", "Y"};
    private Battle battle;
    private BattlePanel battlePanel;
    
    public AIPointPropertiesTableModel(Battle battle, BattlePanel battlePanel) {
        super();
        this.battle = battle;
        this.battlePanel = battlePanel;
        tableData = new Integer[64][];
        int i = 0;
        AIPoint[] points = battle.getSpriteset().getAiPoints();
        if(points!=null){
            while(i<points.length){
                tableData[i] = new Integer[2];
                tableData[i][0] = points[i].getX();
                tableData[i][1] = points[i].getY();
                i++;
            }
        }
        while(i<tableData.length){
            tableData[i] = new Integer[2];
            i++;
        }
    }
    
    public void updateProperties() {
        List<AIPoint> entries = new ArrayList<>();
        for(Integer[] entry : tableData){
            if(entry[0] != null && entry[1] != null){
                AIPoint point = new AIPoint();
                point.setX(entry[0]);
                point.setY(entry[1]);          
                entries.add(point);
            }
        }
        AIPoint[] points = new AIPoint[entries.size()];
        battle.getSpriteset().setAiPoints(entries.toArray(points));
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
        //battlePanel.updateAIPointDisplay();
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
