package pt.feup.cmov.server;

import javax.annotation.Generated;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import pt.feup.cmov.server.Reservation;

@Generated(value="EclipseLink-2.5.1.v20130918-rNA", date="2014-08-26T22:09:30")
@StaticMetamodel(Useracount.class)
public class Useracount_ { 

    public static volatile SingularAttribute<Useracount, Integer> idUser;
    public static volatile CollectionAttribute<Useracount, Reservation> reservationCollection;
    public static volatile SingularAttribute<Useracount, String> email;

}