/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.security.InvalidKeyException;

/**
 *Interface for object that associated with a unique identifier value.
 * @author Leonard T. Erwine
 */
public interface IdReferenceableObject {

    /**
     * @return The unique identifier value.
     */
    int getId();

    /**
     * @param id The unique identifier value to set.
     * @throws java.security.InvalidKeyException Unique identifier is less than zero or another item in its host list already uses that unique identifier.
     */
    void setId(int id) throws InvalidKeyException;
    
}
