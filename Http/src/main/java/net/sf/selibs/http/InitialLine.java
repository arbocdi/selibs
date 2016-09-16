package net.sf.selibs.http;

import java.io.Serializable;

public interface InitialLine extends Serializable {

    String toString();

    String getVersion();

    void setVersion(String hVersion);
}
