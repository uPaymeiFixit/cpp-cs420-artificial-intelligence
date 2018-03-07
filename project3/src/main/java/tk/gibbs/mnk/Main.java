package tk.gibbs.mnk;

import java.util.Scanner;
import java.util.ArrayList;

public class Main {

  private static ArrayList<String> moves = new ArrayList<String>(64);
  private static boolean first_play;

  public static void main (String[] args) {
    menu();
  }

  public static void menu () {
    double max_time = askMaxTime() * 10E8 - 3E6;
    first_play = askFirstPlayer();
    BoardState state = new BoardState();
    printMove(state);
    AI ai = new AI(max_time);

    if (first_play) {
      state = state.copyBoardWithMove(TileState.X, 4, 4);
      printMove(state);
    }

    while (true) {
      state = askMove(state);
      printMove(state);
      System.out.println(); AI.out("Thinking...");
      state = ai.move(state);
      AI.outl("Done (" + (System.nanoTime() - AI.start_time) + "µs)");
      printMove(state);
      // System.out.println("Finished in " + (System.nanoTime() - AI.start_time) + "µs");
    }
    
  }

  public static void printMove (BoardState state) {
    String board_string = state.toString();

    if (state.depth == 0) {
      if (first_play) {
        moves.add("Player vs. Opponent");
      } else {
        moves.add("Opponent vs. Player");
      }
    } else {
      String last_move = (char)(state.last_move_x + 'a') + "" + (state.last_move_y + 1);
      if (first_play) {
        if (state.last_player == TileState.X) {
          moves.add("   " + moves.size() + ". " + last_move);
        } else {
          moves.set(moves.size() - 1, moves.get(moves.size() - 1) + " " + last_move);
        }
      } else {
        if (state.last_player == TileState.O) {
          moves.add("   " + moves.size() + ". " + last_move);
        } else {
          moves.get(moves.size() - 1).concat(" " + last_move);
        }
      }
      String last_player = state.last_player == TileState.X ? "Player" : "Opponent";
      board_string += "                 \n" + last_player + "'s move is: " + last_move;
    }

    String[] board = board_string.split("\n");

    System.out.println();
    if (moves.size() < board.length) {
      for (int i = 0; i < moves.size(); i++) {
        System.out.println(board[i] + "    " + moves.get(i));
      }
      for (int i = moves.size(); i < board.length; i++) {
        System.out.println(board[i]);
      }
    } else {
      for (int i = 0; i < board.length; i++) {
        if (i == 8) {
          System.out.println(board[i] + moves.get(i));
        } else {
          System.out.println(board[i] + "    " + moves.get(i));
        }
      }
      for (int i = board.length; i < moves.size(); i++) {
        System.out.println("                      " + moves.get(i));
      }
    }

  }

  public static BoardState askMove (BoardState board) {
    Scanner scanner = new Scanner(System.in);
    System.out.print("\nChoose Opporent's next move: ");
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
    System.out.print("Are we making the first move? (y/n)\n: ");
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
