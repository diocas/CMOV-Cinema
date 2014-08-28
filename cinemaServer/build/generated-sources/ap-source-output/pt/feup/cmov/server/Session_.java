package pt.feup.cmov.server;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import pt.feup.cmov.server.Movie;
import pt.feup.cmov.server.Reservation;

@Generated(value="EclipseLink-2.5.1.v20130918-rNA", date="2014-08-26T22:09:30")
@StaticMetamodel(Session.class)
public class Session_ { 

    public static volatile SingularAttribute<Session, Movie> idMovie;
    public static volatile CollectionAttribute<Session, Reservation> reservationCollection;
    public static volatile SingularAttribute<Session, Integer> idSession;
    public static volatile SingularAttribute<Session, Date> time;
    public static volatile SingularAttribute<Session, String> room;

}