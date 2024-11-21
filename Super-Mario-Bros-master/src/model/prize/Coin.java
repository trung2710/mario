package model.prize;

import manager.GameEngine;
import model.GameObject;
import model.hero.Mario;

import java.awt.*;
import java.awt.image.BufferedImage;


public class Coin extends GameObject implements Prize{

    private int point;
    //acquired : xác định xem đồng xu đã được ăn chưa.
    //revealed : xác định xem đôngg xu đã đưuocj hiển thị hay chưa.
    private boolean revealed, acquired;
    //giới hạn vị trí để hiện đồng xu.
    //ngưỡng dưới(đáy của đồng xu), đồng xu sẽ xuất hiện từ trên xuống.
    private int revealBoundary;

    public Coin(double x, double y, BufferedImage style, int point){
        super(x, y, style);
        this.point = point;
        revealed = false;
        acquired=false;
        setDimension(30, 42);
        revealBoundary = (int)getY() - getDimension().height;
    }

    @Override
    public int getPoint() {
        return point;
    }

    @Override
    public void reveal() {
        revealed = true;
    }

    @Override
    // xử lí khi đồng xu va chạm với mario
    public void onTouch(Mario mario, GameEngine engine) {
        if(!acquired){
            acquired = true;
            mario.acquirePoints(point);
            mario.acquireCoin();
            engine.playCoin();
        }
    }

    //Nếu đồng xu được hiển thị (revealed = true), nó sẽ di chuyển lên trên 5 pixel mỗi lần cập nhật,
    // tạo hiệu ứng "đồng xu nhảy lên".
    //Hiệu ứng này dừng lại khi đạt ngưỡng revealBoundary.
    @Override
    public void updateLocation(){
        if(revealed){
            setY(getY() - 5);
        }
    }

    @Override
    public void draw(Graphics g){
        if(revealed){
            g.drawImage(getStyle(), (int)getX(), (int)getY(), null);
        }

    }

    public int getRevealBoundary() {
        return revealBoundary;
    }
}
