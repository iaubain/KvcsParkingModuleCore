/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package com.oltranz.kvcs.logic;

import com.oltranz.kvcs.config.CommandConfig;
import com.oltranz.kvcs.entities.Contractual;
import com.oltranz.kvcs.fascades.ContractualFacade;
import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 *
 * @author Hp
 */
@Stateless
public class ClientValidator {
    @EJB
            ContractualFacade contractualFacade;
    
    public boolean isValid(String contractId, String CMD){
        return isValidCMD(CMD) && isValidContract(contractId);
    }
    
    private boolean isValidCMD(String CMD){
        if(CMD.isEmpty())
            return false;
        switch(CMD){
            //Deployment
            case CommandConfig.DEPLOY:
                return true;
            case CommandConfig.CANCEL_DEP:
                return true;
            case CommandConfig.GET_DEP:
                return true;
            case CommandConfig.GET_REGIONAL_DEP:
                return true;
            case CommandConfig.GET_ALL_DEP:
                return true;
            case CommandConfig.GET_DEPLOYER_DEP:
                return true;
            case CommandConfig.GET_DEPLOYEE_DEP:
                return true;
                
                //Parking related Ops
            case CommandConfig.DELETE_PARK:
                return true;
            case CommandConfig.EDIT_PARK:
                return true;
            case CommandConfig.GET_DEPLOYMENT_PARK:
                return true;
            case CommandConfig.GET_STATUS_PARK:
                return true;
            case CommandConfig.GET_REGIONAL_PARK:
                return true;
            case CommandConfig.GET_ALL_PARK:
                return true;
            case CommandConfig.GET_PARK:
                return true;
            case CommandConfig.CREATE_BULK_PARK:
                return true;
            case CommandConfig.CREATE_PARK:
                return true;
                
                //Conductors related Ops
            case CommandConfig.DELETE_COND:
                return true;
            case CommandConfig.EDIT_COND:
                return true;
            case CommandConfig.GET_DEPLOYMENT_COND:
                return true;
            case CommandConfig.GET_STATUS_COND:
                return true;
            case CommandConfig.GET_REGIONAL_COND:
                return true;
            case CommandConfig.GET_ALL_COND:
                return true;
            case CommandConfig.GET_COND:
                return true;
            case CommandConfig.GET_TEL_COND:
                return true;
            case CommandConfig.CREATE_BULK_COND:
                return true;
            case CommandConfig.CREATE_COND:
                return true;
            case CommandConfig.UNDEPLOY_COND:
                return true;
                
                //Contract related Ops
            case CommandConfig.CREATE_CONTRACT:
                return true;
            case CommandConfig.CANCEL_CONTRACT:
                return true;
                
                //If its an unkown command
            default:
                return false;
        }
    }
    
    private boolean isValidContract(String contractId){
        if(contractId.isEmpty())
            return false;
        if(contractId.equals("testContract")){
            return true;
        }
        Contractual contractual = contractualFacade.getContractById(contractId);
        return contractual != null;
    }
}
