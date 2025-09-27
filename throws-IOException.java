import java.io.*;

class Demo {
    public static void main(String[] args) throws IOException {
        InputStreamReader in = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(in);

        int a, b, c;

        System.out.println("Enter first number:");
        a = Integer.parseInt(br.readLine());

        System.out.println("Enter second number:");
        b = Integer.parseInt(br.readLine());

        c = a + b;

        System.out.println("Sum = " + c);
    }
}
