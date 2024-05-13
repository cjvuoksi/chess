package ui;

public class GameUI extends UI {
    @Override
    protected String getHelp() {
        return """
                redraw (d): redraws the chess board
                leave (q): leaves the current game allowing another to join
                move (m): makes a move
                resign (r): resigns the current game
                lm (l): highlights legal moves
                help (h): prints this text
                """;
    }

    @Override
    protected void exit() throws SwitchException {
        throw new SwitchException(SwitchException.exceptionType.LEAVE);
    }

    @Override
    protected void menu() {
        print("GAME"); // ADD to context switcher
    }

    @Override
    protected void evaluate(String s) throws SwitchException {

    }
}
