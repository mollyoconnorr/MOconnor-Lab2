/**
 * Enum representing different note lengths and their corresponding duration in milliseconds.
 * Each note length corresponds to a fraction of a whole note, and calculates the time in milliseconds.
 * Author: Nathan Williams and Molly O'Connor
 * Date: 2025-03-30
 */
enum NoteLength {
    // Enum values representing different note lengths and their fraction of a whole note
    WHOLE(1.0f),   // Whole note (1.0)
    HALF(0.5f),    // Half note (1/2)
    QUARTER(0.25f), // Quarter note (1/4)
    EIGTH(0.125f);  // Eighth note (1/8)

    // Time in milliseconds for the corresponding note length
    private final int timeMs;

    // Constructor to initialize the time in milliseconds for each note length
    NoteLength(float length) {
        // Calculate time in milliseconds by multiplying by the measure length in seconds and converting to ms
        timeMs = (int) (length * Note.MEASURE_LENGTH_SEC * 1000);
    }

    // Method to return the time in milliseconds for the note length
    public int timeMs() {
        return timeMs;
    }
}
