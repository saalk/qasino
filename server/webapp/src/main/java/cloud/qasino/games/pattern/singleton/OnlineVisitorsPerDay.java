package cloud.qasino.games.pattern.singleton;

import lombok.Getter;

import java.time.LocalDate;

public class OnlineVisitorsPerDay {

    private static OnlineVisitorsPerDay instance = null;

    @Getter
    private int onlineVisitors;

    /**
     * The    java.util.Date and Calendar classes are not thread safe
     * So use java.time.LocalDate from java 8 onwards
     */
    private LocalDate today;

    // Stateless means that an object doesn't evolve over time
    // Stateful objects are those that have an internal state that changes over time.

    // Web application consists of two types of objects:
    // SERVICES - SCOPE SINGLETON - @Service without scope - its singleton by default
    // - The first type are services with a lifetime that is longer than a single request
    // - i.e. they can be used by more than one request, often even by multiple requests
    // - simultaneously. These services usually have the singleton scope: you create them once,
    // - and they are reused for the duration of the application.
    // STATE should be in database ->
    // so when a service retrieves a User from db of course you get
    // a statefull object - created by the repository and for (a part of) the lifetime of that object

    // TEMPORARILY - SCOPE PROTOTYPE
    // - The second type are objects that are needed only temporarily.
    // - They are created at some point during the lifetime of a single request, used,
    // - and then discarded again. They are not shared between different requests, and often they are
    // - not even used outside the method where they are first needed.
    // - These objects have the prototype scope: a new instance of them is created every time they are
    // - injected.
    // SINGLETON PATTERN should be created when you start a web request -
    // then you access it during the lifetime of that request and you get the same instance

    private OnlineVisitorsPerDay() {
    }

    // important singleton function
    public static OnlineVisitorsPerDay getInstance() {
        if (instance == null)
            instance = new OnlineVisitorsPerDay();
        return instance;
    }

    public void newLogon() {
        if (this.today == null) this.today = LocalDate.now();
        if (this.today.equals(LocalDate.now())) {
            this.onlineVisitors++;
        } else {
            this.today = LocalDate.now();
            this.onlineVisitors = 1;
        }
    }

    public void newLogoff() {
        if (this.onlineVisitors > 0) {
            this.onlineVisitors--;
        }
    }
}
