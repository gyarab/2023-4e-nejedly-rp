package util;

import org.joml.Vector2f;

public class GamesMath {

    public static void rotate(Vector2f vec, float angleDeg, Vector2f origin) {
        float x = vec.x - origin.x;
        float y = vec.y - origin.y;

        float cos = (float) Math.cos(Math.toRadians(angleDeg));
        float sin = (float) Math.sin(Math.toRadians(angleDeg));

        float xPrime = (x * cos) - (y * sin);
        float yPrime = (x * sin) + (y * cos);

        xPrime += origin.x;
        yPrime += origin.y;

        vec.x = xPrime;
        vec.y = yPrime;
    }



    public static boolean compare(float x, float y, float numOfDecimals) {
        return Math.abs(x - y) <= numOfDecimals * Math.max(1.0f, Math.max(Math.abs(x), Math.abs(y)));
    }

    public static boolean compare(Vector2f vec1, Vector2f vec2, float numOfDecimals) {
        return compare(vec1.x, vec2.x, numOfDecimals) && compare(vec1.y, vec2.y, numOfDecimals);
    }

    public static boolean compare(float x, float y) {
        return Math.abs(x - y) <= Float.MIN_VALUE * Math.max(1.0f, Math.max(Math.abs(x), Math.abs(y)));
    }

    public static boolean compare(Vector2f vec1, Vector2f vec2) {
        return compare(vec1.x, vec2.x) && compare(vec1.y, vec2.y);
    }
}
