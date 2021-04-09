/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.demo;

import generated.World;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import static org.springframework.web.servlet.function.RequestPredicates.path;

/**
 *
 * @author mjp81
 */
public class Services {

    World world = new World();

    String path = "./src/main/resources/";

    public World readWorldFromXml(String username) {
        JAXBContext jaxbContext;
        InputStream input;
        try {
            try {
                System.out.println("in read"+path+username);
                input = new FileInputStream(path+username + "-world.xml");
                
            } catch (Exception ex) {
                input = getClass().getClassLoader().getResourceAsStream("world.xml");
            }
            jaxbContext = JAXBContext.newInstance(World.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            world = (World) jaxbUnmarshaller.unmarshal(input);
        } catch (Exception ex) {
            System.out.println("Erreur lecture du fichier:" + ex.getMessage());
            ex.printStackTrace();
        }
        return world;
    }


    public void saveWorldToXml(World world, String username) {
        JAXBContext jaxbContext;
        try {
            jaxbContext = JAXBContext.newInstance(World.class);
            Marshaller march = jaxbContext.createMarshaller();
            OutputStream output = new FileOutputStream(path  + username + "-world.xml");
            System.out.println(username);
            march.marshal(world, output);
        } catch (Exception ex) {
            System.out.println("Erreur écriture du fichier:" + ex.getMessage());
            ex.printStackTrace();
        }
    }

   
    World getWorld(String username) {
        System.out.println("getWorld()"+username);
        return readWorldFromXml(username);
    }
}
