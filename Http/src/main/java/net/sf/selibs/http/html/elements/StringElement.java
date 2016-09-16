/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.selibs.http.html.elements;

import lombok.Setter;

/**
 *
 * @author root
 */
public class StringElement {
    @Setter
    protected String str;
    
    public StringElement(String str){
        this.str=str;
    }

    public String getString() {
        return str;
    }
}
