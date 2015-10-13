/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package QuantIF_Project.patient.exceptions;

/**
 *
 * @author Cyriac
 */
public class ImageSizeException extends Exception {
    
    public ImageSizeException () {
        super();
    }
    
    public ImageSizeException (String reason) {
        super(reason);
    }
}
