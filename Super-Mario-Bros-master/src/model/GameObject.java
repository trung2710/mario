package model;

import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class GameObject {
    // tọa độ của đối tượng.
    private double x, y;
    // vận tốc của đôí tượng theo 2 trục.
    private double velX, velY;
    // Dimension : bieu din kich thuoc của đối tượng.
    private Dimension dimension;
    // hinh ảnh của đối tượng.
    private BufferedImage style;
    //gia tốc trọng trường tác dụng lên đối tương.
    private double gravityAcc;
    //trạng thái rơi, nhảy của đối tương.
    private boolean falling, jumping;

    public GameObject(double x, double y, BufferedImage style){
        //thiết lập vị trí cho đối tương.
        setLocation(x, y);
        //thiết lập hình ảnh cho đối tương.
        setStyle(style);
        //thiết lập kích thước cho đối tượng theo ảnh.
        if(style != null){
            setDimension(style.getWidth(), style.getHeight());
        }
        // sét vận tốc bằng 0.
        setVelX(0);
        setVelY(0);
        //thiết lập trong lực
        setGravityAcc(0.38);
        jumping = false;
        falling = true;
    }
    //vẽ đối tượng nên màn ảnh.
    public void draw(Graphics g) {
        BufferedImage style = getStyle();

        if(style != null){
            g.drawImage(style, (int)x, (int)y, null);
        }

        //for debugging
        /*Graphics2D g2 = (Graphics2D)g;
        g2.setColor(Color.WHITE);

        g2.draw(getTopBounds());
        g2.draw(getBottomBounds());
        g2.draw(getRightBounds());
        g2.draw(getLeftBounds());*/
    }
    //cập nhật vị trí của đối tương dựa theo vị trí và trạng thái hoạt đông.
    //
    public void updateLocation() {
        if(jumping && velY <= 0){
            jumping = false;
            falling = true;
        }
        else if(jumping){
            velY = velY - gravityAcc;
            y = y - velY;
        }

        if(falling){
            y = y + velY;
            velY = velY + gravityAcc;
        }

        x = x + velX;
    }

    public void setLocation(double x, double y) {
        setX(x);
        setY(y);
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public Dimension getDimension(){
        return dimension;
    }

    public void setDimension(Dimension dimension) {
        this.dimension = dimension;
    }

    public void setDimension(int width, int height){ this.dimension =  new Dimension(width, height); }

    public BufferedImage getStyle() {
        return style;
    }

    public void setStyle(BufferedImage style) {
        this.style = style;
    }

    public double getVelX() {
        return velX;
    }

    public void setVelX(double velX) {
        this.velX = velX;
    }

    public double getVelY() {
        return velY;
    }

    public void setVelY(double velY) {
        this.velY = velY;
    }

    public double getGravityAcc() {
        return gravityAcc;
    }

    public void setGravityAcc(double gravityAcc) {
        this.gravityAcc = gravityAcc;
    }

    //lớp Rectangle để trả về 1 hinhf chữ nhật.
    // đây là các hàm trả về các hình chữ nhật xung quanh và bao quanh đối tượng.
    public Rectangle getTopBounds(){
        return new Rectangle((int)x+dimension.width/6, (int)y, 2*dimension.width/3, dimension.height/2);
    }

    public Rectangle getBottomBounds(){
        return new Rectangle((int)x+dimension.width/6, (int)y + dimension.height/2, 2*dimension.width/3, dimension.height/2);
    }

    public Rectangle getLeftBounds(){
        return new Rectangle((int)x, (int)y + dimension.height/4, dimension.width/4, dimension.height/2);
    }

    public Rectangle getRightBounds(){
        return new Rectangle((int)x + 3*dimension.width/4, (int)y + dimension.height/4, dimension.width/4, dimension.height/2);
    }

    public Rectangle getBounds(){
        return new Rectangle((int)x, (int)y, dimension.width, dimension.height);
    }

    public boolean isFalling() {
        return falling;
    }

    public void setFalling(boolean falling) {
        this.falling = falling;
    }

    public boolean isJumping() {
        return jumping;
    }

    public void setJumping(boolean jumping) {
        this.jumping = jumping;
    }
}
