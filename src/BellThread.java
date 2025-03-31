import javax.sound.sampled.SourceDataLine;
import java.util.concurrent.BlockingQueue;

/**
 * BellThread plays musical notes in a threaded environment, handling turn-taking for note playback.
 * It fetches notes from a queue and plays them in a synchronized manner.
 * Interacts with the Conductor class which manages the BellThreads.
 * Uses logic from the Player.java class provided in lab (CS-410).
 * Author: Molly O'Connor
 * Date: 2025-03-30
 */
public class BellThread extends Thread {
    private final Note note1; // The first note to be played by the thread
    private final Note note2; // The second note to be played by the thread
    private final SourceDataLine sourceDataLine; // The audio output line to play the note
    private final BlockingQueue<BellNote> noteQueue; // Queue of BellNote objects to be played
    private final String name; // The name of the thread/member
    private volatile boolean running = true; // Flag to control the thread's execution
    private boolean myTurn; // Flag to indicate if it's the thread's turn to play a note

    /**
     * Constructor to initialize a BellThread with notes, a note queue, an audio line, and a name.
     *
     * @param note1          The first note to be played by the thread.
     * @param note2          The second note to be played by the thread.
     * @param noteQueue      The queue containing notes to be played.
     * @param sourceDataLine The audio output line to play the notes.
     * @param name           The name of the thread/member.
     */
    public BellThread(Note note1, Note note2, BlockingQueue<BellNote> noteQueue, SourceDataLine sourceDataLine, String name) {
        this.note1 = note1;
        this.note2 = note2;
        this.noteQueue = noteQueue;
        this.sourceDataLine = sourceDataLine;
        this.name = name;
    }

    /**
     * Run method that defines the behavior of the BellThread. It waits for its turn, plays a note from the queue,
     * and then signals the next thread.
     */
    public void run() {
        synchronized (this) {
            while (running) {
                try {
                    // Wait for my turn, unless thread is stopped
                    while (!myTurn && running) {
                        wait();
                    }

                    if (!running) {
                        break; // Exit thread
                    }

                    BellNote bellNote = noteQueue.poll();
                    if (bellNote == null) {
                        running = false;
                        break;
                    }

                    playNote(sourceDataLine, bellNote);
                    myTurn = false;
                    notify();
                } catch (InterruptedException e) {
                    running = false;
                    break;
                }
            }
        }
        System.out.println(name + " exiting.");
    }

    /**
     * Method to give the thread its turn to play a note.
     * This ensures that the thread plays only when it's its turn.
     */
    public void giveTurn() {
        synchronized (this) {
            if (myTurn) {
                throw new IllegalStateException("Attempt to give a turn to a player who hasn't completed the current turn");
            }
            myTurn = true;
            notify();
            while (myTurn) {
                try {
                    wait();
                } catch (InterruptedException ignored) {
                }
            }
        }
    }

    /**
     * Stops the thread's turn and interrupts it.
     */
    public void stopTurn() throws InterruptedException {
        running = false;
        interrupt();
    }

    /**
     * Method to play a note. This method handles the note's playback through the SourceDataLine.
     * It also prints the name of the thread and the note it's playing.
     *
     * @param line The audio line used to play the note.
     * @param bn   The BellNote containing the note to be played.
     */
    private void playNote(SourceDataLine line, BellNote bn) {
        if (Thread.currentThread() instanceof BellThread current) {
            System.out.println(current.getMemberName() + " is playing note: " + bn.note);
        } else {
            System.out.println("Unknown thread is playing note: " + bn.note);
        }

        final int ms = Math.min(bn.length.timeMs(), Note.MEASURE_LENGTH_SEC * 1000);
        final int length = Note.SAMPLE_RATE * ms / 1000;
        line.write(bn.note.sample(), 0, length);
        line.write(Note.REST.sample(), 0, 50);
    }

    /**
     * Gets the first note played by the thread.
     *
     * @return The first note.
     */
    public Note getNote1() {
        return note1;
    }

    /**
     * Gets the second note played by the thread.
     *
     * @return The second note.
     */
    public Note getNote2() {
        return note2;
    }

    /**
     * Gets the name of the member (thread).
     *
     * @return The name of the thread/member.
     */
    public String getMemberName() {
        return name;
    }
}