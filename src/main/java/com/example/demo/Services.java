/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.demo;

import generated.World;
import java.io.File;
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
   
    
    World readWorldFromXml(){
        
        JAXBContext jaxbContext;
        World world=new World();
        try {
            jaxbContext = JAXBContext.newInstance(World.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            InputStream input = getClass().getClassLoader().getResourceAsStream("world.xml");
            world = (World) jaxbUnmarshaller.unmarshal(input);
        } catch (Exception ex) {
            System.out.println("Erreur lecture du fichier:" + ex.getMessage());
            ex.printStackTrace();
        }
        return world;
    }

    void saveWordlToXml(World world){
        JAXBContext jaxbContext;
        try {
            
            jaxbContext = JAXBContext.newInstance(World.class);
            Marshaller march = jaxbContext.createMarshaller();
            march.marshal(world, new File("world.xml"));
        } catch (Exception ex) {
            System.out.println("Erreur écriture du fichier:" + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    World getWorld(){
        return readWorldFromXml();
    }
}
