/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package com.oltranz.kvcs.logic;

import com.oltranz.kvcs.apiclient.LocalWebCall;
import com.oltranz.kvcs.config.ActionConfig;
import com.oltranz.kvcs.config.AppDesc;
import com.oltranz.kvcs.config.StatusConfig;
import com.oltranz.kvcs.entities.Conductor;
import com.oltranz.kvcs.entities.Deployment;
import com.oltranz.kvcs.entities.Parking;
import com.oltranz.kvcs.fascades.ConductorFacade;
import com.oltranz.kvcs.fascades.DeploymentFacade;
import com.oltranz.kvcs.fascades.ParkingFacade;
import com.oltranz.kvcs.simplebeans.commonbeans.Address;
import com.oltranz.kvcs.simplebeans.commonbeans.ConductorBean;
import com.oltranz.kvcs.simplebeans.commonbeans.ConductorTel;
import com.oltranz.kvcs.simplebeans.commonbeans.GetElementsByLimit;
import com.oltranz.kvcs.simplebeans.commonbeans.ParkingBean;
import com.oltranz.kvcs.simplebeans.commonbeans.RequestGetElementById;
import com.oltranz.kvcs.simplebeans.commonbeans.StatusBean;
import com.oltranz.kvcs.simplebeans.conductors.RequestConductorCreation;
import com.oltranz.kvcs.simplebeans.conductors.RequestConductorCreationList;
import com.oltranz.kvcs.simplebeans.conductors.ResponseConductor;
import com.oltranz.kvcs.utilities.ConductorFactory;
import com.oltranz.kvcs.utilities.DataFactory;
import com.oltranz.kvcs.utilities.IdGenerator;
import com.oltranz.kvcs.utilities.ReturnConfig;
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
public class ConductorsLogic {
    @EJB
            ConductorFacade conductorFacade;
    @EJB
            IdGenerator idGenerator;
    @EJB
            DeploymentFacade deploymentFacade;
    @EJB
            ParkingFacade parkingFacade;
    @EJB
            ConductorFactory conductorFactory;
    @EJB
            LocalWebCall localWebCall;
    private String conductorId;
    
    public Response createConductor(String contractId, String body){
        out.print(AppDesc.APP_DESC+"ConductorLogic createConductor received Contract: "+contractId+" and Body: "+body);
        try{
            RequestConductorCreation conductorCreation = (RequestConductorCreation) DataFactory.stringToObject(RequestConductorCreation.class, body);
            String genId = idGenerator()+contractId;
            this.conductorId = genId;
            if(!isConductorCreated(contractId, conductorCreation, genId))
                return ReturnConfig.isFailed(Response.Status.BAD_REQUEST, "Not created, Revise your data");
            
            Conductor conductor = conductorFacade.getConductorById(contractId, this.conductorId);
            
            if(conductor == null){
                out.print(AppDesc.APP_DESC+"ConductorLogic createConductor action failed due to possible data integrity violation");
                return ReturnConfig.isFailed(Response.Status.EXPECTATION_FAILED, "Not created, Revise your data");
            }
            
            String outPut = conductorFactory.tuneConductor(conductor);
            out.print(AppDesc.APP_DESC+"ConductorLogic createConductor action Succeeded output: "+outPut);
            return ReturnConfig.isSuccess(outPut);
        }catch(Exception e){
            out.print(AppDesc.APP_DESC+"ConductorLogic createConductor action failed due to: "+e.getLocalizedMessage());
            return ReturnConfig.isFailed(Response.Status.EXPECTATION_FAILED, e.getLocalizedMessage());
        }
    }
    
    public Response createBulkConductors(String contractId, String body){
        out.print(AppDesc.APP_DESC+"ConductorLogic createBulkConductors received Contract: "+contractId+" and Body: "+body);
        try{
            RequestConductorCreationList conductorCreationList = (RequestConductorCreationList) DataFactory.stringToObject(RequestConductorCreationList.class, body);
            
            List<RequestConductorCreation> conductorList = conductorCreationList.getConductorList();
            if(conductorList.size()<=0){
                out.print(AppDesc.APP_DESC+"ConductorLogic getAllConductor action failed due to: null request object");
                return ReturnConfig.isFailed(Response.Status.BAD_REQUEST, "Not created, Revise your data");
            }
            List<ResponseConductor> responseConductorList = new ArrayList<>();
            List<ParkingBean> mparkings = new ArrayList<>();
            boolean isCreated = true;
            ConductorBean conductorBean = null;
            ResponseConductor responseConductor = null;
            String result = "empty";
            for(RequestConductorCreation conductorCreation : conductorList){
                //generate id for the conductor
                Response response = localWebCall.localService(contractId, "CREATE_CONDUCTOR", DataFactory.objectToString(conductorCreation));
                if(response == null){
                    isCreated = false;
                }else{
                    if(response.getStatus() != 200){
                        isCreated = false;
                    }else{
                        try{
                            result = response.readEntity(String.class).trim();
                            out.print(AppDesc.APP_DESC+" ConductorLogic createdBulkConductor received from localService: "+result);
                            responseConductor = (ResponseConductor) DataFactory.stringToObject(ResponseConductor.class, result);
                            if(responseConductor != null){
                                String outPut = DataFactory.objectToString(responseConductor);
                                out.print(AppDesc.APP_DESC+"ConductorLogic createBulkConductors action Succeeded output: "+outPut);
                                responseConductorList.add(responseConductor);
                            }else{
                                isCreated = false;
                            }
                        }catch(Exception e){
                            out.print(AppDesc.APP_DESC+" ConductorLogic createdBulkConductor failed to parse: "+ result + " due to: "+e.getMessage());
                            isCreated = false;
                        }
                    }
                }
                
                if(!isCreated){
                    conductorBean = new ConductorBean("",
                            conductorCreation.getFirstName(),
                            conductorCreation.getMiddleName(),
                            conductorCreation.getLastName(),
                            conductorCreation.getGender(),
                            conductorCreation.getTel(),
                            conductorCreation.getAddress(),
                            new StatusBean(StatusConfig.NOT_APPLIED, StatusConfig.NOT_APPLIED_DESC));
                    responseConductor = new ResponseConductor(conductorBean, new StatusBean(StatusConfig.NOT_CREATED, StatusConfig.NOT_CREATED_DESC), mparkings);
                    String outPut = DataFactory.objectToString(responseConductor);
                    out.print(AppDesc.APP_DESC+"ConductorLogic createBulkConductors creating conductor failed output: "+outPut);
                    responseConductorList.add(responseConductor);
                }
            }
            String outPut = DataFactory.objectToString(responseConductorList);
            out.print(AppDesc.APP_DESC+"ConductorLogic createBulkConductors action failed output: "+outPut);
            return ReturnConfig.isSuccess(outPut);
        }catch(Exception e){
            out.print(AppDesc.APP_DESC+"ConductorLogic createConductor action failed due to: "+e.getLocalizedMessage());
            return ReturnConfig.isFailed(Response.Status.EXPECTATION_FAILED, e.getLocalizedMessage());
        }
    }
    
    public Response undeployConductor(String contractId, String body){
        out.print(AppDesc.APP_DESC+"ConductorLogic undeployConductor received Contract: "+contractId+" and Body: "+body);
        try{
            RequestGetElementById getElement = (RequestGetElementById) DataFactory.stringToObject(RequestGetElementById.class, body);
            if(getElement == null){
                out.print(AppDesc.APP_DESC+"ConductorLogic undeployConductor action failed due to: null element ID");
                return ReturnConfig.isFailed(Response.Status.BAD_REQUEST, "Bad request, revise your data");
            }
            
            Conductor checkConductor = conductorFacade.getConductorById(contractId, getElement.getId()+contractId);
            
            if(checkConductor == null){
                out.print(AppDesc.APP_DESC+"ConductorLogic undeployConductor action failed due to: null request object");
                return ReturnConfig.isFailed(Response.Status.NO_CONTENT, "No data found.");
            }
            
            if(isConductorUndeployed(checkConductor)){
                String outPut = DataFactory.objectToString(new StatusBean(StatusConfig.CANCELED, StatusConfig.CANCELED_DESC));
                out.print(AppDesc.APP_DESC+"ConductorLogic undeployConductor action Succeeded output: "+outPut);
                return ReturnConfig.isSuccess(outPut);
            }
            
            out.print(AppDesc.APP_DESC+"ConductorLogic undeployConductor action failed due to: issues with conductor or his deployments");
            return ReturnConfig.isFailed(Response.Status.EXPECTATION_FAILED, "Issues with conductor or his deployments");
        }catch(Exception e){
            out.print(AppDesc.APP_DESC+"ConductorLogic undeployConductor action failed due to: "+e.getLocalizedMessage());
            return ReturnConfig.isFailed(Response.Status.EXPECTATION_FAILED, e.getLocalizedMessage());
        }
    }
    
    public Response getConductor(String contractId, String body){
        out.print(AppDesc.APP_DESC+"ConductorLogic getConductor received Contract: "+contractId+" and Body: "+body);
        try{
            RequestGetElementById getElement = (RequestGetElementById) DataFactory.stringToObject(RequestGetElementById.class, body);
            if(getElement == null){
                out.print(AppDesc.APP_DESC+"ConductorLogic getConductor action failed due to: null element ID");
                return ReturnConfig.isFailed(Response.Status.BAD_REQUEST, "Bad request, revise your data");
            }
            
            Conductor checkConductor = conductorFacade.getConductorById(contractId, getElement.getId()+contractId);
            
            if(checkConductor == null){
                out.print(AppDesc.APP_DESC+"ConductorLogic getConductor action failed due to: null request object");
                return ReturnConfig.isFailed(Response.Status.EXPECTATION_FAILED, "No data found.");
            }
            
            adjustConductor(checkConductor);
            
            String outPut = conductorFactory.tuneConductor(checkConductor);
            out.print(AppDesc.APP_DESC+"ConductorLogic createConductor action Succeeded output: "+outPut);
            return ReturnConfig.isSuccess(outPut);
        }catch(Exception e){
            out.print(AppDesc.APP_DESC+"ConductorLogic getConductor action failed due to: "+e.getLocalizedMessage());
            return ReturnConfig.isFailed(Response.Status.EXPECTATION_FAILED, e.getLocalizedMessage());
        }
    }
    
    public Response getConductorByTel(String contractId, String body){
        out.print(AppDesc.APP_DESC+"ConductorLogic getConductor received Contract: "+contractId+" and Body: "+body);
        try{
            ConductorTel getElement = (ConductorTel) DataFactory.stringToObject(ConductorTel.class, body);
            if(getElement == null){
                out.print(AppDesc.APP_DESC+"ConductorLogic getConductor action failed due to: null Tel");
                return ReturnConfig.isFailed(Response.Status.BAD_REQUEST, "Bad request, revise your data");
            }
            
            Conductor checkConductor = conductorFacade.getConductorByTel(contractId, getElement.getTelephone());
            
            if(checkConductor == null){
                out.print(AppDesc.APP_DESC+"ConductorLogic getConductor action failed due to: null request object");
                return ReturnConfig.isFailed(Response.Status.EXPECTATION_FAILED, "No data found.");
            } 
            adjustConductor(checkConductor);
            
            String outPut = conductorFactory.tuneConductor(checkConductor);
            out.print(AppDesc.APP_DESC+"ConductorLogic createConductor action Succeeded output: "+outPut);
            return ReturnConfig.isSuccess(outPut);
        }catch(Exception e){
            out.print(AppDesc.APP_DESC+"ConductorLogic getConductor action failed due to: "+e.getLocalizedMessage());
            return ReturnConfig.isFailed(Response.Status.EXPECTATION_FAILED, e.getLocalizedMessage());
        }
    }
    
    public Response getAllConductor(String contractId, String body){
        out.print(AppDesc.APP_DESC+"ConductorLogic getAllConductor received Contract: "+contractId+" and Body: "+body);
        try{
            GetElementsByLimit limit = (GetElementsByLimit) DataFactory.stringToObject(GetElementsByLimit.class, body);
            if(limit == null){
                out.print(AppDesc.APP_DESC+"ConductorLogic getAllConductor action failed due to: null request object");
                return ReturnConfig.isFailed(Response.Status.BAD_REQUEST, "Bad request, revise your data");
            }
            
            List<Conductor> conductors = conductorFacade.getAllConductor(contractId, limit.getLimit());
            
            if(conductors.size()<= 0){
                out.print(AppDesc.APP_DESC+"ConductorLogic getAllConductor action no data found.");
                return ReturnConfig.isFailed(Response.Status.NO_CONTENT, "Not data found");
            }
            String outPut = conductorFactory.tuneConductorList(conductors);
            out.print(AppDesc.APP_DESC+"ConductorLogic createBulkConductors action failed output: "+outPut);
            return ReturnConfig.isSuccess(outPut);
        }catch(Exception e){
            out.print(AppDesc.APP_DESC+"ConductorLogic getAllConductor action failed due to: "+e.getLocalizedMessage());
            return ReturnConfig.isFailed(Response.Status.EXPECTATION_FAILED, e.getLocalizedMessage());
        }
    }
    
    public Response getRegionalConductors(String contractId, String body){
        out.print(AppDesc.APP_DESC+"ConductorLogic getRegionalConductors received Contract: "+contractId+" and Body: "+body);
        try{
            Address address = (Address) DataFactory.stringToObject(Address.class, body);
            if(address == null){
                out.print(AppDesc.APP_DESC+"ConductorLogic getRegionalConductors action failed due to: null request object");
                return ReturnConfig.isFailed(Response.Status.BAD_REQUEST, "No data found. Revise your data.");
            }
            
            List<Conductor> conductors = conductorFacade.getConductorByAddress(contractId, address);
            
            if(conductors == null){
                out.print(AppDesc.APP_DESC+"ConductorLogic getAllConductor action no data found.");
                return ReturnConfig.isFailed(Response.Status.EXPECTATION_FAILED, "Not data found");
            }
            
            if(conductors.isEmpty()){
                out.print(AppDesc.APP_DESC+"ConductorLogic getAllConductor action no data found.");
                return ReturnConfig.isFailed(Response.Status.EXPECTATION_FAILED, "Not data found");
            }
            
            String outPut = conductorFactory.tuneConductorList(conductors);
            out.print(AppDesc.APP_DESC+"ConductorLogic createBulkConductors action failed output: "+outPut);
            return ReturnConfig.isSuccess(outPut);
        }catch(Exception e){
            out.print(AppDesc.APP_DESC+"ConductorLogic getRegionalConductors action failed due to: "+e.getLocalizedMessage());
            return ReturnConfig.isFailed(Response.Status.EXPECTATION_FAILED, e.getLocalizedMessage());
        }
    }
    
    public Response getConductorByStatus(String contractId, String body){
        
        return ReturnConfig.isFailed(Response.Status.NO_CONTENT, "Service is underconstruction");
    }
    
    public Response getConductorDeployment(String contractId, String body){
        
        return ReturnConfig.isFailed(Response.Status.NO_CONTENT, "Service is underconstruction");
    }
    
    public Response editConductor(String contractId, String body){
        out.print(AppDesc.APP_DESC+"ConductorLogic editConductor received Contract: "+contractId+" and Body: "+body);
        try{
            ConductorBean conductorBean = (ConductorBean) DataFactory.stringToObject(ConductorBean.class, body);
            
            if(!isConductorEdited(contractId, conductorBean)){
                out.print(AppDesc.APP_DESC+"ConductorLogic editConductor action failed");
                return ReturnConfig.isFailed(Response.Status.BAD_REQUEST, "Not edited, Revise your data");
            }
            
            Conductor conductor = conductorFacade.getConductorById(contractId, conductorBean.getConductorId());
            String outPut = conductorFactory.tuneConductor(conductor);
            out.print(AppDesc.APP_DESC+"ConductorLogic editConductor action Succeeded output: "+outPut);
            return ReturnConfig.isSuccess(outPut);
        }catch(Exception e){
            out.print(AppDesc.APP_DESC+"ConductorLogic editConductor action failed due to: "+e.getLocalizedMessage());
            return ReturnConfig.isFailed(Response.Status.EXPECTATION_FAILED, e.getLocalizedMessage());
        }
    }
    
    public Response deleteConductor(String contractId, String body){
        out.print(AppDesc.APP_DESC+"ConductorLogic deleteConductor received Contract: "+contractId+" and Body: "+body);
        try{
            RequestGetElementById getElement = (RequestGetElementById) DataFactory.stringToObject(RequestGetElementById.class, body);
            if(getElement == null){
                out.print(AppDesc.APP_DESC+"ConductorLogic deleteConductor failed due to: null request");
                return ReturnConfig.isFailed(Response.Status.BAD_REQUEST, "Bad request, revise your data");
            }
            
            Conductor checkConductor = conductorFacade.getConductorById(contractId, getElement.getId()+contractId);
            
            if(checkConductor == null){
                out.print(AppDesc.APP_DESC+"ConductorLogic deleteConductor action failed due to: null request object");
                return ReturnConfig.isFailed(Response.Status.EXPECTATION_FAILED, "No data found.");
            }
            
            ConductorBean conductorBean = conductorFactory.getConductorBean(checkConductor);
            
            if(!isConductorDeleted(contractId, conductorBean)){
                out.print(AppDesc.APP_DESC+"ConductorLogic deleteConductor action failed ");
                return ReturnConfig.isFailed(Response.Status.EXPECTATION_FAILED, "Failed to deleted this user");
            }
            
            Conductor conductor = conductorFacade.getConductorById(contractId, conductorBean.getConductorId()+contractId);
            
            String outPut = conductorFactory.tuneConductor(conductor);
            out.print(AppDesc.APP_DESC+"ConductorLogic deleteConductor action Succeeded output: "+outPut);
            return ReturnConfig.isSuccess(outPut);
        }catch(Exception e){
            out.print(AppDesc.APP_DESC+"ConductorLogic deleteConductor action failed due to: "+e.getLocalizedMessage());
            return ReturnConfig.isFailed(Response.Status.EXPECTATION_FAILED, e.getMessage());
        }
    }
    
    private void adjustConductor(Conductor conductor){
        try{
            
            if(conductor == null)
                return;
            Deployment deployment = deploymentFacade.getLastDeployment(conductor.getContractId(), conductor.getConductorId()+conductor.getContractId());
            if(deployment != null){
                if(deployment.getExpirationDate().getTime() < new Date().getTime()){
                    out.print(AppDesc.APP_DESC+"ConductorLogic adjustConductor last conductor: "+conductor.getFname()+" with ID "+conductor.getConductorId().replace(conductor.getContractId(), "")+" Deployment expired has system status "+conductor.getSystemStatusDesc());
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
        }catch(Exception e){
            out.print(AppDesc.APP_DESC+"ConductorLogic adjustConductor last conductor: "+conductor.getFname()+" with ID "+conductor.getConductorId().replace(conductor.getContractId(), "")+" failed to adjust his her deployment data "+conductor.getSystemStatusDesc());
                    
        }
    }
    private boolean isConductorUndeployed(Conductor conductor){
        try{
            List<Deployment> mDeployments = deploymentFacade.getDeploymentByDeployee(conductor.getContractId(), conductor.getConductorId());
            if(mDeployments.isEmpty()){
                out.print(AppDesc.APP_DESC+"ConductorLogic isConductorUndeployed action failed due to: No deployment found.");
                return false;
            }
            
            for(Deployment deployment : mDeployments){
                if(deployment.getStatus() == StatusConfig.MONITOR){
                    deployment.setStatus(StatusConfig.CANCELED);
                    deployment.setStatusDesc(StatusConfig.CANCELED_DESC);
                    deploymentFacade.edit(deployment);
                }
            }
            
            conductor.setWorkStatus(StatusConfig.VACANT);
            conductor.setWorkStatusDesc(StatusConfig.VACANT_DESC);
            conductorFacade.edit(conductor);
            
            return true;
        }catch(Exception e){
            out.print(AppDesc.APP_DESC+"ConductorLogic isConductorUndeployed action failed due to: "+e.getLocalizedMessage());
            return false;
        }
    }
    
    private boolean isConductorCreated(String contractId, RequestConductorCreation conductorCreation, String conductorId){
        if(conductorCreation == null)
            return false;
        
        if(conductorCreation.getAddress() == null)
            return false;
        if(contractId.isEmpty())
            return false;
        try{
            Conductor conductor = null;//conductorFacade.getConductorById(contractId, conductorId);
            int counter = 1;
            
            do{
                if(counter == 3){
                    break;
                }
                conductor = conductorFacade.getConductorById(contractId, conductorId);
                if(conductor != null){
                    conductorId = idGenerator()+contractId;
                    this.conductorId = conductorId;
                }
                counter ++;
            }while(conductor != null);
            
            if(conductor != null)
                return false;
            
            Date date = new Date();
            conductor = new Conductor(conductorId,
                    contractId,
                    conductorCreation.getFirstName(),
                    conductorCreation.getMiddleName(),
                    conductorCreation.getLastName(),
                    conductorCreation.getGender(),
                    conductorCreation.getTel(),
                    conductorCreation.getAddress().getProvince(),
                    conductorCreation.getAddress().getDistrict(),
                    conductorCreation.getAddress().getSector(),
                    conductorCreation.getAddress().getCell(),
                    StatusConfig.ACTIVE,
                    StatusConfig.ACTIVE_DESC,
                    StatusConfig.VACANT,
                    StatusConfig.VACANT_DESC,
                    conductorCreation.getImagePath(),
                    date,
                    date,
                    ActionConfig.CREATE);
            conductorFacade.create(conductor);
            conductorFacade.refreshConductor();
            
            conductor = conductorFacade.getConductorById(contractId, conductorId);
            
            return conductor != null;
        }catch(Exception e){
            out.print(AppDesc.APP_DESC+"ConductorLogic isconductorCreated action failed due to: "+e.getLocalizedMessage());
            return false;
        }
    }
    
    private boolean isConductorEdited(String contractId, ConductorBean conductorBean){
        if(conductorBean == null)
            return false;
        try{
            Conductor checkConductor = conductorFacade.getConductorById(contractId, conductorBean.getConductorId()+contractId);
            if(checkConductor == null)
                return false;
            
            Date date = new Date();
            Conductor conductor = new Conductor(checkConductor.getConductorId(),
                    contractId,
                    conductorBean.getFirstName(),
                    conductorBean.getMiddleName(),
                    conductorBean.getLastName(),
                    conductorBean.getGender(),
                    conductorBean.getTel(),
                    conductorBean.getAddress().getProvince(),
                    conductorBean.getAddress().getDistrict(),
                    conductorBean.getAddress().getSector(),
                    conductorBean.getAddress().getCell(),
                    checkConductor.getSystemStatus(),
                    checkConductor.getSystemStatusDesc(),
                    checkConductor.getWorkStatus(),
                    checkConductor.getWorkStatusDesc(),
                    checkConductor.getImagepath(),
                    checkConductor.getCreationDate(),
                    date,
                    ActionConfig.UPDATE);
            conductor.setId(checkConductor.getId());
            conductorFacade.edit(conductor);
            return true;
        }catch(Exception e){
            out.print(AppDesc.APP_DESC+"ConductorLogic isconductorCreated action failed due to: "+e.getLocalizedMessage());
            return false;
        }
    }
    
    private boolean isConductorDeleted(String contractId, ConductorBean conductorBean){
        try{
            if(conductorBean == null)
                return false;
            
            Conductor checkConductor = conductorFacade.getConductorById(contractId, conductorBean.getConductorId()+contractId);
            if(checkConductor == null)
                return false;
            
            
            List<Deployment> conductorDeployments = deploymentFacade.getDeploymentByDeployee(contractId, checkConductor.getConductorId());
            
            if(conductorDeployments != null){
                if(conductorDeployments.size() >= 1){
                    for(Deployment deployment : conductorDeployments){
                        List<Deployment> parkingDeployment = deploymentFacade.getDeploymentByParkingId(contractId, deployment.getParkingId());
                        if(parkingDeployment.size() <=1 ){
                            Parking parking = parkingFacade.getParkingById(contractId, deployment.getParkingId());
                            if(parking != null){
                                parking.setStatus(StatusConfig.VACANT);
                                parking.setStatusDesc(StatusConfig.VACANT_DESC);
                                
                                parkingFacade.edit(parking);
                            }
                        }
                        deployment.setStatus(StatusConfig.EXPIRED);
                        deployment.setStatusDesc(StatusConfig.EXPIRED_DESC);
                        deployment.setExpirationDate(new Date());
                        deploymentFacade.edit(deployment);
                    }
                }
            }
            Date date = new Date();
            checkConductor.setSystemStatus(StatusConfig.DELETED);
            checkConductor.setSystemStatusDesc(StatusConfig.DELETED_DESC);
            checkConductor.setWorkStatus(StatusConfig.INACTIVE);
            checkConductor.setWorkStatusDesc(StatusConfig.INACTIVE_DESC);
            checkConductor.setLastAcessDate(new Date());
            checkConductor.setLastAccessDesc(ActionConfig.INVALIDATE);
            conductorFacade.edit(checkConductor);
            return true;
        }catch(Exception e){
            out.print(AppDesc.APP_DESC+"ConductorLogic isConductorDeleted action failed due to: "+e.getLocalizedMessage());
            return false;
        }
    }
    
    private String idGenerator(){
        String genId = idGenerator.generate();
        try {
            Thread.sleep(100);
        } catch (InterruptedException ex) {
            out.print(AppDesc.APP_DESC+"ConductorLogic idGenerator thread sleep failed due to: "+ ex.getLocalizedMessage());
            Logger.getLogger(ConductorsLogic.class.getName()).log(Level.SEVERE, null, ex);
            return genId;
        }
        return genId;
    }
}
