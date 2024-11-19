package model.hero;


import model.GameObject;

import java.awt.image.BufferedImage;

public class Fireball extends GameObject {

    public Fireball(double x, double y, BufferedImage style, boolean toRight) {
        super(x, y, style);
        setDimension(24, 24);
        setFalling(false);
        setJumping(false);
        setVelX(10);

        // nếu mario di chuyển sang trái thì bắn ra lửa có vận tốc 5.
        if(!toRight)
            setVelX(-5);
    }
}
