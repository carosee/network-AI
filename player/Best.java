package player;

public class Best {
	
	public static final int compw = 1;
	public static final int opponentw = -1;
	public static final int draw = 0;
	protected int score;
	protected Move move;
	
	/* Best object constructors */ 
	protected Best(Move move){
		this.move = move;
		this.score = 0;
	}
	
	protected Best(){
		this.score = 0;
		this.move = null;
	}
}
