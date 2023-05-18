package com.gerp.shared.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class URISeparatorUtil {

    public static List<String> separateURI(String uri) {
        List<String> list = new ArrayList<>();
        String[] split = uri.split("/");
        Collections.addAll(list, split);
        return list;
    }
}
