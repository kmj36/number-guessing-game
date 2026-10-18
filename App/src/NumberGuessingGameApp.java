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

class NumberGuessingGame {
    private int chance;
    private int attempts;
    private int guess;
    private int level;

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

    private final Scanner sc;
    private static final Random createGuessRandomNum = new Random();

    private static final Path saveDir = Path.of("data");
    private static final Path dataJSON = Path.of("records.json");
    Path file = Path.of(String.format("%s/%s", saveDir, dataJSON));

    private final ObjectMapper mapper = new ObjectMapper();
    public Map<Difficulty, GameRecord> records = new EnumMap<>(Difficulty.class);

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

        level = sc.nextInt();
        while (level < 1 || level > 3) {
            System.out.print("Invalid difficulty value. Please enter a value between 1 and 3 (Easy to Hard).\n\n");
            System.out.print("Enter your choice: ");
            level = sc.nextInt();
        }

        switch (level) {
            case 1 -> {
                chance = 10;
                best = records.get(Difficulty.EASY);
                levelMessage = "Easy";
            }
            case 2 -> {
                chance = 5;
                best = records.get(Difficulty.MEDIUM);
                levelMessage = "Medium";
            }
            case 3 -> {
                chance = 3;
                best = records.get(Difficulty.HARD);
                levelMessage = "Hard";
            }
        }

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

                if(best.attempts == 0 || best.attempts > attempts) {
                    records.put(Difficulty.EASY, new GameRecord(chance, attempts, guess, LocalDateTime.now()));
                    mapper.writerWithDefaultPrettyPrinter().writeValue(file, records);
                }
                break;
            } else
                System.out.printf("Incorrect! The number is %s than %d.\n\n", entered > guess ? "less" : "greater", entered);
        }

        if(attempts >= chance)
            System.out.printf("Game over! You've used all %d attempts. The number was %d.\n\n", chance, guess);
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
