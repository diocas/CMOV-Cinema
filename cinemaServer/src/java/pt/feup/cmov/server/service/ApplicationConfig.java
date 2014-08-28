/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pt.feup.cmov.server.service;

import java.util.Set;
import javax.ws.rs.core.Application;

/**
 *
 * @author diogo
 */
@javax.ws.rs.ApplicationPath("arrabida20")
public class ApplicationConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new java.util.HashSet<>();
        addRestResourceClasses(resources);
        return resources;
    }

    /**
     * Do not modify addRestResourceClasses() method.
     * It is automatically populated with
     * all resources defined in the project.
     * If required, comment out calling this method in getClasses().
     */
    private void addRestResourceClasses(Set<Class<?>> resources) {
        resources.add(pt.feup.cmov.server.service.MovieFacadeREST.class);
        resources.add(pt.feup.cmov.server.service.ReservationFacadeREST.class);
        resources.add(pt.feup.cmov.server.service.SessionFacadeREST.class);
        resources.add(pt.feup.cmov.server.service.UseracountFacadeREST.class);
    }
    
}
