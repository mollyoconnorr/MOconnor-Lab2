import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class Tone {

    public static void main(String[] args)  {
        if (args.length < 1) {
            System.err.println("No song provided");
            return;
        }

        String filePath = args[0];  // command-line arguments
        List<BellNote> song = loadSong(filePath);

        if (song.isEmpty()) {
            System.err.println("Error: The song file is empty or could not be loaded correctly.");
            return;
        }

        final AudioFormat af = new AudioFormat(Note.SAMPLE_RATE, 8, 1, true, false);
        Tone t = new Tone(af);
        try {
            t.playSong(song);
        } catch (LineUnavailableException e) {
            System.err.println("Error: Unable to play song due to audio system issues.");
            throw new RuntimeException(e);
        }
    }

    private final AudioFormat af;

    Tone(AudioFormat af) {
        this.af = af;
    }

    private enum State {
        A, B, C, D, E, F, G; // Add more if needed
    }

    State state;

    void playSong(List<BellNote> song) throws LineUnavailableException {
        try (final SourceDataLine line = AudioSystem.getSourceDataLine(af)) {
            line.open();
            line.start();
            new Conductor(song, line);
            line.drain();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<BellNote> loadSong(String filePath) {
        List<BellNote> song = new ArrayList<>();

        // read line by line
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                BellNote bellNote = toBellNote(line);
                if (bellNote != null) {
                    song.add(bellNote);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        return song;
    }

    private static BellNote toBellNote(String line) {
        String[] parts = line.split("\\s+"); // split by space
        if (parts.length != 2) {
            System.err.println("Invalid line format in song: " + line + ", playing all valid notes provided...");
            return null;
        }
        try {
            Note note = Note.valueOf(parts[0]); // convert to Note enum
            int lengthValue = Integer.parseInt(parts[1]); // get note length

            NoteLength length = getNoteLength(lengthValue); // converts to NoteLength enum
            if (length == null) {
                System.err.println("Invalid note length: " + parts[1] + ", playing all valid notes provided...");
                return null;
            }
            return new BellNote(note, length);
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid note: " + parts[0] + ", playing all valid notes provided...");
            return null;
        }
    }

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
            return null;
        }
    }
}

