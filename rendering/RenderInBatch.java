package rendering;

import Java2D.GameObject;
import Java2D.Window;
import components.SpriteRenderer;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class RenderInBatch implements Comparable<RenderInBatch>{
    //Vertex
    //
    //Pos               Color                           tex coords          tex id      obj id
    //float, float,     float, float, float, float      float, float        float       (float)int

    private final int POS_SIZE = 2;
    private final int COLOR_SIZE = 4;
    private final int TEX_COORDS_SIZE = 2;
    private final int TEX_ID_SIZE = 1;
    private final int OBJECT_ID_SIZE = 1;

    private final int POS_OFFSET = 0;
    private final int COLOR_OFFSET = POS_OFFSET + POS_SIZE * Float.BYTES;
    private final int TEX_COORDS_OFFSET = COLOR_OFFSET + COLOR_SIZE * Float.BYTES;
    private final int TEX_ID_OFFSET = TEX_COORDS_OFFSET + TEX_COORDS_SIZE * Float.BYTES;
    private final int OBJECT_ID_OFFSET = TEX_ID_OFFSET + TEX_ID_SIZE * Float.BYTES;
    private final int VERTEX_SIZE = 10;
    private final int VERTEX_SIZE_BYTES = VERTEX_SIZE * Float.BYTES;

    private SpriteRenderer[] sprites;
    private int numSprites;
    private boolean hasRoom;
    private float[] vertices;
    private int[] texSlots = {0, 1, 2, 3, 4, 5, 6, 7};

    private List<Texture> textures;
    private int vaoID, vboID;
    private int maxBatchSize;
    private int zIndex;

    private Renderer renderer;

    public RenderInBatch(int maxBatchSize, int zIndex, Renderer renderer) {
        this.renderer = renderer;
        this.zIndex =zIndex;
        this.maxBatchSize = maxBatchSize;
        this.sprites = new SpriteRenderer[maxBatchSize];

        vertices = new float[maxBatchSize * 4 * VERTEX_SIZE];

        this.numSprites = 0;
        this.hasRoom = true;
        this.textures = new ArrayList<>();
    }

    public void start() {
        // gen VAO - vertex array object
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        // gen VBO - vertex buffer object
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertices.length * Float.BYTES, GL_DYNAMIC_DRAW);

        // gen EBO - element buffer object
        int eboID = glGenBuffers();
        int[] elements = generateElements();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elements, GL_STATIC_DRAW);

        glVertexAttribPointer(0, POS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, POS_OFFSET);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, COLOR_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, COLOR_OFFSET);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(2, TEX_COORDS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEX_COORDS_OFFSET);
        glEnableVertexAttribArray(2);

        glVertexAttribPointer(3, TEX_ID_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEX_ID_OFFSET);
        glEnableVertexAttribArray(3);

        glVertexAttribPointer(4, OBJECT_ID_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, OBJECT_ID_OFFSET);
        glEnableVertexAttribArray(4);
    }

    public void addSprite(SpriteRenderer sprite) {

        int index = this.numSprites;
        this.sprites[index] = sprite;
        this.numSprites++;

        if (sprite.getTexture() != null){
            if (!textures.contains(sprite.getTexture())) {
                textures.add(sprite.getTexture());
            }
        }

        loadVertexProperties(index);

        if (numSprites >= this.maxBatchSize) {
            this.hasRoom = false;
        }
    }

    public void render() {
        boolean rebufferData = false;
        for (int i = 0; i < numSprites; i ++) {
            SpriteRenderer spriteRenderer = sprites[i];
            if (spriteRenderer.isOld()) {
                loadVertexProperties(i);
                spriteRenderer.setNew();
                rebufferData = true;
            }

            if (spriteRenderer.gameObject.transform.zIndex != this.zIndex) {
                destroyIfExists(spriteRenderer.gameObject);
                renderer.add(spriteRenderer.gameObject);
                i--;
            }
        }
        if (rebufferData) {
            glBindBuffer(GL_ARRAY_BUFFER, vboID);
            glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);
        }

        Shader shader = Renderer.getBoundShader();
        shader.uploadMat4f("uProjection", Window.getScene().camera().getProjectionMat());
        shader.uploadMat4f("uView", Window.getScene().camera().getViewMat());
        for (int i = 0; i < textures.size(); i++){
            glActiveTexture(GL_TEXTURE0 + i + 1);
            textures.get(i).bind();
        }
        shader.uploadIntArray("uTextures", texSlots);

        glBindVertexArray(vaoID);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, this.numSprites * 6, GL_UNSIGNED_INT, 0);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);

        for (int i = 0; i < textures.size(); i++){
            textures.get(i).unbind();
        }
        shader.detach();

    }

    public boolean destroyIfExists(GameObject gameObject) {
        SpriteRenderer sprite = gameObject.getComponent(SpriteRenderer.class);
        for (int i = 0; i < numSprites; i++) {
            if (sprites[i] == sprite) {
                for (int j = i; j < numSprites - 1; j++) {
                    sprites[j] = sprites[j + 1];
                    sprites[j].setOld();
                }
                numSprites--;
                return true;
            }
        }

        return false;
    }

    private void loadVertexProperties(int index) {
        SpriteRenderer sprite = this.sprites[index];

        int offset = index * 4 * VERTEX_SIZE;

        Vector4f color = sprite.getColor();
        Vector2f[] texCoords = sprite.getTexCords();

        int texId = 0;
        if (sprite.getTexture() != null){
            for (int i = 0; i < textures.size(); i ++) {
                if (textures.get(i).equals(sprite.getTexture())) {
                    texId = i + 1;
                    break;
                }
            }
        }

        boolean isRotated = sprite.gameObject.transform.rotation != 0.0f;
        Matrix4f transformMatrix = new Matrix4f().identity();
        if (isRotated) {
            transformMatrix.translate(sprite.gameObject.transform.position.x, sprite.gameObject.transform.position.y, 0);
            transformMatrix.rotate((float) Math.toRadians(sprite.gameObject.transform.rotation), 0, 0, 1);
            transformMatrix.scale(sprite.gameObject.transform.scale.x, sprite.gameObject.transform.scale.y, 1);

        }

        float xAdd = 0.5f;
        float yAdd = 0.5f;
        for (int i=0; i < 4; i++) {
            if ( i == 1) {
                yAdd = -0.5f;
            } else if (i == 2) {
                xAdd = -0.5f;
            } else if (i == 3) {
                yAdd = 0.5f;
            }

            Vector4f currentPos = new Vector4f(sprite.gameObject.transform.position.x + (xAdd * sprite.gameObject.transform.scale.x),
                    sprite.gameObject.transform.position.y + (yAdd * sprite.gameObject.transform.scale.y), 0, 1);
            if (isRotated) {
                currentPos = new Vector4f(xAdd, yAdd, 0, 1).mul(transformMatrix);
            }
            // position
            vertices[offset] = currentPos.x;
            vertices[offset + 1] = currentPos.y;

            //color
            vertices[offset + 2] = color.x;
            vertices[offset + 3] = color.y;
            vertices[offset + 4] = color.z;
            vertices[offset + 5] = color.w;

            //texture coords
            vertices[offset + 6] = texCoords[i].x;
            vertices[offset + 7] = texCoords[i].y;

            //texture id
            vertices[offset + 8] = texId;

            // object unique id
            vertices[offset + 9] = sprite.gameObject.getUid();

            offset += VERTEX_SIZE;
        }
    }

    private int[] generateElements() {
        int[] elements = new int[6 * maxBatchSize];
        for (int i=0; i < maxBatchSize; i++) {
            loadElements(elements, i);
        }
        return elements;
    }

    private void loadElements(int[] elements, int index) {
        // jeden ctverec je tvoren 2 trojuhelniky, jeden trojuhelnik je tvoren 3 body
        int offsetArrayIndex = 6 * index;
        int offset = 4 * index;

        /*
                    X1      X2


                    X3      X0
                v protismeru hodinovych rucicek
                2,1,0, // top right tri

                0,1,3, // bottom left tri
        */

        // 1 tri
        elements[offsetArrayIndex] = offset + 3;
        elements[offsetArrayIndex + 1] = offset + 2;
        elements[offsetArrayIndex + 2] = offset + 0;
        // 2 tri
        elements[offsetArrayIndex + 3] = offset + 0;
        elements[offsetArrayIndex + 4] = offset + 2;
        elements[offsetArrayIndex + 5] = offset + 1;
    }

    public boolean hasRoom() {
        return this.hasRoom;
    }

    public boolean hasTextureRoom() {
        return this.textures.size() < 8;
    }

    public boolean hasTexture(Texture texture) {
        return this.textures.contains(texture);
    }

    public int zIndex() {
        return this.zIndex;
    }

    @Override
    public int compareTo(RenderInBatch o) {
        return Integer.compare(this.zIndex, o.zIndex());
    }
}
