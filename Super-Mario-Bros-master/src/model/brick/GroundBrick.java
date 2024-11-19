package model.brick;

import java.awt.image.BufferedImage;
//gạch không thể bị phá vỡ. Không có hành vi cụ thể nào và không tương tác với nhân vật.
public class GroundBrick extends Brick{

    public GroundBrick(double x, double y, BufferedImage style){
        super(x, y, style);
        //gạch không thể bị phá vỡ.
        setBreakable(false);
        // gạch không chứa vật phẩm.
        // empty : trống
        setEmpty(true);
    }

}
