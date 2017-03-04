/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package com.oltranz.kvcs.utilities;

import com.oltranz.kvcs.config.AppDesc;
import com.oltranz.kvcs.entities.Deployment;
import com.oltranz.kvcs.fascades.ConductorFacade;
import com.oltranz.kvcs.fascades.DeploymentFacade;
import com.oltranz.kvcs.fascades.ParkingFacade;
import com.oltranz.kvcs.fascades.SuperUserFacade;
import com.oltranz.kvcs.simplebeans.commonbeans.ConductorBean;
import com.oltranz.kvcs.simplebeans.commonbeans.ParkingBean;
import com.oltranz.kvcs.simplebeans.commonbeans.StatusBean;
import com.oltranz.kvcs.simplebeans.commonbeans.SuperUserBean;
import com.oltranz.kvcs.simplebeans.deployments.ResponseDeployment;
import static java.lang.System.out;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 *
 * @author Hp
 */
@Stateless
public class DeploymentFactory {
    @EJB
            ConductorFacade conductorFacade;
    @EJB
            DeploymentFacade deploymentFacade;
    @EJB
            ParkingFacade parkingFacade;
    @EJB
            SuperUserFacade superUserFacade;
    @EJB
            ConductorFactory conductorFactory;
    @EJB
            ParkingFactory parkingFactory;
    @EJB
            SuperUserFactory superUserFactory;
    
    public String tuneDeployment(Deployment deployment){
        try{
            if(deployment == null){
                out.print(AppDesc.APP_DESC+" DeploymentFactory tuneDeployment failed to produce result due to: null input");
                return null;
            }
            
            ConductorBean conductorBean = conductorFactory.getConductorBean(conductorFacade.getConductorById(deployment.getContractId(), deployment.getDeployeeId()));
            
            ParkingBean parkingBean = parkingFactory.getParkingBean(parkingFacade.getParkingById(deployment.getContractId(), deployment.getParkingId()));
            
            SuperUserBean superUser = superUserFactory.produceSuperUserBean(superUserFacade.getSuperUSerById(deployment.getContractId(), deployment.getDeployerId()));
            
            ResponseDeployment responseDeployment = new ResponseDeployment(deployment.getDeployId().replace(deployment.getContractId(), ""),
                    superUser,
                    conductorBean,
                    parkingBean,
                    new StatusBean(deployment.getStatus(), deployment.getStatusDesc()));
            
            String outPut = DataFactory.objectToString(responseDeployment);
            out.print(AppDesc.APP_DESC+" DeploymentFactory tuneDeployment action Succeeded output: "+outPut);
            
            return outPut;
        }catch(Exception e){
            out.print(AppDesc.APP_DESC+" DeploymentFactory tuneDeployment failed to produce result due to: "+e.getLocalizedMessage());
            return null;
        }
    }
    
    public String tuneDeploymentList(List<Deployment> mDeployments){
        try{
            if(mDeployments.isEmpty()){
                out.print(AppDesc.APP_DESC+" DeploymentFactory tuneDeploymentList failed to produce result due to: null input");
                return null;
            }
            
            List<ResponseDeployment> respDeployments = new ArrayList<>();
            
            for(Deployment deployment : mDeployments){
                ConductorBean conductorBean = conductorFactory.getConductorBean(conductorFacade.getConductorById(deployment.getContractId(), deployment.getDeployeeId()));
                
                ParkingBean parkingBean = parkingFactory.getParkingBean(parkingFacade.getParkingById(deployment.getContractId(), deployment.getParkingId()));
                
                SuperUserBean superUser = superUserFactory.produceSuperUserBean(superUserFacade.getSuperUSerById(deployment.getContractId(), deployment.getDeployerId()));
                
                ResponseDeployment responseDeployment = new ResponseDeployment(deployment.getDeployId().replace(deployment.getContractId(), ""),
                        superUser,
                        conductorBean,
                        parkingBean,
                        new StatusBean(deployment.getStatus(), deployment.getStatusDesc()));
                respDeployments.add(responseDeployment);
            }
            
            
            String outPut = DataFactory.objectToString(respDeployments);
            out.print(AppDesc.APP_DESC+" DeploymentFactory tuneDeploymentList action Succeeded output: "+outPut);
            
            return outPut;
        }catch(Exception e){
            out.print(AppDesc.APP_DESC+" DeploymentFactory tuneDeploymentList failed to produce result due to: "+e.getLocalizedMessage());
            return null;
        }
    }
}
