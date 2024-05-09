import chess.ChessGame;
import chess.ChessPosition;
import com.google.gson.GsonBuilder;


public class Main {
    public static void main(String[] args) {
        var serializer = new GsonBuilder().enableComplexMapKeySerialization().create();

        String json = serializer.toJson(new ChessPosition(1, 1));

        System.out.println(json);

        json = serializer.toJson(new ChessGame());

        System.out.println(json);

        ChessGame game = serializer.fromJson(json, ChessGame.class);
    }
}