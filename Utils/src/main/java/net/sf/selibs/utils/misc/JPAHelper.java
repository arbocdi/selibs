/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.sf.selibs.utils.misc;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class JPAHelper {

    public static Object makeAction(EntityManagerFactory emf, JPAAction action) throws Exception {
        EntityManager em = null;
        try {
            em = emf.createEntityManager();
            em.getTransaction().begin();
            Object result = action.makeAction(em);
            em.getTransaction().commit();
            return result;
        } catch (Exception ex) {
            if (em != null) {
                em.getTransaction().rollback();
            }
            throw ex;
        } finally {
            UHelper.close(em);
        }
    }

    public static interface JPAAction {

        Object makeAction(EntityManager em) throws Exception;
    }
}
