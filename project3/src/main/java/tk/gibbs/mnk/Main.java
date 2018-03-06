package tk.gibbs.mnk;

import java.util.Scanner;

public class Main {
  public static void main (String[] args) {
    menu();
    System.out.println("Hello, World");
    BoardState board = new BoardState();
    System.out.println(board.toString());
    BoardState newBoard = board.copyWithMove(TileState.X, 4, 4);
    System.out.println(board != newBoard);
    System.out.println(newBoard.toString());
    System.out.println(newBoard.copyWithMove(TileState.O, 3, 3).toString());
  }

  public static void menu () {
    double max_time = askMaxTime();
    boolean first_play = askFirstPlayer();
    System.out.println(max_time);
    System.out.println(first_play);
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
