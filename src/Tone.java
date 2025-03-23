import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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

        final AudioFormat af = new AudioFormat(Note.SAMPLE_RATE, 8, 1, true, false);
        Tone t = new Tone(af);
        try {
            t.playSong(song);
        } catch (LineUnavailableException e) {
            throw new RuntimeException(e);
        }
    }

    private final AudioFormat af;

    Tone(AudioFormat af) {
        this.af = af;
    }

    void playSong(List<BellNote> song) throws LineUnavailableException {
        try (final SourceDataLine line = AudioSystem.getSourceDataLine(af)) {
            line.open();
            line.start();

            for (BellNote bn: song) {
                playNote(line, bn);
            }
            line.drain();
        }
    }

    private void playNote(SourceDataLine line, BellNote bn) {
        final int ms = Math.min(bn.length.timeMs(), Note.MEASURE_LENGTH_SEC * 1000);
        final int length = Note.SAMPLE_RATE * ms / 1000;
        line.write(bn.note.sample(), 0, length);
        line.write(Note.REST.sample(), 0, 50);
    }

    private static List<BellNote> loadSong(String filePath) {
        List<BellNote> song = new ArrayList<>(); // list of notes

        // read file line by line
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            // read until end of the file
            while ((line = br.readLine()) != null) {
                BellNote bellNote = toBellNote(line); // convert line into a BellNote object
                if (bellNote != null) {
                    // add note to list
                    song.add(bellNote);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
        // return list of notes
        return song;
    }

    private static BellNote toBellNote(String line) {
        String[] parts = line.split("\\s+"); // split by spaces
        if (parts.length != 2) {
            System.err.println("Invalid line format: " + line);
            return null;
        }
        try {
            Note note = Note.valueOf(parts[0]); // convert to Note enum
            int lengthValue = Integer.parseInt(parts[1]); // get note length

            NoteLength length = getNoteLength(lengthValue); // converts to NoteLength enum
            if (length == null) {
                System.err.println("Invalid note length: " + parts[1]);
                return null;
            }
            return new BellNote(note, length);
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid note: " + parts[0]);
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

