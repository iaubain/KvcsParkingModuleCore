/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.oltranz.kvcs.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.UniqueConstraint;

/**
 *
 * @author Hp
 */
@Entity
@Table(name = "deployment",uniqueConstraints = {@UniqueConstraint(columnNames = {"deployId"})})
public class Deployment implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;
    
    @Column(name="contractId",nullable=false, length = 522)
    private String contractId;
    
    @Column(name="deployId",nullable=false, length = 522)
    private String deployId;
    @Column(name="deployerId",nullable=false, length = 522)
    private String deployerId;
    @Column(name="deployeeId",nullable=false, length = 522)
    private String deployeeId;
    @Column(name="parkingId",nullable=false, length = 522)
    private String parkingId;
    @Column(name="startingDate",nullable=false)
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date startingDate;
    @Column(name="expirationDate",nullable=false)
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date expirationDate;
    @Column(name="creationDate",nullable=false)
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date creationDate;
    @Column(name="status",nullable=false)
    private int status;
    @Column(name="statusDesc",nullable=false, length = 255)
    private String statusDesc;

    public Deployment() {
    }

    public Deployment(String contractId, String deployId, String deployerId, String deployeeId, String parkingId, Date startingDate, Date expirationDate, Date creationDate, int status, String statusDesc) {
        this.contractId = contractId;
        this.deployId = deployId;
        this.deployerId = deployerId;
        this.deployeeId = deployeeId;
        this.parkingId = parkingId;
        this.startingDate = startingDate;
        this.expirationDate = expirationDate;
        this.creationDate = creationDate;
        this.status = status;
        this.statusDesc = statusDesc;
    }

    

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Deployment)) {
            return false;
        }
        Deployment other = (Deployment) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.oltranz.kvcs.entities.Deployment[ id=" + id + " ]";
    }

    public String getDeployId() {
        return deployId;
    }

    public void setDeployId(String deployId) {
        this.deployId = deployId;
    }

    public String getDeployerId() {
        return deployerId;
    }

    public void setDeployerId(String deployerId) {
        this.deployerId = deployerId;
    }

    public String getDeployeeId() {
        return deployeeId;
    }

    public void setDeployeeId(String deployeeId) {
        this.deployeeId = deployeeId;
    }

    public String getParkingId() {
        return parkingId;
    }

    public void setParkingId(String parkingId) {
        this.parkingId = parkingId;
    }

    public Date getStartingDate() {
        return startingDate;
    }

    public void setStartingDate(Date startingDate) {
        this.startingDate = startingDate;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getStatusDesc() {
        return statusDesc;
    }

    public void setStatusDesc(String statusDesc) {
        this.statusDesc = statusDesc;
    }

    public String getContractId() {
        return contractId;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId;
    }
    
}
