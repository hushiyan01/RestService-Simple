package edu.ufl.team5.inventoryservice;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/")
public class InventoryApplication extends Application {
    public Set<Class<?>> getClasses(){
        HashSet<Class<?>> hashSet = new HashSet<>();
        hashSet.add(InventoryResource.class);
        return hashSet;
    }
}