package components;

import Java2D.MouseListener;
import scenes.LevelEditorScene;

public class ScaleTransformPivot extends Pivot{

    public ScaleTransformPivot(Sprite scaleSprite, LevelEditorScene editorScene) {
        super(scaleSprite, editorScene);

    }


    @Override
    public void editUpdate(float dt) {

        if (activeGameObject != null) {
            if (xAxisActive && !yAxisActive) {
                activeGameObject.transform.scale.x -= MouseListener.getWorldDx();
            } else if (yAxisActive) {
                activeGameObject.transform.scale.y -= MouseListener.getWorldDy();
            }
        }

        super.editUpdate(dt);
    }



}
