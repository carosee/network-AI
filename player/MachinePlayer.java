/* MachinePlayer.java */

package player;

/**
 *  An implementation of an automatic Network player.  Keeps track of moves
 *  made by both players.  Can select a move for itself.
 */


public class MachinePlayer extends Player {

	protected int color;
	protected int searchDepth;
	protected int oppcolor;
	protected GameBoard currentBoard;

	
  // Creates a machine player with the given color.  Color is either 0 (black)
  // or 1 (white).  (White has the first move.)
  public MachinePlayer(int color) {
    this.color = color;
    if (color == 0) {
      oppcolor = 1;
    } else {
      oppcolor = 0;
    }
    currentBoard = new GameBoard();
    searchDepth = 3; // sets default search depth to 3.
  }

  // Creates a machine player with the given color and search depth.  Color is
  // either 0 (black) or 1 (white).  (White has the first move.)
  public MachinePlayer(int color, int searchDepth) {
	    this.color = color;
      if (searchDepth < 1) {
        this.searchDepth = 1;
      } else {
        this.searchDepth = searchDepth;
      }
	    if (color == 0) {
	      oppcolor = 1;
	    } else {
	      oppcolor = 0;
	    }
	    currentBoard = new GameBoard();
  }

public static final boolean COMPUTER = true;
public static final boolean OPPONENT = false;

  protected int eval(GameBoard board) {

    // assigns a score to this gameboard.
    int score = 0;

    if (board.hasNetwork(this.color)) {
        return 97; //return max score
    }

    if (board.hasNetwork(this.oppcolor)) {
        return -97; //return min score
    }

    int connections = 0;
    boolean inGoal = false;
    boolean inGoal2 = false;
    for (int x = 0; x <= 7; x++) {
      for (int y = 0; y <= 7; y++) {
        if (board.hasPiece(x,y)) {
          Chip piece = board.getPiece(x,y);
          if (piece.color == this.color) {
            DList listConnections = board.findConnections(piece);
            connections += listConnections.length();
            if ((this.color == 0 && piece.y == 0) || (this.color == 1 && piece.x == 0)) {
              inGoal = true;
            }
            if ((this.color == 0 && piece.y == 7) || (this.color == 1 && piece.x == 7)) {
              inGoal2 = true;
            }
          }
        }
      }
    }
    score += connections;

    if (inGoal && !inGoal2) {
      score += 10;
    }

    else if (inGoal2 && !inGoal) {
      score += 10;
    }

    else if (inGoal && inGoal2) {
      // if there is at least one chip in each goal area, add ___ to the score.
      score += 30;
    }


    int oconnections = 0;
    boolean oinGoal = false;
    boolean oinGoal2 = false;
    for (int x = 0; x <= 7; x++) {
      for (int y = 0; y <= 7; y++) {
        if (board.hasPiece(x,y)) {
          Chip opiece = board.getPiece(x,y);
          if (opiece.color == this.oppcolor) {
            DList listConnections = board.findConnections(opiece);
            oconnections += listConnections.length();
            if ((this.oppcolor == 0 && opiece.y == 0) || (this.oppcolor == 1 && opiece.x == 0)) {
              oinGoal = true;
            }
            if ((this.oppcolor == 0 && opiece.y == 7) || (this.oppcolor == 1 && opiece.x == 7)) {
              oinGoal2 = true;
            }
          }
        }
      }
    }
    score -= oconnections;

    if (oinGoal && !oinGoal2) {
      score -= 10;
    }

    else if (oinGoal2 && !oinGoal) {
      score -= 10;
    }

    else if (oinGoal && oinGoal2) {
      // if there is at least one chip in each goal area, add ___ to the score.
      score -= 30;
    }

    return score;
  }


  public Best minimax(GameBoard board, boolean side, int depth, int alpha, int beta) {

    int myColor = color;

    if (side == OPPONENT) {
      myColor = oppcolor;
    }

    Best myBest = new Best();
    Best reply;

    if (board.hasNetwork(color) || board.hasNetwork(oppcolor) || depth <= 0) { // if this board has a win
      myBest.score = eval(board) + depth;
      return myBest;
    }

    if (side == COMPUTER) {
      myBest.score = alpha;
    } else {
      myBest.score = beta;
    }

    DList validMoves = board.calculateValidMoves(myColor);
    DListNode current = validMoves.head.next;

    myBest.move = (Move)current.item; //any legal move

    while (current != validMoves.head) { //looping through legal moves
      GameBoard newBoard = board.copy();
      Move m = (Move)current.item;
      newBoard.performMove(m, myColor);

      reply = minimax(newBoard, !side, depth-1, alpha, beta);

      //System.out.print(m.toString());
      //System.out.print(", ");
      //System.out.print(reply.score);
      //System.out.print(", ");
      //System.out.println(depth);

      if (side == COMPUTER && reply.score > myBest.score) {
        myBest.move = m;
        myBest.score = reply.score;
        alpha = reply.score;
      } else if (side == OPPONENT && reply.score < myBest.score){
        myBest.move = m;
        myBest.score = reply.score;
        beta = reply.score;
      }

      if (myBest.score >= 100) {
        return myBest;
      }

      if (alpha >= beta) {
        return myBest;
      }

      current = current.next;
    }
    return myBest;
  }




  // Returns a new move by "this" player.  Internally records the move (updates
  // the internal game board) as a move by "this" player.
  public Move chooseMove() {
    Best myBest = minimax(currentBoard, COMPUTER, searchDepth, -100, 100);
    currentBoard.performMove(myBest.move, color);
    return myBest.move;
  } 

  // If the Move m is legal, records the move as a move by the opponent
  // (updates the internal game board) and returns true.  If the move is
  // illegal, returns false without modifying the internal state of "this"
  // player.  This method allows your opponents to inform you of their moves.
  public boolean opponentMove(Move m) {
    if (currentBoard.isValidMove(m, oppcolor)){
      currentBoard.performMove(m, oppcolor);
      return true;
    } else {
      return false;
    }
  }

  // If the Move m is legal, records the move as a move by "this" player
  // (updates the internal game board) and returns true.  If the move is
  // illegal, returns false without modifying the internal state of "this"
  // player.  This method is used to help set up "Network problems" for your
  // player to solve.
  public boolean forceMove(Move m) {
    if (currentBoard.isValidMove(m, color)){
      currentBoard.performMove(m, color);
      return true;
    } else {
      return false;
    }

  }

  public static void main(String args[]) {
    /**System.out.println("Testing eval function");
    MachinePlayer mp = new MachinePlayer(0);
    GameBoard gameboard12 = new GameBoard();
    gameboard12.addPiece(new Chip(1),0,1);
    gameboard12.addPiece(new Chip(1),2,2);
    gameboard12.addPiece(new Chip(1),0,4);
    gameboard12.addPiece(new Chip(1),5,1);
    gameboard12.addPiece(new Chip(1),5,2);
    gameboard12.addPiece(new Chip(1),3,4);
    
    
    gameboard12.addPiece(new Chip(0),1,0);
    gameboard12.addPiece(new Chip(0),6,5);
    gameboard12.addPiece(new Chip(0),5,5);
    gameboard12.addPiece(new Chip(0),3,3);
    gameboard12.addPiece(new Chip(0),1,5);
    gameboard12.addPiece(new Chip(0),5,7);
    
    System.out.print("evals to: ");
    int result13 = mp.eval(gameboard12, 1);
    System.out.print(result13);
    System.out.println("");
    
    
    MachinePlayer newOne = new MachinePlayer(1); 
    newOne.forceMove(new Move(0, 2));
    newOne.forceMove(new Move(1, 2));
    newOne.forceMove(new Move(4, 2));
    //newOne.forceMove(new Move(0, 4));
    newOne.forceMove(new Move(1, 5));
    newOne.forceMove(new Move(4, 5));
    newOne.opponentMove(new Move(1, 1)); 
    newOne.opponentMove(new Move(2, 1));
    newOne.opponentMove(new Move(4, 1));
    newOne.opponentMove(new Move(5, 1));
    newOne.opponentMove(new Move(1, 6));
 
    DList lst = newOne.currentBoard.calculateValidMoves(1);
    System.out.println(lst.addmoveToString());
 
    Move m = newOne.chooseMove();
    System.out.println(newOne.eval(newOne.currentBoard));
    System.out.println(m.toString());
    
    /**    int x = m.x1;
    int y = m.y1;
    System.out.print(x);
    System.out.print(", ");
    System.out.println(y);
    
    newOne.forceMove(new Move(7, 2));
    System.out.print(newOne.eval(newOne.currentBoard, 1));
    **/
    
  }
  
}
