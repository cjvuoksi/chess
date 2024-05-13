package ui;

public class GameUI extends UI {
    @Override
    protected String getHelp() {
        return "";
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
