package pt.feup.cmov.common;

import java.io.Serializable;

public class Useracount implements Serializable {
    private static final long serialVersionUID = 1L;
   
    private Integer idUser;
    private String email;

    public Useracount() {
    }

    public Useracount(Integer idUser) {
        this.idUser = idUser;
    }

    public Useracount(Integer idUser, String email) {
        this.idUser = idUser;
        this.email = email;
    }

    public Integer getIdUser() {
        return idUser;
    }

    public void setIdUser(Integer idUser) {
        this.idUser = idUser;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idUser != null ? idUser.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Useracount)) {
            return false;
        }
        Useracount other = (Useracount) object;
        if ((this.idUser == null && other.idUser != null) || (this.idUser != null && !this.idUser.equals(other.idUser))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "pt.feup.cmov.server.Useracount[ idUser=" + idUser + " ]";
    }
    
}
