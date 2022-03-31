package comp3350.timeSince.business.exceptions;

public class DuplicateEventException extends RuntimeException {

    public DuplicateEventException(String message) {
        super(message);
    }

    public DuplicateEventException(String message, String error) {
        super(message + "\n" + error);
    }

}
