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
@Table(name = "conductor",uniqueConstraints = {@UniqueConstraint(columnNames = {"conductorId", "tel"})})
public class Conductor implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;
    
    @Column(name="conductorId", length = 522, nullable = false, unique = true)
    private String conductorId;
    @Column(name="contractId",nullable=false, length = 522)
    private String contractId;
    @Column(name="fname",nullable=false, length = 30)
    private String fname;
    @Column(name="mName",nullable=true, length = 30)
    private String mName;
    @Column(name="lName",nullable=true, length = 30)
    private String lName;
    @Column(name="gender",nullable=false, length = 6)
    private String gender;
    @Column(name="tel", length = 60, nullable = false, unique = true)
    private String tel;
    @Column(name="province",nullable=false, length = 20)
    private String province;
    @Column(name="district",nullable=false, length = 20)
    private String district;
    @Column(name="sector",nullable=true, length = 20)
    private String sector;
    @Column(name="cell",nullable=true, length = 20)
    private String cell;
    @Column(name="systemStatus",nullable=false)
    private int systemStatus;
    @Column(name="systemStatusDesc",nullable=false, length = 60)
    private String systemStatusDesc;
    @Column(name="workStatus",nullable=false)
    private int workStatus;
    @Column(name="workStatusDesc",nullable=false, length = 60)
    private String workStatusDesc;
    @Column(name="imagepath",nullable=true, length = 255)
    private String imagepath;
    @Column(name="creationDate",nullable=false)
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date creationDate;
    @Column(name="lastAcessDate",nullable=true)
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date lastAcessDate;
    @Column(name="lastAccessDesc",nullable=true, length = 255)
    private String lastAccessDesc;

    public Conductor() {
    }

    public Conductor(String conductorId, String contractId, String fname, String mName, String lName, String gender, String tel, String province, String district, String sector, String cell, int systemStatus, String systemStatusDesc, int workStatus, String workStatusDesc, String imagepath, Date creationDate, Date lastAcessDate, String lastAccessDesc) {
        this.conductorId = conductorId;
        this.contractId = contractId;
        this.fname = fname;
        this.mName = mName;
        this.lName = lName;
        this.gender = gender;
        this.tel = tel;
        this.province = province;
        this.district = district;
        this.sector = sector;
        this.cell = cell;
        this.systemStatus = systemStatus;
        this.systemStatusDesc = systemStatusDesc;
        this.workStatus = workStatus;
        this.workStatusDesc = workStatusDesc;
        this.imagepath = imagepath;
        this.creationDate = creationDate;
        this.lastAcessDate = lastAcessDate;
        this.lastAccessDesc = lastAccessDesc;
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
        if (!(object instanceof Conductor)) {
            return false;
        }
        Conductor other = (Conductor) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.oltranz.kvcs.entities.Conductor[ id=" + id + " ]";
    }

    public String getConductorId() {
        return conductorId;
    }

    public void setConductorId(String conductorId) {
        this.conductorId = conductorId;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getlName() {
        return lName;
    }

    public void setlName(String lName) {
        this.lName = lName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
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

    public int getSystemStatus() {
        return systemStatus;
    }

    public void setSystemStatus(int systemStatus) {
        this.systemStatus = systemStatus;
    }

    public String getSystemStatusDesc() {
        return systemStatusDesc;
    }

    public void setSystemStatusDesc(String systemStatusDesc) {
        this.systemStatusDesc = systemStatusDesc;
    }

    public int getWorkStatus() {
        return workStatus;
    }

    public void setWorkStatus(int workStatus) {
        this.workStatus = workStatus;
    }

    public String getWorkStatusDesc() {
        return workStatusDesc;
    }

    public void setWorkStatusDesc(String workStatusDesc) {
        this.workStatusDesc = workStatusDesc;
    }

    public String getImagepath() {
        return imagepath;
    }

    public void setImagepath(String imagepath) {
        this.imagepath = imagepath;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getLastAcessDate() {
        return lastAcessDate;
    }

    public void setLastAcessDate(Date lastAcessDate) {
        this.lastAcessDate = lastAcessDate;
    }

    public String getLastAccessDesc() {
        return lastAccessDesc;
    }

    public void setLastAccessDesc(String lastAccessDesc) {
        this.lastAccessDesc = lastAccessDesc;
    }

    public String getContractId() {
        return contractId;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId;
    }
    
}
