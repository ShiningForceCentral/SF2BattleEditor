/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.battle.gui;

import com.sfc.sf2.battle.AIPoint;
import com.sfc.sf2.battle.AIRegion;
import com.sfc.sf2.battle.Ally;
import com.sfc.sf2.battle.Battle;
import com.sfc.sf2.battle.Enemy;
import com.sfc.sf2.map.block.MapBlock;
import com.sfc.sf2.map.block.gui.BlockSlotPanel;
import com.sfc.sf2.map.block.layout.MapBlockLayout;
import com.sfc.sf2.map.layout.MapLayout;
import com.sfc.sf2.mapsprite.MapSprite;
import com.sfc.sf2.mapsprite.layout.MapSpriteLayout;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

/**
 *
 * @author wiz
 */
public class BattlePanel extends JPanel implements MouseListener, MouseMotionListener {
    
    private static final int DEFAULT_TILES_PER_ROW = 64*3;
    
    private static final int ACTION_CHANGE_BLOCK_VALUE = 0;
    private static final int ACTION_CHANGE_BLOCK_FLAGS = 1;
    private static final int ACTION_MASS_COPY = 2;
    
    int lastMapX = 0;
    int lastMapY = 0;
    
    private int lastMouseX = 0;
    private int lastMouseY = 0;
    private TitledBorder titledBorder = null;
    private JPanel titledPanel = null;
    
    public static final int MODE_NONE = 0;
    public static final int MODE_TERRAIN = 1;
    public static final int MODE_SPRITE = 2;
    
    private int currentMode = 0;
    
    public static final int SPRITESETMODE_ALLY = 0;
    public static final int SPRITESETMODE_ENEMY = 1;
    public static final int SPRITESETMODE_AIREGION = 2;
    public static final int SPRITESETMODE_AIPOINT = 3;
    private int currentSpritesetMode = 0;
    private int selectedAlly = -1;
    private int selectedEnemy = -1;
    private int selectedAIRegion = -1;
    private int selectedAIPoint = -1;
    
    private MapBlock selectedBlock0;
    MapBlock[][] copiedBlocks;
    
    private List<int[]> actions = new ArrayList<int[]>();
    
    private int tilesPerRow = DEFAULT_TILES_PER_ROW;
    private Battle battle;
    private MapLayout layout;
    private MapBlock[] blockset;
    private MapSprite[] mapsprites;
    private byte[] enemySpriteIds;
    private int currentDisplaySize = 1;
    
    private int applicableTerrainValue = -1;
    
    private BufferedImage currentImage;
    private boolean redraw = true;
    private int renderCounter = 0;
    private boolean drawExplorationFlags = true;
    private boolean drawInteractionFlags = false;
    private boolean drawGrid = false;
    private boolean drawActionFlags = false;
    private boolean drawCoords = true;
    private boolean drawTerrain = true;
    private boolean drawSprites = true;
    private boolean drawAiRegions = true;
    private boolean drawAiPoints = true;
    
    private BufferedImage gridImage;
    private BufferedImage coordsImage;
    private BufferedImage terrainImage;
    private BufferedImage obstructedImage;
    private BufferedImage leftUpstairsImage;
    private BufferedImage rightUpstairsImage;
    private BufferedImage spritesImage;
    private BufferedImage aiRegionsImage;
    private BufferedImage aiPointsImage;
    
    private BufferedImage[] mapspriteImages = new BufferedImage[256];
    
    private AllyPropertiesTableModel alliesTable = null;
    private EnemyPropertiesTableModel enemiesTable = null;
    
    
    
    

    public BattlePanel() {
        addMouseListener(this);
        addMouseMotionListener(this);
    }
   
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);   
        g.drawImage(buildImage(), 0, 0, this);       
    }
    
    public BufferedImage buildImage(){
        if(redraw){
            currentImage = buildImage(this.battle,this.tilesPerRow, false);
            setSize(currentImage.getWidth(), currentImage.getHeight());
        }
        return currentImage;
    }
    
    public BufferedImage buildImage(Battle battle, int tilesPerRow, boolean pngExport){
        renderCounter++;
        System.out.println("Map render "+renderCounter);
        this.battle = battle;
        if(redraw){
            MapBlock[] blocks = layout.getBlocks();
            int imageHeight = 64*3*8;
            Color[] palette = blocks[0].getTiles()[0].getPalette();
            palette[0] = new Color(255, 255, 255, 0);
            IndexColorModel icm = buildIndexColorModel(palette);
            currentImage = new BufferedImage(tilesPerRow*8, imageHeight , BufferedImage.TYPE_INT_ARGB);
            Graphics graphics = currentImage.getGraphics();            
            for(int y=0;y<64;y++){
                for(int x=0;x<64;x++){
                    MapBlock block = blocks[y*64+x];
                    BufferedImage blockImage = block.getImage();
                    BufferedImage explorationFlagImage = block.getExplorationFlagImage();
                    BufferedImage interactionFlagImage = block.getInteractionFlagImage();
                    if(blockImage==null){
                        blockImage = new BufferedImage(3*8, 3*8 , BufferedImage.TYPE_BYTE_INDEXED, icm);
                        Graphics blockGraphics = blockImage.getGraphics();                    
                        blockGraphics.drawImage(block.getTiles()[0].getImage(), 0*8, 0*8, null);
                        blockGraphics.drawImage(block.getTiles()[1].getImage(), 1*8, 0*8, null);
                        blockGraphics.drawImage(block.getTiles()[2].getImage(), 2*8, 0*8, null);
                        blockGraphics.drawImage(block.getTiles()[3].getImage(), 0*8, 1*8, null);
                        blockGraphics.drawImage(block.getTiles()[4].getImage(), 1*8, 1*8, null);
                        blockGraphics.drawImage(block.getTiles()[5].getImage(), 2*8, 1*8, null);
                        blockGraphics.drawImage(block.getTiles()[6].getImage(), 0*8, 2*8, null);
                        blockGraphics.drawImage(block.getTiles()[7].getImage(), 1*8, 2*8, null);
                        blockGraphics.drawImage(block.getTiles()[8].getImage(), 2*8, 2*8, null);
                        block.setImage(blockImage);
                    }
                    graphics.drawImage(blockImage, x*3*8, y*3*8, null);
                    if(drawExplorationFlags){
                        int explorationFlags = block.getFlags()&0xC000;
                        if(explorationFlagImage==null){
                            explorationFlagImage = new BufferedImage(3*8, 3*8, BufferedImage.TYPE_INT_ARGB);
                            Graphics2D g2 = (Graphics2D) explorationFlagImage.getGraphics();
                            switch (explorationFlags) {
                                case 0xC000:
                                    g2.drawImage(getObstructedImage(), 0, 0, null);
                                    break;
                                case 0x8000:
                                    g2.drawImage(getRightUpstairs(), 0, 0, null);
                                    break;
                                case 0x4000:
                                    g2.drawImage(getLeftUpstairs(), 0, 0, null);
                                    break;
                                default:
                                    break;
                            }
                            block.setExplorationFlagImage(explorationFlagImage);
                        }
                        graphics.drawImage(explorationFlagImage, x*3*8, y*3*8, null); 
                    }                    
                }
                   
            } 
            if(drawGrid){
                graphics.drawImage(getGridImage(), 0, 0, null);
            }
            if(drawCoords){
                graphics.drawImage(getCoordsImage(),0,0,null);
            }
            if(drawTerrain){
                graphics.drawImage(getTerrainImage(),0,0,null);
            }
            if(drawSprites){
                graphics.drawImage(getSpritesImage(),0,0,null);
            }
            if(drawAiRegions){
                graphics.drawImage(getAiRegionsImage(),0,0,null);
            }
            if(drawAiPoints){
                graphics.drawImage(getAiPointsImage(),0,0,null);
            }
            redraw = false;
            currentImage = resize(currentImage);
        }
                  
        return currentImage;
    }
    
    private BufferedImage getGridImage(){
        if(gridImage==null){
            gridImage = new BufferedImage(3*8*64, 3*8*64, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = (Graphics2D) gridImage.getGraphics(); 
            g2.setColor(Color.BLACK);
            for(int i=0;i<64;i++){
                g2.drawLine(3*8+i*3*8, 0, 3*8+i*3*8, 3*8*64-1);
                g2.drawLine(0, 3*8+i*3*8, 3*8*64-1, 3*8+i*3*8);
            }
        }
        return gridImage;
    }
    
    private BufferedImage getCoordsImage(){
        if(coordsImage==null){
            coordsImage = new BufferedImage(3*8*64, 3*8*64, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = (Graphics2D) coordsImage.getGraphics();
            g2.setStroke(new BasicStroke(3)); 
            g2.setColor(Color.YELLOW);
            int width = battle.getMapCoords().getWidth();
            int heigth = battle.getMapCoords().getHeight();
            g2.drawRect(battle.getMapCoords().getX()*24 + 3, battle.getMapCoords().getY()*24+3, width*24-6, heigth*24-6);
            g2.setColor(Color.ORANGE);
            if(battle.getMapCoords().getTrigX() < 64 && battle.getMapCoords().getTrigY() < 64){
                g2.drawRect(battle.getMapCoords().getTrigX()*24 + 3, battle.getMapCoords().getTrigY()*24+3, 1*24-6, 1*24-6);
            }            
        }
        return coordsImage;
    }
    
    private BufferedImage getTerrainImage(){
        if(terrainImage==null){
            terrainImage = new BufferedImage(3*8*64, 3*8*64, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = (Graphics2D) terrainImage.getGraphics();
            byte[] data = battle.getTerrain().getData();
            int width = battle.getMapCoords().getWidth();
            int height = battle.getMapCoords().getHeight();
            int x = battle.getMapCoords().getX();
            int y = battle.getMapCoords().getY();
            for(int i=0;i<height;i++){
                for(int j=0;j<width;j++){
                    int value = data[i*48+j];
                    //Font font = new Font("Courier", Font.BOLD, 12);
                    //g2.setFont(font);
                    int targetX = (x+j)*3*8+16-8;
                    int targetY = (y+i)*3*8+16;
                    String val = String.valueOf(value);
                    g2.setColor(Color.black);
                    g2.drawString(val, targetX-1, targetY-1);
                    g2.drawString(val, targetX-1, targetY+1);
                    g2.drawString(val, targetX+1, targetY-1);
                    g2.drawString(val, targetX+1, targetY+1);
                    g2.setColor(Color.white);
                    g2.drawString(val, targetX, targetY);
                }
            }
        }
        return terrainImage;
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
                int targetX = (x+ally.getX())*3*8+16-8;
                int targetY = (y+ally.getY())*3*8+16;
                String val = String.valueOf(ally.getIndex()+1);
                g2.setColor(Color.black);
                g2.drawString(val, targetX-1, targetY-1);
                g2.drawString(val, targetX-1, targetY+1);
                g2.drawString(val, targetX+1, targetY-1);
                g2.drawString(val, targetX+1, targetY+1);
                g2.setColor(Color.yellow);
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
                int spriteId = (enemySpriteIds[enemy.getIndex()]&0xFF);
                if(spriteId*3>(mapsprites.length)){
                    g2.setColor(Color.black);
                    g2.drawString(String.valueOf(spriteId), targetX-1, targetY+16-1);
                    g2.drawString(String.valueOf(spriteId), targetX-1, targetY+16+1);
                    g2.drawString(String.valueOf(spriteId), targetX+1, targetY+16-1);
                    g2.drawString(String.valueOf(spriteId), targetX+1, targetY+16+1);
                    g2.setColor(Color.RED);
                    g2.drawString(String.valueOf(spriteId), targetX, targetY+16);  
                }else{
                    if(mapspriteImages[spriteId]==null){
                        mapspriteImages[spriteId] = MapSpriteLayout.buildImage(mapsprites[spriteId*3+2].getTiles(), 6).getSubimage(0, 0, 3*8, 3*8);
                    }
                    g2.drawImage(mapspriteImages[spriteId], targetX, targetY, null); 
                } 
                if(currentMode==MODE_SPRITE && currentSpritesetMode==SPRITESETMODE_ENEMY && i==selectedEnemy){
                    g2.setColor(Color.YELLOW);
                    g2.setStroke(new BasicStroke(3));
                    g2.drawRect((x+enemy.getX())*3*8, (y+enemy.getY())*3*8, 1*24, 1*24);
                }
            }
        }
        return spritesImage;
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
    
    private BufferedImage getObstructedImage(){
        if(obstructedImage==null){
            obstructedImage = new BufferedImage(3*8, 3*8, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = (Graphics2D) obstructedImage.getGraphics();  
            g2.setColor(Color.RED);
            Line2D line1 = new Line2D.Double(6, 6, 18, 18);
            g2.draw(line1);
            Line2D line2 = new Line2D.Double(6, 18, 18, 6);
            g2.draw(line2);
        }
        return obstructedImage;
    }
    
    private BufferedImage getLeftUpstairs(){
        if(leftUpstairsImage==null){
            leftUpstairsImage = new BufferedImage(3*8, 3*8, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = (Graphics2D) leftUpstairsImage.getGraphics();  
            g2.setColor(Color.CYAN);
            g2.setStroke(new BasicStroke(3));
            Line2D line1 = new Line2D.Double(3, 3, 21, 21);
            g2.draw(line1);
        }
        return leftUpstairsImage;
    }   
    
    private BufferedImage getRightUpstairs(){
        if(rightUpstairsImage==null){
            rightUpstairsImage = new BufferedImage(3*8, 3*8, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = (Graphics2D) rightUpstairsImage.getGraphics();  
            g2.setColor(Color.CYAN);
            g2.setStroke(new BasicStroke(3));
            Line2D line1 = new Line2D.Double(3, 21, 21, 3);
            g2.draw(line1);
        }
        return rightUpstairsImage;
    }     
    
    private IndexColorModel buildIndexColorModel(Color[] colors){
        byte[] reds = new byte[16];
        byte[] greens = new byte[16];
        byte[] blues = new byte[16];
        byte[] alphas = new byte[16];
        reds[0] = (byte)0xFF;
        greens[0] = (byte)0xFF;
        blues[0] = (byte)0xFF;
        alphas[0] = 0;
        for(int i=1;i<16;i++){
            reds[i] = (byte)colors[i].getRed();
            greens[i] = (byte)colors[i].getGreen();
            blues[i] = (byte)colors[i].getBlue();
            alphas[i] = (byte)0xFF;
        }
        IndexColorModel icm = new IndexColorModel(4,16,reds,greens,blues,alphas);
        return icm;
    }    
    
    public void resize(int size){
        this.currentDisplaySize = size;
        currentImage = resize(currentImage);
    }
    
    private BufferedImage resize(BufferedImage image){
        BufferedImage newImage = new BufferedImage(image.getWidth()*currentDisplaySize, image.getHeight()*currentDisplaySize, BufferedImage.TYPE_INT_ARGB);
        Graphics g = newImage.getGraphics();
        g.drawImage(image, 0, 0, image.getWidth()*currentDisplaySize, image.getHeight()*currentDisplaySize, null);
        g.dispose();
        return newImage;
    }    
    
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(getWidth(), getHeight());
    }
    
    public int getTilesPerRow() {
        return tilesPerRow;
    }

    public void setTilesPerRow(int tilesPerRow) {
        this.tilesPerRow = tilesPerRow;
    }

    public int getCurrentDisplaySize() {
        return currentDisplaySize;
    }

    public void setCurrentDisplaySize(int currentDisplaySize) {
        this.currentDisplaySize = currentDisplaySize;
        redraw = true;
    }

    public MapLayout getMapLayout() {
        return layout;
    }

    public void setMapLayout(MapLayout layout) {
        this.layout = layout;
    }


    @Override
    public void mouseClicked(MouseEvent e) {
    }
    @Override
    public void mouseEntered(MouseEvent e) {

    }
    @Override
    public void mouseExited(MouseEvent e) {

    }
    @Override
    public void mousePressed(MouseEvent e) {
        int x = e.getX() / (currentDisplaySize * 3*8);
        int y = e.getY() / (currentDisplaySize * 3*8);
        int startX = battle.getMapCoords().getX();
        int startY = battle.getMapCoords().getY();
        int width = battle.getMapCoords().getWidth();
        int height = battle.getMapCoords().getHeight();  
        switch (currentMode) {
            
            case MODE_TERRAIN : 
                switch (e.getButton()) {
                    case MouseEvent.BUTTON1:
                        battle.getTerrain().getData()[(y-startY)*48+(x-startX)]++;
                        terrainImage = null;
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
                        terrainImage = null;
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
                            alliesTable.setValueAt(x, selectedAlly, 1);
                            alliesTable.setValueAt(y, selectedAlly, 2);
                        }
                        if(currentSpritesetMode==SPRITESETMODE_ENEMY && selectedEnemy>=0){
                            enemiesTable.setValueAt(x, selectedEnemy, 1);
                            enemiesTable.setValueAt(y, selectedEnemy, 2);
                        }
                        break;
                    case MouseEvent.BUTTON2:

                        break;
                    case MouseEvent.BUTTON3:
                        break;
                    default:
                        break;
                } 
                terrainImage = null;
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
        int endX = e.getX() / (currentDisplaySize * 3 * 8);
        int endY = e.getY() / (currentDisplaySize * 3 * 8);
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
                        terrainImage = null;
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
        
        int x = e.getX() / (currentDisplaySize * 3*8);
        int y = e.getY() / (currentDisplaySize * 3*8);
        
        if(x!=lastMouseX||y!=lastMouseY){
            lastMouseX=x;
            lastMouseY=y;
            titledBorder = (TitledBorder)(titledPanel.getBorder());
            titledBorder.setTitle("Cursor : "+x+","+y);
            titledPanel.revalidate();
            titledPanel.repaint();
            //System.out.println("New cursor pos : "+x+","+y);
        }
        
    }

    public MapBlock[] getBlockset() {
        return blockset;
    }

    public void setBlockset(MapBlock[] blockset) {
        this.blockset = blockset;
    }

    public boolean isDrawExplorationFlags() {
        return drawExplorationFlags;
    }

    public void setDrawExplorationFlags(boolean drawExplorationFlags) {
        this.drawExplorationFlags = drawExplorationFlags;
        this.redraw = true;
    }
    public boolean isDrawInteractionFlags() {
        return drawInteractionFlags;
    }

    public void setDrawInteractionFlags(boolean drawInteractionFlags) {
        this.drawInteractionFlags = drawInteractionFlags;
        this.redraw = true;
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

    public boolean isRedraw() {
        return redraw;
    }

    public void setRedraw(boolean redraw) {
        this.redraw = redraw;
    }

    public boolean isDrawGrid() {
        return drawGrid;
    }

    public void setDrawGrid(boolean drawGrid) {
        this.drawGrid = drawGrid;
        this.redraw = true;
    }

    public int getCurrentMode() {
        return currentMode;
    }

    public void setCurrentMode(int currentMode) {
        this.currentMode = currentMode;
    }
    
    public boolean isDrawActionFlags() {
        return drawActionFlags;
    }

    public void setDrawActionFlags(boolean drawActionFlags) {
        this.drawActionFlags = drawActionFlags;
    }

    public Battle getBattle() {
        return battle;
    }

    public void setBattle(Battle battle) {
        this.battle = battle;
    }

    public boolean isDrawTerrain() {
        return drawTerrain;
    }

    public void setDrawTerrain(boolean drawTerrain) {
        this.drawTerrain = drawTerrain;
        this.redraw = true;
    }

    public boolean isDrawSprites() {
        return drawSprites;
    }

    public void setDrawSprites(boolean drawSprites) {
        this.drawSprites = drawSprites;
        this.redraw = true;
    }
        
    public void updateCoordsDisplay(){
        coordsImage = null;
        terrainImage = null;
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

    public MapSprite[] getMapsprites() {
        return mapsprites;
    }

    public void setMapsprites(MapSprite[] mapsprites) {
        this.mapsprites = mapsprites;
    }

    public byte[] getEnemySpriteIds() {
        return enemySpriteIds;
    }

    public void setEnemySpriteIds(byte[] enemySpriteIds) {
        this.enemySpriteIds = enemySpriteIds;
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
