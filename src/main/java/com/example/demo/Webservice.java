/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.demo;

import static com.sun.corba.se.spi.presentation.rmi.StubAdapter.request;
import generated.PallierType;
import generated.ProductType;
import generated.World;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.springframework.web.bind.annotation.RequestBody;

/**
 *
 * @author mjp81
 */
@Path("generic")
public class Webservice {

    Services services;

    public Webservice() {
        services = new Services();
    }

    @GET
    @Path("world")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Response getWorld(@Context HttpServletRequest request) {
        String username = request.getHeader("X-user");
        System.out.println( username);
        World world = this.services.getWorld(username);
        this.services.saveWorldToXml(world, username);
        return Response.ok(services.getWorld(username)).build();
    }

    @PUT
    @Path("product")
    public void putProduct(@Context HttpServletRequest request, @RequestBody ProductType newproduct) {
        String username = request.getHeader("X-user");
        services.updateProduct(username, newproduct);
    }

    @PUT
    @Path("manager")
    public void putManager(@Context HttpServletRequest request, @RequestBody PallierType newmanager) {
        String username = request.getHeader("X-user");
        services.updateManager(username, newmanager);
    }
}
