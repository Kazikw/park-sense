package kazimirowicz.parkingapp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "parking_zones")
@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ParkingZone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    String name;

    @Column(nullable = false)
    Integer totalCapacity;

    @Column(nullable = false)
    Integer typicalOccupancy;

    @Column(nullable = false)
    Integer currentAppUsersParked = 0;

    /**
     * Oblicza prawdopodobieństwo znalezienia miejsca (0-100).
     * Im currentAppUsersParked jest mniejsze w stosunku do typicalOccupancy, tym wyższy wynik.
     */
    public int calculateProbabilityScore() {
        if (typicalOccupancy == null || typicalOccupancy <= 0) {
            return 100;
        }
        int current = currentAppUsersParked != null ? currentAppUsersParked : 0;
        if (current <= 0) {
            return 100;
        }
        if (current >= typicalOccupancy) {
            return 0;
        }
        double ratio = (double) current / typicalOccupancy;
        int score = (int) Math.round((1.0 - ratio) * 100.0);
        return Math.clamp(score, 0, 100);
    }

    public boolean isFull() {
        if (totalCapacity == null || currentAppUsersParked == null) {
            return false;
        }
        return currentAppUsersParked >= totalCapacity;
    }

    public boolean hasHighProbability() {
        return calculateProbabilityScore() >= 70;
    }
}
