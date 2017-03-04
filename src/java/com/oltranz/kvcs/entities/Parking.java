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

/**
 *
 * @author Hp
 */
@Entity
@Table(name = "parking")
public class Parking implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;
    
    @Column(name="contractId",nullable=false, length = 522)
    private String contractId;
    @Column(name="parkingId",nullable=false, length = 522, unique = true)
    private String parkingId;
    @Column(name="province",nullable=false, length = 20)
    private String province;
    @Column(name="district",nullable=false, length = 20)
    private String district;
    @Column(name="sector",nullable=true, length = 20)
    private String sector;
    @Column(name="cell",nullable=true, length = 20)
    private String cell;
    @Column(name="parkingDesc",nullable=false, length = 255, unique = true)
    private String parkingDesc;
    @Column(name="longitude",nullable=true, length = 255)
    private String longitude;
    @Column(name="latitude",nullable=true, length = 255)
    private String latitude;
    @Column(name="status",nullable=false)
    private int status;
    @Column(name="statusDesc",nullable=false, length = 255)
    private String statusDesc;
    @Column(name="creationDate",nullable=false)
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date creationDate;
    @Column(name="lastAccessDate",nullable=true)
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date lastAccessDate;
    @Column(name="lastAccessAction",nullable=true, length = 255)
    private String lastAccessAction;

    public Parking() {
    }

    public Parking(String contractId, String parkingId, String province, String district, String sector, String cell, String parkingDesc, String longitude, String latitude, int status, String statusDesc, Date creationDate, Date lastAccessDate, String lastAccessAction) {
        this.contractId = contractId;
        this.parkingId = parkingId;
        this.province = province;
        this.district = district;
        this.sector = sector;
        this.cell = cell;
        this.parkingDesc = parkingDesc;
        this.longitude = longitude;
        this.latitude = latitude;
        this.status = status;
        this.statusDesc = statusDesc;
        this.creationDate = creationDate;
        this.lastAccessDate = lastAccessDate;
        this.lastAccessAction = lastAccessAction;
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
        if (!(object instanceof Parking)) {
            return false;
        }
        Parking other = (Parking) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.oltranz.kvcs.entities.Parking[ id=" + id + " ]";
    }

    public String getParkingId() {
        return parkingId;
    }

    public void setParkingId(String parkingId) {
        this.parkingId = parkingId;
    }

    public String getParkingDesc() {
        return parkingDesc;
    }

    public void setParkingDesc(String parkingDesc) {
        this.parkingDesc = parkingDesc;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
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

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getLastAccessDate() {
        return lastAccessDate;
    }

    public void setLastAccessDate(Date lastAccessDate) {
        this.lastAccessDate = lastAccessDate;
    }

    public String getLastAccessAction() {
        return lastAccessAction;
    }

    public void setLastAccessAction(String lastAccessAction) {
        this.lastAccessAction = lastAccessAction;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getCell() {
        return cell;
    }

    public void setCell(String cell) {
        this.cell = cell;
    }

    public String getContractId() {
        return contractId;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId;
    }
    
}
