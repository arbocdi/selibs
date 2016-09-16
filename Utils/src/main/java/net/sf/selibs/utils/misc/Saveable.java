package net.sf.selibs.utils.misc;

import java.io.File;

public interface Saveable {

    public void load(File file) throws Exception;

    public void save(File file) throws Exception;

}
