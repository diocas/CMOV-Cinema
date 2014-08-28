/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pt.feup.cmov.server;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author diogo
 */
public abstract class Arrabida20 {
    
    public enum Location {
        FrontLeft,
        FrontCenter,
        FrontRight,
        BackLeft,
        BackCenter,
        BackRight;
    }
    
    public static Integer nLugares = 10;
    public static String[][] rows = {{"A", "B", "C"},{"D", "E", "F"}};
    
    
    public static ArrayList<String> getSeats(String room, Location location){
        
        ArrayList<String> list = new ArrayList<String>();
        String[] roomRows = rows[0];
        Integer startSeat = 1;
        
        switch (location) {
            case FrontCenter:
                startSeat = 11;
                break;
                
            case FrontRight:
                startSeat = 21;
                break;
                
            case BackLeft:
                roomRows = rows[1];
                break;
                
            case BackCenter:
                roomRows = rows[1];
                startSeat = 11;
                break;
                
            case BackRight:
                roomRows = rows[1];
                startSeat = 21;
                break;
        }
        
        for(String row : roomRows) {
            for(int i = startSeat; i < startSeat + nLugares; i++) {
                list.add(row + Integer.toString(i));
            }
        }
        
        return list;
    }
    
    public static Integer getNumSeats (String room) {
        return 180;
    }
    
}
