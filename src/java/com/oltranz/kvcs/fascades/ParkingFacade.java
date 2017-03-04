/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package com.oltranz.kvcs.fascades;

import com.oltranz.kvcs.entities.Parking;
import com.oltranz.kvcs.simplebeans.commonbeans.Address;
import static java.lang.System.out;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

/**
 *
 * @author Hp
 */
@Stateless
public class ParkingFacade extends AbstractFacade<Parking> {
    
    @PersistenceContext(unitName = "KvcsParkingModulePU")
    private EntityManager em;
    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    public void refreshParking(){
        em.flush();
    }
    
    public ParkingFacade() {
        super(Parking.class);
    }
    
    public Parking getParkingById(String contractId, String parkingId){
        try{
            if(parkingId.isEmpty())
                return null;
            Query q= em.createQuery("Select P from Parking P WHERE P.contractId = :contractId AND P.parkingId = :parkingId");
            q.setParameter("contractId", contractId)
                    .setParameter("parkingId", parkingId);
            List<Parking> list = (List<Parking>)q.getResultList();
            if(!list.isEmpty())
                return list.get(0);
            else
                return null;
        }catch(Exception ex){
            ex.printStackTrace(out);
            return null;
        }
    }
    
    public List<Parking> getParkingByAddress(String contractId, Address address){
        if(address == null)
            return null;
        
        Query mQuery;
        if(!address.getCell().isEmpty() &&
                !address.getDistrict().isEmpty() &&
                !address.getProvince().isEmpty() &&
                !address.getSector().isEmpty()){
            try{
                mQuery = em.createQuery("Select P from Parking P WHERE P.contractId = :contractId AND P.province = :province AND P.district = :district AND P.sector = :sector AND P.cell = :cell");
                mQuery.setParameter("contractId", contractId)
                        .setParameter("province", address.getProvince())
                        .setParameter("district", address.getDistrict())
                        .setParameter("sector", address.getSector())
                        .setParameter("cell", address.getCell());
                List<Parking> list = (List<Parking>)mQuery.getResultList();
                
                return list.isEmpty()?null:list;
            }catch(Exception e){
                e.printStackTrace(out);
                return null;
            }
        }else if(!address.getDistrict().isEmpty() &&
                !address.getProvince().isEmpty() &&
                !address.getSector().isEmpty()){
            try{
                mQuery = em.createQuery("Select p from Parking P WHERE P.contractId = :contractId AND P.province = :province AND P.district = :district AND P.sector = :sector");
                mQuery.setParameter("province", address.getProvince())
                        .setParameter("district", address.getDistrict())
                        .setParameter("sector", address.getSector())
                        .setParameter("contractId", contractId);
                List<Parking> list = (List<Parking>)mQuery.getResultList();
                
                return list.isEmpty()?null:list;
            }catch(Exception e){
                e.printStackTrace(out);
                return null;
            }
        }else if(!address.getDistrict().isEmpty() &&
                !address.getProvince().isEmpty()){
            try{
                mQuery = em.createQuery("Select P from Parking P WHERE P.contractId = :contractId AND P.province = :province AND P.district = :district");
                mQuery.setParameter("province", address.getProvince())
                        .setParameter("district", address.getDistrict())
                        .setParameter("contractId", contractId);
                List<Parking> list = (List<Parking>)mQuery.getResultList();
                
                return list.isEmpty()?null:list;
            }catch(Exception e){
                e.printStackTrace(out);
                return null;
            }
        }else if(!address.getProvince().isEmpty()){
            try{
                mQuery = em.createQuery("Select P from Parking P WHERE P.contractId = :contractId AND P.province = :province");
                mQuery.setParameter("province", address.getProvince())
                        .setParameter("contractId", contractId);
                List<Parking> list = (List<Parking>)mQuery.getResultList();
                
                return list.isEmpty()?null:list;
            }catch(Exception e){
                e.printStackTrace(out);
                return null;
            }
        }else{
            return null;
        }
    }
    
    public List<Parking> getParkingByStatus(String contractId, int status){
        try{
            if(status == 0)
                return null;
            Query q= em.createQuery("Select P from Parking P WHERE P.contractId = :contractId AND P.status = :status");
            q.setParameter("status", status)
                    .setParameter("contractId", contractId);
            List<Parking> list = (List<Parking>)q.getResultList();
            
            return list.isEmpty()?null:list;
        }catch(Exception ex){
            ex.printStackTrace(out);
            return null;
        }
    }
    
    public List<Parking> getAllParking(String contractId, int limit){
        try{
            Query q;
            if(limit == 0)
                q = em.createQuery("Select P from Parking P WHERE P.contractId = :contractId");
            else{
                q = em.createQuery("Select P from Parking P WHERE  P.contractId = :contractId");
                q.setMaxResults(limit);
            }
            q.setParameter("contractId", contractId);
            List<Parking> list = (List<Parking>)q.getResultList();
            
            return list.isEmpty()?null:list;
        }catch(Exception ex){
            ex.printStackTrace(out);
            return null;
        }
    }
}
