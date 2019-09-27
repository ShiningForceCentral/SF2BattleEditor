/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.battle.gui;

import com.sfc.sf2.battle.Ally;
import com.sfc.sf2.battle.Battle;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author wiz
 */
public class AllyPropertiesTableModel extends AbstractTableModel {
    
    private Integer[][] tableData;
    private final String[] columns = {"Index", "X", "Y"};
    private Battle battle;
    private BattlePanel battlePanel;
    
    public AllyPropertiesTableModel(Battle battle, BattlePanel battlePanel) {
        super();
        this.battle = battle;
        this.battlePanel = battlePanel;
        Ally[] allies = battle.getSpriteset().getAllies();
        tableData = new Integer[allies.length][];
        int i = 0;
        if(allies!=null){
            while(i<allies.length){
                tableData[i] = new Integer[3];
                tableData[i][0] = allies[i].getIndex();
                tableData[i][1] = allies[i].getX();
                tableData[i][2] = allies[i].getY();
                i++;
            }
        }
        while(i<tableData.length){
            tableData[i] = new Integer[3];
            i++;
        }
    }
    
    public void updateProperties() {
        List<Ally> entries = new ArrayList<>();
        for(Integer[] entry : tableData){
            if(entry[0] != null && entry[1] != null
                    && entry[2] != null){
                Ally ally = new Ally();
                ally.setIndex(entry[0]);
                ally.setX(entry[1]);
                ally.setY(entry[2]);         
                entries.add(ally);
            }
        }
        Ally[] allies = new Ally[entries.size()];
        battle.getSpriteset().setAllies(entries.toArray(allies));
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
    
    public void addRow(){
        if(tableData.length<12){
            Integer[][] newTable = new Integer[tableData.length+1][];
            for(int i=0;i<tableData.length;i++){
                newTable[i] = tableData[i];
            }
            newTable[newTable.length-1] = new Integer[3];
            newTable[newTable.length-1][0] = newTable.length-1;
            newTable[newTable.length-1][1] = 0;
            newTable[newTable.length-1][2] = 0;
            tableData = newTable;
            updateProperties();
            battlePanel.updateSpriteDisplay();
            battlePanel.revalidate();
            battlePanel.repaint();
        }
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
