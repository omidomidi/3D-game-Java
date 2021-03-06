package textures;
 
import java.nio.ByteBuffer;
 
public class TInfo {
     
    private int width;
    private int height;
    private ByteBuffer buffer;
     
    public TInfo(ByteBuffer buffer, int width, int height){
        this.buffer = buffer;
        this.width = width;
        this.height = height;
    }
     
    public int getWidth(){
        return width;
    }
     
    public int getHeight(){
        return height;
    }
     
    public ByteBuffer getBuffer(){
        return buffer;
    }
 
}
