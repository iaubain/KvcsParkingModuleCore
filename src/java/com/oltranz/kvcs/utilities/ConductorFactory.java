/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package com.oltranz.kvcs.utilities;

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
import com.oltranz.kvcs.simplebeans.commonbeans.ParkingBean;
import com.oltranz.kvcs.simplebeans.commonbeans.StatusBean;
import com.oltranz.kvcs.simplebeans.conductors.ResponseConductor;
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
public class ConductorFactory {
    
    @EJB
            ConductorFacade conductorFacade;
    @EJB
            DeploymentFacade deploymentFacade;
    @EJB
            ParkingFacade parkingFacade;
    @EJB
            ParkingFactory parkingFactory;
    
    public String tuneConductor(Conductor mConductor){
        try{
            if(mConductor == null){
                out.print(AppDesc.APP_DESC+" ConductorFactory fineTuneConductor failed to produce result due to: null input");
                return null;
            }
            ConductorBean conductorBean = getConductorBean(mConductor);
            
            mConductor.setLastAccessDesc(ActionConfig.ACCESS);
            mConductor.setLastAcessDate(new Date());
            conductorFacade.edit(mConductor);
            
            List<Deployment> conductorDeployments = deploymentFacade.getDeploymentByDeployee(mConductor.getContractId(), mConductor.getConductorId().replace(mConductor.getContractId(), ""));
            List<ParkingBean> mParkings = new ArrayList<>();
            if(conductorDeployments != null){
                if(conductorDeployments.size() >= 1){
                    for(Deployment deployment : conductorDeployments){
                        Parking parking = parkingFacade.getParkingById(mConductor.getContractId(), deployment.getParkingId());
                        if(parking != null){
                            ParkingBean parkingBean = parkingFactory.getParkingBean(parking);
                            parking.setLastAccessAction(ActionConfig.ACCESS);
                            parking.setLastAccessDate(new Date());
                            parkingFacade.edit(parking);
                            
                            mParkings.add(parkingBean);
                        }
                    }
                }
            }
            
            ResponseConductor responseConductor = new ResponseConductor(conductorBean, new StatusBean(mConductor.getSystemStatus(), mConductor.getSystemStatusDesc()), mParkings);
            String outPut = DataFactory.objectToString(responseConductor);
            out.print(AppDesc.APP_DESC+"ConductorFactory fineTuneConductor action Succeeded output: "+outPut);
            
            return outPut;
        }catch(Exception e){
            out.print(AppDesc.APP_DESC+" ConductorFactory fineTuneConductor failed to produce result due to: "+e.getLocalizedMessage());
            return null;
        }
    }
    
    public String tuneConductorList(List<Conductor> mConductors){
        try{
            if(mConductors == null){
                out.print(AppDesc.APP_DESC+" ConductorFactory tuneConductorList failed to produce result due to: null input");
                return null;
            }else if(mConductors.isEmpty()){
                out.print(AppDesc.APP_DESC+" ConductorFactory tuneConductorList failed to produce result due to: null input");
                return null;
            }
            
            List<ResponseConductor> conductorResponseBeanlist = new ArrayList<>();
            for(Conductor conductor : mConductors){
                ConductorBean conductorBean =  getConductorBean(conductor);
                
                conductor.setLastAccessDesc(ActionConfig.ACCESS);
                conductor.setLastAcessDate(new Date());
                conductorFacade.edit(conductor);
                
                List<Deployment> conductorDeployments = deploymentFacade.getDeploymentByDeployee(conductor.getContractId(), conductor.getConductorId().replace(conductor.getContractId(), ""));
                List<ParkingBean> mParkings = new ArrayList<>();
                if(conductorDeployments != null){
                    if(conductorDeployments.size() >= 1){
                        for(Deployment deployment : conductorDeployments){
                            Parking parking = parkingFacade.getParkingById(conductor.getContractId(), deployment.getParkingId());
                            if(parking != null){
                                ParkingBean parkingBean = parkingFactory.getParkingBean(parking);
                                parking.setLastAccessAction(ActionConfig.ACCESS);
                                parking.setLastAccessDate(new Date());
                                parkingFacade.edit(parking);
                                
                                mParkings.add(parkingBean);
                            }
                        }
                    }
                }
                
                ResponseConductor responseConductor = new ResponseConductor(conductorBean, new StatusBean(conductor.getSystemStatus(), conductor.getSystemStatusDesc()), mParkings);
                String outPut = DataFactory.objectToString(responseConductor);
                out.print(AppDesc.APP_DESC+"ConductorFactory tuneConductorList action Succeeded output: "+outPut);
                
                conductorResponseBeanlist.add(responseConductor);
            }
            String outPut = DataFactory.objectToString(conductorResponseBeanlist);
            out.print(AppDesc.APP_DESC+"ConductorFactory tuneConductorList action failed output: "+outPut);
            
            return outPut;
        }catch(Exception e){
            out.print(AppDesc.APP_DESC+" ConductorFactory tuneConductorList failed to produce result due to: "+e.getLocalizedMessage());
            return null;
        }
    }
    
    private void adjustConductor(Conductor conductor){
        try{
            
            if(conductor == null)
                return;
            Deployment deployment = deploymentFacade.getLastDeployment(conductor.getContractId(), conductor.getConductorId());
            if(deployment != null){
                if(deployment.getExpirationDate().before(new Date())){
                    out.print(AppDesc.APP_DESC+"ConductorFactory adjustConductor last conductor: "+conductor.getFname()+" with ID "+conductor.getConductorId().replace(conductor.getContractId(), "")+" Deployment expired has system status "+conductor.getSystemStatusDesc());
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
            out.print(AppDesc.APP_DESC+"ConductorFactory adjustConductor last conductor: "+conductor.getFname()+" with ID "+conductor.getConductorId().replace(conductor.getContractId(), "")+" failed to adjust his her deployment data. "+conductor.getSystemStatusDesc()+" cause: "+e.getMessage());
            
        }
    }
    
    public ConductorBean getConductorBean(Conductor conductor){
        try{
            
            adjustConductor(conductor);
                conductor = conductorFacade.getConductorById(conductor.getContractId(), conductor.getConductorId());
                
                if(conductor == null){
                    out.print(AppDesc.APP_DESC+" ConductorFactory tuneConductorList failed to produce result after adjustment of conductor due to: null conducor instance");
                    return null;
                }
                
            return new ConductorBean(conductor.getConductorId().replace(conductor.getContractId(), ""),
                    conductor.getFname(),
                    conductor.getmName(),
                    conductor.getlName(),
                    conductor.getGender(),
                    conductor.getTel(),
                    new Address(conductor.getProvince(), conductor.getDistrict(), conductor.getSector(), conductor.getCell()),
                    new StatusBean(conductor.getWorkStatus(), conductor.getWorkStatusDesc()));
        }catch(Exception e){
            out.print(AppDesc.APP_DESC+" ConductorFactory getConductorBean failed to produce result due to: "+e.getLocalizedMessage());
            return null;
        }
    }
}
