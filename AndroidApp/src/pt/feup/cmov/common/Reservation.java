package pt.feup.cmov.common;

import java.io.Serializable;
import java.sql.Date;

public class Reservation implements Serializable {
    private static final long serialVersionUID = 1L;
   
    private Integer idReservation;
    private String places;
    private Date date;
    private Date updateDate;
    private Session idSession;
    private Useracount idUser;

    public Reservation() {
    }

    public Reservation(Integer idReservation) {
        this.idReservation = idReservation;
    }

    public Reservation(Integer idReservation, String places, Date date, Date updateDate) {
        this.idReservation = idReservation;
        this.places = places;
        this.date = date;
        this.updateDate = updateDate;
    }

    public Integer getIdReservation() {
        return idReservation;
    }

    public void setIdReservation(Integer idReservation) {
        this.idReservation = idReservation;
    }

    public String getPlaces() {
        return places;
    }

    public void setPlaces(String places) {
        this.places = places;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public Session getIdSession() {
        return idSession;
    }

    public void setIdSession(Session idSession) {
        this.idSession = idSession;
    }

    public Useracount getIdUser() {
        return idUser;
    }

    public void setIdUser(Useracount idUser) {
        this.idUser = idUser;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Reservation)) {
            return false;
        }
        Reservation other = (Reservation) object;
        if ((this.idReservation == null && other.idReservation != null) || (this.idReservation != null && !this.idReservation.equals(other.idReservation))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "pt.feup.cmov.server.Reservation[ idReservation=" + idReservation + " ]";
    }
    
}
