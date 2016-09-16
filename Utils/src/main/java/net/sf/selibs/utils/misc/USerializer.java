package net.sf.selibs.utils.misc;

import lombok.Getter;

public abstract class USerializer<O, S> {

    @Getter
    protected S storage;

    public USerializer(S storage) {
        this.storage = storage;
    }

    public abstract void save(O object) throws Exception;

    public abstract O load() throws Exception;
}
