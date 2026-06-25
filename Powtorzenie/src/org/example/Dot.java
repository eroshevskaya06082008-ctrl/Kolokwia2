package org.example;

import java.awt.*;

public record Dot(double x, double y, double r, Color color) {

    public static String toMessage(double x, double y, double r, Color color) {
        return toMessage(x, y, r, color, false);
    }


    public static String toMessage(double x, double y, double r, Color color, boolean isHistory) {
        return (isHistory ? "HIST:" : "NEW:") + x + "," + y + "," + r + "," +
                color.getRed() + "," + color.getGreen() + "," + color.getBlue();
    }

    public static Dot fromMessage(String message) {
        // Если есть префикс, отрезаем его
        String data = message.contains(":") ? message.split(":")[1] : message;

        String[] parts = data.split(",");
        double x = Double.parseDouble(parts[0]);
        double y = Double.parseDouble(parts[1]);
        double r = Double.parseDouble(parts[2]);
        int red = Integer.parseInt(parts[3]);
        int green = Integer.parseInt(parts[4]);
        int blue = Integer.parseInt(parts[5]);

        return new Dot(x, y, r, new Color(red, green, blue));
    }
}
