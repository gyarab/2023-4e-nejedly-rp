package components;

import imgui.ImGui;
import org.joml.Vector2f;
import org.joml.Vector4f;
import rendering.Texture;

public class SpriteRenderer extends Component {

    private Vector4f color = new Vector4f(1, 1, 1, 1);
    private Sprite sprite = new Sprite();

    private transient Transform lastTransform;

    private transient boolean isOld = true;

    public SpriteRenderer() {

    }

//     public SpriteRenderer(Vector4f color) {
//        this.color = color;
//        this.sprite = new Sprite(null);
//        this.isOld = true;
//    }
//
//    public SpriteRenderer(Sprite sprite) {
//        this.sprite = sprite;
//        this.color = new Vector4f(1,1,1,1);
//        this.isOld = true;
//    }

    @Override
    public void start() {
        this.lastTransform = gameObject.transform.copy();
    }

    @Override
    public void update(float dt) {
        if (!this.lastTransform.equals(this.gameObject.transform)) {
            this.gameObject.transform.copy(this.lastTransform);
            isOld = true;
        }
    }

    @Override
    public void editUpdate(float dt) {
        if (!this.lastTransform.equals(this.gameObject.transform)) {
            this.gameObject.transform.copy(this.lastTransform);
            isOld = true;
        }
    }


    @Override
    public void imgui() {
        float[] imColor = {color.x, color.y, color.z, color.w};
        if (ImGui.colorPicker4("Color Picker: ", imColor)) {
            this.color.set(imColor[0], imColor[1], imColor[2], imColor[3]);
            this.isOld = true;
        }
    }

    public Vector4f getColor() {
        return this.color;
    }

    public Texture getTexture() {
        return sprite.getTexture();
    }

    public Vector2f[] getTexCords() {
        return sprite.getTexCoords();
    }

    public Sprite getSprite() {
        return sprite;
    }

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
        this.isOld = true;
    }

    public void setColor(Vector4f color) {
        if (!this.color.equals(color)) {
            this.isOld = true;
            this.color.set(color);
        }
    }

    public void setOld() {
        isOld = true;
    }

    public boolean isOld() {
        return this.isOld;
    }

    public void setNew() {
        this.isOld = false;
    }

    public void setTexture(Texture texture) {
        this.sprite.setTexture(texture);
    }

}
