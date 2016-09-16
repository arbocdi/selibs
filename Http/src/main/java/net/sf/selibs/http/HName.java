package net.sf.selibs.http;

import java.io.Serializable;
import java.util.Objects;

public class HName implements Serializable{

    public String name;
    
    public HName(String name){
        this.name = name;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.name);
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
        final HName other = (HName) obj;

        return (this.name == other.name) || (this.name != null && this.name.equalsIgnoreCase(other.name));
    }

}
