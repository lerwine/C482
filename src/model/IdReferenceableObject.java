/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.beans.PropertyVetoException;

/**
 *
 * @author lerwi
 */
public interface IdReferenceableObject {

    /**
     * @return the id
     */
    int getId();

    /**
     * @param id the id to set
     */
    void setId(int id) throws PropertyVetoException;
    
}
