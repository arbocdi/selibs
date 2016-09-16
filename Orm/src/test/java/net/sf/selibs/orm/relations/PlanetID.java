/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.selibs.orm.relations;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import lombok.Data;
import lombok.ToString;

/**
 *
 * @author selibs
 */
@ToString
@Data
public class PlanetID {

    @GeneratedValue
    @Column(columnDefinition = "serial NOT NULL")
    public int id;

}
