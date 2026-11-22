package telbot.MatoshiBot.filter;

import java.util.ArrayList;
import java.util.List;

public class UserFilter {
    private static final ArrayList<String> whiteList = (ArrayList<String>) List.of("");

    public static boolean isAllow(String userName) {
        return whiteList.contains(userName);
    }
}
