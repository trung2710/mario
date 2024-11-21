package model;

import model.brick.Brick;
import model.brick.OrdinaryBrick;
import model.enemy.Enemy;
import model.enemy.Goomba;
import model.hero.Fireball;
import model.hero.Mario;
import model.prize.BoostItem;
import model.prize.Coin;
import model.prize.Prize;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;

public class Map {

    private double remainingTime;
    private Mario mario;
    private ArrayList<Brick> bricks = new ArrayList<>();
    private ArrayList<Enemy> enemies = new ArrayList<>();
    private ArrayList<Brick> groundBricks = new ArrayList<>();
    private ArrayList<Prize> revealedPrizes = new ArrayList<>();
    private ArrayList<Brick> revealedBricks = new ArrayList<>();
    private ArrayList<Fireball> fireballs = new ArrayList<>();
    private EndFlag endPoint;
    private BufferedImage backgroundImage;
    private double bottomBorder = 720 - 96;
    private String path;


    public Map(double remainingTime, BufferedImage backgroundImage) {
        this.backgroundImage = backgroundImage;
        this.remainingTime = remainingTime;
    }


    public Mario getMario() {
        return mario;
    }

    public void setMario(Mario mario) {
        this.mario = mario;
    }

    public ArrayList<Enemy> getEnemies() {
        return enemies;
    }

    public ArrayList<Fireball> getFireballs() {
        return fireballs;
    }

    public ArrayList<Prize> getRevealedPrizes() {
        return revealedPrizes;
    }

    public ArrayList<Brick> getAllBricks() {
        ArrayList<Brick> allBricks = new ArrayList<>();

        allBricks.addAll(bricks);
        allBricks.addAll(groundBricks);

        return allBricks;
    }

    public void addBrick(Brick brick) {
        this.bricks.add(brick);
    }

    public void addGroundBrick(Brick brick) {
        this.groundBricks.add(brick);
    }

    public void addEnemy(Enemy enemy) {
        this.enemies.add(enemy);
    }

    public void drawMap(Graphics2D g2){
        drawBackground(g2);
        drawPrizes(g2);
        drawBricks(g2);
        drawEnemies(g2);
        drawFireballs(g2);
        drawMario(g2);
        endPoint.draw(g2);
    }

    private void drawFireballs(Graphics2D g2) {
        for(Fireball fireball : fireballs){
            fireball.draw(g2);
        }
    }

    private void drawPrizes(Graphics2D g2) {
        //Việc sử dụng bản sao giúp đảm bảo bạn chỉ làm việc với một ảnh chụp nhanh của danh sách revealedPrizes.
        // Bất kỳ thay đổi nào đối với danh sách gốc sẽ không ảnh hưởng đến vòng lặp.
        // Duyệt qua bản sao của danh sách để tránh ConcurrentModificationException
        for (Prize prize : new ArrayList<>(revealedPrizes)) {
            if (prize instanceof Coin) {
                ((Coin) prize).draw(g2);
            } else if (prize instanceof BoostItem) {
                ((BoostItem) prize).draw(g2);
            }
        }
    }

    private void drawBackground(Graphics2D g2){
        g2.drawImage(backgroundImage, 0, 0, null);
    }

    private void drawBricks(Graphics2D g2) {
        for(Brick brick : new ArrayList<>(bricks)){
            if(brick != null)
                brick.draw(g2);
        }

        for(Brick brick : groundBricks){
            brick.draw(g2);
        }
    }

    private void drawEnemies(Graphics2D g2) {
        for(Enemy enemy : enemies){
            if(enemy != null)
                enemy.draw(g2);
        }
    }

    private void drawMario(Graphics2D g2) {
        mario.draw(g2);
    }

    public void updateLocations() {
        mario.updateLocation();
        for(Enemy enemy : enemies){
            enemy.updateLocation();
        }


        for (Fireball fireball: fireballs) {
            fireball.updateLocation();
        }
        // Xử lý Prize
        //Sử dụng Iterator cho phép bạn xóa phần tử khỏi danh sách gốc trong khi vẫn duyệt qua nó mà không gặp lỗi.
        Iterator<Prize> prizeIterator = revealedPrizes.iterator();
        while (prizeIterator.hasNext()) {
            Prize prize = prizeIterator.next();
            if (prize instanceof Coin) {
                ((Coin) prize).updateLocation();
                // đồng xu chỉ xuất hiện trong 1 khoảng thời gian rồi biến mất.
                if (((Coin) prize).getRevealBoundary() > ((Coin) prize).getY()) {
                    prizeIterator.remove(); // Sử dụng Iterator để xóa an toàn
                }
            } else if (prize instanceof BoostItem) {
                ((BoostItem) prize).updateLocation();
            }
        }

        // Xử lý Brick
        Iterator<Brick> brickIterator = revealedBricks.iterator();
        while (brickIterator.hasNext()) {
            OrdinaryBrick brick = (OrdinaryBrick) brickIterator.next();
            brick.animate();
            if (brick.getFrames() < 0) {
                brickIterator.remove(); // Sử dụng Iterator để xóa an toàn
                bricks.remove(brick);  // Xóa khỏi danh sách bricks nếu cần
            }
        }


        endPoint.updateLocation();
    }
    // vị trí của đường biên của map.
    public double getBottomBorder() {
        return bottomBorder;
    }

    public void addRevealedPrize(Prize prize) {
        revealedPrizes.add(prize);
    }

    public void addFireball(Fireball fireball) {
        fireballs.add(fireball);
    }

    public void setEndPoint(EndFlag endPoint) {
        this.endPoint = endPoint;
    }

    public EndFlag getEndPoint() {
        return endPoint;
    }

    public void addRevealedBrick(OrdinaryBrick ordinaryBrick) {
        revealedBricks.add(ordinaryBrick);
    }

    public void removeFireball(Fireball object) {
        fireballs.remove(object);
    }

    public void removeEnemy(Enemy object) {
        enemies.remove(object);
    }

    public void removePrize(Prize object) {
        revealedPrizes.remove(object);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void updateTime(double passed){
        remainingTime = remainingTime - passed;
    }

    public boolean isTimeOver(){
        return remainingTime <= 0;
    }

    public double getRemainingTime() {
        return remainingTime;
    }
}
