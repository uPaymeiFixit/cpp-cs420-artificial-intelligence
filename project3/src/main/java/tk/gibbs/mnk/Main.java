package tk.gibbs.mnk;

import java.util.Scanner;
import java.util.ArrayList;

public class Main {

  private static ArrayList<String> moves = new ArrayList<String>(64);
  private static boolean first_play;

  public static void main (String[] args) {
    menu();

    new AI(10 * 1E9 - 3E6, first_play);

    TileState X = TileState.X;
    TileState O = TileState.O;
    TileState i = TileState.EMPTY;
    TileState[][] board = new TileState[][] {
      {i, i, i, i, i, i, i, i},
      {i, i, i, i, i, i, i, i},
      {i, i, i, i, i, i, i, i},
      {i, i, i, i, i, i, i, i},
      {i, i, i, i, i, i, i, i},
      {i, i, i, i, i, i, i, i},
      {i, i, i, i, i, i, i, i},
      {i, i, i, i, i, i, i, i}
    };

    BoardState state = new BoardState(board, X, 3, 3, 5, "");
    AI.outl(state.heuristic_value + "");

  }

  public static void menu () {
    double max_time = askMaxTime() * 1E9 - 3E6;
    first_play = askFirstPlayer();
    BoardState state = new BoardState();
    printMove(state);
    AI ai = new AI(max_time, first_play);

    if (first_play) {
      state = state.copyBoardWithMove(TileState.X, 4, 4);
      printMove(state);
    }

    while (!state.terminal_state && state.depth != 64) {
      AI.outl(BoardState.visited_states.size() + "");
      state = askMove(state);
      printMove(state);
      if (state.terminal_state || state.depth == 64) {
        break;
      }
      System.out.print('\n' + ANSIColors.WHITE + "Thinking...");
      state = ai.move(state);
      System.out.println("Done (" + (System.nanoTime() - AI.start_time) / 1E9 + " seconds)" + ANSIColors.RESET);
      AI.start_time = 0;
      printMove(state);
    }
    
  }

  public static void printMove (BoardState state) {
    String board_string = state.toString();

    // If nobody has played, add header to moves
    if (state.depth == 0) {
      if (first_play) {
        moves.add("  Player vs. Opponent");
      } else {
        moves.add("Opponent vs. Player");
      }
    } else {
      // If we're currently playing, get the last move and last player
      String last_move = (char)(state.last_move_x + 'a') + "" + (state.last_move_y + 1);
      if (first_play) {
        if (state.last_player == TileState.X) {
          moves.add("     " + moves.size() + ". " + last_move);
        } else {
          moves.set(moves.size() - 1, moves.get(moves.size() - 1) + " " + last_move);
        }
      } else {
        if (state.last_player == TileState.O) {
          moves.add("     " + moves.size() + ". " + last_move);
        } else {
          moves.set(moves.size() - 1, moves.get(moves.size() - 1) + " " + last_move);
        }
      }
      String last_player = state.last_player == TileState.X ? "  Player" : "Opponent";
      board_string += "                  \n" + last_player + "'s move is: " + last_move;
      if (state.terminal_state) {
        String color = state.last_player == TileState.X ? ANSIColors.GREEN_BOLD_BRIGHT : ANSIColors.RED_BOLD_BRIGHT;
        board_string += "                  \n" + color + last_player.toUpperCase() + " WINS!" + ANSIColors.RESET;
      } else if (state.depth == 64) {
        String color = ANSIColors.CYAN_BOLD_BRIGHT;
        board_string += "                  \n" + color + "DRAW!" + ANSIColors.RESET;
      }
    }

    String[] board = board_string.split("\n");
    System.out.println();

    // If moves column is shorter than board
    if (moves.size() < board.length) {
      // Output all of the moves
      for (int i = 0; i < moves.size(); i++) {
        System.out.println(board[i] + "      " + moves.get(i));
      }
      // Then finish off the board
      for (int i = moves.size(); i < board.length; i++) {
        System.out.println(board[i]);
      }
    } else {
      // If the moves column is longer than or equal to the board
      // Print entire board
      for (int i = 0; i < board.length; i++) {
        if (i == 10) {
          System.out.println(board[i] + " " + moves.get(i));
        } else {
          System.out.println(board[i] + "      " + moves.get(i));
        }
      }
      // Then finish printing the moves
      for (int i = board.length; i < moves.size(); i++) {
        System.out.println("                       " + moves.get(i));
      }
    }

  }

  public static BoardState askMove (BoardState board) {
    Scanner scanner = new Scanner(System.in);
    System.out.print("\nChoose Opponent's next move: ");
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
    
    // If the user did it backwards, forgive them
    if (input.charAt(0) > '0' && input.charAt(0) < '9') {
      x = Character.toLowerCase(input.charAt(0)) - '1';
      y = Character.toLowerCase(input.charAt(1)) - 'a';
    }

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
