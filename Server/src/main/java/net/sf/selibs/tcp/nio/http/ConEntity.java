package net.sf.selibs.tcp.nio.http;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class ConEntity implements Comparable<ConEntity> {

    public int id;
    public String remote;
    public String local;

    @Override
    public int compareTo(ConEntity o) {
        if (this.id == o.id) {
            return 0;
        }
        if (this.id > o.id) {
            return 1;
        } else {
            return -1;
        }
    }

}
