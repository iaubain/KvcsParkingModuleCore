/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oltranz.kvcs.simplebeans.commonbeans;

import com.oltranz.kvcs.entities.SuperUser;

/**
 *
 * @author Hp
 */
public class DeploymentBean {
    private String deploymentId;
    private SuperUser deployer;
    private ConductorBean deployee;
    private ParkingBean parkingBean;
    private StatusBean status;

    public DeploymentBean() {
    }

    public DeploymentBean(String deploymentId, SuperUser deployer, ConductorBean deployee, ParkingBean parkingBean, StatusBean status) {
        this.deploymentId = deploymentId;
        this.deployer = deployer;
        this.deployee = deployee;
        this.parkingBean = parkingBean;
        this.status = status;
    }

    public StatusBean getStatus() {
        return status;
    }

    public void setStatus(StatusBean status) {
        this.status = status;
    }

    public String getDeploymentId() {
        return deploymentId;
    }

    public void setDeploymentId(String deploymentId) {
        this.deploymentId = deploymentId;
    }

    public SuperUser getDeployer() {
        return deployer;
    }

    public void setDeployer(SuperUser deployer) {
        this.deployer = deployer;
    }

    public ConductorBean getDeployee() {
        return deployee;
    }

    public void setDeployee(ConductorBean deployee) {
        this.deployee = deployee;
    }

    public ParkingBean getParkingBean() {
        return parkingBean;
    }

    public void setParkingBean(ParkingBean parkingBean) {
        this.parkingBean = parkingBean;
    }
    
}
