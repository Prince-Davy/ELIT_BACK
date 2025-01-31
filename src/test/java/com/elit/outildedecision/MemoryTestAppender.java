package com.elit.outildedecision;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility class used to access logging messages produced with Logback implementation.
 * <p>
 * Usage Note: to ensure memory safety, please use a separate instance for each test class.
 * Also, please call #start() in @BeforeEach and #stop() in @AfterEach - you can consult the
 * following link for implementation example :
 * <a href="https://www.baeldung.com/junit-asserting-logs#1-creating-a-memoryappender">Baeldung Junit Assert Logs</a>
 */
public class MemoryTestAppender extends ListAppender<ILoggingEvent> {
  public void reset() {
    this.list.clear();
  }

  public boolean contains(String string, Level level) {
    return this.list.stream()
      .anyMatch(event -> event.toString().contains(string)
        && event.getLevel().equals(level));
  }

  public int countEventsForLogger(String loggerName) {
    return (int) this.list.stream()
      .filter(event -> event.getLoggerName().contains(loggerName))
      .count();
  }

  public List<ILoggingEvent> search(Level level) {
    return this.list.stream()
      .filter(event -> event.getLevel().equals(level))
      .collect(Collectors.toList());
  }

  public List<ILoggingEvent> search(String string) {
    return this.list.stream()
      .filter(event -> event.toString().contains(string))
      .collect(Collectors.toList());
  }

  public List<ILoggingEvent> search(String string, Level level) {
    return this.list.stream()
      .filter(event -> event.toString().contains(string)
        && event.getLevel().equals(level))
      .collect(Collectors.toList());
  }

  public int getSize() {
    return this.list.size();
  }

  public List<ILoggingEvent> getLoggedEvents() {
    return Collections.unmodifiableList(this.list);
  }
}
