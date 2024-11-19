package model.brick;

import manager.GameEngine;
import manager.MapManager;
import model.Map;
import model.prize.Prize;
import view.Animation;
import view.ImageLoader;

import java.awt.*;
import java.awt.image.BufferedImage;
//gạch thông thường có thể bị phá vỡ.
public class OrdinaryBrick extends Brick {

    private Animation animation;
    private boolean breaking;
    private int frames;

    public OrdinaryBrick(double x, double y, BufferedImage style){
        super(x, y, style);
        setBreakable(true);
        setEmpty(true);

        setAnimation();
        breaking = false;
        //lưu trữ số lượng khung hình trong hoạt cảnh vỡ.
        frames = animation.getLeftFrames().length;
    }
    //Khởi tạo hoạt ảnh vỡ bằng cách lấy các khung hình từ ImageLoader và tạo đối tượng Animation.
    private void setAnimation(){
        ImageLoader imageLoader = new ImageLoader();
        BufferedImage[] leftFrames = imageLoader.getBrickFrames();

        animation = new Animation(leftFrames, leftFrames);
    }

    @Override
    //xử lí hành động khi mario đập vào khối, ghi đè phương thức lớp cha, Brick
    public Prize reveal(GameEngine engine){
        MapManager manager = engine.getMapManager();
//        if(!manager.getMario().isSuper())
//            return null;
        // điều kiện là khi mario ở dạng super và không ở dạng lửa thì mưới phá hủy được gạch.
        if(manager.getMario().isSuper() && !manager.getMario().getMarioForm().isFire()){
            // đánh dấu là khối gạch đã vỡ.
            breaking = true;
            //Thêm khối gạch này vào danh sách các khối gạch đang vỡ được quản lý bởi MapManager.
            manager.addRevealedBrick(this);
            //Dịch chuyển gạch sang trái trên trục X (giảm tọa độ X).
            //Dịch chuyển gạch lên trên trên trục Y (giảm tọa độ Y).
            //Điều này tạo hiệu ứng gạch bị "đẩy" lên trên và sang trái khi bị phá vỡ, bắn tung lên.
            double newX = getX() - 27, newY = getY() - 27;
            setLocation(newX, newY);
        }

        return null;
    }
    //trả về số lượng khung hình trong hoạt ảnh vỡ.
    public int getFrames(){
        return frames;
    }
    //Cập nhật hoạt ảnh vỡ của khối gạch
    public void animate(){
        if(breaking){
            setStyle(animation.animate(3, true));
            // cần phải có biến frames để đếm vì hoặt ảnh vữo của viên gạch chỉ diênx ra 1 lần
            // còn phương thức animate của Animation là gọi các chuỗi hoạt ảnh liên tiếp nhau.
            frames--;
        }
    }
}
