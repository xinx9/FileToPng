package ehacksapp;

import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.FileInputStream;
import javafx.scene.paint.Color;

public class Key implements java.io.Serializable {
    public static final String[] B64 = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U",
        "V", "W", "X", "Y", "Z", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u",
        "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "+", "/", "=", "*"};
    public String[] ColorArray = new String[66];
    public String[][] RGBArray = new String[2][66];

    public Key() {
        GenerateKey();      //generates our HexColor
        FillColorArray();   //fills our array with our HexColors
        FillRGBArray();     //matches our Base64 characters with a randomly generated HexColor
    }

    public Key(String fileName) {
        // Open filename with an Objectinputstream object\
        // Create a new Key instance from the object input stream
        // Use the objects public instance variables to set this objs
        // instance variables, and you will essentially have a copy of 
        // the dictionary
        RGBArray = datFileReader();
    }

    public String GenerateKey() {  // Change this to use a seed to gen the colors maybe?

        String[] colors = new String[16];
        colors = "0123456789ABCDEF".split(""); //HexCode
        String code = "#";                     //this holds HexCode
        for (int i = 0; i < 6; i++) {
            double ind = Math.random() * 15;   //generates random hexcode 
            int index = (int) Math.round(ind);
            code += colors[index];             //fills our String code with our HexColor
        }
        return code;
    }

    public String[] FillColorArray() {
        for (int i = 0; i < 66; i++) {
            String gencode = GenerateKey(); //generates our HexColor
            ColorArray[i] = gencode;        //fills our ColorArray with with our generated code
            // makes sure no 2 hexcodes are the same
            for (int j = 0; j < i; j++) {
                if (ColorArray[i].equals(ColorArray[j])) {  //if our hexcodes equals eachother
                    ColorArray[i] = gencode;                //we generate a new HexColor    
                }
            }
        }
        return ColorArray; //we are returning a single String Array
    }

    public String[][] FillRGBArray() {
        for (int k = 0; k < 2; k++) {
            for (int l = 0; l < 66; l++) {
                if (k == 0) {
                    RGBArray[k][l] = B64[l]; //when the k index equals 0 we set the index 0 to equal B64(our base64 array)
                } else {
                    RGBArray[k][l] = ColorArray[l]; //when k isn't equal to 0 we set the index equal to ColorArray
                }
            }
        }
        return RGBArray; //we are returning a multidimensional array
    }

    public String cHexToB64(String color) {
        int x = 0;
        for (int i = 0; i < 66; i++) {
            //if our colorChecking variable equals a color in our rgb array then we set x 
            // equal to the index where it matched in the rgb array
            if (color.equals(RGBArray[1][i])) {
                x = i;
            }
        }
        return RGBArray[0][x]; //we return the corresponding index position that was matched 
    }
    
    public String colorToB64(Color color){
        String found = "";
        for (int i = 0; i < 66; i++) {
            Color toColor = Color.web(RGBArray[1][i]); //creates our checking variable
            if(color.equals(toColor)){//if our Color object color equals our checking variable
                found = RGBArray[0][i];//we fill our String variable with it's HexColor
                System.out.println("Found");//and then we return that we found it
                break;
            }
            else{
                found = null; //if nothing is "found" then we return a nulled value
            }
        }
        
        return found; //we return the Color object (this will return a HexColor if we find anything, otherwise we return a nulled value)
    }

    
    public String B64ToColor(String color) {
        int x = 0;
        for (int i = 0; i < 66; i++) {
            //if our colorChecking variable equals a color in our rgb array then we set x 
            // equal to the index where it matched in the rgb array
            if (color.equals(RGBArray[0][i])) {
                x = i;
                break;
            }

        }
        return RGBArray[1][x]; //we return the corresponding index position that was matched 
    }
    
    public void DatFileWriter()
    {
        try(ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("Key.dat"));) //creates a key.dat file
        {
                out.writeObject(this); //writes the key object to the key.dat file
        }
        catch(java.io.IOException ex)
        {
            System.out.println(ex.toString()); //error checking
        }
    }
    
    
    
    public String[][] datFileReader()
    {
        String[][] outArray = null;
        try(ObjectInputStream in = new ObjectInputStream(new FileInputStream("Key.dat"))) //opens key.dat file
        {

            try{
            Key o = (Key)in.readObject(); //reads in the object
            outArray = o.RGBArray;        //sets a variable equal to the objects RGBArray
            
            
            }
            catch(java.lang.ClassNotFoundException e){
                System.out.println(e.toString());
            }
        }
        catch(java.io.IOException ex){
            System.out.println(ex.toString());
        }
        return outArray; //we are returning a multidimensional array
    }
}
