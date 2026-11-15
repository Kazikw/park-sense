package kazimirowicz.parkingapp.exception;

public class ZoneNotFoundException extends RuntimeException {

    public ZoneNotFoundException(Long id) {
        super("Nie znaleziono strefy parkingowej o ID: " + id);
    }

    public ZoneNotFoundException(String message) {
        super(message);
    }
}
