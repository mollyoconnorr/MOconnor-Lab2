# Bell Choir Project - Synchronization in Multi-Threading

<h2>Overview</h2>
<p>The Bell Choir project is a multi-threaded Java program that plays songs using a simulated bell choir. Each note in a song is assigned to a member of the choir, and the conductor coordinates when each member plays their assigned notes to ensure correct sequencing and timing. The program reads a song file containing a list of bell notes and plays them in order, following the correct tempo and note lengths.</p>

<h2>Features</h2>
<ul>
    <li>Reads a formatted song file containing bell notes and durations.</li>
    <li>Assigns notes to members, ensuring each note is played by the correct member.</li>
    <li>Uses a separate thread for each choir member to play their assigned notes.</li>
    <li>A conductor thread manages the tempo and coordinates the timing of notes.</li>
    <li>Supports standard musical notation, including whole, half, quarter, and eighth notes.</li>
    <li>Allows for REST notes where no sound is played.</li>
    <li>Implements synchronization to ensure that only one note is played at a time.</li>
    <li>Supports various songs as long as they are correctly formatted.</li>
</ul>

<h3>Classes</h3>
<p>To view UML Diagrams, click here! <a href="https://github.com/user-attachments/files/18914096/Juice.Bottler_.UML.Diagrams.pdf" target="_blank">Juice Bottler UML Diagrams PDF</a></p>

<ol>
    <li><strong>Note (Enum)</strong>: Defines musical notes and their corresponding frequencies, including the "REST" note for silence, to generate sinusoidal waveforms for audio playback.</li>
    <li><strong>NoteLength (Enum)</strong>: Represents different note lengths (e.g., whole, half, quarter) and their corresponding duration in milliseconds.</li>
    <li><strong>BellNote</strong>: Represents a musical note with its corresponding duration, used for creating and processing notes in the song.</li>
    <li><strong>BellThread</strong>: Manages musical note playback in a threaded environment, synchronizing the execution of each thread to ensure notes are played in the correct order. It interacts with the Conductor class to handle note timing.</li>
    <li><strong>SongManager</strong>: Handles loading and playing a song represented as a sequence of BellNotes, converting them to audio and playing them through the system’s audio output.</li>
    <li><strong>Conductor</strong>: Coordinates the synchronization of BellThread instances, ensuring each thread plays its designated note at the correct time, based on the song's structure.</li>
</ol>

<h2>Project Requirements and How They Were Met</h2>
<table>
    <tr>
        <th>Requirement</th>
        <th>Implementation</th>
    </tr>
    <tr>
        <td>Read song file and validate format</td>
        <td>Implemented file reading and parsing logic to ensure correct format before processing.</td>
    </tr>
    <tr>
        <td>Assign notes to members (1-2 notes per member)</td>
        <td>Finds the total number of unique notes, divides by 2, and rounds up to determine the number of threads needed. Each thread is assigned unique notes based on their note1 and note2 attributes, where note2 may be null in some cases.</td>
    </tr>
    <tr>
        <td>Ensure only assigned members play their notes</td>
        <td>Each note is mapped to a specific member who plays it using a separate thread.</td>
    </tr>
    <tr>
        <td>Conductor controls tempo</td>
        <td>The Conductor class signals when members should play based on note lengths.</td>
    </tr>
    <tr>
        <td>Only one note plays at a time</td>
        <td>Implemented synchronization mechanisms using thread coordination.</td>
    </tr>
    <tr>
        <td>Play song notes in order with correct timing</td>
        <td>Ensured threads wait for the conductor's signal before playing their assigned notes.</td>
    </tr>
    <tr>
        <td>Support for various songs</td>
        <td>Reads external song files and validates structure to allow multiple song inputs.</td>
    </tr>
    <tr>
        <td>Built using ANT</td>
        <td>The project includes a <code>build.xml</code> file to compile and run using ANT.</td>
    </tr>
    <tr>
        <td>Push project to GitHub</td>
        <td>The project is version-controlled with Git and hosted on GitHub.</td>
    </tr>
</table>

<h2>Challenges Faced</h2>

<h3>1. Assigning Notes to Members</h3>
<p>Figuring out how to fairly distribute notes among members while making sure each got only one or two was tricky. The challenge was that I needed to work with unique notes, but the threads had to play BellNotes, which include both a note and its length.</p>

<p>For example, if a song had A5 4, A5 2, and A5 8, these shouldn’t be treated as three separate BellNotes, but rather as just one unique note (A5). To handle this, I used two attributes in each class: <code>Note1</code> and <code>Note2</code>. These keep track of which notes are assigned to each thread, making sure every member plays only what they need while keeping the music accurate.</p>

<h3>2. Creating the Conductor Class</h3>
<p>One of the hardest parts was figuring out how to manage turns between threads. I needed a way to keep track of whose turn it was while making sure each thread played the right notes from the list of BellNotes that made up the song.</p>

<p>My final solution worked by looping through the song’s BellNotes, checking which thread was responsible for playing each note, and then giving that thread its turn. Here’s how it works:</p>

<pre>
<code>
System.out.println("Song starting...");
for (BellNote bn : bellNotes) {
    Note note = bn.getNote(); // Get just the note value (e.g. A5)
    // Loop through the threads and check if that note belongs to the thread
    for (BellThread bellThread : bellThreads) {
        if (bellThread.getNote1() == note || bellThread.getNote2() == note) {
            // If note does belong to the thread than give that thread a turn to play
            bellThread.giveTurn();
        }
    }
}
</code>
</pre>

<p>This made sure that only the right thread played each note at the right time, keeping everything in sync.</p>

<h3>3. Implementing Proper Thread Waits</h3>
<p>One of the biggest challenges was making sure that no two threads played at the same time. I had to use synchronization carefully so that each thread waited its turn before playing. To handle this, I used <code>wait()</code> and <code>notify()</code> to coordinate execution and prevent race conditions.</p>

<p>My solution worked by having each thread wait until it was its turn. When a thread finished playing its note, it signaled the next thread to take over. Here’s how I implemented it:</p>

<pre>
<code>
/**
 * Run method that defines the behavior of the BellThread. It waits for its turn, 
 * plays a note from the queue, and then signals the next thread.
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
</code>
</pre>

<p>This made sure that only one thread played at a time and prevented any overlapping sounds. The <code>giveTurn()</code> method ensured that each thread only played when it was supposed to, keeping the timing of the song smooth.</p>

<h2>How to Run the Project</h2>

<h3>1. Clone the Repository</h3>
<p>Run the following command to clone the repository from GitHub:</p>
<pre><code>git clone &lt;https://github.com/mollyoconnorr/MOconnor-Lab2&gt;</code></pre>

<h3>2. Compile and Run the Project</h3>

<h4>Using <strong>Ant</strong>:</h4>
<p>Navigate to the project directory and execute:</p>
<pre><code>ant run</code></pre>
