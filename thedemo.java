class Thsdemo
{

int a, b;

public void get (int x, int y)
{
a=x;
b=y;
    
}

public void put()

{
    System.out.println("a="+a);
  System.out.println("b= "+b);
}
}
 class Main{

public static void main(String[] args)

{

Thsdemo ob1 = new Thsdemo();

ob1.get(10,20);

ob1.put();

}
}
