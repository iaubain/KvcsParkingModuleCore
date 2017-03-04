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
import com.oltranz.kvcs.simplebeans.commonbeans.GetElementsByLimit;
import com.oltranz.kvcs.simplebeans.commonbeans.ParkingBean;
import com.oltranz.kvcs.simplebeans.commonbeans.RequestGetElementById;
import com.oltranz.kvcs.simplebeans.commonbeans.StatusBean;
import com.oltranz.kvcs.simplebeans.parkings.RequestParkingCreation;
import com.oltranz.kvcs.simplebeans.parkings.RequestParkingCreationList;
import com.oltranz.kvcs.simplebeans.parkings.ResponseParking;
import com.oltranz.kvcs.utilities.DataFactory;
import com.oltranz.kvcs.utilities.IdGenerator;
import com.oltranz.kvcs.utilities.ParkingFactory;
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
public class ParkingLogic {
    @EJB
            IdGenerator idGenerator;
    @EJB
            ParkingFacade parkingFacade;
    @EJB
            DeploymentFacade deploymentFacade;
    @EJB
            ConductorFacade conductorFacade;
    @EJB
            ParkingFactory parkingFactory;
    @EJB
            LocalWebCall localWebCall;
    private String parkingId;
    
    public Response createParking(String contractId, String body){
        out.print(AppDesc.APP_DESC+"ParkingLogic createParking received Contract: "+contractId+" and Body: "+body);
        try{
            RequestParkingCreation requestParkingCreation = (RequestParkingCreation) DataFactory.stringToObject(RequestParkingCreation.class, body);
            String genId = idGenerator()+contractId;
            this.parkingId = genId;
            if(!isParkingCreated(contractId, requestParkingCreation, genId))
                return ReturnConfig.isFailed(Response.Status.BAD_REQUEST, "Not created, Revise your data");
            
            Parking parking = parkingFacade.getParkingById(contractId, parkingId);
            
            String outPut = parkingFactory.tuneParking(parking);
            
            out.print(AppDesc.APP_DESC+"ParkingLogic createParking action Succeeded output: "+outPut);
            return ReturnConfig.isSuccess(outPut);
        }catch(Exception e){
            out.print(AppDesc.APP_DESC+"ParkingLogic createParking action failed due to: "+e.getLocalizedMessage());
            return ReturnConfig.isFailed(Response.Status.EXPECTATION_FAILED, e.getLocalizedMessage());
        }
    }
    
    public Response createBulkParking(String contractId, String body){
        out.print(AppDesc.APP_DESC+"ParkingLogic createBulkParkings received Contract: "+contractId+" and Body: "+body);
        try{
            RequestParkingCreationList parkingCreationList = (RequestParkingCreationList) DataFactory.stringToObject(RequestParkingCreationList.class, body);
            
            List<RequestParkingCreation> parkingList = parkingCreationList.getParkingList();
            if(parkingList.size() <= 0){
                out.print(AppDesc.APP_DESC+"ParkingLogic createBulkParkings action failed due to: null request object");
                return ReturnConfig.isFailed(Response.Status.BAD_REQUEST, "Not created, Revise your data");
            }
            
            List<ResponseParking> responseParkingList = new ArrayList<>();
            boolean isCreated = true;
            ParkingBean parkingBean = null;
            ResponseParking responseParking = null;
            String result = "empty";
            
            for(RequestParkingCreation parkingCreation : parkingList){
                //generate id for the parking
                Response response = localWebCall.localService(contractId, "CREATE_PARKING", DataFactory.objectToString(parkingCreation));
                if(response == null){
                    isCreated = false;
                }else{
                    if(response.getStatus() != 200){
                        isCreated = false;
                    }else{
                        try{
                            result = response.readEntity(String.class).trim();
                            out.print(AppDesc.APP_DESC+" ParkingLogic createBulkParking received from localService: "+result);
                            responseParking = (ResponseParking) DataFactory.stringToObject(ResponseParking.class, result);
                            if(responseParking != null){
                                String outPut = DataFactory.objectToString(responseParking);
                                out.print(AppDesc.APP_DESC+" ParkingLogic createBulkParking action Succeeded output: "+outPut);
                                responseParkingList.add(responseParking);
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
                    responseParking = new ResponseParking(parkingBean, new ArrayList<>(), new StatusBean(StatusConfig.NOT_CREATED, StatusConfig.NOT_CREATED_DESC));
                    String outPut = DataFactory.objectToString(responseParking);
                    out.print(AppDesc.APP_DESC+"ParkingLogic createBulkParkings action failed output: "+outPut);
                    responseParkingList.add(responseParking);
                }
            }
            String outPut = DataFactory.objectToString(responseParkingList);
            out.print(AppDesc.APP_DESC+"ConductorLogic createBulkConductors action failed output: "+outPut);
            return ReturnConfig.isSuccess(outPut);
        }catch(Exception e){
            out.print(AppDesc.APP_DESC+"ConductorLogic createConductor action failed due to: "+e.getLocalizedMessage());
            return ReturnConfig.isFailed(Response.Status.EXPECTATION_FAILED, e.getLocalizedMessage());
        }
    }
    
    public Response getParking(String contractId, String body){
        out.print(AppDesc.APP_DESC+"ParkingLogic getParking received Contract: "+contractId+" and Body: "+body);
        try{
            RequestGetElementById getElement = (RequestGetElementById) DataFactory.stringToObject(RequestGetElementById.class, body);
            if(getElement == null)
                return ReturnConfig.isFailed(Response.Status.BAD_REQUEST, "Bad request, revise your data");
            
            Parking parking = parkingFacade.getParkingById(contractId, getElement.getId()+contractId);
            
            if(parking == null){
                out.print(AppDesc.APP_DESC+"ParkingLogic getParking action failed due to: null request object");
                return ReturnConfig.isFailed(Response.Status.NO_CONTENT, "No data found.");
            }
            String outPut = parkingFactory.tuneParking(parking);
            out.print(AppDesc.APP_DESC+"ParkingLogic getParking action Succeeded output: "+outPut);
            return ReturnConfig.isSuccess(outPut);
        }catch(Exception e){
            out.print(AppDesc.APP_DESC+"ParkingLogic getParking action failed due to: "+e.getLocalizedMessage());
            return ReturnConfig.isFailed(Response.Status.EXPECTATION_FAILED, e.getLocalizedMessage());
        }
    }
    
    public Response getAllParkings(String contractId, String body){
        out.print(AppDesc.APP_DESC+"ParkingLogic getAllParkings received Contract: "+contractId+" and Body: "+body);
        try{
            GetElementsByLimit limit = (GetElementsByLimit) DataFactory.stringToObject(GetElementsByLimit.class, body);
            if(limit == null){
                out.print(AppDesc.APP_DESC+"ParkingLogic getAllParkings action failed due to: null request object");
                return ReturnConfig.isFailed(Response.Status.BAD_REQUEST, "Bad request, revise your data");
            }
            
            List<Parking> parkings = parkingFacade.getAllParking(contractId, limit.getLimit());
            
            if(parkings.size()<= 0){
                out.print(AppDesc.APP_DESC+"ParkingLogic getAllParkings action no data found.");
                return ReturnConfig.isFailed(Response.Status.NO_CONTENT, "Not data found");
            }
            String outPut = parkingFactory.tuneParkingList(parkings);
            out.print(AppDesc.APP_DESC+"ParkingLogic getAllParkings action failed output: "+outPut);
            return ReturnConfig.isSuccess(outPut);
        }catch(Exception e){
            out.print(AppDesc.APP_DESC+"ParkingLogic getAllParkings action failed due to: "+e.getLocalizedMessage());
            return ReturnConfig.isFailed(Response.Status.EXPECTATION_FAILED, e.getLocalizedMessage());
        }
    }
    
    public Response getRegionalParkings(String contractId, String body){
        out.print(AppDesc.APP_DESC+"ParkingLogic getRegionalParkings received Contract: "+contractId+" and Body: "+body);
        try{
            Address address = (Address) DataFactory.stringToObject(Address.class, body);
            if(address == null){
                out.print(AppDesc.APP_DESC+"ParkingLogic getRegionalParkings action failed due to: null request object");
                return ReturnConfig.isFailed(Response.Status.BAD_REQUEST, "No data found. Revise your data.");
            }
            
            List<Parking> parkings = parkingFacade.getParkingByAddress(contractId, address);
            if(parkings == null){
                out.print(AppDesc.APP_DESC+"ParkingLogic getRegionalParkings action no data found.");
                return ReturnConfig.isFailed(Response.Status.EXPECTATION_FAILED, "Not data found");
            }
            if(parkings.isEmpty()){
                out.print(AppDesc.APP_DESC+"ParkingLogic getRegionalParkings action no data found.");
                return ReturnConfig.isFailed(Response.Status.EXPECTATION_FAILED, "Not data found");
            }
            
            String outPut = parkingFactory.tuneParkingList(parkings);
            out.print(AppDesc.APP_DESC+"ParkingLogic getRegionalParkings action failed output: "+outPut);
            return ReturnConfig.isSuccess(outPut);
        }catch(Exception e){
            out.print(AppDesc.APP_DESC+"ParkingLogic getRegionalParkings action failed due to: "+e.getLocalizedMessage());
            return ReturnConfig.isFailed(Response.Status.EXPECTATION_FAILED, e.getLocalizedMessage());
        }
    }
    
    public Response editParking(String contractId, String body){
        out.print(AppDesc.APP_DESC+"ParkingLogic editParking received Contract: "+contractId+" and Body: "+body);
        try{
            ParkingBean parkingBean = (ParkingBean) DataFactory.stringToObject(ParkingBean.class, body);
            
            if(!isParkingEdited(contractId, parkingBean)){
                out.print(AppDesc.APP_DESC+"ParkingLogic editParking action failed");
                return ReturnConfig.isFailed(Response.Status.BAD_REQUEST, "Not edited, Revise your data");
            }
            
            Parking parking = parkingFacade.getParkingById(contractId, parkingBean.getParkingId()+contractId);
            String outPut = parkingFactory.tuneParking(parking);
            out.print(AppDesc.APP_DESC+"ParkingLogic editParking action Succeeded output: "+outPut);
            return ReturnConfig.isSuccess(outPut);
        }catch(Exception e){
            out.print(AppDesc.APP_DESC+"ParkingLogic editParking action failed due to: "+e.getLocalizedMessage());
            return ReturnConfig.isFailed(Response.Status.EXPECTATION_FAILED, e.getLocalizedMessage());
        }
    }
    
    public Response deleteParking(String contractId, String body){
        out.print(AppDesc.APP_DESC+"ParkingLogic deleteParking received Contract: "+contractId+" and Body: "+body);
        try{
            RequestGetElementById getElement = (RequestGetElementById) DataFactory.stringToObject(RequestGetElementById.class, body);
            if(getElement == null){
                out.print(AppDesc.APP_DESC+"ParkingLogic deleteParking failed due to: null request");
                return ReturnConfig.isFailed(Response.Status.BAD_REQUEST, "Bad request, revise your data");
            }
            
            Parking checkParking = parkingFacade.getParkingById(contractId, getElement.getId()+contractId);
            
            if(checkParking == null){
                out.print(AppDesc.APP_DESC+"ParkingLogic deleteParking action failed due to: null request object");
                return ReturnConfig.isFailed(Response.Status.NO_CONTENT, "No data found. Revise your input.");
            }
            
            ParkingBean parkingBean = parkingFactory.getParkingBean(checkParking);
            
            
            if(!isParkingDeleted(contractId, parkingBean)){
                out.print(AppDesc.APP_DESC+"ParkingLogic deleteParking action failed ");
                return ReturnConfig.isFailed(Response.Status.EXPECTATION_FAILED, "Failed to deleted this parking");
            }
            
            Parking parking = parkingFacade.getParkingById(contractId, parkingBean.getParkingId()+contractId);
            String outPut = parkingFactory.tuneParking(parking);
            out.print(AppDesc.APP_DESC+"ParkingLogic deleteParking action Succeeded output: "+outPut);
            return ReturnConfig.isSuccess(outPut);
        }catch(Exception e){
            out.print(AppDesc.APP_DESC+"ParkingLogic deleteParking action failed due to: "+e.getLocalizedMessage());
            return ReturnConfig.isFailed(Response.Status.EXPECTATION_FAILED, e.getLocalizedMessage());
        }
    }
    
    private boolean isParkingCreated(String contractId, RequestParkingCreation requestParkingCreation, String parkingId){
        if(requestParkingCreation == null)
            return false;
        if(contractId.isEmpty())
            return false;
        
        try{
            Parking parking = null;// = parkingFacade.getParkingById(contractId, parkingId);
            int counter = 1;
            do{
                if(counter == 3){
                    break;
                }
                parking = parkingFacade.getParkingById(contractId, parkingId);
                if(parking != null){
                    parkingId = idGenerator()+contractId;
                    this.parkingId = parkingId;
                }
                counter ++;
            }while(parking != null);
            
            if(parking != null)
                return false;
            
            Date date = new Date();
            
            parking = new Parking(contractId,
                    parkingId,
                    requestParkingCreation.getAddress().getProvince(),
                    requestParkingCreation.getAddress().getDistrict(),
                    requestParkingCreation.getAddress().getSector(),
                    requestParkingCreation.getAddress().getCell(),
                    requestParkingCreation.getDescription(),
                    requestParkingCreation.getCoordinates().getLongitude(),
                    requestParkingCreation.getCoordinates().getLatitude(),
                    StatusConfig.VACANT,
                    StatusConfig.VACANT_DESC,
                    date,
                    date,
                    ActionConfig.CREATE);
            parkingFacade.create(parking);
            parkingFacade.refreshParking();
            
            parking = parkingFacade.getParkingById(contractId, parkingId);
            
            return parking != null;
        }catch(Exception e){
            out.print(AppDesc.APP_DESC+"ParkingLogic isParkingCreated action failed due to: "+e.getLocalizedMessage());
            return false;
        }
    }
    
    private boolean isParkingEdited(String contractId, ParkingBean parkingBean){
        if(parkingBean == null)
            return false;
        try{
            Parking checkParking = parkingFacade.getParkingById(contractId, parkingBean.getParkingId()+contractId);
            if(checkParking == null)
                return false;
            
            Date date = new Date();
            
            Parking parking = new Parking(contractId,
                    checkParking.getParkingId(),
                    parkingBean.getAddress().getProvince(),
                    parkingBean.getAddress().getDistrict(),
                    parkingBean.getAddress().getSector(),
                    parkingBean.getAddress().getCell(),
                    parkingBean.getDescription(),
                    parkingBean.getCoordinates().getLongitude(),
                    parkingBean.getCoordinates().getLatitude(),
                    checkParking.getStatus(),
                    checkParking.getStatusDesc(),
                    checkParking.getCreationDate(),
                    date,
                    ActionConfig.UPDATE);
            parking.setId(checkParking.getId());
            parkingFacade.edit(parking);
            return true;
        }catch(Exception e){
            out.print(AppDesc.APP_DESC+"ConductorLogic isconductorCreated action failed due to: "+e.getLocalizedMessage());
            return false;
        }
    }
    
    private boolean isParkingDeleted(String contractId, ParkingBean parkingBean){
        if(parkingBean == null)
            return false;
        try{
            Parking checkParking = parkingFacade.getParkingById(contractId, parkingBean.getParkingId()+contractId);
            if(checkParking == null)
                return false;
            
            List<Deployment> parkingDeployments = deploymentFacade.getDeploymentByDeployee(contractId, checkParking.getParkingId());
            
            if(parkingDeployments != null){
                if(parkingDeployments.size() >= 1){
                    for(Deployment deployment : parkingDeployments){
                        List<Deployment> conductorDeployment = deploymentFacade.getDeploymentByParkingId(contractId, deployment.getDeployeeId());
                        if(conductorDeployment.size() <=1 ){
                            Conductor conductor = conductorFacade.getConductorById(contractId, deployment.getDeployeeId());
                            if(conductor != null){
                                conductor.setWorkStatus(StatusConfig.VACANT);
                                conductor.setWorkStatusDesc(StatusConfig.VACANT_DESC);
                                conductor.setLastAccessDesc(ActionConfig.ACCESS);
                                conductor.setLastAcessDate(new Date());
                                
                                conductorFacade.edit(conductor);
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
            checkParking.setStatus(StatusConfig.DELETED);
            checkParking.setStatusDesc(StatusConfig.DELETED_DESC);
            checkParking.setLastAccessDate(new Date());
            checkParking.setLastAccessAction(ActionConfig.INVALIDATE);
            
            parkingFacade.edit(checkParking);
            return true;
        }catch(Exception e){
            out.print(AppDesc.APP_DESC+"ConductorLogic isconductorCreated action failed due to: "+e.getLocalizedMessage());
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
