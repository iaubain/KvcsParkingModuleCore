/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oltranz.kvcs.fascades;

import com.oltranz.kvcs.entities.SuperUser;
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
public class SuperUserFacade extends AbstractFacade<SuperUser> {

    @PersistenceContext(unitName = "KvcsParkingModulePU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
    public void refreshSuperUser(){
        em.flush();
    }

    public SuperUserFacade() {
        super(SuperUser.class);
    }
    
    public SuperUser getSuperUSerById(String contractId, String userId){
        try{
            if(userId.isEmpty())
                return null;
            Query q= em.createQuery("Select S from SuperUser S WHERE S.contractId = :contractId AND S.userId = :userId");
            q.setParameter("userId", userId)
                    .setParameter("contractId", contractId);
            List<SuperUser> list = (List<SuperUser>)q.getResultList();
            if(!list.isEmpty())
                return list.get(0);
            else
                return null;
        }catch(Exception ex){
            ex.printStackTrace(out);
            return null;
        }
    }
    
    public SuperUser getSuperUSerByTel(String contractId, String tel){
        try{
            if(tel.isEmpty())
                return null;
            Query q= em.createQuery("Select S from SuperUser S WHERE S.contractId = :contractId AND S.tel = :tel");
            q.setParameter("tel", tel)
                    .setParameter("contractId", contractId);
            List<SuperUser> list = (List<SuperUser>)q.getResultList();
            if(!list.isEmpty())
                return list.get(0);
            else
                return null;
        }catch(Exception ex){
            ex.printStackTrace(out);
            return null;
        }
    }
    
}
