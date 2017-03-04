/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oltranz.kvcs.logic;

import com.oltranz.kvcs.config.AppDesc;
import com.oltranz.kvcs.config.StatusConfig;
import com.oltranz.kvcs.entities.Contractual;
import com.oltranz.kvcs.fascades.ContractualFacade;
import com.oltranz.kvcs.simplebeans.contractBeans.CancelContract;
import com.oltranz.kvcs.simplebeans.contractBeans.CreateContractRequest;
import com.oltranz.kvcs.simplebeans.contractBeans.CreateContractResponse;
import com.oltranz.kvcs.utilities.DataFactory;
import com.oltranz.kvcs.utilities.IdGenerator;
import static java.lang.System.out;
import java.util.Date;
import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 *
 * @author Hp
 */
@Stateless
public class MyContractual {

    @EJB
    ContractualFacade contractualFacade;
    @EJB
    IdGenerator idGenerator;
    
    public String createContract(String body){
        try{
            CreateContractRequest contractRequest = (CreateContractRequest) DataFactory.stringToObject(CreateContractRequest.class, body);
            if(contractRequest.getAppName() != null){
                //create contract
                String contractId = idGen();
              
                Contractual contractual = new Contractual(contractId, 
                        contractRequest.getAppName(), 
                        contractRequest.getLocation(), 
                        StatusConfig.CREATED, 
                        contractRequest.getDescription(), 
                        new Date(),
                        null);               
                contractualFacade.create(contractual);
                return DataFactory.objectToString(new CreateContractResponse(contractId));
            }else{
                return null;
            }                
        }catch(Exception e){
            out.print(AppDesc.APP_DESC+" Failed to map "+body+" due to "+ e.getLocalizedMessage());
            return null;
        }
    }
    
    public String cancelContract(String body){
        try{
            CancelContract cancelContract = (CancelContract) DataFactory.stringToObject(CancelContract.class, body);
            if(cancelContract != null){
                Contractual contractual = contractualFacade.getContractById(cancelContract.getContractId());
                contractual.setStatus(StatusConfig.CANCELED);
                contractual.setEnded(new Date());
                contractualFacade.edit(contractual);
                return "Cancelled";
            }
            return null;
        }catch(Exception e){
            out.print(AppDesc.APP_DESC+" Cancelling Contract Failed "+ body+" due to "+ e.getLocalizedMessage());
            return null;
        }
    }
    
    private String idGen(){
        return idGenerator.genContractId();
    }
}
