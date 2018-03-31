package com.javarush.task.task19.task1918;

/* 
Знакомство с тегами
*/

import java.io.*;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Solution {
    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String fileName = reader.readLine();
        while (!fileName.endsWith(".html")) fileName = reader.readLine();
        reader.close();

        reader = new BufferedReader(new FileReader(fileName));
        StringBuilder builder = new StringBuilder();
        while (reader.ready()) {
            builder.append(reader.readLine());
        }
        reader.close();
        String content = builder.toString();

        ChestLockList<Tag> data =  new ChestLockList<>();
        Pattern pattern =  Pattern.compile("<\\/?" + args[0] + "[^<]*>");
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            if (!matcher.group().startsWith("</")) data.add(new Tag(matcher.start(), matcher.end(), false));
            else data.add(new Tag(matcher.start(), matcher.end(), true));
        }

        for (int i = 0; i < data.size(); i++) {
            data.get(i).print(content);
        }
    }
}
