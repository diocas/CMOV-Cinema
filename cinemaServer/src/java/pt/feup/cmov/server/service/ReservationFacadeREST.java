/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pt.feup.cmov.server.service;

import com.google.gson.GsonBuilder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import pt.feup.cmov.server.Reservation;
import pt.feup.cmov.server.Session;
import pt.feup.cmov.server.Useracount;

/**
 *
 * @author diogo
 */
@Stateless
@Path("reservations")
public class ReservationFacadeREST extends AbstractFacade<Reservation> {
    @PersistenceContext(unitName = "cinemaServerPU")
    private EntityManager em;

    public ReservationFacadeREST() {
        super(Reservation.class);
    }
/*

    @PUT
    @Path("{id}")
    @Consumes({"application/json"})
    public void edit(@PathParam("id") Integer id, Reservation entity) {
        super.edit(entity);
    }

    @GET
    @Path("{id}")
    @Produces({"application/json"})
    public Reservation find(@PathParam("id") Integer id) {
        return super.find(id);
    }

    @GET
    @Override
    @Produces({"application/json"})
    public List<Reservation> findAll() {
        return super.findAll();
    }

    @GET
    @Path("{from}/{to}")
    @Produces({"application/json"})
    public List<Reservation> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
        return super.findRange(new int[]{from, to});
    }

    @GET
    @Path("count")
    @Produces("text/plain")
    public String countREST() {
        return String.valueOf(super.count());
    }
    
    */
    @GET
    @Path("{id}")
    @Produces({"application/json"})
    public Reservation find(@PathParam("id") Integer id) {
        return super.find(id);
    }
    
    @POST
    @Consumes({"application/json"})
    public String newReservation(Reservation entity) {
        
        Object reservationId = em.createNamedQuery("Reservation.lastIndex")
                .getSingleResult();
        
        Session session = em.createNamedQuery("Session.findByIdSession", Session.class)
            .setParameter("idSession", entity.getIdSession().getIdSession())
            .getSingleResult();
        
        Useracount user = em.createNamedQuery("Useracount.findByIdUser", Useracount.class)
            .setParameter("idUser", entity.getIdUser().getIdUser())
            .getSingleResult();
        
        entity.setIdReservation(Integer.decode(reservationId.toString()) + 1);
        entity.setIdUser(user);
        entity.setIdSession(session);
        super.create(entity);
        return String.valueOf(entity.getIdReservation());
    }

    @DELETE
    @Path("{id}")
    public void remove(@PathParam("id") Integer id) {
        super.remove(super.find(id));
    }

    @GET
    @Path("user/{id}")
    @Produces({"application/json"})
    public List<Reservation> findByUser(@PathParam("id") Integer id) {
        
        Useracount user = em.find(Useracount.class, id);
        
        return em.createNamedQuery("Reservation.findByUser")
            .setParameter("idUser", user)
            .setParameter("date", new Date())
            .getResultList();
    }

    @GET
    @Path("user/{id}/{date}")
    @Produces({"application/json"})
    public List<Reservation> findByUserAndUpdateDate(@PathParam("id") Integer id, @PathParam("date") String date) throws ParseException {
        
        Useracount user = em.find(Useracount.class, id);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        
        return em.createNamedQuery("Reservation.findByUserAndUpdateDate")
            .setParameter("idUser", user)
            .setParameter("updateDate", df.parse(date))
            .setParameter("date", new Date())
            .getResultList();
    }
    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
}
