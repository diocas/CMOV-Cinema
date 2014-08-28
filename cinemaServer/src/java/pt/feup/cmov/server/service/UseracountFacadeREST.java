/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pt.feup.cmov.server.service;

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
import pt.feup.cmov.server.Useracount;

/**
 *
 * @author diogo
 */
@Stateless
@Path("users")
public class UseracountFacadeREST extends AbstractFacade<Useracount> {
    @PersistenceContext(unitName = "cinemaServerPU")
    private EntityManager em;

    public UseracountFacadeREST() {
        super(Useracount.class);
    }
/*

    @DELETE
    @Path("{id}")
    public void remove(@PathParam("id") Integer id) {
        super.remove(super.find(id));
    }

    @GET
    @Override
    @Produces({"application/json"})
    public List<Useracount> findAll() {
        return super.findAll();
    }

    @GET
    @Path("{from}/{to}")
    @Produces({"application/json"})
    public List<Useracount> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
        return super.findRange(new int[]{from, to});
    }

    @GET
    @Path("count")
    @Produces("text/plain")
    public String countREST() {
        return String.valueOf(super.count());
    }*/
    
    /*@POST
    @Consumes({"application/json"})
    @Override
    public void create(Useracount entity) {
        super.create(entity);
    }*/
    
    @POST
    @Consumes({"application/json"})
    public String createUser(Useracount entity) {
        super.create(entity);
        return String.valueOf(entity.getIdUser());
    }

    @PUT
    @Path("{id}")
    @Consumes({"application/json"})
    public void edit(@PathParam("id") Integer id, Useracount entity) {
        super.edit(entity);
    }

    @GET
    @Path("{id}")
    @Produces({"application/json"})
    public Useracount find(@PathParam("id") Integer id) {
        return super.find(id);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
}
