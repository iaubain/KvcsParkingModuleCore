/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oltranz.kvcs.simplebeans.parkings;

import com.oltranz.kvcs.simplebeans.commonbeans.ParkingBean;

/**
 *
 * @author Hp
 */
public class RequestEditParking {
    private ParkingBean parking;

    public RequestEditParking() {
    }

    public RequestEditParking(ParkingBean parking) {
        this.parking = parking;
    }

    public ParkingBean getParking() {
        return parking;
    }

    public void setParking(ParkingBean parking) {
        this.parking = parking;
    }
    
}
