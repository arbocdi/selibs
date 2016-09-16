package net.sf.selibs.utils.filter;


public interface Filter <T>{
    boolean applicable(T t);
}
