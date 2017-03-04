/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oltranz.kvcs.utilities;

import com.oltranz.kvcs.config.ActionConfig;
import com.oltranz.kvcs.config.AppDesc;
import com.oltranz.kvcs.entities.Conductor;
import com.oltranz.kvcs.entities.Deployment;
import com.oltranz.kvcs.entities.Parking;
import com.oltranz.kvcs.fascades.ConductorFacade;
import com.oltranz.kvcs.fascades.DeploymentFacade;
import com.oltranz.kvcs.fascades.ParkingFacade;
import com.oltranz.kvcs.simplebeans.commonbeans.Address;
import com.oltranz.kvcs.simplebeans.commonbeans.ConductorBean;
import com.oltranz.kvcs.simplebeans.commonbeans.Coordinates;
import com.oltranz.kvcs.simplebeans.commonbeans.ParkingBean;
import com.oltranz.kvcs.simplebeans.commonbeans.StatusBean;
import com.oltranz.kvcs.simplebeans.parkings.ResponseParking;
import static java.lang.System.out;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 *
 * @author Hp
 */
@Stateless
public class ParkingFactory {
    @EJB
    ConductorFacade conductorFacade;
    @EJB
    DeploymentFacade deploymentFacade;
    @EJB
    ParkingFacade parkingFacade;
    @EJB
    ConductorFactory conductorFactory;
    
    public String tuneParking(Parking mParking){
        try{
            List<Deployment> parkingDeployments = deploymentFacade.getDeploymentByParkingId(mParking.getContractId(), mParking.getParkingId());
            List<ConductorBean> conductorList = new ArrayList<>();
            if(parkingDeployments != null){
                if(parkingDeployments.size() >= 1){
                    for(Deployment deployment : parkingDeployments){
                        Conductor conductor = conductorFacade.getConductorById(mParking.getContractId(), deployment.getDeployeeId());
                        if(conductor != null){
                            ConductorBean conductorBean = conductorFactory.getConductorBean(conductor);
                            conductorList.add(conductorBean);
                        }
                    }
                }
            }
            
            ResponseParking responseParking = new ResponseParking(getParkingBean(mParking), conductorList, new StatusBean(mParking.getStatus(), mParking.getStatusDesc()));
            String outPut = DataFactory.objectToString(responseParking);
            out.print(AppDesc.APP_DESC+"ParkingFactory tuneParking action Succeeded output: "+outPut);
            
            return outPut;
        }catch(Exception e){
            out.print(AppDesc.APP_DESC+" ParkingFactory tuneParking failed to produce result due to: "+e.getLocalizedMessage());
            return null;
        }
    }
    
    public String tuneParkingList(List<Parking> mParkings){
            try{
                List<ResponseParking> responseParkings = new ArrayList<>();
            for(Parking parking : mParkings){
                ParkingBean parkingBean = getParkingBean(parking);
                
                parking.setLastAccessAction(ActionConfig.ACCESS);
                parking.setLastAccessDate(new Date());
                parkingFacade.edit(parking);
            
            List<Deployment> parkingDeployments = deploymentFacade.getDeploymentByParkingId(parking.getContractId(), parking.getParkingId().replace(parking.getContractId(), ""));
            List<ConductorBean> conductorList = new ArrayList<>();
            if(parkingDeployments != null){
                if(parkingDeployments.size() >= 1){
                    for(Deployment deployment : parkingDeployments){
                        Conductor conductor = conductorFacade.getConductorById(parking.getContractId(), deployment.getDeployeeId());
                        if(conductor != null){
                            ConductorBean conductorBean = conductorFactory.getConductorBean(conductor);
                            conductor.setLastAccessDesc(ActionConfig.ACCESS);
                            conductor.setLastAcessDate(new Date());
                            conductorFacade.edit(conductor);
                        
                            conductorList.add(conductorBean);
                        }
                    }
                }
            }
            
            ResponseParking responseParking = new ResponseParking(parkingBean, conductorList, new StatusBean(parking.getStatus(), parking.getStatusDesc()));
            String outPut = DataFactory.objectToString(responseParking);
            out.print(AppDesc.APP_DESC+"ParkingFactory tuneParking action Succeeded output: "+outPut);
            
            responseParkings.add(responseParking);
            }
            String outPut = DataFactory.objectToString(responseParkings);
            out.print(AppDesc.APP_DESC+"ConductorFactory tuneConductorList action failed output: "+outPut); 
            
            return outPut;
            }catch(Exception e){
                out.print(AppDesc.APP_DESC+" ConductorFactory tuneConductorList failed to produce result due to: "+e.getLocalizedMessage());
                return null;
            }
        }
    
    public ParkingBean getParkingBean(Parking parking){
         try{
                 return new ParkingBean(parking.getParkingId().replace(parking.getContractId(), ""),
                         parking.getParkingDesc(), 
                         new Address(parking.getProvince(), parking.getDistrict(), parking.getSector(), parking.getCell()), 
                         new Coordinates(parking.getLatitude(), parking.getLongitude()), 
                         new StatusBean(parking.getStatus(), parking.getStatusDesc()));
            }catch(Exception e){
                out.print(AppDesc.APP_DESC+" ParkingFactory getParkingBean failed to produce result due to: "+e.getLocalizedMessage());
                return null;
            } 
    }
}
