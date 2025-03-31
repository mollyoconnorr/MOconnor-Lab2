# Lab 2 - Bell Choir

## Overview

The project was designed to represent a **bell choir** where each thread acts as a member of the choir, and each member is responsible for playing one or two notes in the song. This simulates the collaborative nature of a bell choir, where each participant plays different notes in harmony.

This project implements a tone generator that plays musical notes based on input from a song file. The program reads the song, processes each note, and plays it with appropriate timing. The tone generator is built to play the instructor-provided song "Mary Had a Little Lamb," and it can also handle additional song files for testing and validation. The program uses the Java Sound API for audio playback, and each note is played in a separate thread as required by the project specifications.

## Requirements

- **Project must be committed and pushed up to GitHub**: This project is fully committed and pushed to GitHub.
- **Must use ANT to build/run**: The project is set up with an ANT build script (`build.xml`) that compiles, runs, and tests the program.
- **Each Member must play each assigned note in a separate thread**: The `Tone` class, along with `BellNote` and `Conductor`, ensures that each note is played in a separate thread to satisfy this requirement. Each thread represents a bell choir member playing one or two notes.
- **The assignment must be able to play the instructor provided song ‘Mary Had a Little Lamb’ with recognizable sound output and appropriate timing**: The program correctly plays the song with accurate timing, and the note lengths are respected to produce the expected melody.
- **Student provided songs may be provided as additional song files to other students for testing/validation**: The program accepts user-provided song files, which are validated and loaded dynamically at runtime.
- **Improper song files will be provided during the final instructor demonstration to determine how well the program behaves when given invalid data**: The program gracefully handles invalid song files with error messages and continues playing valid notes when possible.

## Features

- **Plays musical notes using Java Sound API**: The program generates and plays tones based on note data, using the `SourceDataLine` to output sound.
- **Bell Choir Simulation with Multi-threading**: Each note is played by a separate thread, representing a member of a bell choir. Each member (thread) is assigned up to two notes, creating a collaborative, harmonious sound.
- **Song file input**: The program reads song files containing notes and their respective lengths, and can handle additional song files from students for testing.
- **Error handling**: The program checks for invalid song files, invalid note formats, and missing note lengths, displaying appropriate error messages and continuing to play valid notes.
- **Support for "Mary Had a Little Lamb"**: The program is capable of playing the instructor-provided song, "Mary Had a Little Lamb," with accurate note timing.

## Setup Instructions

### Prerequisites

- Java 8 or higher
- ANT (used for building and running the project)
