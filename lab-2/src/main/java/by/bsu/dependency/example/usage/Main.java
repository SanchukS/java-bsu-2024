package by.bsu.dependency.example.usage;

import by.bsu.dependency.context.AutoScanApplicationContext;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        AutoScanApplicationContext applicationContext =
                new AutoScanApplicationContext("by.bsu.dependency.example.usage");
        while (true) {
            System.out.println("Show next human? (1/0)");
            int answer = scanner.nextInt();
            switch (answer) {
                case 1:
                    System.out.println(applicationContext.getBean(Human.class));
                    break;
                case 0:
                    System.out.println("Goodbye");
                    break;
                default:
                    System.out.println("Unknown command");
            }
        }
    }
}
