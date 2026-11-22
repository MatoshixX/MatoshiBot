package telbot.MatoshiBot.filter;

import java.util.ArrayList;
import java.util.List;

public class UserFilter {
    private static final ArrayList<String> whiteList = new ArrayList<>(List.of("Matoshi", "Matoshix"));
    public static boolean isAllow(String userName) {
        return userName != null && whiteList.contains(userName);
    }
}
