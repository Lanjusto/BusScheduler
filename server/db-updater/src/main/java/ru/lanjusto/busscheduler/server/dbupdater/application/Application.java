package ru.lanjusto.busscheduler.server.dbupdater.application;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.wideplay.warp.persist.PersistenceService;
import com.wideplay.warp.persist.Transactional;
import ru.lanjusto.busscheduler.server.dbupdater.datapicker.IDataPicker;

import javax.persistence.EntityManager;

/**
 * Приложение
 */
public class Application {
    private final PersistenceService persistenceService;
    private final IDataPicker dataPicker;
    private final Provider<EntityManager> em;

    @Inject
    public Application(PersistenceService persistenceService, IDataPicker dataPicker, Provider<EntityManager> em) {
        this.persistenceService = persistenceService;
        this.dataPicker = dataPicker;
        this.em = em;
    }

    void startUp() {
        persistenceService.start();
        System.setProperty("http.proxyHost", "proxy.custis.ru");
        System.setProperty("http.proxyPort", "3128");

        dataPicker.pickData();
    }
}
