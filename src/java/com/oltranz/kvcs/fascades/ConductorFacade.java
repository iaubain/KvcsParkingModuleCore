/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oltranz.kvcs.fascades;

import com.oltranz.kvcs.entities.Conductor;
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
public class ConductorFacade extends AbstractFacade<Conductor> {

    @PersistenceContext(unitName = "KvcsParkingModulePU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ConductorFacade() {
        super(Conductor.class);
    }
    
    public void refreshConductor(){
        em.flush();
    }
    
    public Conductor getConductorById(String contractId, String conductorId){
        try{
            if(conductorId.isEmpty())
                return null;
            Query q= em.createQuery("Select C from Conductor C WHERE C.contractId = :contractId AND C.conductorId = :conductorId");
            q.setParameter("conductorId", conductorId)
             .setParameter("contractId", contractId);
            List<Conductor> list = (List<Conductor>)q.getResultList();
            if(!list.isEmpty())
                return list.get(0);
            else
                return null;
        }catch(Exception ex){
            ex.printStackTrace(out);
            return null;
        }
    }
    
    public Conductor getConductorByTel(String contractId, String tel){
        try{
            if(tel.isEmpty())
                return null;
            Query q= em.createQuery("Select C from Conductor C WHERE C.contractId = :contractId AND C.tel = :msisdn");
            q.setParameter("msisdn", tel)
             .setParameter("contractId", contractId);
            List<Conductor> list = (List<Conductor>)q.getResultList();
            if(!list.isEmpty())
                return list.get(0);
            else
                return null;
        }catch(Exception ex){
            ex.printStackTrace(out);
            return null;
        }
    }
    
    public List<Conductor> getConductorByAddress(String contractId, Address address){
        if(address == null)
            return null;
        
        Query mQuery;
        if(!address.getCell().isEmpty() && 
                !address.getDistrict().isEmpty() && 
                !address.getProvince().isEmpty() &&
                !address.getSector().isEmpty()){
            try{
                mQuery = em.createQuery("Select C from Conductor C WHERE C.contractId = :contractId AND C.province = :province AND C.district = :district AND C.sector = :sector AND C.cell = :cell");
                mQuery.setParameter("contractId", contractId)
                        .setParameter("province", address.getProvince())
                        .setParameter("district", address.getDistrict())
                        .setParameter("sector", address.getSector())
                        .setParameter("cell", address.getCell());
                List<Conductor> list = (List<Conductor>)mQuery.getResultList();
                
                return list.isEmpty()?null:list;
            }catch(Exception e){
                e.printStackTrace(out);
                return null;
            }
        }else if(!address.getDistrict().isEmpty() && 
                !address.getProvince().isEmpty() &&
                !address.getSector().isEmpty()){
            try{
                mQuery = em.createQuery("Select C from Conductor C WHERE C.contractId = :contractId AND C.province = :province AND C.district = :district AND C.sector = :sector");
                mQuery.setParameter("province", address.getProvince())
                        .setParameter("district", address.getDistrict())
                        .setParameter("sector", address.getSector())
                        .setParameter("contractId", contractId);
                List<Conductor> list = (List<Conductor>)mQuery.getResultList();
                
                return list.isEmpty()?null:list;
            }catch(Exception e){
                e.printStackTrace(out);
                return null;
            }
        }else if(!address.getDistrict().isEmpty() && 
                !address.getProvince().isEmpty()){
            try{
                mQuery = em.createQuery("Select C from Conductor C WHERE C.contractId = :contractId AND C.province = :province AND C.district = :district");
                mQuery.setParameter("province", address.getProvince())
                        .setParameter("district", address.getDistrict())
                        .setParameter("contractId", contractId);
                List<Conductor> list = (List<Conductor>)mQuery.getResultList();
                
                return list.isEmpty()?null:list;
            }catch(Exception e){
                e.printStackTrace(out);
                return null;
            }
        }else if(!address.getProvince().isEmpty()){
            try{
                mQuery = em.createQuery("Select C from Conductor C WHERE C.contractId = :contractId AND C.province = :province");
                mQuery.setParameter("province", address.getProvince())
                        .setParameter("contractId", contractId);
                List<Conductor> list = (List<Conductor>)mQuery.getResultList();
                
                return list.isEmpty()?null:list;
            }catch(Exception e){
                e.printStackTrace(out);
                return null;
            }
        }else{
            return null;
        }
    }
    
    public List<Conductor> getConductorBySystemStatus(String contractId, int status){
        try{
            if(status == 0)
                return null;
            Query q= em.createQuery("Select C from Conductor C WHERE C.contractId = :contractId AND C.systemStatus = :status");
            q.setParameter("status", status)
                        .setParameter("contractId", contractId);
            List<Conductor> list = (List<Conductor>)q.getResultList();
            if(list.isEmpty())
                return null;
            else
                return list;
        }catch(Exception ex){
            ex.printStackTrace(out);
            return null;
        }
    }
    
    public List<Conductor> getConductorByWorkStatus(String contractId, int status){
        try{
            if(status == 0)
                return null;
            Query q= em.createQuery("Select C from Conductor C WHERE C.contractId = :contractId AND C.workStatus = :status");
            q.setParameter("status", status)
                        .setParameter("contractId", contractId);
            List<Conductor> list = (List<Conductor>)q.getResultList();
            if(list.isEmpty())
                return null;
            else
                return list;
        }catch(Exception ex){
            ex.printStackTrace(out);
            return null;
        }
    }
    
    public List<Conductor> getAllConductor(String contractId, int limit){
        try{
            Query q;
            if(limit == 0){
                q = em.createQuery("Select C from Conductor C WHERE C.contractId = :contractId");
                q.setParameter("contractId", contractId);
            }else{
                q = em.createQuery("Select C from Conductor C WHERE C.contractId = :contractId");
                q.setParameter("contractId", contractId);
                q.setMaxResults(limit);
            }
            List<Conductor> list = (List<Conductor>)q.getResultList();
            
            return list.isEmpty()?null:list;
        }catch(Exception ex){
            ex.printStackTrace(out);
            return null;
        }
    }
}
