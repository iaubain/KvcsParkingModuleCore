/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oltranz.kvcs.simplebeans.contractBeans;

/**
 *
 * @author Hp
 */
public class CreateContractResponse {
    private String contractId;

    public CreateContractResponse() {
    }

    public CreateContractResponse(String contractId) {
        this.contractId = contractId;
    }

    /**
     * @return the contractId
     */
    public String getContractId() {
        return contractId;
    }

    /**
     * @param contractId the contractId to set
     */
    public void setContractId(String contractId) {
        this.contractId = contractId;
    }
    
}
