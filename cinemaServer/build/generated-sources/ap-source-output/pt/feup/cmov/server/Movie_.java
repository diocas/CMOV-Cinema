package pt.feup.cmov.server;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import pt.feup.cmov.server.Session;

@Generated(value="EclipseLink-2.5.1.v20130918-rNA", date="2014-08-26T22:09:30")
@StaticMetamodel(Movie.class)
public class Movie_ { 

    public static volatile SingularAttribute<Movie, Integer> duration;
    public static volatile SingularAttribute<Movie, String> cover;
    public static volatile SingularAttribute<Movie, String> trailer;
    public static volatile SingularAttribute<Movie, Integer> idMovie;
    public static volatile SingularAttribute<Movie, Date> updateDate;
    public static volatile SingularAttribute<Movie, String> name;
    public static volatile CollectionAttribute<Movie, Session> sessionCollection;
    public static volatile SingularAttribute<Movie, Date> dateUntil;
    public static volatile SingularAttribute<Movie, Date> dateFrom;
    public static volatile SingularAttribute<Movie, String> sinopsis;

}