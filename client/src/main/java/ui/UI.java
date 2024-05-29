package ui;

import communication.ServerFacade;

import java.util.Scanner;

public abstract class UI {

    protected final ServerFacade server = new ServerFacade();

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

    public void printError(String message) {
        print(EscapeSequences.SET_TEXT_COLOR_RED + EscapeSequences.SET_TEXT_ITALIC + message + EscapeSequences.RESET_TEXT_ITALIC + EscapeSequences.RESET_TEXT_COLOR);
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
        clearScreen();
        menu();
        while (true) {
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
        if (command.equalsIgnoreCase("help") || command.equalsIgnoreCase("h")) {
            help();
            return true;
        }
        if (command.equalsIgnoreCase("clear")) {
            clearScreen();
            return true;
        }
        if (command.equals("q") || command.equals("quit")) {
            exit();
        }

        return false;
    }
}
