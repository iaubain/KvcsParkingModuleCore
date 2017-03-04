/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package com.oltranz.kvcs.logic;

import com.oltranz.kvcs.config.AppDesc;
import com.oltranz.kvcs.config.CommandConfig;
import com.oltranz.kvcs.utilities.ReturnConfig;
import static java.lang.System.out;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.core.Response;

/**
 *
 * @author Hp
 */
@Stateless
public class CommandExec {
    @EJB
            ConductorsLogic conductorsLogic;
    @EJB
            DeploymentLogic deploymentLogic;
    @EJB
            ParkingLogic parkingLogic;
    @EJB
            MyContractual myContractual;
    
    public Response exec(String contractId, String cmd, String body){
        out.print(AppDesc.APP_DESC+" Command Handler executing "+cmd+" and its corresponding body: "+body);
        switch(cmd){
            
            //Conductors Command
            case CommandConfig.CREATE_COND:
                return conductorsLogic.createConductor(contractId, body);
            case CommandConfig.CREATE_BULK_COND:
                return conductorsLogic.createBulkConductors(contractId, body);
            case CommandConfig.GET_COND:
                return conductorsLogic.getConductor(contractId, body);
            case CommandConfig.GET_TEL_COND:
                return conductorsLogic.getConductorByTel(contractId, body);
            case CommandConfig.GET_ALL_COND:
                return conductorsLogic.getAllConductor(contractId, body);
            case CommandConfig.GET_REGIONAL_COND:
                return conductorsLogic.getRegionalConductors(contractId, body);
            case CommandConfig.GET_STATUS_COND:
                return conductorsLogic.getConductorByStatus(contractId, body);
            case CommandConfig.GET_DEPLOYMENT_COND:
                return conductorsLogic.getConductorDeployment(contractId, body);
            case CommandConfig.EDIT_COND:
                return conductorsLogic.editConductor(contractId, body);
            case CommandConfig.DELETE_COND:
                return conductorsLogic.deleteConductor(contractId, body);
            case CommandConfig.UNDEPLOY_COND:
                return conductorsLogic.undeployConductor(contractId, body);
                
                //Contract Commands
            case CommandConfig.CREATE_CONTRACT:
                return createContract(body);
            case CommandConfig.CANCEL_CONTRACT:
                return cancelContract(body);
                
                //Parking Commands
            case CommandConfig.CREATE_PARK:
                return parkingLogic.createParking(contractId, body);
            case CommandConfig.CREATE_BULK_PARK:
                return parkingLogic.createBulkParking(contractId, body);
            case CommandConfig.GET_PARK:
                return parkingLogic.getParking(contractId, body);
            case CommandConfig.GET_ALL_PARK:
                return parkingLogic.getAllParkings(contractId, body);
            case CommandConfig.GET_REGIONAL_PARK:
                return parkingLogic.getRegionalParkings(contractId, body);
            case CommandConfig.GET_STATUS_PARK:
                return ReturnConfig.isFailed(Response.Status.NO_CONTENT, "No content available, service underconstruction");
            case CommandConfig.GET_DEPLOYMENT_PARK:
                return ReturnConfig.isFailed(Response.Status.NO_CONTENT, "Service migrated into deployments.");
            case CommandConfig.EDIT_PARK:
                return parkingLogic.editParking(contractId, body);
            case CommandConfig.DELETE_PARK:
                return parkingLogic.deleteParking(contractId, body);
                
                //Deployment commands
            case CommandConfig.DEPLOY:
                return deploymentLogic.createDeployment(contractId, body);
            case CommandConfig.CANCEL_DEP:
                return deploymentLogic.cancelDeployment(contractId, body);
            case CommandConfig.GET_DEP:
                return deploymentLogic.getDeploymentPerId(contractId, body);
            case CommandConfig.GET_REGIONAL_DEP:
                return deploymentLogic.getRegionDeployments(contractId, body);
            case CommandConfig.GET_ALL_DEP:
                return deploymentLogic.getAllDeployments(contractId, body);
            case CommandConfig.GET_DEPLOYER_DEP:
                return deploymentLogic.getAllDeploymentByDeployer(contractId, body);
            case CommandConfig.GET_DEPLOYEE_DEP:
                return deploymentLogic.getAllDeploymentByDeployee(contractId, body);
                
            default:
                return ReturnConfig.isFailed(Response.Status.NOT_ACCEPTABLE, "Exec command failed");
        }
    }
    
    private Response createContract(String body){
        String result = myContractual.createContract(body);
        if(result == null || result.equals(""))
            return ReturnConfig.isFailed(Response.Status.NOT_ACCEPTABLE, "Exec command failed");
        return ReturnConfig.isSuccess(result);
    }
    private Response cancelContract(String body){
        String result = myContractual.cancelContract(body);
        if(result == null || result.equals(""))
            return ReturnConfig.isFailed(Response.Status.NOT_ACCEPTABLE, "Exec command failed");
        return ReturnConfig.isSuccess(result);
    }
}
