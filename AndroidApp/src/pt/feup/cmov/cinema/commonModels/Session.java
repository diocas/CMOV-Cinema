package pt.feup.cmov.cinema.commonModels;

import java.io.Serializable;
import java.util.Date;

/**
 * Model of a session
 * @author diogo
 *
 */
public class Session implements Serializable {
    private static final long serialVersionUID = 1L;
   
    private Integer idSession;
    private Date time;
    private String room;
    private Movie idMovie;

    public Session() {
    }

    public Session(Integer idSession) {
        this.idSession = idSession;
    }

    public Session(Integer idSession, Date time, String room) {
        this.idSession = idSession;
        this.time = time;
        this.room = room;
    }

    public Integer getIdSession() {
        return idSession;
    }

    public void setIdSession(Integer idSession) {
        this.idSession = idSession;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public Movie getIdMovie() {
        return idMovie;
    }

    public void setIdMovie(Movie idMovie) {
        this.idMovie = idMovie;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Session)) {
            return false;
        }
        Session other = (Session) object;
        if ((this.idSession == null && other.idSession != null) || (this.idSession != null && !this.idSession.equals(other.idSession))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "pt.feup.cmov.server.Session[ idSession=" + idSession + " ]";
    }
    
}
