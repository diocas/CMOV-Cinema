package pt.feup.cmov.server;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import pt.feup.cmov.server.Session;
import pt.feup.cmov.server.Useracount;

@Generated(value="EclipseLink-2.5.1.v20130918-rNA", date="2014-08-26T22:09:30")
@StaticMetamodel(Reservation.class)
public class Reservation_ { 

    public static volatile SingularAttribute<Reservation, Integer> idReservation;
    public static volatile SingularAttribute<Reservation, Date> date;
    public static volatile SingularAttribute<Reservation, Useracount> idUser;
    public static volatile SingularAttribute<Reservation, String> places;
    public static volatile SingularAttribute<Reservation, Date> updateDate;
    public static volatile SingularAttribute<Reservation, Session> idSession;

}