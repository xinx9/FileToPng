package ehacksapp;

import java.io.File;
import java.util.ArrayList;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javafx.application.Application;
import javafx.stage.Stage;

public class EHacksApp extends Application {

    public static final int mB = 1048576;

    public void start(Stage main) {
        //DOnt do shit
        main.show();
    }

    public static void main(String[] args) {
        Key sameKey = new Key();

        sameKey.DatFileWriter();

        ArrayList<Chunky> ChunkyChunks = makeChunks("Test.mp4", sameKey);
        chunksToImages(ChunkyChunks);
        System.out.println("Finishes Image");

        //Decoder - doesn't work Atm
        // Image x = new Image(new File("0.png").toURI().toString());
        // Chunky chunk1 = new Chunky(x, sameKey);
        //  chunk1.img2B64();
        com.sun.javafx.application.PlatformImpl.tkExit();
    }

    public static void chunksToImages(ArrayList<Chunky> chunks) {
        /*
        Takes an arraylist of chunks and writes each chunk to an image.
         */

        for (int i = 0; i < chunks.size(); i++) {
            Chunky curChunk = chunks.get(i);

            curChunk.toImgObj();
            curChunk.img2File(curChunk.pieceNum + ".png");
            System.out.println("EO4");

        }
        System.out.println("Exits 4");
    }

    public static ArrayList<Chunky> makeChunks(String fileName, Key key) {
        /*
        Creates an array of chunks with a size of about 6MB
        
         */

        ArrayList<Chunky> chunks = new ArrayList();
        Chunky.setFilename(fileName);  // Set the filename of the current file-image set

        byte[] totalBytes = readBytes(fileName);  // read the filename into a big array of bytes
        System.out.println("TotalBytes: " + totalBytes.length);

        int fourMb = 4 * mB;  // Set what 4mB is in bytes
        int pieceCount = 0;  // Set the current piece count
        boolean keepReading = true;  // Loop control
        int offsetCounter = 0;  // Offset to read in more than one section, needed
        //          below to access bytes further than sectionByteSize from the main byteArray(totalBytes)

        while (keepReading) {
            // Grab how many extra bytes we can fit in
            int xtraFromCompression = ((Chunky.compressionAvg >= 1) ? 0 : (int) (Chunky.compressionAvg * fourMb));
            // To still be at about 4mb after compression
            // int sectionByteSize = (fourMb) + xtraFromCompression;  // Total bytes in chunk
            int sectionByteSize = fourMb;
            //System.out.println("Comrpession_Extra: " + xtraFromCompression);  // Print bytes added bc compression
            //System.out.println(Chunky.compressionAvg*100 + "%"); // This doesnt seem quite right..... Problem for later
            byte[] chunkByte = new byte[(sectionByteSize >= totalBytes.length) ? totalBytes.length : sectionByteSize];

            for (int i = 0; i < sectionByteSize; i++) {
                try {
                    // Read in a chunk of the correct size with extra from compression
                    chunkByte[i] = totalBytes[i + offsetCounter];  // Does this make sense and read all the bytes once?
                } catch (java.lang.IndexOutOfBoundsException e) {
                    // If it goes out of bounds, we are done reading
                    System.out.println("End of Data chunk");
                    keepReading = false; // End the loop
                    break;
                }
                offsetCounter += 1;  // Add to the total number of bytes
            }
            Chunky curChunk = new Chunky(chunkByte, key);  // Create a new chunk obj
            chunks.add(curChunk);  // Add this chunk obj to the arrayList
            curChunk.compress();  // Compress the chunk
            curChunk.getMD5();
            curChunk.setPiece(pieceCount);  // set the piece of the chunk
            System.out.println("Done adding chunk");

            pieceCount += 1;  // Increment the piece counter

        }
        return chunks;
    }

    public static byte[] readBytes(String fileName) {
        int BS = byteSize(fileName);
        byte[] mb = new byte[BS];

        Path path = Paths.get(fileName);
        try {
            byte[] data = Files.readAllBytes(path); // temporary byte array which reads in all bytes from the file
            mb = data; //set primary byte array to equal temporary byte array
        } catch (FileNotFoundException e) {
            System.out.println(e.toString());
        } catch (IOException ex) {
            System.out.println(ex.toString());
        }

        return mb;
    }

    public static int byteSize(String fileName) {
        File file = new File(fileName);
        double Dbytes = (double) file.length(); //we grab the size of the file in bytes
        //rint rounds to the nearest integer, this is useful so we don't lose any precision 
        int bytes = (int) Math.rint(Dbytes); //10^1

        return bytes;
    }

}
