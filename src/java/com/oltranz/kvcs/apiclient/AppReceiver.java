/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package com.oltranz.kvcs.apiclient;

import com.oltranz.kvcs.config.AppDesc;
import com.oltranz.kvcs.config.HeaderConfig;
import com.oltranz.kvcs.logic.ClientValidator;
import com.oltranz.kvcs.logic.CommandExec;
import com.oltranz.kvcs.utilities.ReturnConfig;
import static java.lang.System.out;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

/**
 *
 * @author Hp
 */
@Stateless
public class AppReceiver {
    @EJB
            ClientValidator validator;
    @EJB
            CommandExec commandExec;
    public Response clientReceiver(HttpHeaders headers, String body){
        if(headers == null){
            out.print(AppDesc.APP_DESC+"Client receiver receives Headers: isEmpty Body: "+body);
            return ReturnConfig.isFailed(Response.Status.BAD_REQUEST, "Bad request");
        }
        out.print(AppDesc.APP_DESC+"Client receiver receives Headers:"+headers.toString()+"Body: "+body);
       
        if(headers.getHeaderString(HeaderConfig.CONTRACT) != null && 
                headers.getHeaderString(HeaderConfig.CMD) != null){
            String cmd = headers.getHeaderString(HeaderConfig.CMD);
            String contractId = headers.getHeaderString(HeaderConfig.CONTRACT);
            
            out.print(AppDesc.APP_DESC+"Client receiver validating CMD:"+cmd+" and Contract: "+contractId);
            if(validator.isValid(contractId, cmd)){
                out.print(AppDesc.APP_DESC+"Client receiver receves valid CMD:"+cmd+" and Contract: "+contractId);
                return commandExec.exec(contractId, cmd, body);
            }else{
            out.print(AppDesc.APP_DESC+"Client receiver receves Invalid CMD:"+cmd+" and Contract: "+contractId);
            return ReturnConfig.isFailed(Response.Status.BAD_REQUEST, "Bad request");
            }
        }else{
            out.print(AppDesc.APP_DESC+"Client receiver receves Invalid headers");
            return ReturnConfig.isFailed(Response.Status.BAD_REQUEST, "Bad request");
        }
        
    }
}
