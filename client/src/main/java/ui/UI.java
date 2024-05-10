package ui;

import java.util.Scanner;

public abstract class UI {

    protected ServerFacade server = new ServerFacade();

    public void help() {
        print(getHelp());
    }

    protected abstract String getHelp();

    protected abstract void exit() throws SwitchException;

    protected abstract void menu();

    //Graphics
    public void print(String message) {
        System.out.println(message);
    }

    public void clearScreen() {
        print(EscapeSequences.ERASE_SCREEN);
        print(EscapeSequences.moveCursorToLocation(0, 0));
    }

    public String promptInput(String prompt) {
        print(prompt);
        return getCommandLineInput();
    }

    public String getCommandLineInput() {
        Scanner scanner = new Scanner(System.in);
        String readLine = scanner.nextLine();
        return readLine.isEmpty() ? null : readLine;
    }

    public void run() throws SwitchException {
        while (true) {
            menu();
            String input = getCommandLineInput();
            if (evaluateCommonCommand(input)) {
                continue;
            }
            evaluate(input);
        }
    }

    protected abstract void evaluate(String s) throws SwitchException;

    protected boolean evaluateCommonCommand(String command) throws SwitchException {
        if (command == null) {
            return true;
        }
        if (command.equalsIgnoreCase("help")) {
            help();
            return true;
        }
        if (command.equalsIgnoreCase("clear")) {
            clearScreen();
            return true;
        }
        if (command.equalsIgnoreCase("q") || command.equals("quit")) {
            exit();
        }

        return false;
    }
}