/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package com.oltranz.kvcs.utilities;

import com.oltranz.kvcs.config.ActionConfig;
import com.oltranz.kvcs.config.AppDesc;
import com.oltranz.kvcs.config.StatusConfig;
import com.oltranz.kvcs.entities.SuperUser;
import com.oltranz.kvcs.fascades.SuperUserFacade;
import com.oltranz.kvcs.logic.ConductorsLogic;
import com.oltranz.kvcs.simplebeans.commonbeans.Address;
import com.oltranz.kvcs.simplebeans.commonbeans.StatusBean;
import com.oltranz.kvcs.simplebeans.commonbeans.SuperUserBean;
import com.oltranz.kvcs.simplebeans.superusers.RequestCreateSuperUser;
import static java.lang.System.out;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 *
 * @author Hp
 */
@Stateless
public class SuperUserFactory {
    @EJB
            SuperUserFacade superUserFacade;
    @EJB
            IdGenerator idGenerator;
    
    public SuperUserBean produceSuperUserBean(SuperUser superUser){
        try{
            SuperUserBean superUserBean = new SuperUserBean(superUser.getUserId().replace(superUser.getContractId(), ""),
                    superUser.getUserNames(),
                    superUser.getTel(),
                    superUser.getEmail(),
                    new Address(superUser.getProvince(), superUser.getDistrict(), superUser.getSector(), superUser.getCell()),
                    new StatusBean(superUser.getStatus(), superUser.getStatusDesc()));
            return superUserBean;
        }catch(Exception e){
            out.print(AppDesc.APP_DESC+" SuperUserFactory produceSuperUserBean failed to produce result due to: "+e.getLocalizedMessage());
            return null;
        }
    }
    
    public boolean isSuperUserCtreated(String contractId, RequestCreateSuperUser createSuperUser){
        try{
            if(contractId.isEmpty()){
                out.print(AppDesc.APP_DESC+" SuperUserFactory isSuperUserCtreated failed to produce result due to: null contract");
                return false;
            }
            if(createSuperUser == null){
                out.print(AppDesc.APP_DESC+" SuperUserFactory isSuperUserCtreated failed to produce result due to: null input");
                return false;
            }
            String genId = idGenerator()+contractId;
            SuperUser superUser = superUserFacade.getSuperUSerByTel(contractId, createSuperUser.getTel());
            Date date = new Date();
            SuperUser checkSuper;
            if(superUser == null){
                int counter = 1;
                do{
                    checkSuper = superUserFacade.getSuperUSerById(contractId, genId);
                    if(checkSuper != null)
                        genId = idGenerator()+contractId;
                }while(checkSuper != null);
                
                superUser = new SuperUser(contractId,
                        genId,
                        createSuperUser.getUserNames(),
                        createSuperUser.getAddress().getProvince(),
                        createSuperUser.getAddress().getDistrict(),
                        createSuperUser.getAddress().getSector(),
                        createSuperUser.getAddress().getCell(),
                        createSuperUser.getPermissions(),
                        createSuperUser.getTel(),
                        createSuperUser.getEmail(),
                        date,
                        date,
                        ActionConfig.CREATE,
                        StatusConfig.ACTIVE,
                        StatusConfig.ACTIVE_DESC);
                
                superUserFacade.create(superUser);
                superUserFacade.refreshSuperUser();
                return true;
            }else{
                String superUserId = superUser.getId();
                superUser = new SuperUser(contractId,
                        superUser.getUserId(),
                        createSuperUser.getUserNames(),
                        createSuperUser.getAddress().getProvince(),
                        createSuperUser.getAddress().getDistrict(),
                        createSuperUser.getAddress().getSector(),
                        createSuperUser.getAddress().getCell(),
                        createSuperUser.getPermissions(),
                        createSuperUser.getTel(),
                        createSuperUser.getEmail(),
                        superUser.getCreationDate(),
                        date,
                        ActionConfig.ACCESS,
                        StatusConfig.ACTIVE,
                        StatusConfig.ACTIVE_DESC);
                superUser.setId(superUserId);
                superUserFacade.edit(superUser);
                return true;
            }
        }catch(Exception e){
            out.print(AppDesc.APP_DESC+" SuperUserFactory isSuperUserCtreated failed to produce result due to: "+e.getLocalizedMessage());
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
