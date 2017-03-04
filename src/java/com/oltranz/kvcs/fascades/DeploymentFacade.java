/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package com.oltranz.kvcs.fascades;

import com.oltranz.kvcs.entities.Deployment;
import static java.lang.System.out;
import java.util.Date;
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
public class DeploymentFacade extends AbstractFacade<Deployment> {
    
    @PersistenceContext(unitName = "KvcsParkingModulePU")
    private EntityManager em;
    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
    public void refreshDeployment(){
        em.flush();
    }
    
    public DeploymentFacade() {
        super(Deployment.class);
    }
    
    public Deployment getDeploymentById(String contractId, String deploymentId){
        try{
            if(deploymentId.isEmpty())
                return null;
            Query q= em.createQuery("Select D from Deployment D WHERE D.contractId = :contractId AND D.deployId = :deploymentId");
            q.setParameter("deploymentId", deploymentId)
                    .setParameter("contractId", contractId);
            List<Deployment> list = (List<Deployment>)q.getResultList();
            if(!list.isEmpty())
                return list.get(0);
            else
                return null;
        }catch(Exception ex){
            ex.printStackTrace(out);
            return null;
        }
    }
    
    public List<Deployment> getDeploymentByDeployer(String contractId, String deployerId){
        try{
            if(deployerId.isEmpty())
                return null;
            Query q= em.createQuery("Select D from Deployment D WHERE D.contractId = :contractId AND D.deployerId = :deployerId");
            q.setParameter("deployerId", deployerId)
                    .setParameter("contractId", contractId);
            List<Deployment> list = (List<Deployment>)q.getResultList();
            return list.isEmpty()?null:list;
        }catch(Exception ex){
            ex.printStackTrace(out);
            return null;
        }
    }
    
    public List<Deployment> getDeploymentByDeployee(String contractId, String deployeeId){
        try{
            if(deployeeId.isEmpty())
                return null;
            Query q= em.createQuery("Select D from Deployment D WHERE D.contractId = :contractId AND D.deployeeId = :deployeeId");
            q.setParameter("deployeeId", deployeeId)
                    .setParameter("contractId", contractId);
            List<Deployment> list = (List<Deployment>)q.getResultList();
            return list.isEmpty()?null:list;
        }catch(Exception ex){
            ex.printStackTrace(out);
            return null;
        }
    }
    
    public List<Deployment> getDeploymentByParkingId(String contractId, String parkingId){
        try{
            if(parkingId.isEmpty())
                return null;
            Query q= em.createQuery("Select D from Deployment D WHERE D.contractId = :contractId AND D.parkingId = :parkingId");
            q.setParameter("parkingId", parkingId)
                    .setParameter("contractId", contractId);
            List<Deployment> list = (List<Deployment>)q.getResultList();
            return list.isEmpty()?null:list;
        }catch(Exception ex){
            ex.printStackTrace(out);
            return null;
        }
    }
    
    public List<Deployment> getDeploymentByStartDate(String contractId, Date startDate){
        try{
            if(startDate == null)
                return null;
            Query q= em.createQuery("Select D from Deployment D WHERE D.contractId = :contractId AND D.startingDate = :startDate");
            q.setParameter("startDate", startDate)
                    .setParameter("contractId", contractId);
            List<Deployment> list = (List<Deployment>)q.getResultList();
            return list.isEmpty()?null:list;
        }catch(Exception ex){
            ex.printStackTrace(out);
            return null;
        }
    }
    
    public List<Deployment> getDeploymentByStartDateRange(String contractId, Date startDate1, Date startDate2){
        try{
            if(startDate1 == null)
                return null;
            Query q= em.createQuery("Select D from Deployment D WHERE D.contractId = :contractId AND D.startingDate BETWEEN :startDate AND :endDate");
            q.setParameter("endDate", startDate2)
                    .setParameter("startDate", startDate1)
                    .setParameter("contractId", contractId);
            List<Deployment> list = (List<Deployment>)q.getResultList();
            return list.isEmpty()?null:list;
        }catch(Exception ex){
            ex.printStackTrace(out);
            return null;
        }
    }
    
    public List<Deployment> getDeploymentByEndDate(String contractId, Date endDate){
        try{
            if(endDate == null)
                return null;
            Query q= em.createQuery("Select D from Deployment D WHERE D.contractId = :contractId AND D.expirationDate = :endDate");
            q.setParameter("endDate", endDate)
                    .setParameter("contractId", contractId);
            List<Deployment> list = (List<Deployment>)q.getResultList();
            return list.isEmpty()?null:list;
        }catch(Exception ex){
            ex.printStackTrace(out);
            return null;
        }
    }
    
    public List<Deployment> getDeploymentByEndDateRange(String contractId, Date endDate1, Date endDate2){
        try{
            if(endDate1 == null)
                return null;
            Query q= em.createQuery("Select D from Deployment D WHERE D.contractId = :contractId AND D.expirationDate BETWEEN :startDate AND :endDate");
            q.setParameter("endDate", endDate2)
                    .setParameter("startDate", endDate1)
                    .setParameter("contractId", contractId);
            List<Deployment> list = (List<Deployment>)q.getResultList();
            return list.isEmpty()?null:list;
        }catch(Exception ex){
            ex.printStackTrace(out);
            return null;
        }
    }
    
    public List<Deployment> getAllDeployment(String contractId, int limit){
        try{
            Query q;
            if(limit == 0)
                q = em.createQuery("Select D from Deployment D WHERE D.contractId = :contractId");
            else{
                q = em.createQuery("Select D from Deployment D WHERE D.contractId = :contractId");
                q.setMaxResults(limit);
            }
            q.setParameter("contractId", contractId);
            List<Deployment> list = (List<Deployment>)q.getResultList();
            
            return list.isEmpty()?null:list;
        }catch(Exception ex){
            ex.printStackTrace(out);
            return null;
        }
    }
}
