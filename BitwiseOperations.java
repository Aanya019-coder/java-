public class BitwiseOperations {
    public static void main(String[] args) {
        int a = 8;   
        int b = 4;  

        int leftShift = a << 1;   
        System.out.println("Left Shift (a << 1): " + leftShift);

        int rightShift = a >> 1;  
        System.out.println("Right Shift (a >> 1): " + rightShift);

        int andResult = a & b;
        System.out.println("Bitwise AND (a & b): " + andResult);

        int orResult = a | b;
        System.out.println("Bitwise OR (a | b): " + orResult);
    }
}
