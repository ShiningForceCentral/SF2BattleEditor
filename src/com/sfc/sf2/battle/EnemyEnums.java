/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sfc.sf2.battle;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author TiMMy
 */
public class EnemyEnums {
    
    private Map<String, Integer> items;
    private Map<String, Integer> aiCommandSets;
    private Map<String, Integer> aiOrders;
    private Map<String, Integer> spawnParams;

    public Map<String, Integer> getItems() {
        return items;
    }

    public void setItems(Map<String, Integer> items) {
        this.items = items;
    }

    public Map<String, Integer> getCommandSets() {
        return aiCommandSets;
    }

    public void setCommandSets(Map<String, Integer> aiCommandSets) {
        this.aiCommandSets = aiCommandSets;
    }

    public Map<String, Integer> getOrders() {
        return aiOrders;
    }

    public void setOrders(Map<String, Integer> aiOrders) {
        this.aiOrders = aiOrders;
    }

    public Map<String, Integer> getSpawnParams() {
        return spawnParams;
    }

    public void setSpawnParams(Map<String, Integer> spawnParams) {
        this.spawnParams = spawnParams;
    }
    
    //Helper functions
    
    public static String toEnumString(String data, Map<String, Integer> en) {
        try{
            short number = Short.parseShort(data);
            return toEnumString(number, en);
        }
        catch (NumberFormatException ex){
            //Not a number
            return data;
        }
    }
    
    public static String toEnumString(byte data, Map<String, Integer> en) {
        return toEnumString((short)data, en);
    }
    
    public static String toEnumString(short data, Map<String, Integer> en) {
        int number = (int)data;
        for (Map.Entry<String, Integer> entry : en.entrySet()) {
            if (number == entry.getValue())
                return entry.getKey();
        }

        return String.valueOf(data);    //Fallback
    }
    
    public static byte toEnumByte(String data, Map<String, Integer> en) {
        return (byte)(toEnumShort(data, en)&0xFF);
    }
    
    public static short toEnumShort(String data, Map<String, Integer> en) {
        try{
            short number = Short.parseShort(data);
            return number;
        }
        catch (NumberFormatException ex){
            //Not a number
        }
        
        String[] split = data.split("|");
        short value = 0;
        for (int i = 0; i < split.length; i++) {
            if (en.containsKey(split[i]))
                value = (short)(en.get(split[i])&0xFFFF);
        }
        
        return value;
    }
    
    public static String stringToItemString(String data, Map<String, Integer> items) {
        try{
            short number = Short.parseShort(data);
            return itemNumToString(number, items);
        }
        catch (NumberFormatException ex){
            //Not a number
            return toEnumString(data, items);
        }
    }
    
    public static String itemNumToString(short data, Map<String, Integer> items) {
        String s = "";
        
        if (data >= 0x1000){  //If flags
            for (Map.Entry<String, Integer> entry : items.entrySet()) {
                if ((data&entry.getValue()) != 0){
                    s = appendItem(s, entry.getKey());
                    data = (short)(data&(~entry.getValue()));
                }
            }

            for (Map.Entry<String, Integer> entry : items.entrySet()) {
                if ((data == entry.getValue())){
                    s = prependItem(s, entry.getKey());
                    return s;
                }
            }
        }

        return Short.toString(data);    //Fallback
    }
    
    public static short itemStringToNum(String data, Map<String, Integer> items) {
        String[] split = data.split("|");
                
        short value = 0;
        for (int i = 0; i < split.length; i++) {
            split[i] = split[i].trim();
            
            if (items.containsKey(split[i])) {
                    value += items.get(split[i]);
            }
        }
        
        return value;
    }
    
    public static String stringToAiOrderString(String data, Map<String, Integer> orders) {
        try{
            byte number = Byte.parseByte(data);
            return aiOrderNumToString(number, orders);
        }
        catch (NumberFormatException ex){
            //Not a number
            return toEnumString(data, orders);
        }
    }
    
    public static String aiOrderNumToString(byte data, Map<String, Integer> orders) {        
        byte value1;
        byte value2;
        
        if (data == 0xFF){
            value1 = data;
            value2 = 0;
        }
        else{
            value1 = (byte)(data&0xF0);
            value2 = (byte)(data&0x0F);
        }
        
        String s = Byte.toString(value2);
        for (Map.Entry<String, Integer> entry : orders.entrySet()) {
            if ((value1 == entry.getValue())){
                s = prependItem(s, entry.getKey());
                return s;
            }
        }
        
        return Byte.toString(data); //Fallback
    }
    
    public static byte aiOrderStringToNum(String data, Map<String, Integer> orders) {
        String[] split = data.split("|");
        if (split.length < 2)
            return toEnumByte(split[0], orders);
        
        byte value = (byte)(Byte.valueOf(split[1])&0x0F);
        if (orders.containsKey(split[0])){
            if (orders.get(split[0]) == 0xFF)
                value = (byte)0xFF;
            else
                value += (byte)(orders.get(split[0])&0xF0);
        }
        
        return value;
    }
    
    private static String appendItem(String current, String append)
    {
        if (current.length() == 0)
            return append;
        else
            return current+"|"+append;
    }
    
    private static String prependItem(String current, String prepend)
    {
        if (current.length() == 0)
            return prepend;
        else
            return prepend+"|"+current;
    }
}
