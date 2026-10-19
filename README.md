# number_guessing_game

This is a practice project written in Java, based on the Number Guessing Game project from roadmap.sh.
https://roadmap.sh/projects/number-guessing-game

## Features

- Guess a randomly chosen number between 1 and 100
- Choose a difficulty level: Easy (10 chances), Medium (5 chances), Hard (3 chances)
- Get a higher/lower hint after every incorrect guess
- Request a proximity hint with `h` (costs 1 attempt)
- See how long each round took
- Keep the best record for each difficulty level (fewest attempts, then shortest time)
- Play multiple rounds without restarting the program
- Save records in JSON format

## Requirements

- Java 25 (or higher) is required.

## How to run

Clone the number_guessing_game repository and run the following commands:

```
git clone https://github.com/kmj36/number_guessing_game.git
cd number_guessing_game
```

Compile with the required library on the classpath, then run:

```
javac -cp "lib\*" -sourcepath app\src -d out app\src\NumberGuessingGameApp.java

java -cp "out;lib/*" NumberGuessingGameApp    # Windows
java -cp "out:lib/*" NumberGuessingGameApp    # macOS/Linux
```

## Usage

```
Please select the difficulty level:
1. Easy (10 chances)
2. Medium (5 chances)
3. Hard (3 chances)

Enter your choice: 2

[No record yet. Set the first one!]

Great! You have selected the Medium difficulty level.
Let's start the game!

Enter your guess (or 'h' for a hint, costs 1 attempt): 50
Incorrect! The number is less than 50.

Enter your guess (or 'h' for a hint, costs 1 attempt): h
Close. (within 15)

Enter your guess (or 'h' for a hint, costs 1 attempt): 40
Congratulations! You guessed the correct number in 3 attempts.
Time played: 12 seconds

Would you like to play another round? Enter 'y' to restart or 'n' to quit: n
```

## Data storage

Best records for each difficulty level are stored in `data/records.json`. The file is created automatically on first run if it does not already exist.