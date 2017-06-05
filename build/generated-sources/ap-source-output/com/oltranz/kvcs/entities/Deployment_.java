package com.oltranz.kvcs.entities;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2017-03-19T12:53:53")
@StaticMetamodel(Deployment.class)
public class Deployment_ { 

    public static volatile SingularAttribute<Deployment, String> statusDesc;
    public static volatile SingularAttribute<Deployment, String> deployeeId;
    public static volatile SingularAttribute<Deployment, String> contractId;
    public static volatile SingularAttribute<Deployment, String> deployerId;
    public static volatile SingularAttribute<Deployment, String> parkingId;
    public static volatile SingularAttribute<Deployment, String> id;
    public static volatile SingularAttribute<Deployment, Date> creationDate;
    public static volatile SingularAttribute<Deployment, String> deployId;
    public static volatile SingularAttribute<Deployment, Date> startingDate;
    public static volatile SingularAttribute<Deployment, Date> expirationDate;
    public static volatile SingularAttribute<Deployment, Integer> status;

}