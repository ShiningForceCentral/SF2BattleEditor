/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.battle.gui;

import com.sfc.sf2.battle.Battle;
import com.sfc.sf2.map.block.MapBlock;
import com.sfc.sf2.map.block.gui.BlockSlotPanel;
import com.sfc.sf2.map.block.layout.MapBlockLayout;
import com.sfc.sf2.map.layout.MapLayout;
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
    
    public static final int MODE_NONE = 0;
    public static final int MODE_TERRAIN = 1;
    public static final int MODE_SPRITE = 2;
    
    BlockSlotPanel leftSlot = null;
    
    private int currentMode = 0;
    
    private MapBlock selectedBlock0;
    MapBlock[][] copiedBlocks;
    
    private List<int[]> actions = new ArrayList<int[]>();
    
    private int tilesPerRow = DEFAULT_TILES_PER_ROW;
    private Battle battle;
    private MapLayout layout;
    private MapBlock[] blockset;
    private int currentDisplaySize = 1;
    
    private BufferedImage currentImage;
    private boolean redraw = true;
    private int renderCounter = 0;
    private boolean drawExplorationFlags = true;
    private boolean drawInteractionFlags = false;
    private boolean drawGrid = false;
    private boolean drawActionFlags = false;
    private boolean drawCoords = true;
    private boolean drawTerrain = false;
    private boolean drawSprites = false;
    
    private BufferedImage gridImage;
    private BufferedImage coordsImage;
    private BufferedImage terrainImage;
    private BufferedImage obstructedImage;
    private BufferedImage leftUpstairsImage;
    private BufferedImage rightUpstairsImage;
    private BufferedImage spritesImage;

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
            g2.setColor(Color.BLACK);
            for(int i=0;i<64;i++){
                g2.drawLine(3*8+i*3*8, 0, 3*8+i*3*8, 3*8*64-1);
                g2.drawLine(0, 3*8+i*3*8, 3*8*64-1, 3*8+i*3*8);
            }
        }
        return spritesImage;
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
                        battle.getTerrain().getData()[(startY+y)*48+startX+x]++;
                        break;
                    case MouseEvent.BUTTON2:

                        break;
                    case MouseEvent.BUTTON3:
                        battle.getTerrain().getData()[(startY+y)*48+startX+x]--;
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
       
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        
    }
    
    private void updateLeftSlot(MapBlock block){
        BufferedImage img = new BufferedImage(3*8,3*8,BufferedImage.TYPE_INT_ARGB);
        Graphics g = img.getGraphics();
        g.drawImage(block.getImage(), 0, 0, null);
        g.drawImage(block.getExplorationFlagImage(), 0, 0, null);
        leftSlot.setBlockImage(img);
        leftSlot.revalidate();
        leftSlot.repaint(); 
    }
    
    public void setBlockValue(int x, int y, int value){
        MapBlock[] blocks = layout.getBlocks();
        MapBlock block = blocks[y*64+x];
        if(block.getIndex()!=value){
            int[] action = new int[3];
            action[0] = ACTION_CHANGE_BLOCK_VALUE;
            action[1] = y*64+x;
            action[2] = block.getIndex();
            block.setIndex(value);
            block.setImage(null);
            block.setTiles(blockset[block.getIndex()].getTiles());
            actions.add(action);
            redraw = true;
        }
    }
    
    public void setFlagValue(int x, int y, int value){
        MapBlock[] blocks = layout.getBlocks();
        MapBlock block = blocks[y*64+x];
        if(block.getFlags()!=value){
            int[] action = new int[3];
            action[0] = ACTION_CHANGE_BLOCK_FLAGS;
            action[1] = y*64+x;
            int origFlags = block.getFlags();
            action[2] = origFlags;
            int newFlags = (0xC000 & value) + (0x3C00 & origFlags);
            block.setFlags(newFlags);
            block.setExplorationFlagImage(null);
            actions.add(action);
            redraw = true;
        }
    }
    
    public void clearFlagValue(int x, int y){
        MapBlock[] blocks = layout.getBlocks();
        MapBlock block = blocks[y*64+x];
        if(block.getFlags()!=0){
            int[] action = new int[3];
            action[0] = ACTION_CHANGE_BLOCK_FLAGS;
            action[1] = y*64+x;
            int origFlags = block.getFlags();
            action[2] = origFlags;
            int newFlags = 0;
            block.setFlags(newFlags);
            block.setExplorationFlagImage(null);
            actions.add(action);
            redraw = true;
        }
    }
    
    public void revertLastAction(){
        if(actions.size()>0){
            int[] action = actions.get(actions.size()-1);
            switch (action[0]) {
                case ACTION_CHANGE_BLOCK_VALUE:
                    {
                        MapBlock block = layout.getBlocks()[action[1]];
                        block.setIndex(action[2]);
                        block.setImage(null);
                        block.setTiles(blockset[block.getIndex()].getTiles());
                        actions.remove(actions.size()-1);
                        redraw = true;
                        this.repaint();
                        break;
                    }
                case ACTION_CHANGE_BLOCK_FLAGS:
                    {
                        MapBlock block = layout.getBlocks()[action[1]];
                        block.setFlags(action[2]);               
                        block.setExplorationFlagImage(null);
                        block.setInteractionFlagImage(null);
                        actions.remove(actions.size()-1);
                        redraw = true;
                        this.repaint();
                        break;
                    }
                case ACTION_MASS_COPY:
                    int blockIndex = action[1];
                    int width = action[2];
                    int height = action[3];
                    for(int j=0;j<height;j++){
                        for(int i=0;i<width;i++){
                            int value = action[4+2*(j*width+i)];
                            int flags = action[4+2*(j*width+i)+1];
                            if(value != -1 && flags != -1){
                                MapBlock block = new MapBlock();
                                block.setIndex(value);
                                block.setFlags(flags);
                                block.setTiles(blockset[block.getIndex()].getTiles());
                                layout.getBlocks()[blockIndex+j*64+i] = block;
                            }
                        }
                    }   actions.remove(actions.size()-1);
                    redraw = true;
                    this.repaint();
                    break;
                default:
                    break;
            }
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

    public BlockSlotPanel getLeftSlot() {
        return leftSlot;
    }

    public void setLeftSlot(BlockSlotPanel leftSlot) {
        this.leftSlot = leftSlot;
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
        this.redraw = true;
    }
    
}
