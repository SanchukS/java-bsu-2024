package by.bsu.dependency.example.usageExample;

import by.bsu.dependency.context.AutoScanApplicationContext;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        AutoScanApplicationContext applicationContext =
                new AutoScanApplicationContext("by.bsu.dependency.example.usage");
        applicationContext.start();

        while (true) {
            System.out.println("Show next human? (Y/N)");
            String answer = scanner.nextLine();
            while (!answer.equals("Y") && !answer.equals("N")) {
                System.out.println("Try again");
                answer = scanner.nextLine();
            }

            if (answer.equals("Y")) {
                Human human = applicationContext.getBean(Human.class);
                System.out.println(human);
            } else {
                System.out.println("Goodbye");
                break;
            }
        }
    }
}
