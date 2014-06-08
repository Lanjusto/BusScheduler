package ru.lanjusto.busscheduler.server.dbupdater.application;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.wideplay.warp.persist.PersistenceService;
import org.json.simple.parser.ParseException;
import ru.lanjusto.busscheduler.server.dbupdater.datapicker.SPBPicker;
import ru.lanjusto.busscheduler.server.dbupdater.service.RouteMergeService;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

/**
 * Приложение
 */
public class Application {
    private final PersistenceService persistenceService;
    private final Provider<EntityManager> em;
    private final RouteMergeService routeMergeService;

    @Inject
    public Application(PersistenceService persistenceService, Provider<EntityManager> em) {
        this.persistenceService = persistenceService;
        this.em = em;
        this.routeMergeService = new RouteMergeService(em);
    }

    void startUp() throws IOException, ParseException {
        persistenceService.start();

        Calendar cal = Calendar.getInstance();
      //  cal.add(Calendar.WEEK_OF_MONTH, -1);
        Date expireDate = cal.getTime();

        //new DataPicker(em).pickData(expireDate);

        new SPBPicker(em, routeMergeService).pickData(expireDate);

    }
}
