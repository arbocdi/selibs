/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.selibs.orm.relations;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.ToString;
import net.sf.selibs.orm.JoinField;
import net.sf.selibs.orm.sql.Cascade;

/**
 *
 * @author selibs
 */
@Entity
@Table(name = "planet")
public class Planet {

    @EmbeddedId
    public PlanetID id;
    public String name;
    @JoinField(name = "sys_id", referencedColumnName = "id", referencedClass = SolarSystem.class)
    public int sys_id;
    @JoinField(name = "sys_uid", referencedColumnName = "uid", referencedClass = SolarSystem.class)
    public String sys_uid;
    @JoinColumns({
        @JoinColumn(name = "sys_id", referencedColumnName = "id"),
        @JoinColumn(name = "sys_uid", referencedColumnName = "uid")
    })
    @Cascade
    @ManyToOne
    public SolarSystem system;

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 37 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 37 * hash + this.sys_id;
        hash = 37 * hash + (this.sys_uid != null ? this.sys_uid.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Planet other = (Planet) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        if (this.sys_id != other.sys_id) {
            return false;
        }
        if ((this.sys_uid == null) ? (other.sys_uid != null) : !this.sys_uid.equals(other.sys_uid)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Planet{" + "id=" + id + ", name=" + name + ", sys_id=" + sys_id + ", sys_uid=" + sys_uid + '}';
    }
    
    
    
}
