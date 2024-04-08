package components;

import Java2D.Window;
import org.joml.Vector2f;
import org.joml.Vector3f;
import rendering.Draw;

public class GridLines extends Component{

    private float gridScale = 0.25f;

    @Override
    public void editUpdate(float dt) {
        Vector2f cameraPos = Window.getScene().camera().position;
        Vector2f projectionSize = Window.getScene().camera().getProjectionSize();

        float firstX = (((int) (cameraPos.x / gridScale) - 1) * gridScale);
        float firstY = (((int) (cameraPos.y / gridScale) - 1) * gridScale);

        int numVertLines = (int) ((projectionSize.x * Window.getScene().camera().getZoom()) / gridScale) + 2;
        int numHorzLines = (int) ((projectionSize.y * Window.getScene().camera().getZoom()) / gridScale) + 2;

        float height = (int) ((projectionSize.y * Window.getScene().camera().getZoom()) + gridScale * 2);
        float width = (int) ((projectionSize.x * Window.getScene().camera().getZoom()) + gridScale * 2);

        int maxLines = Math.max(numVertLines, numHorzLines);
        Vector3f color = new Vector3f(0.2f, 0.2f, 0.2f);
        for (int i = 0; i < maxLines; i++) {
            float x = firstX + (gridScale * i);
            float y = firstY + (gridScale * i);

            if (i < numVertLines){
                Draw.addLine2D(new Vector2f(x, firstY), new Vector2f(x, firstY + height), color);
            }

            if (i < numHorzLines){
                Draw.addLine2D(new Vector2f(firstX, y), new Vector2f(firstX + width, y), color);
            }
        }
    }



}
