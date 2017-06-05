/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package com.oltranz.kvcs.logic;

import com.oltranz.kvcs.config.ActionConfig;
import com.oltranz.kvcs.config.AppDesc;
import com.oltranz.kvcs.config.StatusConfig;
import com.oltranz.kvcs.entities.Conductor;
import com.oltranz.kvcs.entities.Deployment;
import com.oltranz.kvcs.entities.Parking;
import com.oltranz.kvcs.entities.SuperUser;
import com.oltranz.kvcs.fascades.ConductorFacade;
import com.oltranz.kvcs.fascades.DeploymentFacade;
import com.oltranz.kvcs.fascades.ParkingFacade;
import com.oltranz.kvcs.fascades.SuperUserFacade;
import com.oltranz.kvcs.simplebeans.commonbeans.Address;
import com.oltranz.kvcs.simplebeans.commonbeans.GetElementsByLimit;
import com.oltranz.kvcs.simplebeans.commonbeans.RequestGetElementById;
import com.oltranz.kvcs.simplebeans.commonbeans.StatusBean;
import com.oltranz.kvcs.simplebeans.deployments.RequestDeploymentCreation;
import com.oltranz.kvcs.simplebeans.superusers.RequestCreateSuperUser;
import com.oltranz.kvcs.utilities.ConductorFactory;
import com.oltranz.kvcs.utilities.DataFactory;
import com.oltranz.kvcs.utilities.DateFactory;
import com.oltranz.kvcs.utilities.DeploymentFactory;
import com.oltranz.kvcs.utilities.IdGenerator;
import com.oltranz.kvcs.utilities.ParkingFactory;
import com.oltranz.kvcs.utilities.ReturnConfig;
import com.oltranz.kvcs.utilities.SuperUserFactory;
import static java.lang.System.out;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.core.Response;

/**
 *
 * @author Hp
 */
@Stateless
public class DeploymentLogic {
    
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
    @EJB
            IdGenerator idGenerator;
    @EJB
            DeploymentFactory deploymentFactory;
    private String parkDetails;
    public Response createDeployment(String contractId, String body){
        out.print(AppDesc.APP_DESC+"DeploymentLogic createDeployment received Contract: "+contractId+" and Body: "+body);
        try{
            RequestDeploymentCreation deploymentCreation = (RequestDeploymentCreation) DataFactory.stringToObject(RequestDeploymentCreation.class, body);
            String deploymentId = idGenerator(contractId);
            
            String conductorStatus = isConductorVacant(contractId, deploymentCreation);
            if(!conductorStatus.equals(StatusConfig.VACANT_DESC)){
                out.print(AppDesc.APP_DESC+"DeploymentLogic createDeployment action failed due to conductor : "+conductorStatus);
                return ReturnConfig.isFailed(Response.Status.EXPECTATION_FAILED, conductorStatus);
            }
            
             if(DateFactory.makeDate(deploymentCreation.getEndDate()).before(new Date())){
                out.print(AppDesc.APP_DESC+"DeploymentLogic isDeploymentCreated action failed due to: Invalid expiration date. "+deploymentCreation.getEndDate());
                return ReturnConfig.isFailed(Response.Status.EXPECTATION_FAILED, "Invalid end date.");
            }
            
            if(!isDeploymentCreated(contractId, deploymentCreation, deploymentId)){
                out.print(AppDesc.APP_DESC+"DeploymentLogic createDeployment action failed due to: "+parkDetails != null?" Possible reason: "+parkDetails:" ");
                return ReturnConfig.isFailed(Response.Status.BAD_REQUEST, "Not created,"+parkDetails != null?" Possible reason: "+parkDetails:" "+"Revise your data");
            }
            
            Deployment deployment = deploymentFacade.getDeploymentById(contractId, deploymentId);
            String outPut = deploymentFactory.tuneDeployment(deployment);
            out.print(AppDesc.APP_DESC+"DeploymentLogic createDeployment action Succeeded output: "+outPut);
            return ReturnConfig.isSuccess(outPut);
        }catch(Exception e){
            out.print(AppDesc.APP_DESC+"DeploymentLogic createDeployment action failed due to: "+e.getLocalizedMessage());
            return ReturnConfig.isFailed(Response.Status.EXPECTATION_FAILED, e.getLocalizedMessage());
        }
    }
    
    public Response cancelDeployment(String contractId, String body){
        try{
            RequestGetElementById mElement = (RequestGetElementById) DataFactory.stringToObject(RequestGetElementById.class, body);
            if(mElement == null){
                out.print(AppDesc.APP_DESC+"DeploymentLogic cancelDeployment action failed due to: null element ID");
                return ReturnConfig.isFailed(Response.Status.BAD_REQUEST, "Bad request, revise your data");
            }
            
            Deployment deployment = deploymentFacade.getDeploymentById(contractId, mElement.getId()+contractId);
            if(deployment == null){
                out.print(AppDesc.APP_DESC+"DeploymentLogic cancelDeployment action failed due to: null request object");
                return ReturnConfig.isFailed(Response.Status.NO_CONTENT, "No data found.");
            }
            
            if(isConductorUndeployed(contractId, deployment)){
                deployment.setStatus(StatusConfig.CANCELED);
                deployment.setStatusDesc(StatusConfig.CANCELED_DESC);
                deploymentFacade.edit(deployment);
                String outPut = DataFactory.objectToString(new StatusBean(StatusConfig.CANCELED, StatusConfig.CANCELED_DESC));
                out.print(AppDesc.APP_DESC+"DeploymentLogic cancelDeployment action Succeeded output: "+outPut);
                return ReturnConfig.isSuccess(outPut);
            }else{
                out.print(AppDesc.APP_DESC+"DeploymentLogic cancelDeployment action failed due to: issues with conductor");
                return ReturnConfig.isFailed(Response.Status.EXPECTATION_FAILED, "Issues with conductor.");
            }
        }catch(Exception e){
            out.print(AppDesc.APP_DESC+"DeploymentLogic cancelDeployment action failed due to: "+e.getLocalizedMessage());
            return ReturnConfig.isFailed(Response.Status.EXPECTATION_FAILED, e.getLocalizedMessage());
        }
    }
    
    public Response getDeploymentPerId(String contractId, String body){
        try{
            RequestGetElementById mElement = (RequestGetElementById) DataFactory.stringToObject(RequestGetElementById.class, body);
            if(mElement == null){
                out.print(AppDesc.APP_DESC+"DeploymentLogic getDeploymentPerId action failed due to: null element ID");
                return ReturnConfig.isFailed(Response.Status.BAD_REQUEST, "Bad request, revise your data");
            }
            
            Deployment deployment = deploymentFacade.getDeploymentById(contractId, mElement.getId()+contractId);
            if(deployment == null){
                out.print(AppDesc.APP_DESC+"getDeploymentPerId getDeploymentPerId action failed due to: null request object");
                return ReturnConfig.isFailed(Response.Status.NO_CONTENT, "No data found.");
            }
            
            String outPut = deploymentFactory.tuneDeployment(deployment);
            out.print(AppDesc.APP_DESC+"DeploymentLogic getDeploymentPerId action Succeeded output: "+outPut);
            return ReturnConfig.isSuccess(outPut);
        }catch(Exception e){
            out.print(AppDesc.APP_DESC+"DeploymentLogic getDeploymentPerId action failed due to: "+e.getLocalizedMessage());
            return ReturnConfig.isFailed(Response.Status.EXPECTATION_FAILED, e.getLocalizedMessage());
        }
    }
    
    public Response getRegionDeployments(String contractId, String body){
        try{
            Address address = (Address) DataFactory.stringToObject(Address.class, body);
            List<Parking> parkingList = parkingFacade.getParkingByAddress(contractId, address);
            
            if(parkingList == null){
                out.print(AppDesc.APP_DESC+"DeploymentLogic getRegionDeployments action failed due to: No related parking found.");
                return ReturnConfig.isFailed(Response.Status.EXPECTATION_FAILED,"No related parking found.");
            }
            
            if(parkingList.isEmpty()){
                out.print(AppDesc.APP_DESC+"DeploymentLogic getRegionDeployments action failed due to: No related parking found.");
                return ReturnConfig.isFailed(Response.Status.EXPECTATION_FAILED,"No related parking found.");
            }
            List<Deployment> mDeployments = new ArrayList<>();
            for(Parking parking : parkingList){
                List<Deployment> currentDeployments = deploymentFacade.getDeploymentByParkingId(contractId, parking.getParkingId());
                if(currentDeployments == null)
                    continue;
                for(Deployment deployment : currentDeployments){
                    mDeployments.add(deployment);
                }
            }
            String outPut = deploymentFactory.tuneDeploymentList(mDeployments);
            out.print(AppDesc.APP_DESC+"DeploymentLogic getRegionDeployments action Succeeded output: "+outPut);
            return ReturnConfig.isSuccess(outPut);
        }catch(Exception e){
            out.print(AppDesc.APP_DESC+"DeploymentLogic getRegionDeployments action failed due to: "+e.getLocalizedMessage());
            return ReturnConfig.isFailed(Response.Status.EXPECTATION_FAILED, e.getLocalizedMessage());
        }
    }
    
    public Response getAllDeployments(String contractId, String body){
        try{
            GetElementsByLimit limit = (GetElementsByLimit) DataFactory.stringToObject(GetElementsByLimit.class, body);
            if(limit == null){
                out.print(AppDesc.APP_DESC+"DeploymentLogic getAllDeployments action failed due to: null request object");
                return ReturnConfig.isFailed(Response.Status.BAD_REQUEST, "Bad request, revise your data");
            }
            List<Deployment> mDeployments = deploymentFacade.getAllDeployment(contractId, limit.getLimit());
            String outPut = deploymentFactory.tuneDeploymentList(mDeployments);
            out.print(AppDesc.APP_DESC+"DeploymentLogic getRegionDeployments action Succeeded output: "+outPut);
            return ReturnConfig.isSuccess(outPut);
        }catch(Exception e){
            out.print(AppDesc.APP_DESC+"DeploymentLogic getAllDeployments action failed due to: "+e.getLocalizedMessage());
            return ReturnConfig.isFailed(Response.Status.EXPECTATION_FAILED, e.getLocalizedMessage());
        }
    }
    
    public Response getAllDeploymentByDeployer(String contractId, String body){
        try{
            RequestCreateSuperUser mSuperUser = (RequestCreateSuperUser) DataFactory.stringToObject(RequestCreateSuperUser.class, body);
            if(mSuperUser == null){
                out.print(AppDesc.APP_DESC+"DeploymentLogic getAllDeploymentByDeployer action failed due to: null request object");
                return ReturnConfig.isFailed(Response.Status.BAD_REQUEST, "Bad request, revise your data");
            }
            SuperUser superUser = superUserFacade.getSuperUSerByTel(contractId, mSuperUser.getTel());
            if(superUser == null){
                out.print(AppDesc.APP_DESC+"DeploymentLogic getAllDeploymentByDeployer action failed due to: null request object");
                return ReturnConfig.isFailed(Response.Status.BAD_REQUEST, "Bad request, revise your data");
            }
            List<Deployment> mDeployments = deploymentFacade.getDeploymentByDeployer(contractId, superUser.getUserId());
            String outPut = deploymentFactory.tuneDeploymentList(mDeployments);
            out.print(AppDesc.APP_DESC+"DeploymentLogic getAllDeploymentByDeployer action Succeeded output: "+outPut);
            return ReturnConfig.isSuccess(outPut);
        }catch(Exception e){
            out.print(AppDesc.APP_DESC+"DeploymentLogic getAllDeploymentByDeployer action failed due to: "+e.getLocalizedMessage());
            return ReturnConfig.isFailed(Response.Status.EXPECTATION_FAILED, e.getLocalizedMessage());
        }
    }
    
    public Response getAllDeploymentByDeployee(String contractId, String body){
        try{
            RequestGetElementById mElement = (RequestGetElementById) DataFactory.stringToObject(RequestGetElementById.class, body);
            if(mElement == null){
                out.print(AppDesc.APP_DESC+"DeploymentLogic getAllDeploymentByDeployee action failed due to: null request object");
                return ReturnConfig.isFailed(Response.Status.BAD_REQUEST, "Bad request, revise your data");
            }
            List<Deployment> mDeployments = deploymentFacade.getDeploymentByDeployee(contractId, mElement.getId()+contractId);
            if(mDeployments == null || mDeployments.size()<=0){
                out.print(AppDesc.APP_DESC+"DeploymentLogic getAllDeploymentByDeployee action failed due to: No data found");
                return ReturnConfig.isFailed(Response.Status.EXPECTATION_FAILED, "No data found");
            }
            String outPut = deploymentFactory.tuneDeploymentList(mDeployments);
            out.print(AppDesc.APP_DESC+"DeploymentLogic getAllDeploymentByDeployee action Succeeded output: "+outPut);
            return ReturnConfig.isSuccess(outPut);
        }catch(Exception e){
            out.print(AppDesc.APP_DESC+"DeploymentLogic getAllDeploymentByDeployee action failed due to: "+e.getLocalizedMessage());
            return ReturnConfig.isFailed(Response.Status.EXPECTATION_FAILED, e.getLocalizedMessage());
        }
    }
    
    private String isConductorVacant(String contractId, RequestDeploymentCreation deploymentCreation){
        try{
            if(contractId.isEmpty()){
                out.print(AppDesc.APP_DESC+"DeploymentLogic isConductorVacant action failed due to: Contract ID is empty");
                return "Contract ID can't be empty";
            }
            if(deploymentCreation == null){
                out.print(AppDesc.APP_DESC+"DeploymentLogic isConductorVacant action failed due to: Empty request");
                return "Deployment request can't be empty";
            }
            
            Conductor conductor = conductorFacade.getConductorById(contractId, deploymentCreation.getConductorId()+contractId);
            if(conductor == null){
                out.print(AppDesc.APP_DESC+"DeploymentLogic isConductorVacant action failed due to: Conductor not found");
                return "No such conductor found.";
            }
            
            if(conductor.getSystemStatus() == StatusConfig.DELETED){
                out.print(AppDesc.APP_DESC+"DeploymentLogic isConductorVacant action failed due to: Conductor "+conductor.getFname()+" with ID"+conductor.getConductorId().replace(contractId, "")+" has system status "+conductor.getSystemStatusDesc());
                return conductor.getSystemStatusDesc();
            }
            
            Deployment deployment = deploymentFacade.getLastDeployment(contractId, conductor.getConductorId());
            if(deployment != null){
                if(deployment.getExpirationDate().before(new Date())){
                     out.print(AppDesc.APP_DESC+"DeploymentLogic isConductorVacant last conductor: "+conductor.getFname()+" with ID "+conductor.getConductorId().replace(contractId, "")+" Deployment expired has system status "+conductor.getSystemStatusDesc());
                     deployment.setStatus(StatusConfig.EXPIRED);
                     deployment.setStatusDesc(StatusConfig.EXPIRED_DESC);
                     deploymentFacade.edit(deployment);
                     deploymentFacade.refreshDeployment();
                     
                     conductor.setWorkStatus(StatusConfig.VACANT);
                     conductor.setWorkStatusDesc(StatusConfig.VACANT_DESC);
                     conductor.setLastAcessDate(new Date());
                     conductor.setLastAccessDesc(ActionConfig.ACCESS);
                     conductorFacade.edit(conductor);
                     conductorFacade.refreshConductor();
                }
            }
            if(conductor.getWorkStatus() == StatusConfig.DEPLOYED){
                out.print(AppDesc.APP_DESC+"DeploymentLogic isConductorVacant action failed due to: Conductor "+conductor.getFname()+" with ID"+conductor.getConductorId().replace(contractId, "")+" has work status "+conductor.getWorkStatusDesc());
                return conductor.getWorkStatusDesc();
            }
            
            out.print(AppDesc.APP_DESC+"DeploymentLogic isConductorVacant action successful due to: Conductor work status "+conductor.getWorkStatusDesc());
            return conductor.getWorkStatusDesc();
            
        }catch(Exception e){
            out.print(AppDesc.APP_DESC+"DeploymentLogic isConductorVacant action failed due to: "+e.getLocalizedMessage());
            return "Faillure due to: "+e.getLocalizedMessage();
        }
    }
    
    private boolean isConductorUndeployed(String contractId, Deployment deployment){
        try{
            Conductor conductor = conductorFacade.getConductorById(contractId, deployment.getDeployeeId());
            if(conductor == null){
                out.print(AppDesc.APP_DESC+"DeploymentLogic isConductorUndeployed action failed due to: Conductor not found");
                return false;
            }
            conductor.setWorkStatus(StatusConfig.VACANT);
            conductor.setWorkStatusDesc(StatusConfig.VACANT_DESC);
            conductorFacade.edit(conductor);
            
            return true;
        }catch(Exception e){
            out.print(AppDesc.APP_DESC+"DeploymentLogic isConductorUndeployed action failed due to: "+e.getLocalizedMessage());
            return false;
        }
    }
    
    private boolean isDeploymentCreated(String contractId, RequestDeploymentCreation deploymentCreation, String deploymentId){
        try{
            if(!superUserFactory.isSuperUserCtreated(contractId, deploymentCreation.getDeployer())){
                out.print(AppDesc.APP_DESC+"DeploymentLogic createDeployment action failed due to: Super user not created");
                return false;
            }
            SuperUser superUser = superUserFacade.getSuperUSerByTel(contractId, deploymentCreation.getDeployer().getTel());
            if(superUser == null){
                return false;
            }
            
            Date date = new Date();
            
            Parking parking = parkingFacade.getParkingById(contractId, deploymentCreation.getParkingId()+contractId);
            if(parking == null){
                out.print(AppDesc.APP_DESC+"DeploymentLogic createDeployment action failed due to: Parking not found");
                return false;
            }
//            if(parking.getStatus() == StatusConfig.MONITOR){
//                this.parkDetails = parking.getStatusDesc();
//                out.print(AppDesc.APP_DESC+"DeploymentLogic createDeployment action failed due to: Parking "+parking.getParkingId()+" "+parking.getParkingDesc()+" already under monitoring");
//                return false;
//            }
            parking.setLastAccessAction(ActionConfig.ACCESS);
            parking.setLastAccessDate(date);
            
            Conductor conductor = conductorFacade.getConductorById(contractId, deploymentCreation.getConductorId()+contractId);
            if(conductor == null){
                out.print(AppDesc.APP_DESC+"DeploymentLogic createDeployment action failed due to: Conductor not found");
                return false;
            }
            
            conductor.setLastAccessDesc(ActionConfig.ACCESS);
            conductor.setLastAcessDate(date);
            
            Deployment deployment = new Deployment(contractId,
                    deploymentId,
                    superUser.getUserId(),
                    conductor.getConductorId(),
                    parking.getParkingId(),
                    DateFactory.makeDate(deploymentCreation.getStartDate()),
                    DateFactory.makeDate(deploymentCreation.getEndDate()),
                    date,
                    StatusConfig.MONITOR,
                    StatusConfig.MONITOR_DESC);
            deploymentFacade.create(deployment);
            deploymentFacade.refreshDeployment();
            
            parking.setStatus(StatusConfig.MONITOR);
            parking.setStatusDesc(StatusConfig.MONITOR_DESC);
            parkingFacade.edit(parking);
            
            conductor.setWorkStatus(StatusConfig.DEPLOYED);
            conductor.setWorkStatusDesc(StatusConfig.DEPLOYED_DESC);
            conductorFacade.edit(conductor);
            
            return true;
        }catch(Exception e){
            out.print(AppDesc.APP_DESC+"DeploymentLogic createDeployment action failed due to: "+e.getLocalizedMessage());
            return false;
        }
    }
    
    private String idGenerator(String contractId){
        String genId = idGenerator.generate() + contractId;
        try {
            boolean check = false;
            do{
                Deployment deployment = deploymentFacade.getDeploymentById(contractId, genId);
                if(deployment == null){
                    return genId;
                }
                genId = idGenerator.generate()+contractId;
            }while(!check);
            Thread.sleep(100);
        } catch (InterruptedException ex) {
            out.print(AppDesc.APP_DESC+"ConductorLogic idGenerator thread sleep failed due to: "+ ex.getLocalizedMessage());
            Logger.getLogger(ConductorsLogic.class.getName()).log(Level.SEVERE, null, ex);
            return genId;
        }
        return genId;
    }
    
}
