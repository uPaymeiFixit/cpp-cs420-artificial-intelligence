package tk.gibbs.mnk;

import java.util.Scanner;

public class Main {
  public static void main (String[] args) {
    menu();
  }

  public static void menu () {
    double max_time = 30.0;//askMaxTime();
    boolean first_play = true;//askFirstPlayer();

    BoardState state = new BoardState();
    System.out.println(state); // Print blank board
    
    AI ai = new AI(max_time);

    if (first_play) {
      state = state.copyBoardWithMove(TileState.X, 4, 4);
      System.out.println(state);
    }

    while (true) {
      state = askMove(state);
      System.out.println(state);
      System.out.println("Thinking...");
      state = ai.move(state);
      System.out.println(state);
    }
    
  }

  public static BoardState askMove (BoardState board) {
    Scanner scanner = new Scanner(System.in);
    System.out.print("Choose player's next move:\n: ");
    while (!scanner.hasNextLine()) {
      scanner.next();
    }
    String input = scanner.nextLine();
    if (input.length() < 2 || input.length() > 2) {
      System.out.println("Invalid input: Move must be only two characters.");
      return askMove(board);
    }
    int x = Character.toLowerCase(input.charAt(1)) - '1';
    int y = Character.toLowerCase(input.charAt(0)) - 'a';
    try {
      return board.copyBoardWithMove(TileState.O, y, x);
    } catch (Error error) {
      System.out.println(error.getMessage());
      return askMove(board);
    }
  }


  public static double askMaxTime () {
    Scanner scanner = new Scanner(System.in);
    System.out.print("Enter maximum turn time in seconds\n: ");
    while (!scanner.hasNextDouble()) {
      scanner.next();
    }
    double max_time = scanner.nextDouble();
    if (max_time <= 0) {
      System.out.println("Invalid input: Time in seconds must be greater than 0.");
      return askMaxTime();
    }
    return max_time;
  }

  public static boolean askFirstPlayer () {
    Scanner scanner = new Scanner(System.in);
    System.out.println("Are we making the first move? (y/n)\n: ");
    while (!scanner.hasNextLine()) {
      scanner.next();
    }
    char input = Character.toLowerCase(scanner.nextLine().charAt(0));
    if (input == 'y' || input == 'n') {
      return input == 'y';
    }
    System.out.println("Invalid input: Please enter 'yes' or 'no'");
    return askFirstPlayer();
  }
}
