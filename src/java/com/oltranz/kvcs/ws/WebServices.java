/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package com.oltranz.kvcs.ws;

import com.oltranz.kvcs.apiclient.AppReceiver;
import com.oltranz.kvcs.config.AppDesc;
import com.oltranz.kvcs.utilities.ReturnConfig;
import static java.lang.System.out;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

/**
 *
 * @author Hp
 */
@Path("/system")
@Stateless
public class WebServices {
    @EJB
            AppReceiver receiver;
    
    @POST
    @Path("/actions")
    @Consumes("application/json")
    public Response parkingModule(@Context HttpServletRequest requestContext, @Context HttpHeaders headers, String body){
        out.print(AppDesc.APP_DESC+" Node Received Headers "+headers.toString()+" and Body  "+body);        
        out.print(AppDesc.APP_DESC+" Source IP: " + requestContext.getRemoteAddr() + ", Port: " + requestContext.getRemotePort() + ", Host: " + requestContext.getRemoteHost()); 
        return receiver.clientReceiver(headers, body);
    }
    
    @POST
    @Path("/localservice")
    @Consumes("application/json")
    public Response localService(@Context HttpServletRequest requestContext,@Context HttpHeaders headers, String body){
        out.print(AppDesc.APP_DESC+" Node Received Headers "+headers.toString()+" and Body  "+body);
        out.print(AppDesc.APP_DESC+" Source IP: " + requestContext.getRemoteAddr() + ", Port: " + requestContext.getRemotePort() + ", Host: " + requestContext.getRemoteHost()); 
        if(!requestContext.getRemoteAddr().equals("0:0:0:0:0:0:0:1") && !requestContext.getRemoteAddr().equals("127.0.0.1"))
            return ReturnConfig.isFailed(Response.Status.FORBIDDEN, "Not allowed this service.");
        return receiver.clientReceiver(headers, body);
    }
}