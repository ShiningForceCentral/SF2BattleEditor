/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.battle.layout;

import com.sfc.sf2.battle.AIPoint;
import com.sfc.sf2.battle.AIRegion;
import com.sfc.sf2.battle.Ally;
import com.sfc.sf2.battle.Battle;
import com.sfc.sf2.battle.Enemy;
import com.sfc.sf2.battle.gui.AllyPropertiesTableModel;
import com.sfc.sf2.battle.gui.EnemyPropertiesTableModel;
import com.sfc.sf2.battle.mapterrain.layout.BattleMapTerrainLayout;
import com.sfc.sf2.map.block.MapBlock;
import com.sfc.sf2.map.layout.MapLayout;
import com.sfc.sf2.mapsprite.MapSprite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

/**
 *
 * @author wiz
 */
public class BattleLayout extends BattleMapTerrainLayout {
    
    public static final int SPRITESETMODE_ALLY = 0;
    public static final int SPRITESETMODE_ENEMY = 1;
    public static final int SPRITESETMODE_AIREGION = 2;
    public static final int SPRITESETMODE_AIPOINT = 3;
    
    public static final int MODE_NONE = 0;
    public static final int MODE_TERRAIN = 1;
    public static final int MODE_SPRITE = 2;
    
    protected Battle battle;
    
    protected int currentMode = 0;
    protected int currentSpritesetMode = 0;
    protected int selectedAlly = -1;
    protected int selectedEnemy = -1;
    protected int selectedAIRegion = -1;
    protected int selectedAIPoint = -1;
    protected int applicableTerrainValue = -1;
    private int lastMouseX = 0;
    private int lastMouseY = 0;
    private int battleCoordsOffsetX = 0;
    private int battleCoordsOffsetY = 0;
    int lastMapX = 0;
    int lastMapY = 0;
    
    protected TitledBorder titledBorder = null;
    protected JPanel titledPanel = null;
    
    protected MapBlock selectedBlock0;
    protected MapBlock[][] copiedBlocks;
    
    protected List<int[]> actions = new ArrayList<>();
    
    protected boolean drawSprites = true;
    protected boolean drawAiRegions = true;
    protected boolean drawAiPoints = true;
    
    private BufferedImage spritesImage;
    private BufferedImage aiRegionsImage;
    private BufferedImage aiPointsImage;
    
    private final BufferedImage[] mapspriteImages = new BufferedImage[256];
    
    private AllyPropertiesTableModel alliesTable = null;
    private EnemyPropertiesTableModel enemiesTable = null;
    
    public BattleLayout() {
        super();
        addMouseListener(this);
        addMouseMotionListener(this);
    }
    
    @Override
    public BufferedImage buildImage(MapLayout layout, int tilesPerRow) {
        BufferedImage image = super.buildImage(layout, tilesPerRow);
        Graphics graphics = image.getGraphics();
        if(drawSprites){
            graphics.drawImage(getSpritesImage(),0,0,null);
        }
        if(drawAiRegions){
            graphics.drawImage(getAiRegionsImage(),0,0,null);
        }
        if(drawAiPoints){
            graphics.drawImage(getAiPointsImage(),0,0,null);
        }
        graphics.dispose();
        return image;
    }
    
    private BufferedImage getSpritesImage(){
        if(spritesImage==null){
            spritesImage = new BufferedImage(3*8*64, 3*8*64, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = (Graphics2D) spritesImage.getGraphics();
            int x = battle.getMapCoords().getX();
            int y = battle.getMapCoords().getY();            
            Ally[] allies = battle.getSpriteset().getAllies();
            for(int i=0;i<allies.length;i++){
                Ally ally = allies[i];
                Font font = new Font("Courier", Font.BOLD, 16);
                g2.setFont(font);
                int offset = (ally.getIndex()+1 < 10) ? 0 : -4;
                int targetX = (x+ally.getX())*3*8+8+offset;
                int targetY = (y+ally.getY())*3*8+18;
                String val = String.valueOf(ally.getIndex()+1);
                g2.setColor(Color.BLACK);
                g2.drawString(val, targetX-1, targetY-1);
                g2.drawString(val, targetX-1, targetY+1);
                g2.drawString(val, targetX+1, targetY-1);
                g2.drawString(val, targetX+1, targetY+1);
                g2.setColor(Color.YELLOW);
                g2.drawString(val, targetX, targetY);  
                if(currentMode==MODE_SPRITE && currentSpritesetMode==SPRITESETMODE_ALLY && i==selectedAlly){
                    g2.setColor(Color.YELLOW);
                    g2.setStroke(new BasicStroke(3));
                    g2.drawRect((x+ally.getX())*3*8, (y+ally.getY())*3*8, 1*24, 1*24);
                }
            }
            Enemy[] enemies = battle.getSpriteset().getEnemies();
            for(int i=0;i<enemies.length;i++){
                Enemy enemy = enemies[i];
                int targetX = (x+enemy.getX())*3*8;
                int targetY = (y+enemy.getY())*3*8;
                int id = enemy.getEnemyData().getID();
                MapSprite sprite = enemy.getEnemyData().getMapSprite();
                if(sprite == null){
                    targetX += 8 + ((enemy.getEnemyData().getID()+1 < 10) ? 0 : -4);
                    targetY += 3;
                    g2.setColor(Color.BLACK);
                    g2.drawString(String.valueOf(id), targetX-1, targetY+16-1);
                    g2.drawString(String.valueOf(id), targetX-1, targetY+16+1);
                    g2.drawString(String.valueOf(id), targetX+1, targetY+16-1);
                    g2.drawString(String.valueOf(id), targetX+1, targetY+16+1);
                    g2.setColor(Color.RED);
                    g2.drawString(String.valueOf(id), targetX, targetY+16);
                }else{
                    if(mapspriteImages[id] == null){
                        mapspriteImages[id] = sprite.getIndexedColorImage().getSubimage(0, 0, 3*8, 3*8);
                    }
                    g2.drawImage(mapspriteImages[id], targetX, targetY, null);
                } 
                if(currentMode==MODE_SPRITE && currentSpritesetMode==SPRITESETMODE_ENEMY && i==selectedEnemy){
                    g2.setColor(Color.YELLOW);
                    g2.setStroke(new BasicStroke(3));
                    g2.drawRect((x+enemy.getX())*3*8, (y+enemy.getY())*3*8, 1*24, 1*24);
                }
            }
            for(int i=0;i<enemies.length;i++){
                Enemy enemy = enemies[i];
                drawAiTargets(g2, x, y, enemy.getX(), enemy.getY(), enemy.getMoveOrder1());
                drawAiTargets(g2, x, y, enemy.getX(), enemy.getY(), enemy.getMoveOrder2());
            }
        }
        return spritesImage;
    }
    
    private void drawAiTargets(Graphics2D g2, int mapOffsetX, int mapOffsetY, int enemyX, int enemyY, String order) {
        int target = 0;
        int targetX = -1, targetY = -1;
        String[] orderSplit = order.split("\\|");
        if (orderSplit.length > 1)
            target = Integer.parseInt(orderSplit[1]);
        switch (orderSplit[0]) {
            case "FOLLOW_TARGET":     //Follow target (ally)
                Ally[] allies = battle.getSpriteset().getAllies();
                if (target >= 0 && target < allies.length) {
                    targetX = allies[target].getX();
                    targetY = allies[target].getY();
                }
                break;
            case "FOLLOW_ENEMY":     //Follow enemy
                Enemy[] enemies = battle.getSpriteset().getEnemies();
                if (target >= 0 && target < enemies.length) {
                    targetX = enemies[target].getX();
                    targetY = enemies[target].getY();
                }
                break;
            case "MOVE_TO":
                AIPoint[] points = battle.getSpriteset().getAiPoints();
                if (target >= 0 && target < points.length) {
                    targetX = points[target].getX();
                    targetY = points[target].getY();
                }
                break;
            default:
                break;
        }        
        if (targetX >= 0 && targetY >= 0) {
            g2.setColor(Color.YELLOW);
            g2.setStroke(new BasicStroke(1));
            g2.drawLine((mapOffsetX + enemyX)*3*8+12, (mapOffsetY + enemyY)*3*8+12, (mapOffsetX+targetX)*3*8+12, (mapOffsetY+targetY)*3*8+12);
        }
    }
    
    private BufferedImage getAiRegionsImage(){
        if(aiRegionsImage==null){
            aiRegionsImage = new BufferedImage(3*8*64, 3*8*64, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = (Graphics2D) aiRegionsImage.getGraphics();
            int x = battle.getMapCoords().getX();
            int y = battle.getMapCoords().getY();            
            AIRegion[] regions = battle.getSpriteset().getAiRegions();
            for(int i=0;i<regions.length;i++){ 
                if(currentMode==MODE_SPRITE && currentSpritesetMode==SPRITESETMODE_AIREGION && i==selectedAIRegion){
                    AIRegion r = regions[i];
                    int x1 = x + r.getX1();
                    int y1 = y + r.getY1();
                    int x2 = x + r.getX2();
                    int y2 = y + r.getY2();
                    int x3 = x + r.getX3();
                    int y3 = y + r.getY3();
                    int x4 = x + r.getX4();
                    int y4 = y + r.getY4();
                    g2.setColor(Color.GREEN);
                    g2.setStroke(new BasicStroke(3));
                    g2.drawLine(x1*3*8+12, y1*3*8+12, x2*3*8+12, y2*3*8+12);
                    g2.drawLine(x2*3*8+12, y2*3*8+12, x3*3*8+12, y3*3*8+12);
                    g2.drawLine(x3*3*8+12, y3*3*8+12, x4*3*8+12, y4*3*8+12);
                    g2.drawLine(x4*3*8+12, y4*3*8+12, x1*3*8+12, y1*3*8+12);
                }
            }
        }
        return aiRegionsImage;
    }
    
    private BufferedImage getAiPointsImage(){
        if(aiPointsImage==null){
            aiPointsImage = new BufferedImage(3*8*64, 3*8*64, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = (Graphics2D) aiPointsImage.getGraphics();
            int x = battle.getMapCoords().getX();
            int y = battle.getMapCoords().getY();            
            AIPoint[] points = battle.getSpriteset().getAiPoints();
            for(int i=0;i<points.length;i++){ 
                if(currentMode==MODE_SPRITE && currentSpritesetMode==SPRITESETMODE_AIPOINT && i==selectedAIPoint){
                    AIPoint p = points[i];
                    int px = x + p.getX();
                    int py = y + p.getY();
                    g2.setColor(Color.GREEN);
                    g2.setStroke(new BasicStroke(3));
                    g2.drawRect(px*3*8, py*3*8, 3*8, 3*8);
                }
            }
        }
        return aiPointsImage;
    }
    
    @Override
    public void mousePressed(MouseEvent e) {
        int x = e.getX() / (displaySize * 3*8);
        int y = e.getY() / (displaySize * 3*8);
        int startX = battle.getMapCoords().getX();
        int startY = battle.getMapCoords().getY();
        int width = battle.getMapCoords().getWidth();
        int height = battle.getMapCoords().getHeight();  
        switch (currentMode) {
            
            case MODE_TERRAIN : 
                switch (e.getButton()) {
                    case MouseEvent.BUTTON1:
                        battle.getTerrain().getData()[(y-startY)*48+(x-startX)]++;
                        updateTerrainDisplay();
                        redraw = true;
                        this.revalidate();
                        this.repaint();
                        break;
                    case MouseEvent.BUTTON2:
                        lastMapX = x;
                        lastMapY = y;
                        break;
                    case MouseEvent.BUTTON3:
                        battle.getTerrain().getData()[(y-startY)*48+(x-startX)]--;
                        updateTerrainDisplay();
                        redraw = true;
                        this.revalidate();
                        this.repaint();
                        break;
                    default:
                        break;
                }           
                break;
               
            case MODE_SPRITE:
                switch (e.getButton()) {
                    case MouseEvent.BUTTON1:
                        if(currentSpritesetMode==SPRITESETMODE_ALLY && selectedAlly>=0){
                            alliesTable.setValueAt(x-battleCoordsOffsetX, selectedAlly, 1);
                            alliesTable.setValueAt(y-battleCoordsOffsetY, selectedAlly, 2);
                        }
                        if(currentSpritesetMode==SPRITESETMODE_ENEMY && selectedEnemy>=0){
                            enemiesTable.setValueAt(x-battleCoordsOffsetX, selectedEnemy, 1);
                            enemiesTable.setValueAt(y-battleCoordsOffsetY, selectedEnemy, 2);
                        }
                        break;
                    case MouseEvent.BUTTON2:

                        break;
                    case MouseEvent.BUTTON3:
                        break;
                    default:
                        break;
                }
                updateBattleDisplay();
                redraw = true;
                this.revalidate();
                this.repaint();
                break;
            
            default:
                break;
        }

        this.repaint();
        //System.out.println("Map press "+e.getButton()+" "+x+" - "+y);
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
        int endX = e.getX() / (displaySize * 3 * 8);
        int endY = e.getY() / (displaySize * 3 * 8);
        int startX = battle.getMapCoords().getX();
        int startY = battle.getMapCoords().getY();
        switch (currentMode) {
            
            case MODE_TERRAIN :           
                switch (e.getButton()) {
                    case MouseEvent.BUTTON2:
                        /* Zone change */
                        int xStart;
                        int xEnd;
                        int yStart;
                        int yEnd;
                        if(endX>lastMapX){
                            xStart = lastMapX;
                            xEnd = endX;
                        }else{
                            xStart = endX;
                            xEnd = lastMapX;
                        }
                        if(endY>lastMapY){
                            yStart = lastMapY;
                            yEnd = endY;
                        }else{
                            yStart = endY;
                            yEnd = lastMapY;
                        }           
                        System.out.println(xStart+":"+yStart+".."+xEnd+":"+yEnd+":"+applicableTerrainValue);
                        for(int y=yStart;y<=yEnd;y++){
                            for(int x=xStart;x<=xEnd;x++){
                                battle.getTerrain().getData()[(y-startY)*48+(x-startX)] = (byte)(applicableTerrainValue&0xFF);
                            }
                        }
                        updateTerrainDisplay();
                        this.redraw=true;
                        this.repaint();
                        break;
                    default:
                        break;
                } 
                //terrainImage = null;
                //redraw = true;
                //this.revalidate();
                //this.repaint();
                break;        
        
            default:
                break;
        }
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        
        int x = e.getX() / (displaySize * 3*8);
        int y = e.getY() / (displaySize * 3*8);
        
        if(x!=lastMouseX||y!=lastMouseY){
            lastMouseX=x;
            lastMouseY=y;
            titledBorder = (TitledBorder)(titledPanel.getBorder());
            titledBorder.setTitle("Cursor : "+x+","+y+"\t (battle : "+(x-battleCoordsOffsetX)+", "+(y-battleCoordsOffsetY)+")");
            titledPanel.revalidate();
            titledPanel.repaint();
            //System.out.println("New cursor pos : "+x+","+y);
        }
    }

    public MapBlock getSelectedBlock0() {
        return selectedBlock0;
    }

    public void setSelectedBlock0(MapBlock selectedBlock0) {
        this.selectedBlock0 = selectedBlock0;
    }

    public List<int[]> getActions() {
        return actions;
    }

    public void setActions(List<int[]> actions) {
        this.actions = actions;
    }

    public int getCurrentMode() {
        return currentMode;
    }

    public void setCurrentMode(int currentMode) {
        this.currentMode = currentMode;
    }

    public Battle getBattle() {
        return battle;
    }

    public void setBattle(Battle battle) {
        this.battle = battle;
    }

    public boolean isDrawSprites() {
        return drawSprites;
    }

    public void setDrawSprites(boolean drawSprites) {
        this.drawSprites = drawSprites;
        this.redraw = true;
    }
        
    public void updateBattleDisplay(){
        updateCoordsDisplay();
        updateTerrainDisplay();
        spritesImage = null;
        aiRegionsImage = null;
        aiPointsImage = null;
        this.redraw = true;
    }
    
    public void updateSpriteDisplay(){
        spritesImage = null;
        this.redraw = true;
    }
    
    public void updateAIRegionDisplay(){
        aiRegionsImage = null;
        this.redraw = true;
    }
    
    public void updateAIPointDisplay(){
        aiPointsImage = null;
        this.redraw = true;
    }

    public int getSelectedAlly() {
        return selectedAlly;
    }

    public void setSelectedAlly(int selectedAlly) {
        this.selectedAlly = selectedAlly;
    }

    public int getSelectedEnemy() {
        return selectedEnemy;
    }

    public void setSelectedEnemy(int selectedEnemy) {
        this.selectedEnemy = selectedEnemy;
    }

    public int getSelectedAIRegion() {
        return selectedAIRegion;
    }

    public void setSelectedAIRegion(int selectedAIRegion) {
        this.selectedAIRegion = selectedAIRegion;
    }

    public int getSelectedAIPoint() {
        return selectedAIPoint;
    }

    public void setSelectedAIPoint(int selectedAIPoint) {
        this.selectedAIPoint = selectedAIPoint;
    }

    public int getCurrentSpritesetMode() {
        return currentSpritesetMode;
    }

    public void setCurrentSpritesetMode(int currentSpritesetMode) {
        this.currentSpritesetMode = currentSpritesetMode;
    }

    public boolean isDrawAiRegions() {
        return drawAiRegions;
    }

    public void setDrawAiRegions(boolean drawAiRegions) {
        this.drawAiRegions = drawAiRegions;
    }

    public boolean isDrawAiPoints() {
        return drawAiPoints;
    }

    public void setDrawAiPoints(boolean drawAiPoints) {
        this.drawAiPoints = drawAiPoints;
    }

    public AllyPropertiesTableModel getAlliesTable() {
        return alliesTable;
    }

    public void setAlliesTable(AllyPropertiesTableModel alliesTable) {
        this.alliesTable = alliesTable;
    }

    public EnemyPropertiesTableModel getEnemiesTable() {
        return enemiesTable;
    }

    public void setEnemiesTable(EnemyPropertiesTableModel enemiesTable) {
        this.enemiesTable = enemiesTable;
    }

    public int getApplicableTerrainValue() {
        return applicableTerrainValue;
    }

    public void setApplicableTerrainValue(int applicableTerrainValue) {
        this.applicableTerrainValue = applicableTerrainValue;
    }

    public TitledBorder getTitledBorder() {
        return titledBorder;
    }

    public void setTitledBorder(TitledBorder titledBorder) {
        this.titledBorder = titledBorder;
    }

    public JPanel getTitledPanel() {
        return titledPanel;
    }

    public void setTitledPanel(JPanel titledPanel) {
        this.titledPanel = titledPanel;
    }
}
