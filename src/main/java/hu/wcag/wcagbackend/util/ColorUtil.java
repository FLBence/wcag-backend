package hu.wcag.wcagbackend.util;

import java.awt.Color;
import java.util.Locale;

public class ColorUtil {

    public static Color parseColor(String colorStr) {
        if (colorStr == null || colorStr.trim().isEmpty() || colorStr.equals("transparent"))
            return Color.WHITE;

        colorStr = colorStr.trim().toLowerCase();

        try{
                if (colorStr.startsWith("rgb")) {
                    String color = colorStr.substring(colorStr.indexOf("(") + 1, colorStr.indexOf(")"));
                    String[] colors = color.split(",");
                    int r = Integer.parseInt(colors[0].trim());
                    int g = Integer.parseInt(colors[1].trim());
                    int b = Integer.parseInt(colors[2].trim());

                    return new Color(r, g, b);
                }

                if (colorStr.startsWith("#")){
                    return Color.decode(colorStr);
                }

                switch (colorStr){
                    case "white": return Color.WHITE;
                    case "black": return Color.BLACK;
                    case "red": return Color.RED;
                    case "blue": return Color.BLUE;
                    case "green": return Color.GREEN;
                    case "gray":
                    case "grey": return Color.GRAY;
                    default:
                        return Color.WHITE;
                }
            } catch (Exception e) {
            return Color.WHITE;
        }
    }
}
