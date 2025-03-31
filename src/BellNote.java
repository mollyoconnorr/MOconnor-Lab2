/**
 * BellNote class represents a musical note with its corresponding note length.
 * Author: Nathan Williams and Molly O'Connor
 * Date: 2025-03-30
 */

class BellNote {

    final Note note;

    final NoteLength length;

    /**
     * Constructor for creating a BellNote with a specific note and length.
     *
     * @param note   The musical note (e.g., A5, C4).
     * @param length The duration or length of the note (e.g., quarter, half note).
     */
    BellNote(Note note, NoteLength length) {
        this.note = note;
        this.length = length;
    }

    /**
     * Gets the musical note of this BellNote.
     *
     * @return The note
     */
    public Note getNote() {
        return note;
    }
}
