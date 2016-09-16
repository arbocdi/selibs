package net.sf.selibs.orm.relations;

import java.util.Map;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
@Entity
@Table(name="system")
public class SolarSystem {
    @EmbeddedId
    public SystemID id;
    @Column(name = "name")
    public String name;
    @OneToMany(targetEntity = Planet.class)
    public Map planets;
    @OneToOne
    public Star star;

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + (this.id != null ? this.id.hashCode() : 0);
        hash = 47 * hash + (this.name != null ? this.name.hashCode() : 0);
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
        final SolarSystem other = (SolarSystem) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "SolarSystem{" + "id=" + id + ", name=" + name + '}';
    }
    
    
}
