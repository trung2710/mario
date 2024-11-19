package model.brick;

import manager.GameEngine;
import model.Map;
import model.hero.Mario;
import model.prize.Prize;
import view.ImageLoader;

import java.awt.image.BufferedImage;
//đây là khối gạch ẩn chưuas vật phẩm.
public class SurpriseBrick extends Brick{

    private Prize prize;

    public SurpriseBrick(double x, double y, BufferedImage style, Prize prize) {
        super(x, y, style);
        setBreakable(false);
        setEmpty(false);
        // vật phẩm bên trong khối gạch.
        this.prize = prize;
    }

    @Override
    // xử lí hành động khi mario tương tác với khối gạch.
    //sau đó trả về vật phẩm bên trong khối gạch.
    public Prize reveal(GameEngine engine){
        BufferedImage newStyle = engine.getImageLoader().loadImage("/sprite.png");
        // hình ảnh viên gạch trống.
        newStyle = engine.getImageLoader().getSubImage(newStyle, 1, 2, 48, 48);
        //đánh dấu vật phẩm đã xuất hiện.
        if(prize != null){
            prize.reveal();
        }

        setEmpty(true);
        setStyle(newStyle);

        Prize toReturn = this.prize;
        //gán vật phẩm của khối gạch bằng null, chứng tỏ đã lấy được vật phẩm.
        this.prize = null;
        return toReturn;
    }

    @Override
    public Prize getPrize(){
        return prize;
    }
}
