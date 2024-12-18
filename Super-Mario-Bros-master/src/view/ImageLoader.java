package view;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageLoader {

    private BufferedImage marioForms;
    private BufferedImage brickAnimation;

    public ImageLoader(){
        marioForms = loadImage("/mario-forms.png");
        brickAnimation = loadImage("/brick-animation.png");
    }

    public BufferedImage loadImage(String path){
        BufferedImage imageToReturn = null;

        try {
            imageToReturn = ImageIO.read(getClass().getResource("/media" + path));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return imageToReturn;
    }

    public BufferedImage loadImage(File file){
        BufferedImage imageToReturn = null;

        try {
            imageToReturn = ImageIO.read(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return imageToReturn;
    }

    public BufferedImage getSubImage(BufferedImage image, int col, int row, int w, int h){
        if((col == 1 || col == 4) && row == 3){ //koopa
            //x: Tọa độ x của góc trên bên trái của vùng cắt.
            //y: Tọa độ y của góc trên bên trái của vùng cắt.
            //w: Chiều rộng của vùng cắt.
            //h: Chiều cao của vùng cắt.
            return image.getSubimage((col-1)*48, 128, w, h);
        }
        return image.getSubimage((col-1)*48, (row-1)*48, w, h);
    }
    // Phương thức trích xuất các khung hình hoạt hình cho Mario di chuyển sang trái và phải.
    //Hàm này lấy các khung hình động của Mario từ một sprite sheet, dựa trên dạng thức của Mario.
    // Các khung hình này sẽ được sử dụng để tạo hiệu ứng hoạt hình khi Mario di chuyển sang trái.
    public BufferedImage[] getLeftFrames(int marioForm){
        BufferedImage[] leftFrames = new BufferedImage[5];
        int col = 1;
        int width = 52, height = 48;

        if(marioForm == 1) { //super mario
            col = 4;
            width = 48;
            height = 96;
        }
        else if(marioForm == 2){ //fire mario
            col = 7;
            width = 48;
            height = 96;
        }

        for(int i = 0; i < 5; i++){
            leftFrames[i] = marioForms.getSubimage((col-1)*width, (i)*height, width, height);
        }
        return leftFrames;
    }

    public BufferedImage[] getRightFrames(int marioForm){
        BufferedImage[] rightFrames = new BufferedImage[5];
        int col = 2;
        int width = 52, height = 48;

        if(marioForm == 1) { //super mario
            col = 5;
            width = 48;
            height = 96;
        }
        else if(marioForm == 2){ //fire mario
            col = 8;
            width = 48;
            height = 96;
        }

        for(int i = 0; i < 5; i++){
            rightFrames[i] = marioForms.getSubimage((col-1)*width, (i)*height, width, height);
        }
        return rightFrames;
    }
    //Phương thức trích xuất các khung hình hoạt hình của gạch khi vỡ.
    public BufferedImage[] getBrickFrames() {
        BufferedImage[] frames = new BufferedImage[4];
        for(int i = 0; i < 4; i++){
            frames[i] = brickAnimation.getSubimage(i*105, 0, 105, 105);
        }
        return frames;
    }
}
