package model.brick;

import manager.GameEngine;
import model.GameObject;
import model.Map;
import model.prize.Prize;

import java.awt.image.BufferedImage;

public abstract class Brick extends GameObject{
    //cờ xác định khối gạch đã bị phá vỡ hay chưa
    private boolean breakable;
    //xác định xem khối gạch có chưaus vật phẩm bên trong hay không.
    // true : trống, flase : có chứa vật phẩm.
    private boolean empty;

    // xác định kích trước và hình ảnh cho đối tượng.
    public Brick(double x, double y, BufferedImage style){
        super(x, y, style);
        setDimension(48, 48);
    }

    public boolean isBreakable() {
        return breakable;
    }

    public void setBreakable(boolean breakable) {
        this.breakable = breakable;
    }

    public boolean isEmpty() {
        return empty;
    }

    public void setEmpty(boolean empty) {
        this.empty = empty;
    }
    //các đối tượng con tự định nghĩa cách thức hoạt động khi đập vào gạch.có thể trarr về 1 đối tượng vật phẩm.
    public Prize reveal(GameEngine engine){ return null;}

    public Prize getPrize() {
        return null;
    }
}
