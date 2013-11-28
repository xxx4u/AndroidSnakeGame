package com.scatman.framework;


import java.util.ArrayList;
import java.util.List;

/**
 * From what I understand, the main point of all this Pool business is to avoid garbage collection as much
 * as possible. So what we do here is recycle objects: specifically events
 * 
 * Generically typed classes allow us to store any type of object in our Pool without having
 * to cast continuously
 */
public class Pool<T> {
	// An ArrayList to store pooled objects
	private final List<T> freeObjects;
	// A PoolObjectFactory that is used to generate new instances of the type held by the class
    private final PoolObjectFactory<T> factory;
    // stores the maxiumum number of objects the Pool can hold; necessary so our Pool does not grow indefinitely
    private final int maxSize;
    
    
    /**
     * PRIMARY CONSTRUCTOR
     * Constructor takes a poolObjectFactory and the maximum number of objects it should store
     */
    public Pool(PoolObjectFactory<T> factory, int maxSize) {
    	// Store the parameters in our class members
        this.factory = factory;
        this.maxSize = maxSize;
        // Create a new arrayList with a maximum size equal to the parameter passed in for maxSize
        this.freeObjects = new ArrayList<T>(maxSize);
    }
    
    
    
	/**
	 * This interface has only one method: createObject()
	 */
    public interface PoolObjectFactory<T> {
    	// Simply creates and returns a new object of the generic type T
        public T createObject();
    }

    
    /**
     * This method is responsible for eithe rhanding us a brand-new instance of the type held by Pool, via the .newObject() method,
     * or it reutrns a pooled instance in case there's one in the freeObjectsArrayList
     * @return
     */
    public T newObject() {
    	// Create a new object of the generic type T
        T object = null;

        // If the arrayList has no objects in it then create a new object
        if (freeObjects.size() == 0) {
            object = factory.createObject();
        }
        else { // if there are objects in the arrayList, then take an object out of it and store it in "object"
            object = freeObjects.remove(freeObjects.size() - 1);
        }

        // Return the object that we either created or recycled from the ArrayList
        return object;
    }

    
    /**
     * free() lets use reinsert objects that we no longer use.
     */
    public void free(T object) {
    	// If the arrayList is not at full capacity...
        if (freeObjects.size() < maxSize)
        	//... then insert the object into the list
            freeObjects.add(object);
    }
    
    
    
}