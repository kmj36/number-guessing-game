import java.util.Random;
import java.util.Scanner;

class NumberGuessingGame {
    private int chance;
    private int attempts;
    private int guess;
    private int level;
    private final Scanner sc;
    private static final Random createGuessRandomNum = new Random();

    NumberGuessingGame(Scanner sc) {
        WelcomeMessage();
        this.sc = sc;
    }

    public static void WelcomeMessage() {
        String Message = """
                
                Welcome to the Number Guessing Game!
                I`m thinking of a number between 1 and 100.
                You have 5 chances to guess the correct number.
                """;
        System.out.print(Message);
    }

    public void GameInitialize() {
        String Mesage = """
                
                Please select the difficulty level:
                1. Easy (10 chances)
                2. Medium (5 chances)
                3. Hard (3 chances)
                
                Enter your choice:\s""";
        System.out.print(Mesage);

        level = sc.nextInt();
        while (level < 1 || level > 3) {
            System.out.print("Invalid difficulty value. Please enter a value between 1 and 3 (Easy to Hard).\n\n");
            System.out.print("Enter your choice: ");
            level = sc.nextInt();
        }

        switch (level) {
            case 1 -> {
                chance = 10;
            }
            case 2 -> {
                chance = 5;
            }
            case 3 -> {
                chance = 3;
            }
        }

        attempts = 0;
        guess = createGuessRandomNum.nextInt(100) + 1;
    }

    public static void StartMessage(int level) {
        String difficulty = "None";
        switch (level) {
            case 1 -> difficulty = "Easy";
            case 2 -> difficulty = "Medium";
            case 3 -> difficulty = "Hard";
        }
        String Message = """
                
                Great! You have selected the %s difficulty level.
                Let's start the game!
                """;
        System.out.printf(Message, difficulty);
    }

    public void start() {
        GameInitialize();
        StartMessage(level);

        while(attempts < chance) {
            attempts++;

            int entered;
            while(true) {
                System.out.print("Enter your guess: ");

                if(sc.hasNextInt()) {
                    entered = sc.nextInt();
                    break;
                } else {
                    System.out.println("Invalid Number. Try again.");
                    sc.next();
                }
            }

            if(entered == guess) {
                System.out.printf("Congratulations! You guessed the correct number in %d attempts.\n\n", attempts);
                break;
            } else
                System.out.printf("Incorrect! The number is %s than %d.\n\n", entered > guess ? "less" : "greater", entered);
        }

        if(attempts >= chance)
            System.out.printf("Game over! You've used all %d attempts. The number was %d.\n\n", chance, guess);
    }
}

public class NumberGuessingGameApp {
    static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        NumberGuessingGame g = new NumberGuessingGame(sc);
        boolean restart;

        // 게임 시작
        do {
            g.start();

            System.out.print("Would you like to play another round? Enter 'y' to restart or 'n' to quit: ");
            String wantRestart = sc.next();
            restart = wantRestart.equalsIgnoreCase("y") || wantRestart.equalsIgnoreCase("yes");

        } while(restart);
    }
}
