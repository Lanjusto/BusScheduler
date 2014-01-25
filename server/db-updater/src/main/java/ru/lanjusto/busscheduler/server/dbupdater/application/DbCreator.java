package ru.lanjusto.busscheduler.server.dbupdater.application;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.wideplay.warp.persist.PersistenceService;

import javax.persistence.EntityManager;


/**
 * Создание базы данных
 */
public class DbCreator {
    final PersistenceService persistenceService;
    final Provider<EntityManager> em;

    public static void main(String[] args) {
        final DbCreator dbCreator = Guice.createInjector(new DbModule("deploy")).getInstance(DbCreator.class);

        try {
            dbCreator.init();
        } finally {
            dbCreator.destroy();
        }
    }

    @Inject
    public DbCreator(PersistenceService persistenceService, Provider<EntityManager> em) {
        this.persistenceService = persistenceService;
        this.em = em;
    }

    private void init() {
        persistenceService.start();
        em.get().createQuery("SELECT r FROM Route r").getResultList();
    }

    private void destroy() {
        persistenceService.shutdown();
    }
}
