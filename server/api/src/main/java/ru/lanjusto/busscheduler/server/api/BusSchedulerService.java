package ru.lanjusto.busscheduler.server.api;

import com.google.inject.Guice;
import com.google.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Restlet;
import org.restlet.data.ChallengeScheme;
import org.restlet.data.Protocol;
import org.restlet.resource.Directory;
import org.restlet.routing.Router;
import org.restlet.security.ChallengeAuthenticator;
import org.restlet.security.MapVerifier;
import ru.lanjusto.busscheduler.common.utils.CommonData;
import ru.lanjusto.busscheduler.server.api.restlets.RouteRestlet;
import ru.lanjusto.busscheduler.server.api.restlets.RoutesListRestlet;
import ru.lanjusto.busscheduler.server.api.restlets.TimeTableRestlet;

public class BusSchedulerService extends Application {
    public static void main(String[] args) throws Exception {
        //new Server(Protocol.HTTP, 8182, BusSchedulerService.class).start();

        // Create a component
        final Component component = new Component();
        component.getServers().add(Protocol.HTTP, CommonData.PORT);
        component.getClients().add(Protocol.FILE);

        // Create an application
        final BusSchedulerService application = Guice.createInjector(new DbModule("production")).getInstance(BusSchedulerService.class);

        // Attach the application to the component and start it
        component.getDefaultHost().attach(application);
        component.start();
    }

    private final IDataProvider dataProvider;

    @Inject
    public BusSchedulerService(@NotNull IDataProvider dataProvider) {
        this.dataProvider = dataProvider;

        System.setProperty("http.proxyHost", "proxy.custis.ru");
        System.setProperty("http.proxyPort", "3128");
    }

    @Override
    public Restlet createInboundRoot() {
        // Create a root router
        final Router router = new Router(getContext());

        // Create a simple password verifier
        final MapVerifier verifier = new MapVerifier();
        verifier.getLocalSecrets().put("scott", "tiger".toCharArray());

        // Create a Guard
        // Attach a guard to secure access to the directory
        final ChallengeAuthenticator guard = new ChallengeAuthenticator(getContext(), ChallengeScheme.HTTP_BASIC, "Tutorial");
        guard.setVerifier(verifier);
        //router.attach("/docs/", guard).setMatchingMode(Template.MODE_STARTS_WITH);

        // Create a directory able to expose a hierarchy of files
        final Directory directory = new Directory(getContext(), CommonData.URL);
        guard.setNext(directory);

        // Create the routesRestlet handler
        final Restlet routesListRestlet = new RoutesListRestlet(dataProvider);
        final Restlet routeRestlet = new RouteRestlet(dataProvider);
        final Restlet timeTableRestlet = new TimeTableRestlet(dataProvider);

        // Attach the restlets to the root router
        attach(router, CommonData.ROUTES_PAGE, routesListRestlet);
        attach(router, CommonData.ROUTE_PAGE, routeRestlet);
        attach(router, CommonData.TIMETABLE_PAGE, timeTableRestlet);

        // Return the root router
        return router;
    }

    private void attach(@NotNull Router router, @NotNull String url, @NotNull Restlet restlet) {
        router.attach(url, restlet);
        router.attach(url + "/", restlet);
    }

}
