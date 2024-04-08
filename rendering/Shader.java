package rendering;

import org.joml.*;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;

public class Shader {


    private int shaderProgramID;
    private boolean inUse = false;

    private String verSrc;
    private String fragSrc;
    private String filepath;

    public Shader(String filepath) {
        this.filepath = filepath;
        try {
            String src = new String(Files.readAllBytes(Paths.get(filepath)));
            String[] splitString = src.split("(#type)( )+([a-zA-Z]+)");

            // najde prvni pattern v shaderu po #type 'nazev patternu'
            int index = src.indexOf("#type") + 6;
            int lineEnd = src.indexOf("\r\n", index);
            String firstPattern = src.substring(index, lineEnd).trim();

            // najde druhy pattern v shaderu po #type 'nazev patternu'
            index = src.indexOf("#type", lineEnd) + 6;
            lineEnd = src.indexOf("\r\n", index);
            String secondPattern = src.substring(index, lineEnd).trim();

            if (firstPattern.equals("vertex")) {
                verSrc = splitString[1];
            } else if (firstPattern.equals("fragment")) {
                fragSrc = splitString[1];
            }

            if (secondPattern.equals("vertex")) {
                verSrc = splitString[2];
            } else if (secondPattern.equals("fragment")) {
                fragSrc = splitString[2];
            }
        } catch (IOException e) {
            System.out.println(e);
        }

    //    System.out.println(verSrc);
    //    System.out.println(fragSrc);
    }

    public void compile() {
        int vertexID, fragmentID;

        // vertex shader
        vertexID = glCreateShader(GL_VERTEX_SHADER);

        glShaderSource(vertexID, verSrc);
        glCompileShader(vertexID);

        if(glGetShaderi(vertexID, GL_COMPILE_STATUS) == GL_FALSE){
            int length = glGetShaderi(vertexID, GL_INFO_LOG_LENGTH);
            System.out.println(glGetShaderInfoLog(vertexID, length));
            throw new RuntimeException("Vertex shader compilation error in '" + filepath + "'");
        }

        // fragment shader
        fragmentID = glCreateShader(GL_FRAGMENT_SHADER);

        glShaderSource(fragmentID, fragSrc);
        glCompileShader(fragmentID);

        if(glGetShaderi(fragmentID, GL_COMPILE_STATUS) == GL_FALSE){
            int length = glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH);
            System.out.println(glGetShaderInfoLog(fragmentID, length));
            throw new RuntimeException("Fragment shader compilation error in '" + filepath + "'");
        }

        // shader link
        shaderProgramID = glCreateProgram();
        glAttachShader(shaderProgramID, vertexID);
        glAttachShader(shaderProgramID, fragmentID);
        glLinkProgram(shaderProgramID);

        if (glGetProgrami(shaderProgramID, GL_LINK_STATUS) == GL_FALSE){
            int length = glGetProgrami(shaderProgramID, GL_INFO_LOG_LENGTH);
            System.out.println(glGetProgramInfoLog(shaderProgramID, length));
            throw new RuntimeException("Shaders linking error in '" + filepath + "'");
        }
    }

    public void use(){
        if (!inUse) {
            glUseProgram(shaderProgramID);
            inUse = true;
        }
    }

    public void detach() {
        glUseProgram(0);
        inUse = false;
    }

    public void uploadMat4f(String varName, Matrix4f mat4) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(16);
        mat4.get(matBuffer);
        glUniformMatrix4fv(varLocation, false, matBuffer);
    }

    public void uploadMat3f(String varName, Matrix3f mat3) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(9);
        mat3.get(matBuffer);
        glUniformMatrix3fv(varLocation, false, matBuffer);
    }

    public void uploadVec4f(String varName, Vector4f vec) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform4f(varLocation, vec.x, vec.y, vec.z, vec.w);
    }

    public void uploadVec3f(String varName, Vector3f vec) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform3f(varLocation, vec.x, vec.y, vec.z);
    }
    public void uploadVec2f(String varName, Vector2f vec) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform2f(varLocation, vec.x, vec.y);
    }

    public void uploadFloat(String varName, float value){
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform1f(varLocation, value);
    }

    public void uploadInt(String varName, int value) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform1i(varLocation, value);
    }

    public void uploadTexture(String varName, int slot) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform1i(varLocation, slot);
    }

    public void uploadIntArray(String varName, int[] array) {
        int varLocation = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform1iv(varLocation, array);
    }

}
