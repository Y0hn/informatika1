import java.util.Scanner;

public class Hello 
{
    public static void main(String[] args) 
    {
        System.out.println("Hello Wolrd!");

        if (0 < args.length)
        {
            Greeter.Greeting();
        }
    }
}
public class Ahoj 
{
    public static void main(String[] args) 
    {
        System.out.println("Ahoj Svet!");
    }
}


public class Greeter 
{
    public static void Greeting()
    {
        Scanner scaner = new Scanner(System.in);

        System.out.print("Enter your name: ");

        String name = scaner.nextLine();

        System.out.println("Greetings " + name + ", nice to meet u!");

        scaner.close();
    }
}