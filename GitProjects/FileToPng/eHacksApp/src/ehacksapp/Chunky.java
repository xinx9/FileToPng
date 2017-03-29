package ehacksapp;

import java.awt.image.BufferedImage;
import javafx.scene.image.Image;
import java.util.zip.Deflater;
import java.io.File;
import java.security.MessageDigest;
import java.util.Base64;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.embed.swing.SwingFXUtils;
import javax.imageio.ImageIO;

public class Chunky implements Comparable {
    // TODO: GO back and change some of these methods to private
    //      they all really dont need to be public
   
    public static double compressionAvg=0.0; // Stores the overall compression ration avg
    public static String fileName;
    public double curRatio;
    public int size;
    public Key key;
    public int compressedSize;
    public String md5;
    private byte[] data;
    public int pieceNum;
    public Image image;
    public WritableImage written;
    public String b64Data;
    public byte[] compressedData;

    public Chunky(byte[] data, Key key) { // Add a key class
        this.data = data;
        this.size = this.data.length;
        this.key = key;
        // do more stuff
    }

    public Chunky(Image img, Key key) { // Add a key class here too
        this.image = img;
        this.key = key;
        // Do stuff
    }

    public void compress() {
        // Compresses the data
        Deflater compressor = new Deflater(); // Create deflater object
        compressor.setInput(this.data);  // Set the input data
        compressor.finish();  // Compress it
        this.compressedData = new byte[data.length];  // Set the max length of compressed data
        this.compressedSize = compressor.deflate(compressedData, 0, data.length);
        // Compress the data at offest=0 and with max length of the orig length
        compressor.end();
        double avg = this.size/(double)this.compressedSize - 1;  // Fixed the compression ratio calculation
        if (Chunky.compressionAvg == 0.0){
            Chunky.compressionAvg = avg;
        }
        else{
            Chunky.compressionAvg += avg;
            Chunky.compressionAvg = Chunky.compressionAvg/2;
        }
        this.curRatio = avg;
        
        
    }
    
    public static void setFilename(String fileName){
        Chunky.fileName = fileName;
    }

    public void getMD5() {
        // Gets the md5 of the current data var
        // stores it in the this.md5
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(data);
            this.md5 = this.md5ByteToString(hash);
        } catch (java.security.NoSuchAlgorithmException e) {
            System.out.println(e.toString());
        }

    }

    public WritableImage toImgObj() {
        // at this point, data should be compressed. Maybe I'll just do it in this method??
        // nah should def already be compressed at this point....
        this.b64Data = toB64(this.compressedData); // turn the compreesed data to a B64 string
        // Use the key here to turn the base64 string of data into 
        // gotta turn the first 2 pixels of the image into a base64 representation of the the this.pieceNum
        // the next few bits of data should be the md5sum, then the data.
        String b64Piece = this.pieceNum + "";
        b64Piece = toB64(b64Piece);
        String padding = "";
        String b64Filename = toB64(Chunky.fileName);
        String finalOutData = b64Piece + this.md5 + b64Filename + "*" + this.b64Data;
        int sideLength = (int)Math.ceil(Math.sqrt(finalOutData.length()));
        int imgLength = (int)Math.pow(sideLength, 2);
        int numPadding = imgLength - finalOutData.length();
        for (int i=0; i<numPadding; i++){
            padding = padding + "*";
            // Pad the data so it fits perfectly in the image
        }
        finalOutData = finalOutData + padding;
        //System.out.println(b64Piece);
        //String finalOutData = b64Piece + this.md5 + b64Filename + "*" + this.b64Data;
        System.out.println("b64Piece: " + b64Piece);
        System.out.println("md5: " + this.md5);
        System.out.println("b64Filename: " + b64Filename);
        System.out.println("*");
        System.out.println("unCompressed b64Data: " + toB64(this.data));
        System.out.println("this.b64Data: " + this.b64Data);
        System.out.println("padding: " + padding);
        // ^^^^ Is the data that will be converted to pixels, and then to an image
        //System.out.println(finalOutData);
        WritableImage outImage = new WritableImage(sideLength, sideLength);
        PixelWriter writer = outImage.getPixelWriter();
        int dataCounter = 0;
        // x & y coords in picture
        for (int y = 0; y < sideLength; y++){
            for (int x=0; x<sideLength; x++){
                
                // WRITE THE IMAGE HERE
                writer.setColor(x, y, Color.web(key.B64ToColor(Character.toString(finalOutData.charAt(dataCounter))))); // Fix this spaghetti
                dataCounter+=1;
            }
        }
     this.written = outImage;
     return outImage;  
    }
    public void img2B64(){
        PixelReader reader = image.getPixelReader();
        int imgSide = (int)image.getHeight();
        System.out.println(imgSide);
        int totalPixels = (int)Math.pow(imgSide, 2);
        String b64Data = "";
        String[] rgbArray = new String[totalPixels];
        Color[] colorArray = new Color[totalPixels];
        int pixCount = 0;
        for (int y=0; y<imgSide; y++){
            for(int x=0; x<imgSide; x++){
                // Work on this shit
                Color curColor = reader.getColor(x, y);
                String d = key.colorToB64(curColor);
                System.out.println(pixCount + "/" + (imgSide*imgSide) + "/////////////////////////////////////////////////////////" + d);
                pixCount += 1;
            }
        }   
    }
    public String toB64(byte[] data) {
        // from byte[] to base64
        Base64.Encoder enc = Base64.getEncoder();
        return enc.encodeToString(data);
    }
    
    public String toB64(String data){
        // From string 2 base64
        Base64.Encoder enc = Base64.getEncoder();
        return enc.encodeToString(data.getBytes());
    }

    public void setPiece(int pieceNum) {
        // Setter for the piece object
        this.pieceNum = pieceNum;
    }

    
    
    public int compareTo(Object obj) {
        Chunky otherChunk = (Chunky) obj;
        int difference = this.pieceNum - otherChunk.pieceNum;
        // I think this is done now????
        return difference;
    }



    public byte[] fromB64Tobyte(String data) {
        // Converts b64 String to byte[]
        return Base64.getDecoder().decode(data);
    }

    public String md5ByteToString(byte[] hash){
        // Converts a byte hash to a String hash
        //Voodoo From stack overflow
        StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < hash.length; i++) {
                if ((0xff & hash[i]) < 0x10) {
                    hexString.append("0"
                            + Integer.toHexString((0xFF & hash[i])));
                } else {
                    hexString.append(Integer.toHexString(0xFF & hash[i]));
                }
            }
            this.md5 = hexString.toString();
        return hexString.toString();
    }
    
    public boolean checkMD5() {
            // Implement me:
            //      Check the decompressed datas md5 to the one stored in the image
            // return true if matches, false if else
        return true;
    }

    public byte[] getData() {
        // getter method for data maybe????
        return this.data;
    }

    public void decompress() {
        // decompresses data
    }

    public void img2File(String fileName){
        // Writes image to fileName
        File outFile = new File(fileName);  // Sets up the output file
        BufferedImage bImage = SwingFXUtils.fromFXImage(this.written, null);
        try{
        ImageIO.write(bImage, "png", outFile);
        }
        catch (java.io.IOException e){
            System.out.println(e.toString());
        }
    }

}
