class thsdemo {

    int num = 3;

    public void set() {
        num++;
    }

    public void get() {
        System.out.println(num);
    }
}

class Main {
    public static void main(String[] args) {
        thsdemo ob1 = new thsdemo();
        thsdemo ob2 = new thsdemo();
        thsdemo ob3 = new thsdemo();

        ob1.set();
        ob1.set();
        ob2.set();
        ob3.set();
        ob3.set();
        ob3.set();

        ob1.get();
        ob2.get();
        ob3.get();
    }
}
