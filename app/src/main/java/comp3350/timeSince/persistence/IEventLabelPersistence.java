package comp3350.timeSince.persistence;

import java.util.List;

import comp3350.timeSince.business.exceptions.DuplicateEventLabelException;
import comp3350.timeSince.business.exceptions.EventLabelNotFoundException;
import comp3350.timeSince.objects.EventLabelDSO;
import comp3350.timeSince.objects.UserDSO;

public interface IEventLabelPersistence {

    /**
     * @return List of Event Labels (unmodifiable), null if unsuccessful.
     */
    List<EventLabelDSO> getEventLabelList();

    /**
     * @param labelName The unique (positive int) ID of the Event Label.
     * @return The Event Label object associated with the ID, null otherwise.
     * @throws EventLabelNotFoundException If the Event Label is not in the database.
     */
    EventLabelDSO getEventLabelByID(String userID, String labelName) throws EventLabelNotFoundException;

    /**
     * @param newEventLabel The Event Label object to be added to the database.
     * @return The Event Label object that was added to the database, null otherwise.
     * @throws DuplicateEventLabelException If the Event Label is already stored in the database.
     */
    EventLabelDSO insertEventLabel(UserDSO user, EventLabelDSO newEventLabel) throws DuplicateEventLabelException;

    /**
     * @param eventLabel The Event Label object to be deleted from the database.
     * @return The Event Label object that was deleted, null otherwise.
     * @throws EventLabelNotFoundException If the Event Label is not found in the database.
     */
    EventLabelDSO deleteEventLabel(UserDSO user, EventLabelDSO eventLabel) throws EventLabelNotFoundException;

    /**
     * @return The number of event labels in the database, -1 otherwise.
     */
    int numLabels();

}
