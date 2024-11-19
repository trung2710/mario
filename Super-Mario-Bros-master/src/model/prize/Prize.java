package model.prize;

import manager.GameEngine;
import manager.MapManager;
import model.hero.Mario;

import java.awt.*;

public interface Prize {

    int getPoint();

    void reveal();

    Rectangle getBounds();
    //Thực hiện các hành động khi vật phẩm được chạm bởi nhân vật Mario.Tăng điểm số của Mario,Thay đổi trạng thái của Mario (ví dụ, biến đổi thành Super Mario hoặc Fire Mario).
    //Thực hiện các hiệu ứng âm thanh và hình ảnh.
    //Xóa vật phẩm khỏi màn hình.
    void onTouch(Mario mario, GameEngine engine);

}
