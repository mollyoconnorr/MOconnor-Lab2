import javax.sound.sampled.SourceDataLine;
import java.util.concurrent.BlockingQueue;

public class BellThread extends Thread {
    private final Note note1;
    private final Note note2;
    private final SourceDataLine sourceDataLine;
    private final BlockingQueue<BellNote> noteQueue;
    private final String name;
    private volatile boolean running = true;
    private boolean myTurn;


    public BellThread(Note note1, Note note2, BlockingQueue<BellNote> noteQueue, SourceDataLine sourceDataLine, String name) {
        this.note1 = note1;
        this.note2 = note2;
        this.noteQueue = noteQueue;
        this.sourceDataLine = sourceDataLine;
        this.name = name;
    }

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

    public void giveTurn() {
        synchronized (this) {
            if (myTurn) {
                throw new IllegalStateException("Attempt to give a turn to a player who's hasn't completed the current turn");
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

    public void stopTurn() throws InterruptedException {
        running = false;
        interrupt();
    }

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

    public Note getNote1() {
        return note1;
    }

    public Note getNote2() {
        return note2;
    }

    public String getMemberName() {
        return name;
    }
}
