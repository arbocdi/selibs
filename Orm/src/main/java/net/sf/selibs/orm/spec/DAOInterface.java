package net.sf.selibs.orm.spec;

import java.util.List;

/**
 *
 * @author root
 * @param <E>
 */
public interface DAOInterface<E> {

    public int insert(E e) throws DataAccessException;

    public int remove(E e) throws DataAccessException;

    public void insertReturnPK(E e) throws DataAccessException;

    public int update(E e) throws DataAccessException;

    public int updateSpecifiedColumns(E e, String columns) throws DataAccessException;

    public int clearTable() throws DataAccessException;

    public int dropTable() throws DataAccessException;

    public List<E> findAll() throws DataAccessException;

    public E find(Object pk) throws DataAccessException;

    public List<E> findWhere(String condition, Object... values) throws DataAccessException;

    public E findWhereSingle(String condition, Object... values) throws DataAccessException;
}
