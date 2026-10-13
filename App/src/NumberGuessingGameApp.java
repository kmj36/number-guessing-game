class NumberGuessingGame {
    private final int count;
    private final int guess;
    private int tryingCount = 0;

    public enum Difficulty {
        EASY, MEDIUM, HARD;
    }

    NumberGuessingGame(Difficulty level) {
        switch (level) {
            case EASY -> {
                count = 10;
            }
            case MEDIUM -> {
                count = 5;
            }
            case HARD -> {
                count = 3;
            }
            default -> throw new IllegalArgumentException("Difficulty ");
        }

        guess = Math.random() % 100.0;
    }

    public static void WelcomeMessage() {
        System.out.println("Welcome to the Number Guessing Game!");
        System.out.println("I`m thinking of a number between 1 and 100.");
        System.out.println("You have 5 chances to guess the correct number.");
        System.out.println();
    }


}

public class NumberGuessingGameApp {
    static void main(String[] args) {
        NumberGuessingGame g = new NumberGuessingGame();

        // 게임 시작
        NumberGuessingGame.WelcomeMessage();
    }
}
