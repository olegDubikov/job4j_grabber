package ru.job4j.grabber.grab;

import org.quartz.SchedulerException;

public interface Grab {
    void init() throws SchedulerException;
}