package ui;

import request.RegisterRequest;
import request.UserRequest;
import response.LoginResponse;

public class PreLogin extends UI {
    @Override
    protected String getHelp() {
        return """
                register (r): register as a new user
                login (l): login with your credentials
                help (h): print this message
                quit (q): exit
                """;
    }

    @Override
    protected void exit() throws SwitchException {
        throw new SwitchException(SwitchException.exceptionType.EXIT);
    }

    private void login() throws SwitchException {
        String username = promptInput("Enter username or (q) to quit: ");
        if (username == null || username.isEmpty()) {
            login();
        } else if (username.equals("q")) {
            return;
        }
        String password = promptInput("Enter password: ");

        LoginResponse response = server.login(new UserRequest(username, password));

        signIn(response);
    }

    @Override
    protected void menu() {
        print("Welcome to the Pre-Login UI");
    }

    @Override
    protected void evaluate(String s) throws SwitchException {
        if (s.equalsIgnoreCase("login") || s.equals("l")) {
            login();
        } else if (s.equalsIgnoreCase("register") || s.equals("r")) {
            register();
        } else {
            print(String.format("Command %s not recognized", s));
            help();
        }
    }

    private void register() throws SwitchException {

        String username = promptInput("Username: ");
        if ("q".equals(username)) {
            print("Invalid username");
            register();
            return;
        }
        String password = promptInput("Password: ");
        String email = promptInput("Email: ");
        print("");

        LoginResponse response = server.register(new RegisterRequest(username, password, email));

        signIn(response);
    }

    private void signIn(LoginResponse response) throws SwitchException {
        if (response.getMessage() != null) {
            print(response.getMessage());
        } else {
            throw new SwitchException(SwitchException.exceptionType.LOGIN, response.getAuthToken(), response.getUsername());
        }
    }
}
