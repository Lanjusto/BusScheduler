package ru.lanjusto.busscheduler.server.dbupdater.datapicker;

import com.google.inject.ImplementedBy;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.Date;

/**
 * Наполнитель базы данных.
 */
@ImplementedBy(DataPicker.class)
public interface IDataPicker {
    void pickData(Date expireDate) throws IOException, ParseException;
}
