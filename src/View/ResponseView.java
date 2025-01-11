package View;

import Model.Message;
import com.google.gson.Gson;

public class ResponseView {

    private static final Gson gson = new Gson();

    public static String render(Message message) {
        return gson.toJson(message);
    }

}
