/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oltranz.kvcs.simplebeans.commonbeans;

/**
 *
 * @author Hp
 */
public class RequestGetElementById {
    private String id;

    public RequestGetElementById() {
    }

    public RequestGetElementById(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
}
