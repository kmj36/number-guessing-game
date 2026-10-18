import com.fasterxml.jackson.annotation.JsonProperty;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.time.Duration;
import java.util.Random;
import java.util.Scanner;

class NumberGuessingGame {
    private int chance;
    private int attempts;
    private int guess;
    private Difficulty level;

    private GameRecord best;
    private String levelMessage;

    public enum Difficulty {
        @JsonProperty("Easy") EASY,
        @JsonProperty("Medium") MEDIUM,
        @JsonProperty("Hard") HARD
    }

    public record GameRecord (
            int chance,
            int attempts,
            int guess,
            LocalDateTime timestamp
    ) {}

    private long startTime;
    private long endTime;

    private final Scanner sc;
    private static final Random createGuessRandomNum = new Random();

    private static final Path saveDir = Path.of("data");
    private static final Path dataJSON = Path.of("records.json");
    Path file = Path.of(String.format("%s/%s", saveDir, dataJSON));

    private final ObjectMapper mapper = new ObjectMapper();
    public Map<Difficulty, GameRecord> records = new EnumMap<>(Difficulty.class);

    private static final int VERY_CLOSE = 5;
    private static final int CLOSE = 15;

    NumberGuessingGame(Scanner sc) throws JacksonException, IOException {
        WelcomeMessage();
        this.sc = sc;

        // 디렉터리가 없으면 생성
        if (!Files.exists(saveDir)) {
            Files.createDirectories(saveDir);
        }

        // 파일이 없으면 빈 데이터로 초기화 후 파일까지 생성
        if (!Files.exists(file)) {
            records.put(Difficulty.EASY, new GameRecord(10, 0, 0, null));
            records.put(Difficulty.MEDIUM, new GameRecord(5, 0, 0, null));
            records.put(Difficulty.HARD, new GameRecord(3, 0, 0, null));

            mapper.writerWithDefaultPrettyPrinter().writeValue(file, records);
            return;
        }

        String fileData = Files.readString(file);
        records = mapper.readValue(fileData, new TypeReference<Map<Difficulty, GameRecord>>() {});
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

        int setLevel = sc.nextInt();
        while (setLevel < 1 || setLevel > 3) {
            System.out.printf("Invalid difficulty value. Please enter a value between 1 and 3 (Easy to Hard).%n%n");
            System.out.print("Enter your choice: ");
            setLevel = sc.nextInt();
        }

        switch (setLevel) {
            case 1 -> {
                chance = 10;
                level = Difficulty.EASY;
                levelMessage = "Easy";
            }
            case 2 -> {
                chance = 5;
                level = Difficulty.MEDIUM;
                levelMessage = "Medium";
            }
            case 3 -> {
                chance = 3;
                level = Difficulty.HARD;
                levelMessage = "Hard";
            }
        }

        best = records.get(level);
        attempts = 0;
        guess = createGuessRandomNum.nextInt(100) + 1;
    }

    public void StartMessage() {
        String recordLine = best.attempts() == 0
                ? "No record yet. Set the first one!"
                : String.format("Best record: %d attempts on %s",
                best.attempts(),
                best.timestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        String Message = """
                
                [%s]
                
                Great! You have selected the %s difficulty level.
                Let's start the game!
                
                """;
        System.out.printf(Message, recordLine, levelMessage);
    }

    public void start() {
        GameInitialize();
        StartMessage();

        String entered = "";
        int inputNum = -1;

        startTime = System.nanoTime();
        while(attempts < chance) {
            attempts++;

            System.out.print("Enter your guess (or 'h' for a hint, costs 1 attempt): ");
            entered = sc.next();

            if(entered.equalsIgnoreCase("h")) {
                int diff = Math.abs(guess - inputNum);

                if (diff <= VERY_CLOSE)
                    System.out.printf("Very Close! (within %d)%n%n", VERY_CLOSE);
                else if (diff <= CLOSE)
                    System.out.printf("Close. (within %d)%n%n", CLOSE);
                else
                    System.out.printf("Far away. (more than %d off)%n%n", CLOSE);

                continue;
            }

            inputNum = Integer.parseInt(entered);

            if(inputNum == guess)
                break;

            System.out.printf("Incorrect! The number is %s than %d.\n\n", inputNum > guess ? "less" : "greater", inputNum);
        }
        endTime = System.nanoTime();

        if(inputNum == guess) {
            System.out.printf("Congratulations! You guessed the correct number in %d attempts.%n", attempts);

            if (best.attempts == 0 || best.attempts > attempts) {
                records.put(level, new GameRecord(chance, attempts, guess, LocalDateTime.now()));
                mapper.writerWithDefaultPrettyPrinter().writeValue(file, records);
            }
        }
        else
            System.out.printf("Game over! You've used all %d attempts. The number was %d.%n", chance, guess);
        
        System.out.printf("Time played: %d seconds%n%n", Duration.ofNanos(endTime - startTime).toSeconds());
    }
}

public class NumberGuessingGameApp {
    static void main(String[] args) throws IOException {
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
