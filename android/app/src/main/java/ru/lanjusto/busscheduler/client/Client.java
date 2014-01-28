package ru.lanjusto.busscheduler.client;


import android.os.AsyncTask;
import com.thoughtworks.xstream.XStream;
import org.restlet.resource.ClientResource;
import ru.lanjusto.busscheduler.common.model.RouteStop;
import ru.lanjusto.busscheduler.common.utils.CommonData;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Client {
    public List<RouteStop> getRouteStops(final long routeId) throws DataIsNotAvailableException {
        //TODO выделить потомок AsyncTask, который будет использоваться всегда и в методах
        //TODO before и after определять ожидание в UI. Всякий раз одинаковое

        final AsyncTask<Void, Void, List<RouteStop>> asyncTask = new AsyncTask<Void, Void, List<RouteStop>>() {
            @Override
            protected List<RouteStop> doInBackground(Void... params) {
                final String text;
                try {
                    text = new ClientResource(getRouteUrl(routeId)).get().getText();
                    //TODO обработка ошибок
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                final XStream xStream = new XStream(CommonData.getXStreamDriver());
                xStream.setMode(XStream.NO_REFERENCES);
                //xStream.autodetectAnnotations(true);
                //TODO  autodetectAnnotations не срабатывает из-за нехватки чего-то...
                //TODO возможно, следует отказаться от аннотаций xStream и вынести настройки
                //TODO альясов в CommonData.
                xStream.alias("routeStop", RouteStop.class);

                final Object object = xStream.fromXML(text);
                return objectToList(object, RouteStop.class);
            }
        };

        asyncTask.execute();
        try {
            return asyncTask.get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }


    private String getRouteUrl(long routeId) {
        return CommonData.ROUTE_URL.replace("{routeId}", String.valueOf(routeId));
    }

    @SuppressWarnings("unchecked")
    private <T> List<T> objectToList(Object object, Class<T> clazz) {
        if (object instanceof List) {
            final List list = (List) object;
            if (list.isEmpty()) {
                return (List<T>) list;
            } else {
                for (Object o : list) {
                    if (o instanceof List) {
                        return objectToList(o, clazz);
                    } else {
                        if (!clazz.isAssignableFrom(o.getClass())) {
                            throw new RuntimeException("Wrong data");
                        }
                    }
                }
            }
            return (List<T>) list;
        } else {
            throw new RuntimeException("Wrong data");
        }
    }
}
