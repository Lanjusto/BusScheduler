package ru.lanjusto.busscheduler.server.dbupdater.datapicker;

import com.google.inject.ImplementedBy;

/**
 * Наполнитель базы данных.
 */
@ImplementedBy(DataPicker.class)
public interface IDataPicker {
    void pickData();
}
