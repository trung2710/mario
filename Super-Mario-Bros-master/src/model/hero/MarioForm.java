package model.hero;

import view.Animation;
import view.ImageLoader;

import java.awt.image.BufferedImage;
// quản lí hình dạng và trạng thái của nhận vật mario

public class MarioForm {

    public static final int SMALL = 0, SUPER = 1, FIRE = 2;
    //Lưu trữ các khung hình hoạt hình của Mario cho các hành động khác nhau (đi, nhảy, đứng yên...).
    private Animation animation;
    //2 chờ theo dõi trạng thái cuả mario: có ở dạng siêu hay dạng lửa hay không.
    private boolean isSuper, isFire; //note: fire form has priority over super
    //hình ảnh quả cầu lauwr mà mario có thể bắn ra khi ở dạng lửa
    private BufferedImage fireballStyle;

    public MarioForm(Animation animation, boolean isSuper, boolean isFire){
        this.animation = animation;
        this.isSuper = isSuper;
        this.isFire = isFire;

        ImageLoader imageLoader = new ImageLoader();
        BufferedImage fireball = imageLoader.loadImage("/sprite.png");
        fireballStyle = imageLoader.getSubImage(fireball, 3, 4, 24, 24);
    }
    //lấy hình ảnh của mario dựa trên trạng thái của mario.
    //các biến boolean xác định trạng thái di chuyển của nhân vật, movingInX: di chuyển theo chiều ngang
    //movingInY; di chuyển theo chiều dọc, toRight : di chuyển sang phải
    public BufferedImage getCurrentStyle(boolean toRight, boolean movingInX, boolean movingInY){

        BufferedImage style;
        //nếu di chuyển theo trục y và hướng sang phải
        // thì lấy khung hình đầu tiên sang phải
        if(movingInY && toRight){
            style = animation.getRightFrames()[0];
        }
        else if(movingInY){
            style = animation.getLeftFrames()[0];
        }
        //nếu di chuyển theo trục ngang gọi phương thức thay đôir khung hình sau
        // 5 lần cập nhật.(speed=5)
        else if(movingInX){
            style = animation.animate(5, toRight);
        }
        // đây là trường hợp đối tượng đứng im

        else {
            //lấy khung hình thú 2 trong chuỗi hoạt hình sang phải
            if(toRight){
                style = animation.getRightFrames()[1];
            }//lấy khung hình thú 2 trong chuỗi hoạt hình sang trái.
            else
                style = animation.getLeftFrames()[1];
        }

        return style;
    }
    // khi chạm vào kẻ địch thì mario sẽ chuyển về dạng nhỏ.
    // nếu trong ở dạng siêu hoạc lửa.
    public MarioForm onTouchEnemy(ImageLoader imageLoader) {
        BufferedImage[] leftFrames = imageLoader.getLeftFrames(0);
        BufferedImage[] rightFrames= imageLoader.getRightFrames(0);

        Animation newAnimation = new Animation(leftFrames, rightFrames);

        return new MarioForm(newAnimation, false, false);
    }
    // khi mario ở dạng lửa.
    //bắn ra lửa.
    public Fireball fire(boolean toRight, double x, double y) {
        if(isFire){
            return new Fireball(x, y + 48, fireballStyle, toRight);
        }
        return null;
    }

    public boolean isSuper() {
        return isSuper;
    }

    public void setSuper(boolean aSuper) {
        isSuper = aSuper;
    }

    public boolean isFire() {
        return isFire;
    }
}
