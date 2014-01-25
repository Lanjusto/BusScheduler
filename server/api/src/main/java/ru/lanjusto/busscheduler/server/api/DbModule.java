package ru.lanjusto.busscheduler.server.api;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.wideplay.warp.persist.PersistenceService;
import com.wideplay.warp.persist.jpa.JpaUnit;


public class DbModule implements Module {
    private final String dbPersistenceUnitName;

    public DbModule(String dbPersistenceUnitName) {
        this.dbPersistenceUnitName = dbPersistenceUnitName;
    }

    public void configure(Binder binder) {
        binder.install(PersistenceService.usingJpa().buildModule());
        binder.bindConstant().annotatedWith(JpaUnit.class).to(dbPersistenceUnitName);
    }
}
