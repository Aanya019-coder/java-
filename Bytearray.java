import java.io.*;
class Geeks {
    public static void main(String[] args) {
       
        // Byte array for "AANYACHAUDHARY"
        byte[] byteArray = {65, 65, 78, 89, 65, 67, 72, 65, 85, 68, 72, 65, 82, 89};
        String str1 = new String(byteArray);
        System.out.println("String from byte array: " + str1);
        
        // Byte array for "AANYACHAUDHARY@GMAIL.COM"
        byte[] byteArra = {65, 65, 78, 89, 65, 67, 72, 65, 85, 68, 72, 65, 82, 89, 64, 71, 77, 65, 73, 76, 46, 67, 79, 77};
        String str2 = new String(byteArra);
        System.out.println("String from byte array: " + str2);
    }
}
