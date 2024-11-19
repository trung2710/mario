package view;

import manager.GameEngine;
import manager.GameStatus;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

//Lớp UIManager có trách nhiệm xử lý giao diện người dùng (UI) của game. Nó thực hiện các chức năng:
//Vẽ các màn hình khác nhau của game (màn hình bắt đầu, màn hình chọn bản đồ, màn hình trợ giúp,...)
//Hiển thị thông tin trong game (điểm, mạng sống còn lại, thời gian còn lại,...)
//Xử lý lựa chọn bản đồ thông qua chuột hoặc bàn phím

public class UIManager extends JPanel{

    private GameEngine engine;
    private Font gameFont;
    private BufferedImage startScreenImage, aboutScreenImage, helpScreenImage, gameOverScreen;
    private BufferedImage heartIcon;
    private BufferedImage coinIcon;
    private BufferedImage selectIcon;
    private MapSelection mapSelection;
    private BufferedImage logoStart;

    //UIManager: Hàm khởi tạo thiết lập các kích thước mặc định của panel, tham chiếu đến GameEngine để lấy thông tin
    // và các hình ảnh cần thiết. Khởi tạo font chữ từ file.
    public UIManager(GameEngine engine, int width, int height) {
        setPreferredSize(new Dimension(width, height));
        setMaximumSize(new Dimension(width, height));
        setMinimumSize(new Dimension(width, height));


        this.engine = engine;
        ImageLoader loader = engine.getImageLoader();

        mapSelection = new MapSelection();

        BufferedImage sprite = loader.loadImage("/sprite.png");
        this.heartIcon = loader.loadImage("/heart-icon.png");
        this.coinIcon = loader.getSubImage(sprite, 1, 5, 48, 48);
        this.selectIcon = loader.loadImage("/select-icon.png");
        this.startScreenImage = loader.loadImage("/start-screen.png");
        this.helpScreenImage = loader.loadImage("/help-screen.png");
        this.aboutScreenImage = loader.loadImage("/about-screen.png");
        this.gameOverScreen = loader.loadImage("/game-over.png");
        //them moi vao.
        this.logoStart=loader.loadImage("/logo.png");

        try {
            InputStream in = getClass().getResourceAsStream("/media/font/mario-font.ttf");
            gameFont = Font.createFont(Font.TRUETYPE_FONT, in);
        } catch (FontFormatException | IOException e) {
            gameFont = new Font("Verdana", Font.PLAIN, 12);
            e.printStackTrace();
        }
    }

    // phương thức chính để vễ giao diện người dùng
    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        //Tạo một đối tượng Graphics2D từ đối tượng Graphics ban đầu để sử dụng các tính năng vẽ nâng cao.
        Graphics2D g2 = (Graphics2D) g.create();
        GameStatus gameStatus = engine.getGameStatus();

        if(gameStatus == GameStatus.START_SCREEN){
            drawStartScreen(g2);
        }
        else if(gameStatus == GameStatus.MAP_SELECTION){
            drawMapSelectionScreen(g2);
        }
        else if(gameStatus == GameStatus.ABOUT_SCREEN){
            drawAboutScreen(g2);
        }
        else if(gameStatus == GameStatus.HELP_SCREEN){
            drawHelpScreen(g2);
        }
        else if(gameStatus == GameStatus.GAME_OVER){
            drawGameOverScreen(g2);
        }
        else if(gameStatus == GameStatus.HIGH_SCORE){
            drawHighScore(g2);
        }
        // day la truong hop trnag thai game la RUNNING.
        else {
            // translate để vẽ tọa độ của các đối tượng trong map vào trong khoảng của màn hình.
            // sau đó lại phải dịch hệ tọa độ về vị trí cũ để vẽ các thành phần khác.
            //còn tọa độ của camera là tọa độ trên map to.
            // ví dụ mario có tọa độ (1000, 1000) vượt quá màn hình, ta phải dịch chuyển trục tọa độ về bên trái,
            // để vẽ mario trong màn hình, rôi fdichj trục tọa độ trở lại như cũ.
            Point camLocation = engine.getCameraLocation();
            g2.translate(-camLocation.x, -camLocation.y);
            //vẽ các đối tượng nên màn hình.
            //hàm vẽ map đầu tiên trong file mapmanager.java
            //xong file GameEngine.java gọi hàm này
            //rồi file UIManager.java lại gọi lại
            engine.drawMap(g2);
            g2.translate(camLocation.x, camLocation.y);

            //các phương thức vẽ này vẽ cố định theo tọa độ của màn hình.
            drawPoints(g2);
            drawRemainingLives(g2);
            drawAcquiredCoins(g2);
            drawRemainingTime(g2);
            // khi pause game
            if(gameStatus == GameStatus.PAUSED){
                drawPauseScreen(g2);
            }
            //khi ma da danh chien thang
            else if(gameStatus == GameStatus.MISSION_PASSED){
                // khi mà đã dành chiến thắng thì set vận tốc bằng 0 .
                engine.getMapManager().getMario().setVelX(0);
                drawVictoryScreen(g2);
            }
        }
        //Giải phóng tài nguyên đồ họa để tránh rò rỉ bộ nhớ.
        g2.dispose();
    }

    private void drawRemainingTime(Graphics2D g2) {
        g2.setFont(gameFont.deriveFont(25f));
        g2.setColor(Color.WHITE);
        String displayedStr = "TIME: " + engine.getRemainingTime();
        g2.drawString(displayedStr, 750, 50);
    }

    private void drawVictoryScreen(Graphics2D g2) {
        g2.setFont(gameFont.deriveFont(50f));
        g2.setColor(Color.WHITE);
        String displayedStr = "YOU WON!";
        int stringLength = g2.getFontMetrics().stringWidth(displayedStr);
        g2.drawString(displayedStr, (getWidth()-stringLength)/2, getHeight()/2);
    }

    private void drawHelpScreen(Graphics2D g2) {
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, 1280, 720);
        g2.setFont(gameFont.deriveFont(50f));
        String title="HELP";
        g2.setColor(Color.WHITE);
        int x_title=(1280-g2.getFontMetrics().stringWidth(title))/2;
        g2.drawString(title, x_title, 200);
        g2.setFont(gameFont.deriveFont(30f));
        String xau[]={"ARROW KEYS TO MOVE", "ENTER KEY TO SELECT OPTION", "ESCAPE KEY TO PAUSE GAME ","OR GO TO START SCREEN"};
        for(int i=0;i< xau.length;i++){
            int width = g2.getFontMetrics().stringWidth(xau[i]);
            int x_select = (1280 - width) / 2;
            int y_select = 300 + i * 70;  // Tính toán y cho mỗi lựa chọn
            g2.drawString(xau[i], x_select, y_select);
        }
    }

    private void drawAboutScreen(Graphics2D g2) {
        g2.drawImage(aboutScreenImage, 0, 0, null);
    }
    public void  drawHighScore(Graphics2D g2){
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, 1280, 720);
        g2.setFont(gameFont.deriveFont(50f));
        String title = "Top 5 High Score";
        //tính toán vị trí ngang để hiển thị tiêu đề giữa màn hình.
        //lấy ra độ dài của tiêu đề với phông chữ hiện tại.
        //lấy ra điểm trái trên cùng của tiêu đề.
        int x_location = (1280 - g2.getFontMetrics().stringWidth(title))/2;
        //chọn màu của chữ và bắt đầu vẽ.
        g2.setColor(Color.YELLOW);
        g2.drawString(title, x_location, 100);
        ArrayList<String>scores=new ArrayList<>();
        try{
            File f=new File("high_score.txt");
            Scanner in=new Scanner(f);
            while(in.hasNextLine()){
                scores.add(in.nextLine());
            }

        }catch (Exception e){

        }
        Collections.sort(scores, Collections.reverseOrder());
        int cnt=0;
        for(int i=0;i<scores.size();i++){
            g2.setColor(Color.WHITE);
            String top_score=(i+1)+" : "+scores.get(i);
            int width = g2.getFontMetrics().stringWidth(top_score);
            int x_point = (1280 - width) / 2;
            int y_point=200+i*70;
            g2.drawString(top_score, x_point,y_point);
            cnt++;
            if(cnt==5) break;
        }


    }

    private void drawGameOverScreen(Graphics2D g2) {
        g2.drawImage(gameOverScreen, 0, 0, null);
        g2.setFont(gameFont.deriveFont(50f));
        g2.setColor(new Color(130, 48, 48));
        String acquiredPoints = "Score: " + engine.getScore();
        int stringLength = g2.getFontMetrics().stringWidth(acquiredPoints);
        int stringHeight = g2.getFontMetrics().getHeight();
        g2.drawString(acquiredPoints, (getWidth()-stringLength)/2, getHeight()-stringHeight*2);
    }

    private void drawPauseScreen(Graphics2D g2) {
        g2.setFont(gameFont.deriveFont(50f));
        g2.setColor(Color.YELLOW);
        String displayedStr = "PAUSED";
        int stringLength = g2.getFontMetrics().stringWidth(displayedStr);
        g2.drawString(displayedStr, (getWidth()-stringLength)/2, getHeight()/4);
        String xau[]={"NEW GAME", "RESUME", "QUIT"};
        for(int i=0;i< xau.length;i++){
            g2.setColor(Color.BLACK);
            int width = g2.getFontMetrics().stringWidth(xau[i]);
            int x_select = (1280 - width) / 2;
            int y_select = 360 + i * 100;  // Tính toán y cho mỗi lựa chọn
            g2.drawString(xau[i], x_select, y_select);
        }
        //Phương thức này trả về giá trị lineNumber của hằng số enum.
        //Khi bạn gọi engine.getPauseSelection().getLineNumber(),
        // phương thức sẽ lấy giá trị lineNumber đã được khởi tạo trong enum.
        int row=engine.getPauseSelection().getLineNumber();
        int y_selectIcon=row*100+360-selectIcon.getHeight();
        g2.drawImage(selectIcon, 300, y_selectIcon, null);  // Kiểm tra giá trị của row để đảm bảo hợp lệ
    }

    private void drawAcquiredCoins(Graphics2D g2) {
        g2.setFont(gameFont.deriveFont(30f));
        g2.setColor(Color.WHITE);
        String displayedStr = "" + engine.getCoins();
        g2.drawImage(coinIcon, getWidth()-115, 10, null);
        g2.drawString(displayedStr, getWidth()-65, 50);
    }

    private void drawRemainingLives(Graphics2D g2) {
        g2.setFont(gameFont.deriveFont(30f));
        g2.setColor(Color.WHITE);
        String displayedStr = "" + engine.getRemainingLives();
        g2.drawImage(heartIcon, 50, 10, null);
        g2.drawString(displayedStr, 100, 50);
    }

    private void drawPoints(Graphics2D g2){
        g2.setFont(gameFont.deriveFont(25f));
        g2.setColor(Color.WHITE);
        String displayedStr = "Points: " + engine.getScore();
        int stringLength = g2.getFontMetrics().stringWidth(displayedStr);;
        //g2.drawImage(coinIcon, 50, 10, null);
        g2.drawString(displayedStr, 300, 50);
    }

    private void drawStartScreen(Graphics2D g2) {
        // Vẽ nền đen
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, 1280, 720);

        // Thay đổi kích thước logo và vẽ logo
        logoStart.getScaledInstance(500, 300, BufferedImage.SCALE_SMOOTH);
        int x_logo = (1280 - logoStart.getWidth(null)) / 2;
        int y_logo = 50;
        g2.drawImage(logoStart, x_logo, y_logo, null);

        // Cài đặt font chữ và vẽ các lựa chọn menu
        g2.setFont(gameFont.deriveFont(50f));
        g2.setColor(Color.WHITE);
        String[] xau = {"START GAME", "HELP", "ABOUT", "HIGHSCORE", "QUIT"};

        for (int i = 0; i < xau.length; i++) {
            int width = g2.getFontMetrics().stringWidth(xau[i]);
            int x_select = (1280 - width) / 2;
            int y_select = 360 + i * 70;  // Tính toán y cho mỗi lựa chọn
            g2.drawString(xau[i], x_select, y_select);
        }

        // Vẽ biểu tượng chọn lựa (selectIcon)
        int row = engine.getStartScreenSelection().getLineNumber();
        int y_selectIcon=row*70+360-selectIcon.getHeight();
        g2.drawImage(selectIcon, 300, y_selectIcon, null);  // Kiểm tra giá trị của row để đảm bảo hợp lệ
    }


    private void drawMapSelectionScreen(Graphics2D g2){
        g2.setFont(gameFont.deriveFont(50f));
        g2.setColor(Color.WHITE);
        mapSelection.draw(g2);
        int row = engine.getSelectedMap();
        int y_location = row*100+300-selectIcon.getHeight();
        g2.drawImage(selectIcon, 375, y_location, null);
    }


    public String selectMapViaMouse(Point mouseLocation) {
        return mapSelection.selectMap(mouseLocation);
    }

    public String selectMapViaKeyboard(int index){
        return mapSelection.selectMap(index);
    }

    public int changeSelectedMap(int index, boolean up){
        return mapSelection.changeSelectedMap(index, up);
    }
}