/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.selibs.orm.spec;

import java.util.List;

/**
 *
 * @author root
 */
public interface DAOCollectionInterface<E> {

    public int insert(E e) throws DataAccessException;

    public int remove(E e) throws DataAccessException;

    public void insertReturnPK(E e) throws DataAccessException;

    public int update(E e) throws DataAccessException;

    public int updateSpecifiedColumns(E e, String columns) throws DataAccessException;

    public int clearTable(Class<E> clazz) throws DataAccessException;

    public int dropTable(Class<E> clazz) throws DataAccessException;

    public <E> List<E> findAll(Class<E> clazz) throws DataAccessException;

    public <E> E find(Class<E> clazz, Object pk) throws DataAccessException;

    public <E> List<E> findWhere(Class<E> clazz, String condition, Object... values) throws DataAccessException;

    public <E> E findWhereSingle(Class<E> clazz, String condition, Object... values) throws DataAccessException;
}
