import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The SongManager class handles loading and playing a song, where the song is represented as a sequence of BellNotes.
 * It outputs audio via a SourceDataLine. The song is loaded from a file, where each line contains a note and its corresponding length.
 * The class also handles note conversion, validation, and the playback of the song through the system's audio line.
 * Author: Nathan Williams and Molly O'Connor
 * Date: 2025-03-30
 */
public class SongManager {

    private final AudioFormat af;  // Audio format used for playback
    private static boolean validSong = true;

    // Constructor to initialize the Tone with a specific AudioFormat
    SongManager(AudioFormat af) {
        this.af = af;
    }

    // Main method to start the playback of the song
    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("No song provided");
            return;
        }

        String filePath = args[0];  // Get the song file path from command-line arguments
        List<BellNote> song = loadSong(filePath);  // Load the song from the file

        if (song.isEmpty()) {
            System.err.println("Error: The song file is empty or could not be loaded correctly.");
            return;
        }
        if (!validSong) {
            System.err.println("Error: The song file has issues and could not be loaded correctly.");
            return;
        }

        final AudioFormat af = new AudioFormat(Note.SAMPLE_RATE, 8, 1, true, false);  // Define audio format
        SongManager t = new SongManager(af);  // Create a new Tone object
        try {
            t.playSong(song);  // Play the loaded song
        } catch (LineUnavailableException e) {
            System.err.println("Error: Unable to play song due to audio system issues.");
            throw new RuntimeException(e);
        }
    }

    // Method to load a song from a file
    private static List<BellNote> loadSong(String filePath) {
        List<BellNote> song = new ArrayList<>();

        // Read each line from the song file
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            int lineCount = 0;
            while ((line = br.readLine()) != null) {
                lineCount++;
                BellNote bellNote = toBellNote(line, lineCount);  // Convert each line to a BellNote
                if (bellNote != null) {
                    song.add(bellNote);  // Add valid BellNote to the song list
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        return song;  // Return the loaded song
    }

    // Method to convert a line of text to a BellNote
    private static BellNote toBellNote(String line, int lineCount) {
        String[] parts = line.split("\\s+");  // Split the line into parts (note and length)
        if (parts.length != 2) {
            validSong = false;
            System.err.println("Error (Line " + lineCount + "): Invalid format - \"" + line + "\". Expected format: <NOTE> <LENGTH>.");
            return null;
        }
        try {
            Note note = Note.valueOf(parts[0]);  // Convert the first part to a Note enum
            int lengthValue = Integer.parseInt(parts[1]);  // Parse the note length

            NoteLength length = getNoteLength(lengthValue);  // Convert length to NoteLength enum
            if (length == null) {
                validSong = false;
                System.err.println("Error (Line " + lineCount + "): Invalid note length - \"" + parts[1] + "\".");
                return null;
            }
            return new BellNote(note, length);  // Return a new BellNote object
        } catch (IllegalArgumentException e) {
            System.err.println("Error (Line " + lineCount + "): Invalid note - \"" + parts[0] + "\".");
            return null;
        }
    }

    // Helper method to convert an integer value to a NoteLength
    private static NoteLength getNoteLength(int value) {
        if (value == 1) {
            return NoteLength.WHOLE;
        } else if (value == 2) {
            return NoteLength.HALF;
        } else if (value == 4) {
            return NoteLength.QUARTER;
        } else if (value == 8) {
            return NoteLength.EIGTH;
        } else {
            return null;  // Return null for invalid note length values
        }
    }

    // Method to play the song using the SourceDataLine
    void playSong(List<BellNote> song) throws LineUnavailableException {
        try (final SourceDataLine line = AudioSystem.getSourceDataLine(af)) {
            line.open();
            line.start();
            new Conductor(song, line);  // Pass the song to the conductor to start playing
            line.drain();  // Wait for the playback to finish
        } catch (InterruptedException e) {
            throw new RuntimeException(e);  // Handle interruption during playback
        }
    }
}

