
import java.util.*;

public class Player {

    private int[] weights = {4, 4, 4, 4,
            4, 3, 3, 3,
            3, 2, 2, 4,
            4, 2, 1, 3,
            3, 1, 2, 4,
            4, 2, 2, 3,
            3, 3, 3, 4,
            4, 4, 4, 4};

    public int alphaInitValue = 99999999;
    public int betaInitValue = -99999999;

    private class GameStateNDepth{
        int depth;
        GameState state;

        public GameStateNDepth(GameState state, int depth){
            this.state = state;
            this.depth = depth;
        }
        public GameState getState() {
            return state;
        }

        public int getDepth() {
            return depth;
        }
    }


    Map<GameStateNDepth, Integer> gameStateMap = new HashMap<>();

    public int myself;
    String temp;
    int hit;//hitt en

    public GameState play(final GameState pState, final Deadline pDue) {
        Vector<GameState> lNextStates = new Vector<GameState>();
        pState.findPossibleMoves(lNextStates);

        if (lNextStates.size() == 0) {
            // Must play "pass" move if there are no other moves possible.
            return new GameState(pState, new Move());
        }
        myself = pState.getNextPlayer();

        GameState nexMove = new GameState();
        GameState myState;
        int maximum_score = -99999999;
        int temp;

        int depth = 0;

        int [] scores = new int [lNextStates.size()];
        while(depth < 12){
            for (int i = 0; i < lNextStates.size(); i++) {
                myState = lNextStates.elementAt(i);
                temp = alphabeta(myState, depth, betaInitValue, alphaInitValue, Constants.CELL_WHITE, pDue);
                scores[i] += temp;
            }
            depth++;
        }


        int max = Integer.MIN_VALUE;
        for(int i = 0; i < lNextStates.size(); i++){
            if(scores[i] > max){
                max = scores[i];
                nexMove = lNextStates.elementAt(i);
            }
        }


        return nexMove;

    }

    public int evalFunc(GameState stateIn, int player) {
        int resultFinal = 0;
        if (stateIn.isEOG()) {
            if ((player == Constants.CELL_RED && stateIn.isRedWin()) || (player == Constants.CELL_WHITE && stateIn.isWhiteWin())) {
                resultFinal = 1000;
            } else if ((player == Constants.CELL_RED && stateIn.isWhiteWin()) || (player == Constants.CELL_WHITE && stateIn.isRedWin())) {
                resultFinal = -1000;
            }
            return resultFinal;
        } else {

            int whiteCounter = 0;
            int redCounter = 0;

            int i = 0;
            while (i < 32) {
                if (stateIn.get(i) == Constants.CELL_EMPTY) {
                    //do nothing
                } else {
                    switch (stateIn.get(i)) {
                        case 1:
                            redCounter += weights[i];
                            break;
                        case 5:
                            redCounter += weights[i] + 4;
                            break;
                        case 2:
                            whiteCounter += weights[i];
                            break;
                        case 6:
                            whiteCounter += weights[i] + 4;
                    }
                }
                i++;
            }
            resultFinal = 0;
            if (myself != 1) {
                resultFinal = whiteCounter - redCounter;
            } else {
                resultFinal = redCounter - whiteCounter;
            }
            return resultFinal;
        }
    }

    public int alphabeta(GameState gameState, int depth, int alpha, int beta, int player, Deadline pdue) {
        Vector<GameState> nextStates = new Vector<GameState>();
        gameState.findPossibleMoves(nextStates);
        int v;
        if (depth == 0 || nextStates.isEmpty() || pdue.timeUntil() < 100) {
            v = evalFunc(gameState, myself);
        } else if (player == Constants.CELL_RED) {
            v = betaInitValue;
            for (GameState nextState : nextStates) {
                v = Math.max(v, alphabeta(nextState, depth - 1, alpha, beta, Constants.CELL_WHITE, pdue));

                alpha = Math.max(alpha, v);
                if (beta <= alpha) {
                    break;
                }
            }
        } else {
            v = alphaInitValue;
            for (GameState nextState : nextStates) {

                    v = Math.min(v, (alphabeta(nextState, depth - 1, alpha, beta, Constants.CELL_RED, pdue)));

                beta = Math.min(beta, v);
                if (beta <= alpha) {
                    break;
                }
            }
        }
        return v;
    }
}
