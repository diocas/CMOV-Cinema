package pt.feup.cmov.common;

import java.io.Serializable;
import java.sql.Date;

public class Movie implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Integer idMovie;
    private String name;
    private Integer duration;
    private String sinopsis;
    private String cover;
    private String trailer;
    private Date dateFrom;
    private Date dateUntil;
    private Date updateDate;

    public Movie() {
    }

    public Movie(Integer idMovie) {
        this.idMovie = idMovie;
    }

    public Movie(Integer idMovie, String name, Date dateFrom, Date dateUntil, Date updateDate) {
        this.idMovie = idMovie;
        this.name = name;
        this.dateFrom = dateFrom;
        this.dateUntil = dateUntil;
        this.updateDate = updateDate;
    }

    public Integer getIdMovie() {
        return idMovie;
    }

    public void setIdMovie(Integer idMovie) {
        this.idMovie = idMovie;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getSinopsis() {
        return sinopsis;
    }

    public void setSinopsis(String sinopsis) {
        this.sinopsis = sinopsis;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getTrailer() {
        return trailer;
    }

    public void setTrailer(String trailer) {
        this.trailer = trailer;
    }

    public Date getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
    }

    public Date getDateUntil() {
        return dateUntil;
    }

    public void setDateUntil(Date dateUntil) {
        this.dateUntil = dateUntil;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }


    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Movie)) {
            return false;
        }
        Movie other = (Movie) object;
        if ((this.idMovie == null && other.idMovie != null) || (this.idMovie != null && !this.idMovie.equals(other.idMovie))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "pt.feup.cmov.server.Movie[ idMovie=" + idMovie + " ]";
    }
    
}
