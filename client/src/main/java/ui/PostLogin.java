package ui;

public class PostLogin extends UI {
    String username;
    String authToken;

    public PostLogin(String username, String authToken) {
        this.username = username;
        this.authToken = authToken;
    }

    @Override
    protected String getHelp() {
        return "";
    }

    @Override
    protected void exit() throws SwitchException {
        throw new SwitchException(SwitchException.exceptionType.LOGOUT);
    }

    @Override
    protected void menu() {
        print(String.format("Welcome %s!", username));
    }

    @Override
    protected void evaluate(String s) throws SwitchException {

    }
}
