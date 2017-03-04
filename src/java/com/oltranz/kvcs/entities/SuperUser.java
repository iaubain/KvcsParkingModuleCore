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
@Table(name = "superuser",uniqueConstraints = {@UniqueConstraint(columnNames = {"userId","tel"})})
public class SuperUser implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;
    
    @Column(name="contractId",nullable=false, length = 522)
    private String contractId;
    @Column(name="userId",nullable=false, length = 522)
    private String userId;
    @Column(name="userNames",nullable=false, length = 255)
    private String userNames;
    @Column(name="province",nullable=false, length = 20)
    private String province;
    @Column(name="district",nullable=false, length = 20)
    private String district;
    @Column(name="sector",nullable=true, length = 20)
    private String sector;
    @Column(name="cell",nullable=true, length = 20)
    private String cell;
    @Column(name="pemissions",nullable=false, length = 255)
    private String pemissions;
    @Column(name="tel",nullable=false, length = 60)
    private String tel;
    @Column(name="email",nullable=true, length = 255)
    private String email;
    @Column(name="creationDate",nullable=false)
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date creationDate;
    @Column(name="lastAcessDate",nullable=true)
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date lastAcessDate;
    @Column(name="lastaccessAction",nullable=true, length = 255)
    private String lastaccessAction;
    @Column(name="status",nullable=false)
    private int status;
    @Column(name="statusDesc",nullable=false, length = 255)
    private String statusDesc;

    public SuperUser() {
    }

    public SuperUser(String contractId, String userId, String userNames, String province, String district, String sector, String cell, String pemissions, String tel, String email, Date creationDate, Date lastAcessDate, String lastaccessAction, int status, String statusDesc) {
        this.contractId = contractId;
        this.userId = userId;
        this.userNames = userNames;
        this.province = province;
        this.district = district;
        this.sector = sector;
        this.cell = cell;
        this.pemissions = pemissions;
        this.tel = tel;
        this.email = email;
        this.creationDate = creationDate;
        this.lastAcessDate = lastAcessDate;
        this.lastaccessAction = lastaccessAction;
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
        if (!(object instanceof SuperUser)) {
            return false;
        }
        SuperUser other = (SuperUser) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.oltranz.kvcs.entities.SuperUser[ id=" + id + " ]";
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserNames() {
        return userNames;
    }

    public void setUserNames(String userNames) {
        this.userNames = userNames;
    }

    public String getPemissions() {
        return pemissions;
    }

    public void setPemissions(String pemissions) {
        this.pemissions = pemissions;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getLastaccessAction() {
        return lastaccessAction;
    }

    public void setLastaccessAction(String lastaccessAction) {
        this.lastaccessAction = lastaccessAction;
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
