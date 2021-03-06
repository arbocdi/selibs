package net.sf.selibs.orm.relations;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import net.sf.selibs.orm.sql.Cascade;

@Table(name="stars")
@Entity
public class Star {
    @EmbeddedId
    public StarID id;
    @Column(nullable = false,unique = true)
    public String name;
    @Column(name = "sys_id",nullable = false)
    public int s_id;
    @Column(name = "sys_uid",nullable = false)
    public String s_uid;
    @OneToOne
    @JoinColumns({
        @JoinColumn(name="sys_id",referencedColumnName = "id"),
        @JoinColumn(name="sys_uid",referencedColumnName = "uid")
    })
    @Cascade
    public SolarSystem system;

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 47 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 47 * hash + this.s_id;
        hash = 47 * hash + (this.s_uid != null ? this.s_uid.hashCode() : 0);
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
        final Star other = (Star) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        if (this.s_id != other.s_id) {
            return false;
        }
        if ((this.s_uid == null) ? (other.s_uid != null) : !this.s_uid.equals(other.s_uid)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Star{" + "id=" + id + ", name=" + name + ", s_id=" + s_id + ", s_uid=" + s_uid + '}';
    }

    
    
    
}
