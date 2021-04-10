/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.demo;

import generated.PallierType;
import generated.ProductType;
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
                System.out.println("coucou" + username);
                input = new FileInputStream(path + username + "-world.xml");

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
            System.out.println("coucou2" + username);
            OutputStream output = new FileOutputStream(path + username + "-world.xml");
            march.marshal(world, output);
        } catch (Exception ex) {
            System.out.println("Erreur écriture du fichier:" + ex.getMessage());
            ex.printStackTrace();
        }
    }

    World getWorld(String username) {
        World world = this.readWorldFromXml(username);
        this.updateScore(world);
        saveWorldToXml(world, username);
        return readWorldFromXml(username);
    }

    public Boolean updateProduct(String username, ProductType newproduct) {
        // aller chercher le monde qui correspond au joueur
        World world = getWorld(username);
        // trouver dans ce monde, le produit équivalent à celui passé
        // en paramètre
        ProductType product = findProductById(world, newproduct.getId());
        if (product == null) {
            return false;
        }

        // calculer la variation de quantité. Si elle est positive c'est
        // que le joueur a acheté une certaine quantité de ce produit
        // sinon c’est qu’il s’agit d’un lancement de production.
        int qtchange = newproduct.getQuantite() - product.getQuantite();
        if (qtchange > 0) {
            // soustraire de l'argent du joueur le cout de la quantité

            double achat = product.getCout() * ((1 - Math.pow(product.getCroissance(), qtchange)) / (1 - product.getCroissance()));
            world.setMoney(world.getMoney() - achat);

            // achetée et mettre à jour la quantité de product 
            int qte=newproduct.getQuantite();
            product.setQuantite(qte);
        } else {
           product.timeleft=product.vitesse;
            // initialiser product.timeleft à product.vitesse
            // pour lancer la production
        }
        // sauvegarder les changements du monde
        saveWorldToXml(world, username);
        return true;
    }

    private ProductType findProductById(World world, int id) {
        ProductType product = null;
        for (ProductType p : world.getProducts().getProduct()) {
            if (p.getId() == id) {
                product = p;
            }
        }
        return product;
    }

    public Boolean updateManager(String username, PallierType newmanager) {
        // aller chercher le monde qui correspond au joueur
        World world = getWorld(username);
        // trouver dans ce monde, le manager équivalent à celui passé
        // en paramètre
        PallierType manager = findManagerByName(world, newmanager.getName());
        if (manager == null) {
            return false;
        }

        // débloquer ce manager
        manager.setUnlocked(true);
        // trouver le produit correspondant au manager
        ProductType product = findProductById(world, manager.getIdcible());
        if (product == null) {
            return false;
        }
        // débloquer le manager de ce produit
        product.setManagerUnlocked(true);
        // soustraire de l'argent du joueur le cout du manager
        world.setMoney(world.getMoney() - manager.getSeuil());
        // sauvegarder les changements au monde
        saveWorldToXml(world, username);
        return true;
    }

    private PallierType findManagerByName(World world, String name) {
        PallierType manager = null;
        for (PallierType palier : world.getManagers().getPallier()) {
            if (palier.getName().equals(name)) {
                manager = palier;
            }
        }
        return manager;
    }

    private void updateScore(World world) {
        long tempsEcoule = System.currentTimeMillis() - world.getLastupdate();
        for (ProductType product : world.getProducts().getProduct()) {
            if (!product.isManagerUnlocked()) {
                if (product.getTimeleft() != 0 && tempsEcoule > product.getTimeleft()) {
                    world.setMoney(world.getMoney() + product.getQuantite() * product.getRevenu());
                    world.setScore(world.getScore() + product.getQuantite() * product.getRevenu());
                } else {
                    long tps = product.getTimeleft() - tempsEcoule;
                    if (tps < 0) {
                        product.setTimeleft(0);
                    } else {
                        product.setTimeleft(tps);
                    }
                }
            } else {
                //on divise  le temps écoulé par le temps de production pour obtenir la quatite fabriqué durant le temps écoulé 
                double qtefabriquee = Math.floor(tempsEcoule / product.getVitesse());
                world.setScore(world.getScore() + product.getQuantite() * product.getRevenu() * qtefabriquee);
                world.setMoney(world.getMoney() + product.getQuantite() * product.getRevenu() * qtefabriquee);
                long tps = product.getTimeleft() - tempsEcoule;
                if (tps < 0) {
                    product.setTimeleft(0);
                } else {
                    product.setTimeleft(tps);
                }
            }
        }
        world.setLastupdate(System.currentTimeMillis());
    }
}
