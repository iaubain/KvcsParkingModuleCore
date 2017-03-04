/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package com.oltranz.kvcs.apiclient;

import com.oltranz.kvcs.config.AppDesc;
import com.oltranz.kvcs.config.HeaderConfig;
import static java.lang.System.out;
import javax.ejb.Stateless;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 * @author Hp
 */
@Stateless
public class LocalWebCall {
    public Response localService(String contractId, String cmd, String body){
        out.print(AppDesc.APP_DESC+" LocalWebCall localService going to push to localService: "+body);
        try{
            String url = "http://localhost:8080/ParkingModule/system/localservice";
            
            out.print(AppDesc.APP_DESC+" Contacting localService on URL: "+url+" And body: "+body);
            Client client = ClientBuilder.newClient();
            WebTarget target =client.target(url);
            Response response = target.request()
                    .header(HeaderConfig.CONTENT, MediaType.APPLICATION_JSON)
                    .header(HeaderConfig.CONTRACT, contractId)
                    .header(HeaderConfig.CMD, cmd)
                    .post(Entity.entity(body, MediaType.APPLICATION_JSON));
            
            return response;
        }catch(Exception e){
            out.print(AppDesc.APP_DESC+" Reporting error due to: "+e.getLocalizedMessage());
            return null;
        }
    }
}
