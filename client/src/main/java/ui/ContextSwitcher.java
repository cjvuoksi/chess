package ui;

public class ContextSwitcher {
    private UI current = new PreLogin();
    private final UI pre = current;
    private UI post;
    private UI game;

    private String username;
    private String authToken;
    private Boolean exit = false;

    public void start() {
        Runtime r = Runtime.getRuntime();
        r.addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down");
            if (game != null) {
                try {
                    game.exit();
                } catch (SwitchException _) {

                }
            }
            if (post != null) {
                try {
                    post.exit();
                } catch (SwitchException _) {

                }
            }
            try {
                pre.exit();
            } catch (SwitchException _) {

            }
        }));
        while (!exit) {
            run();
        }
    }

    public void run() {
        try {
            current.run();
        } catch (SwitchException e) {
            if (e.getType() == SwitchException.exceptionType.EXIT) {
                exit = true;
            } else if (e.getType() == SwitchException.exceptionType.LOGIN) {
                authToken = e.getPayload()[0];
                username = e.getPayload()[1];
                post = new PostLogin(username, authToken);
                current = post;
            } else if (e.getType() == SwitchException.exceptionType.LOGOUT) {
                current = pre;
                post = null;
                authToken = null;
                username = null;
            } else if (e.getType() == SwitchException.exceptionType.PLAY || e.getType() == SwitchException.exceptionType.WATCH) {
                game = new GameUI();
                current = game;
            } else if (e.getType() == SwitchException.exceptionType.LEAVE) {
                game = null;
                current = post;
            }
        }
    }
}
