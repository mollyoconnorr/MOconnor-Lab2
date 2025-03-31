import javax.sound.sampled.SourceDataLine;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


public class Conductor {
    private final List<BellThread> bellThreads;
    private final BlockingQueue<BellNote> bellNotes; // Use BlockingQueue for safe access
    private final SourceDataLine sourceDataLine;

    public Conductor(List<BellNote> song, SourceDataLine sourceDataLine) throws InterruptedException {
        this.bellThreads = new ArrayList<>();
        this.bellNotes = new LinkedBlockingQueue<>(song); // Use LinkedBlockingQueue
        this.sourceDataLine = sourceDataLine;

        Set<Note> uniqueNotes = new HashSet<>();
        for (BellNote note : song) {
            uniqueNotes.add(note.note);
        }

        int threadCount = (int) Math.ceil(uniqueNotes.size() / 2.0);
        List<Note> noteList = new ArrayList<>(uniqueNotes);

        for (int i = 0; i < threadCount; i++) {
            Note note1 = noteList.get(i * 2);
            Note note2 = (i * 2 + 1 < noteList.size()) ? noteList.get(i * 2 + 1) : null;
            String name = "Member-" + i;
            BellThread thread = new BellThread(note1, note2, bellNotes, sourceDataLine, name);
            if (note2 != null) {
                System.out.println(name + " is responsible for notes: " + note1 + " and " + note2);
            }
            else {
                System.out.println(name + " is responsible for note: " + note1);
            }
            bellThreads.add(thread);
            thread.start();
        }

        System.out.println("Song starting...");
        for (BellNote bn : bellNotes) {
            Note note = bn.getNote();
            for (BellThread bellThread : bellThreads) {
                if (bellThread.getNote1() == note || bellThread.getNote2() == note) {
                    bellThread.giveTurn();
                }
            }
        }
        for (BellThread thread : bellThreads) {
            thread.stopTurn();
        }
    }
}
