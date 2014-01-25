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
import ru.lanjusto.busscheduler.server.api.restlets.RouteRestlet;
import ru.lanjusto.busscheduler.server.api.restlets.RoutesListRestlet;

/**
 * Created by estroykov on 07.01.14.
 */
public class BusSchedulerService extends Application {
    public static void main(String[] args) throws Exception {
        //new Server(Protocol.HTTP, 8182, BusSchedulerService.class).start();

        // Create a component
        final Component component = new Component();
        component.getServers().add(Protocol.HTTP, 8182);
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
        final Directory directory = new Directory(getContext(), "localhost:8182");
        guard.setNext(directory);

        // Create the routesRestlet handler
        final Restlet routesListHandler = new RoutesListRestlet(dataProvider);
        final Restlet routeHandler = new RouteRestlet(dataProvider);


        // Create the orders handler
       /* final Restlet orders = new Restlet(getContext()) {
            @Override
            public void handle(Request request, Response response) {
                // Print the user name of the requested orders
                String message = "Orders of user \"" + request.getAttributes().get("user") + "\"";
                response.setEntity(message, MediaType.TEXT_PLAIN);
            }
        };

        // Create the order handler
        Restlet order = new Restlet(getContext()) {
            @Override
            public void handle(Request request, Response response) {
                // Print the user name of the requested orders
                String message = "Order \""
                                 + request.getAttributes().get("order")
                                 + "\" for user \""
                                 + request.getAttributes().get("user") + "\"";
                response.setEntity(message, MediaType.TEXT_PLAIN);
            }
        };*/

        // Attach the restlets to the root router
        router.attach("/routes", routesListHandler);
        router.attach("/routes/", routesListHandler);
        router.attach("/routes/{routeId}/", routeHandler);
        router.attach("/routes/{routeId}", routeHandler);
        /*router.attach("/users/{user}", routesHandler);
        router.attach("/users/{user}/orders", orders);
        router.attach("/users/{user}/orders/{order}", order);*/

        // Return the root router
        return router;
    }

}
