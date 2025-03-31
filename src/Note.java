/**
 * Enum representing musical notes and generating corresponding sinusoidal waveforms for audio purposes.
 * Each note is associated with a frequency, and the "REST" note represents silence.
 * Author: Nathan William and Molly O'Connor
 * Date: 2025-03-30
 */
enum Note {
    // REST note represents silence, must be the first 'Note'
    REST,
    A4, A4S, B4, C4, C4S, D4, D4S, E4, F4, F4S, G4, G4S,
    A5, A5S, B5, C5, C5S, D5, D5S, E5, F5, F5S, G5, G5S,
    A6, A6S, B6, C6, C6S, D6, D6S, E6, F6, F6S, G6, G6S,
    A7, A7S, B7, C7, C7S, D7, D7S, E7, F7, F7S, G7, G7S,
    A8, A8S, B8;

    // Constants for sound generation
    public static final int SAMPLE_RATE = 48 * 1024; // Sample rate ~48KHz
    public static final int MEASURE_LENGTH_SEC = 1;  // Duration of each sample in seconds
    private static final double step_alpha = (2.0d * Math.PI) / SAMPLE_RATE; // Step size for sine wave generation

    public static boolean bellAssigned = false;

    // Constants for sound properties
    private final double FREQUENCY_A_HZ = 440.0d; // Frequency of A4 in Hz
    private final double MAX_VOLUME = 127.0d; // Maximum sound volume (max amplitude)

    // Array to store the generated sound sample for the note
    private final byte[] sinSample = new byte[MEASURE_LENGTH_SEC * SAMPLE_RATE];

    // Constructor to generate the waveform for each note
    Note() {
        int n = this.ordinal(); // Get the ordinal number of the note (its position in the enum)
        if (n > 0) { // Skip REST, as it represents silence
            // Calculate the frequency of the note relative to A4
            final double halfStepUpFromA = n - 1;
            final double exp = halfStepUpFromA / 12.0d;
            final double freq = FREQUENCY_A_HZ * Math.pow(2.0d, exp); // Frequency of the note

            // Generate the sinusoidal waveform for the note
            final double sinStep = freq * step_alpha;
            for (int i = 0; i < sinSample.length; i++) {
                sinSample[i] = (byte) (Math.sin(i * sinStep) * MAX_VOLUME); // Apply the sine wave formula
            }
        }
    }

    // Method to return the generated sound sample for the note
    public byte[] sample() {
        return sinSample;
    }

    // Method to check if the bell (sound) is assigned
    public boolean bellAssigned() {
        return bellAssigned;
    }
}
