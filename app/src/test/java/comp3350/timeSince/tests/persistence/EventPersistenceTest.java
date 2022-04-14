package comp3350.timeSince.tests.persistence;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import comp3350.timeSince.application.Services;
import comp3350.timeSince.business.exceptions.DuplicateEventException;
import comp3350.timeSince.business.exceptions.EventNotFoundException;
import comp3350.timeSince.objects.EventDSO;
import comp3350.timeSince.objects.EventLabelDSO;
import comp3350.timeSince.persistence.IEventLabelPersistence;
import comp3350.timeSince.persistence.IEventPersistence;

@FixMethodOrder(MethodSorters.JVM)
public class EventPersistenceTest {

    private IEventPersistence eventDatabase;
    private IEventLabelPersistence eventLabelDatabase;
    private EventDSO event1, event2, event3, event4;
    private final Calendar date = Calendar.getInstance();
    private List<EventDSO> eventList;
    private static final int initialEventCount = 8;
    private static final int initialLabelCount = 9;

    @Before
    public void setUp() {
        eventDatabase = Services.getEventPersistence(false);
        eventLabelDatabase = Services.getEventLabelPersistence(false);
        event1 = new EventDSO(initialEventCount + 1, date, "event1");
        event2 = new EventDSO(initialEventCount + 2, date, "event2");
        event3 = new EventDSO(initialEventCount + 3, date, "event3");
        event4 = new EventDSO(initialEventCount + 2, date, "event4"); // for duplication checks
        eventList = new ArrayList<>(Arrays.asList(event1, event2, event3));
    }

    @After
    public void tearDown() {
        Services.clean();
    }

    @Test
    public void testGetEventList() {
        assertNotNull("Newly created database object should not be null",
                eventDatabase);
        assertEquals("Newly created database should have " + initialEventCount + " events",
                initialEventCount, eventDatabase.numEvents());

        eventDatabase.insertEvent(event1);
        eventDatabase.insertEvent(event2);
        eventDatabase.insertEvent(event3);
        List<EventDSO> actual = eventDatabase.getEventList();

        assertTrue("Database should contain event1", actual.contains(event1));
        assertTrue("Database should contain event2", actual.contains(event2));
        assertTrue("Database should contain event3", actual.contains(event3));
        assertTrue("Database should have all existing events",
                actual.containsAll(eventList));
        assertFalse("Database should not contain an event that does not exist",
                actual.contains(event4));
    }

    @Test
    public void testGetEventByID() {
        eventDatabase.insertEvent(event1);
        eventDatabase.insertEvent(event2);
        assertEquals("The correct event should be returned if present",
                event1, eventDatabase.getEventByID(event1.getID()));
    }

    @Test(expected = EventNotFoundException.class)
    public void testGetEventByIDException() {
        eventDatabase.insertEvent(event1);
        eventDatabase.getEventByID(event3.getID());
    }

    @Test
    public void testInsertEvent() {
        assertEquals("Size of database should be " + initialEventCount, initialEventCount,
                eventDatabase.numEvents());

        eventDatabase.insertEvent(event1);
        assertEquals("Size of database should be " + (initialEventCount + 1), initialEventCount + 1,
                eventDatabase.numEvents());

        assertEquals("Inserted event should return", event2,
                eventDatabase.insertEvent(event2));
        assertEquals("Size of database should be " + (initialEventCount + 2), initialEventCount + 2,
                eventDatabase.numEvents());

        eventDatabase.insertEvent(event3);
        assertEquals("Size of database should be " + (initialEventCount + 3), initialEventCount + 3,
                eventDatabase.numEvents());

        assertEquals("Database should contain event2", event2,
                eventDatabase.getEventByID(event2.getID()));
    }

    @Test(expected = DuplicateEventException.class)
    public void testInsertEventException() {
        eventDatabase.insertEvent(event1);
        eventDatabase.insertEvent(event2);
        eventDatabase.insertEvent(event1);
        eventDatabase.insertEvent(new EventDSO(initialEventCount + 2, date, "event4"));
    }

    @Test
    public void testUpdateEvent() {
        eventDatabase.insertEvent(event1);
        assertEquals("Size of database should be " + (initialEventCount + 1), initialEventCount + 1,
                eventDatabase.numEvents());
        event1 = eventDatabase.updateEventDescription(event1, "hello");
        assertEquals("New attributes should match", "hello",
                eventDatabase.getEventByID(event1.getID()).getDescription());

        event1 = eventDatabase.updateEventName(event1, "good-bye");
        assertEquals("Updated event should be returned", "good-bye",
                event1.getName());
    }

    @Test(expected = EventNotFoundException.class)
    public void testUpdateEventException() {
        eventDatabase.updateEventName(event1, "not present"); // should not be able to update an event not in db
    }

    @Test
    public void testUpdateEventFinishTime() {
        eventDatabase.insertEvent(event1);
        date.set(2023, 1, 1);
        event1 = eventDatabase.updateEventFinishTime(event1, date);

        assertEquals("New attributes should match", date,
                eventDatabase.getEventByID(event1.getID()).getTargetFinishTime());
    }

    @Test
    public void testEventWithLabels() {
        EventLabelDSO label1 = new EventLabelDSO(initialLabelCount + 1, "Label1");
        EventLabelDSO label2 = new EventLabelDSO(initialLabelCount + 2, "Label2");

        eventLabelDatabase.insertEventLabel(label1);
        eventLabelDatabase.insertEventLabel(label2);

        event1 = eventDatabase.insertEvent(event1);
        event1 = eventDatabase.addLabel(event1, label1);
        event1 = eventDatabase.addLabel(event1, label2);
        EventDSO result = eventDatabase.getEventByID(event1.getID());
        List<EventLabelDSO> labels = result.getEventLabels();

        assertEquals("The event should have 2 labels in it", 2, labels.size());
        assertTrue("The event should contain label1", labels.contains(label1));
        assertTrue("The event should contain label2", labels.contains(label2));

        event1 = eventDatabase.removeLabel(event1, label1);
        labels = event1.getEventLabels();
        assertEquals("The event should now have 1 label", 1, labels.size());
        assertTrue("The event should contain label2", labels.contains(label2));
        assertFalse("The event should not contain the deleted label1", labels.contains(label1));
    }

    @Test
    public void testDeleteEvent() {
        eventDatabase.insertEvent(event1);
        eventDatabase.insertEvent(event2);
        eventDatabase.insertEvent(event3);

        assertEquals("Size of database should be " + (initialEventCount + 3), initialEventCount + 3,
                eventDatabase.numEvents());
        eventDatabase.deleteEvent(event2);
        assertEquals("Size of database should be " + (initialEventCount + 2), initialEventCount + 2,
                eventDatabase.numEvents());
        assertEquals("If event exists, return the event that was deleted", event1,
                eventDatabase.deleteEvent(event1));
        assertEquals("Size of database should be " + (initialEventCount + 1), initialEventCount + 1,
                eventDatabase.numEvents());
        eventDatabase.deleteEvent(event3);
        assertEquals("Size of database should be " + initialEventCount, initialEventCount,
                eventDatabase.numEvents());
    }

    @Test(expected = EventNotFoundException.class)
    public void testDeleteEventException() {
        eventDatabase.deleteEvent(event4); // should not be able to delete an event not in db
    }

    @Test
    public void testGetNextID() {
        assertEquals("The first ID should be " + (initialEventCount + 1),
                initialEventCount + 1, eventDatabase.getNextID());
        eventDatabase.insertEvent(event1);
        assertEquals("The ID of the first event inserted should be " + (initialEventCount + 1),
                initialEventCount + 1, event1.getID());

        assertEquals("The second ID should be " + (initialEventCount + 2),
                initialEventCount + 2, eventDatabase.getNextID());
        eventDatabase.insertEvent(event2);
        assertEquals("The ID of the second event inserted should be " + (initialEventCount + 2),
                initialEventCount + 2, event2.getID());

        eventDatabase.insertEvent(event3);
        try {
            eventDatabase.insertEvent(event3);
        } catch (DuplicateEventException e) {
            System.out.println(e.getMessage());
        }
        assertEquals("The next ID after three events, with one duplicate attempt should be " + (initialEventCount + 4),
                initialEventCount + 4, eventDatabase.getNextID());

        eventDatabase.deleteEvent(event2);
        assertNotEquals("The next ID after a deletion should not be the deleted ID.",
                event2.getID(), eventDatabase.getNextID());
        assertEquals("The next ID should be " + (initialEventCount + 4), initialEventCount + 4, eventDatabase.getNextID());
    }
}
