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
    System.out.println(max_time);
  }

  public static double askMaxTime () {
    Scanner scanner = new Scanner(System.in);
    System.out.println("Enter maximum turn time in seconds\n: ");
    while (!scanner.hasNextDouble()) {
      scanner.next();
    }
    double max_time = scanner.nextDouble();
    if (max_time <= 0) {
      System.out.println("Invalid input: Time in seconds must be greater than 0.");
      scanner.close();
      return askMaxTime();
    }
    scanner.close();
    return max_time;
  }
}
