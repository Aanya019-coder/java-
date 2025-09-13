class Geeks {

// data members of the class

String name;

int id;

24

Geeks(String name, int id) {

this.name = name;

000

this.id = id;

}

}

class GFG

{ public static void main(String args)

{

// This would invoke the parameterized constructor

Geeks geek1 = new Geeks("Sweta", 68);

System.out.println("GeekName:" + geek1.name + " and Geekld: " + geek1.id);

}

}
