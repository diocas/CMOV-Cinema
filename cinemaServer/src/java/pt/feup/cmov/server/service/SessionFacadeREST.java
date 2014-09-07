/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pt.feup.cmov.server.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import javax.ejb.Stateless;
import javax.json.JsonArray;
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
import javax.xml.bind.annotation.XmlRootElement;
import pt.feup.cmov.server.Arrabida20;
import pt.feup.cmov.server.Arrabida20.Location;
import pt.feup.cmov.server.Movie;
import pt.feup.cmov.server.Session;

/**
 *
 * @author diogo
 */
@Stateless
@Path("sessions")
public class SessionFacadeREST extends AbstractFacade<Session> {
    @PersistenceContext(unitName = "cinemaServerPU")
    private EntityManager em;

    public SessionFacadeREST() {
        super(Session.class);
    }

    /*@POST
    @Override
    @Consumes({"application/xml", "application/json"})
    public void create(Session entity) {
        super.create(entity);
    }

    @PUT
    @Path("{id}")
    @Consumes({"application/xml", "application/json"})
    public void edit(@PathParam("id") Integer id, Session entity) {
        super.edit(entity);
    }

    @DELETE
    @Path("{id}")
    public void remove(@PathParam("id") Integer id) {
        super.remove(super.find(id));
    }

    @GET
    @Override
    @Produces({"application/xml", "application/json"})
    public List<Session> findAll() {
        return super.findAll();
    }

    @GET
    @Path("{from}/{to}")
    @Produces({"application/xml", "application/json"})
    public List<Session> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
        return super.findRange(new int[]{from, to});
    }

    @GET
    @Path("count")
    @Produces("text/plain")
    public String countREST() {
        return String.valueOf(super.count());  
    }*/

    
    
    @GET
    @Path("id/{id}")
    @Produces({"application/json"})
    public Session find(@PathParam("id") Integer id)  {
        return super.find(id);
    }

    
    
    @GET
    @Path("{date}")
    @Produces({"application/json"})
    public List<Session> find(@PathParam("date") String date) throws ParseException {

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        
        return em.createNamedQuery("Session.findByUpdateDateGreater")
            .setParameter("currentDate", new Date())
            .setParameter("updateDate", df.parse(date))
            .getResultList();
    }

    @GET
    @Path("movie/{id}")
    @Produces({"application/json"})
    public List<Session> findMovies(@PathParam("id") Integer id) {
        
        return em.createNamedQuery("Session.findByMovie")
            .setParameter("idMovie", em.find(Movie.class, id))
            .getResultList();
    }

    @GET
    @Path("seats/{id}/{date}")
    @Produces({"application/json"})
    public String occupiedSeats(@PathParam("id") Integer id, @PathParam("date") String date) throws ParseException {

        Session session = em.find(Session.class, id);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        
        
        if(session == null)
            return "Not valid";
        
        List<String> completeReservations = new ArrayList<String>();
        
        List<String> reservations = em.createNamedQuery("Reservation.occupiedSeats")
            .setParameter("idSession", session)
            .setParameter("date", df.parse(date))
            .getResultList();
        
        for(String reservation : reservations) {
            completeReservations.addAll(Arrays.asList(reservation.split(",")));
        }
        
        return new GsonBuilder().create().toJson(completeReservations);
    }

    @GET
    @Path("seats/{id}/{date}/{location}")
    @Produces({"application/json"})
    public String freeSeats(@PathParam("id") Integer id, @PathParam("date") String date, @PathParam("location") Location location) throws ParseException {

        Session session = em.find(Session.class, id);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        
        
        if(session == null)
            return "Not valid";
        
        List<String> availableSeats = Arrabida20.getSeats(super.find(id).getRoom(), location);
        
        List<String> reservations = em.createNamedQuery("Reservation.occupiedSeats")
            .setParameter("idSession", session)
            .setParameter("date", df.parse(date))
            .getResultList();
        
        for(String reservation : reservations) {
            availableSeats.removeAll(Arrays.asList(reservation.split(",")));
        }
        
        return new GsonBuilder().create().toJson(availableSeats);
    }

    @GET
    @Path("seats/{id}/{date}/{location}/count")
    @Produces({"application/json"})
    public String freeSeatsLocationCount(@PathParam("id") Integer id, @PathParam("date") String date, @PathParam("location") Location location) throws ParseException {

        Session session = em.find(Session.class, id);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        
        
        if(session == null)
            return "Not valid";
        
        List<String> availableSeats = Arrabida20.getSeats(super.find(id).getRoom(), location);
        
        List<String> reservations = em.createNamedQuery("Reservation.occupiedSeats")
            .setParameter("idSession", session)
            .setParameter("date", df.parse(date))
            .getResultList();
        
        for(String reservation : reservations) {
            availableSeats.removeAll(Arrays.asList(reservation.split(",")));
        }
        
        return String.valueOf(availableSeats.size());
    }

    @GET
    @Path("seats/{id}/{date}/list/count")
    @Produces({"application/json"})
    public String freeSeatsLocationCountList(@PathParam("id") Integer id, @PathParam("date") String date) throws ParseException {
        List<String> locations = new ArrayList<>();
        locations.add(freeSeatsLocationCount(id, date, Location.FrontLeft));
        locations.add(freeSeatsLocationCount(id, date, Location.FrontCenter));
        locations.add(freeSeatsLocationCount(id, date, Location.FrontRight));
        locations.add(freeSeatsLocationCount(id, date, Location.BackLeft));
        locations.add(freeSeatsLocationCount(id, date, Location.BackCenter));
        locations.add(freeSeatsLocationCount(id, date, Location.BackRight));
        return new GsonBuilder().create().toJson(locations);
    }
    
    @GET
    @Path("seats/{id}/{date}/count")
    @Produces({"application/json"})
    public String freeSeatsCount(@PathParam("id") Integer id, @PathParam("date") String date) throws ParseException {
        
        Session session = em.find(Session.class, id);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        
        
        if(session == null)
            return "Not valid";
        
        int total = Arrabida20.getNumSeats(super.find(id).getRoom());
        
        List<String> reservations = em.createNamedQuery("Reservation.occupiedSeats")
            .setParameter("idSession", session)
            .setParameter("date", df.parse(date))
            .getResultList();
        
        for(String reservation : reservations) {
            total -= reservation.split(",").length;
        }
        
        return String.valueOf(total);
    }
    
    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
}
