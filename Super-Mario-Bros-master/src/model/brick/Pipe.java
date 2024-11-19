package model.brick;

import java.awt.image.BufferedImage;
//các ống cống trong game, có kích thước lớn hơn các loại gạch khác, không thể bị phá võe, không chứa vật phẩm.
//kihcs thước là 96x96
public class Pipe extends Brick{

    public Pipe(double x, double y, BufferedImage style){
        super(x, y, style);
        setBreakable(false);
        setEmpty(true);
        setDimension(96, 96);
    }
}
