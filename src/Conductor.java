import javax.sound.sampled.SourceDataLine;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * The Conductor class manages the synchronization of BellThread instances for playing a musical song.
 * It initializes threads for unique notes in the song and assigns them to play specific notes.
 * The class ensures that each BellThread plays its designated notes in turn based on a song.
 * Author: Molly O'Connor
 * Date: 2025-03-30
 */
public class Conductor {
    private final List<BellThread> bellThreads; // List of threads to manage
    private final BlockingQueue<BellNote> bellNotes; // Queue for storing notes to be played
    private final SourceDataLine sourceDataLine; // Used for output audio

    /**
     * Constructor that initializes the conductor, prepares the threads, and starts the song.
     *
     * @param song           List of BellNote objects representing the song to be played.
     * @param sourceDataLine The audio line used for note playback.
     * @throws InterruptedException If the thread is interrupted during the process.
     */
    public Conductor(List<BellNote> song, SourceDataLine sourceDataLine) throws InterruptedException {
        this.bellThreads = new ArrayList<>();
        this.bellNotes = new LinkedBlockingQueue<>(song); // Queue for safe access to the notes
        this.sourceDataLine = sourceDataLine;

        // Set to ensure only unique notes are considered for threads
        Set<Note> uniqueNotes = new HashSet<>();
        for (BellNote note : song) {
            uniqueNotes.add(note.note);
        }

        int threadCount = (int) Math.ceil(uniqueNotes.size() / 2.0); // Calculate number of threads needed
        List<Note> noteList = new ArrayList<>(uniqueNotes);

        // Create BellThread instances for each pair of unique notes
        for (int i = 0; i < threadCount; i++) {
            Note note1 = noteList.get(i * 2);
            Note note2 = (i * 2 + 1 < noteList.size()) ? noteList.get(i * 2 + 1) : null;
            String name = "Member-" + i;
            BellThread thread = new BellThread(note1, note2, bellNotes, sourceDataLine, name);
            // Log which notes are being played by the current thread
            if (note2 != null) {
                System.out.println(name + " is responsible for notes: " + note1 + " and " + note2);
            } else {
                System.out.println(name + " is responsible for note: " + note1);
            }
            bellThreads.add(thread);
            thread.start();
        }

        // Start the song by giving each thread its turn to play the notes
        System.out.println("Song starting...");
        for (BellNote bn : bellNotes) {
            Note note = bn.getNote();
            for (BellThread bellThread : bellThreads) {
                if (bellThread.getNote1() == note || bellThread.getNote2() == note) {
                    bellThread.giveTurn();
                }
            }
        }

        // Stop all threads once the song is completed
        for (BellThread thread : bellThreads) {
            thread.stopTurn();
        }
    }
}
