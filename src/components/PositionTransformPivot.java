package components;

import Java2D.MouseListener;
import scenes.LevelEditorScene;

public class PositionTransformPivot extends Pivot{


    public PositionTransformPivot(Sprite arrowSprite, LevelEditorScene editorScene) {
        super(arrowSprite, editorScene);

    }


    @Override
    public void editUpdate(float dt) {

        if (activeGameObject != null) {
            if (xAxisActive && !yAxisActive) {
                activeGameObject.transform.position.x -= MouseListener.getWorldDx();
            } else if (yAxisActive) {
                activeGameObject.transform.position.y -= MouseListener.getWorldDy();
            }
        }

        super.editUpdate(dt);
    }



}
