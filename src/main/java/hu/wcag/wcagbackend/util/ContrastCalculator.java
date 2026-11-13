package hu.wcag.wcagbackend.util;

import java.awt.Color;

/*
* A WCAG szerinti színkontrasztarány kiszámítására szolgáló segédosztály.
*
* Felhasznált forrás: https://dev.to/alvaromontoro/building-your-own-color-contrast-checker-4j7o
*
* Az implementáció a forrásban bemutatott JavaScript-megoldás alapján,
* Java nyelvre átalakítva készült.
*/
public class ContrastCalculator {

    /*
     * A megadott szín relatív fényességének kiszámítása.
     *
     * A szín RGB értékeit linearizálja, majd a WCAG által meghatározott súlyozással kiszámítja a relatív fényességet.
     */
    private static double getRelativeLuminance(Color color){
        double r = linearize(color.getRed() / 255.0);
        double g = linearize(color.getGreen() / 255.0);
        double b = linearize(color.getBlue() / 255.0);

        return 0.2126 * r + 0.7152 * g + 0.0722 * b;
    }

    /*
     * Az RGB színcsatorna értékének linearizálása a relatív fényesség kiszámításához.
     *
     * A metódus a forrásban szereplő számítás mesterséges intelligencia
     * közreműködéssel történő különválasztásával készült.
     */
    private static double linearize(double channelVal) {
        if (channelVal <= 0.04045)
            return channelVal / 12.92;
        else
            return Math.pow((channelVal + 0.055) / 1.055, 2.4);
    }

    /*
     * Két szín közötti kontrasztarány kiszámítása.
     *
     * A relatív fényességük alapján meghatározza a világosabb
     * és sötétebb színt, majd kiszámítja a kontrasztarányt.
     */
    public static double calculateContrastRatio(Color color1, Color color2){
        double lum1 = getRelativeLuminance(color1);
        double lum2 = getRelativeLuminance(color2);

        double lighter = Math.max(lum1, lum2);
        double darker = Math.min(lum1, lum2);

        return (lighter + 0.05) / (darker + 0.05);
    }
}
