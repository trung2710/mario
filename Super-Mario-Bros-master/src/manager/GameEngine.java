package manager;

import model.hero.Mario;
import model.hero.MarioForm;
import view.*;
import view.UIManager;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Scanner;

public class GameEngine implements Runnable {

    private final static int WIDTH = 1268, HEIGHT = 708;

    private MapManager mapManager;
    private UIManager uiManager;
    private SoundManager soundManager;
    private GameStatus gameStatus;
    // kiểm tra khi màn vào chế độ RUNNING thì dừng nhạc nền.
    private boolean isSoundEnabled;
    // kiểm tra xem game có đang chạy không.
    private boolean isRunning;
    private Camera camera;
    private ImageLoader imageLoader;
    private Thread thread;
    private StartScreenSelection startScreenSelection = StartScreenSelection.START_GAME;
    private int selectedMap = 0;
    private PauseSelection pauselection=PauseSelection.NEW_GAME;
    private String pathMap;
    private GameEngine() {
        init();
    }

    private void init() {
        imageLoader = new ImageLoader();
        InputManager inputManager = new InputManager(this);
        gameStatus = GameStatus.START_SCREEN;
        camera = new Camera();
        uiManager = new UIManager(this, WIDTH, HEIGHT);
        soundManager = new SoundManager();
        mapManager = new MapManager();
        isSoundEnabled=true;
        // khi bắt đầu trò chơi là âm thanh kêu.
        if(isSoundEnabled)
            soundManager.restartBackground();
        JFrame frame = new JFrame("Super Mario Bros.");
        //jpanel
        frame.add(uiManager);
        frame.addKeyListener(inputManager);
        frame.addMouseListener(inputManager);
        frame.pack();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setResizable(true);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        start();
    }

    private synchronized void start() {
        if (isRunning)
            return;

        isRunning = true;
        thread = new Thread(this);
        thread.start();
    }

    private void reset(){
        setGameStatus(GameStatus.START_SCREEN);
        resetCamera();

    }

    public void resetCamera(){
        camera = new Camera();
        if(gameStatus==GameStatus.START_SCREEN){
            soundManager.restartBackground();
        }

    }

    public void selectMapViaMouse() {
        String path = uiManager.selectMapViaMouse(uiManager.getMousePosition());
        if (path != null) {
            createMap(path);
        }
    }

    public void selectMapViaKeyboard(){
        String path = uiManager.selectMapViaKeyboard(selectedMap);
        this.pathMap=path;
        if (path != null) {
            createMap(path);
        }
    }

    public void changeSelectedMap(boolean up){
        selectedMap = uiManager.changeSelectedMap(selectedMap, up);
    }


    // đây là hàm tạo map mới.
    private void createMap(String path) {
        boolean loaded = mapManager.createMap(imageLoader, path);
        if(loaded){
            setGameStatus(GameStatus.RUNNING);
            soundManager.pauseBackground();
        }

        else{
            setGameStatus(GameStatus.START_SCREEN);
            soundManager.restartBackground();
        }

    }

    @Override
    //Phương thức run là một phần trong vòng đời của một luồng (thread).
    // Phương thức này điều khiển toàn bộ hoạt động của trò chơi, bao gồm cập nhật logic, vẽ hình ảnh và xử lý thời gian.
    public void run() {
        long lastTime = System.nanoTime();
        double amountOfTicks = 70.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        long timer = System.currentTimeMillis();

        //game dang trong trang thai : RUNNING,
        //thread.isInterrupted==true khi :Người dùng đóng ứng dụng, và bạn sử dụng interrupt() để dừng các luồng nền.
        while (isRunning && !thread.isInterrupted()) {

            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            while (delta >= 1) {
                if (gameStatus == GameStatus.RUNNING) {
                    gameLoop();
                }
                delta--;
            }
            render();

            if(gameStatus != GameStatus.RUNNING) {
                timer = System.currentTimeMillis();
            }

            if (System.currentTimeMillis() - timer > 1000) {
                timer += 1000;
                mapManager.updateTime();
            }
        }
    }
    //vẽ lại giao diện của trò chơi.
    private void render() {
        uiManager.repaint();
    }

    // mô tả các hoạt động trong vòng lặp game
    private void gameLoop(){
        updateLocations();
        checkCollisions();
        updateCamera();

        if (isGameOver()) {
            setGameStatus(GameStatus.GAME_OVER);
            try {
                SavePointFile();
            }
            catch (Exception e){

            }

        }

        int missionPassed = passMission();
        if(missionPassed > -1){
            mapManager.acquirePoints(missionPassed);
            //setGameStatus(GameStatus.MISSION_PASSED);
        }
        else if(mapManager.endLevel()){
            setGameStatus(GameStatus.MISSION_PASSED);
            try {
                SavePointFile();
            }
            catch (Exception e){

            }
        }

    }
    // di chuyen vi tri cua camera de hien thi du khung hinh.
    private void updateCamera() {
        Mario mario = mapManager.getMario();
        double marioVelocityX = mario.getVelX();
        double shiftAmount = 0;

        // Di chuyển camera theo Mario khi đi sang phải
        if (marioVelocityX > 0 && mario.getX() - 600 > camera.getX()) {
            shiftAmount = marioVelocityX;
        }

        // Di chuyển camera theo Mario khi đi sang trái
        else if (marioVelocityX < 0 && mario.getX() - 400 < camera.getX()) {
            shiftAmount = marioVelocityX;
        }

        camera.moveCam(shiftAmount, 0);
    }

    private void updateLocations() {
        mapManager.updateLocations();
    }

    private void checkCollisions() {
        mapManager.checkCollisions(this);
    }

    // đây là hàm xử lý các hành động của người chơi trong game
    // dựa và gameStatus : trạng thái game và loại hành động nhận được(ButtonAction): hàm sẽ thực hiện các
    // thao tác tương ứng.
    //receiveInput không được gọi trong vòng lặp chính của game.
    //Nó được gọi bởi InputManager, khi người chơi nhấn phím hoặc dùng chuột.
    //Các thay đổi do receiveInput gây ra sẽ được phản ánh trong vòng lặp chính thông qua các hàm như updateLocations,
    // checkCollisions, hoặc thay đổi vận tốc/động lượng của Mario.
    public void receiveInput(ButtonAction input) {
        // nếu game đang ở màn hình bắt đầu
        if (gameStatus == GameStatus.START_SCREEN) {
            // nếu hành động laf lựa chọn và chọn start_game thì gọi hàm startgame để vào chọn map.
            if (input == ButtonAction.SELECT && startScreenSelection == StartScreenSelection.START_GAME) {
                startGame();
            } else if (input == ButtonAction.SELECT && startScreenSelection == StartScreenSelection.VIEW_ABOUT) {
                setGameStatus(GameStatus.ABOUT_SCREEN);
            } else if (input == ButtonAction.SELECT && startScreenSelection == StartScreenSelection.VIEW_HELP) {
                setGameStatus(GameStatus.HELP_SCREEN);

                // nếu hành động là phím lên/ xuống
            }
            else if(input == ButtonAction.SELECT && startScreenSelection == StartScreenSelection.HIGH_SCORE){
                setGameStatus(GameStatus.HIGH_SCORE);
            }
            else if(input == ButtonAction.SELECT && startScreenSelection == StartScreenSelection.QUIT){
                System.exit(0);
            }
            else if (input == ButtonAction.GO_UP) {
                selectOption(true);
            } else if (input == ButtonAction.GO_DOWN) {
                selectOption(false);
            }
        }
        // nếu đang ở màn hình chọn map
        else if(gameStatus == GameStatus.MAP_SELECTION){
            if(input == ButtonAction.SELECT){
                selectMapViaKeyboard();
            }
            else if(input == ButtonAction.GO_UP){
                //thay doi lua chon, thay doi chi so.
                changeSelectedMap(true);
            }
            else if(input == ButtonAction.GO_DOWN){
                changeSelectedMap(false);
            }
        }
        // khi đang trong giao diện game, thời gian bắt đầu chay.
        else if (gameStatus == GameStatus.RUNNING) {
            ImageLoader imageLoader = new ImageLoader();
            Mario mario = mapManager.getMario();
            if (input == ButtonAction.JUMP) {
                mario.jump(this);
            } else if (input == ButtonAction.M_RIGHT) {
                mario.move(true, camera);
            } else if (input == ButtonAction.M_LEFT) {
                mario.move(false, camera);
            }
            // đây là trạng thái mà mario vừa kết thúc 1 hành động nào đó như : nhảy, chạy ,...
            else if (input == ButtonAction.ACTION_COMPLETED) {
                mario.setVelX(0);
            } else if (input == ButtonAction.FIRE) {
                mapManager.fire(this);
            } else if (input == ButtonAction.PAUSE_RESUME) {
//              pauseGame();
                setGameStatus(GameStatus.PAUSED);
            }
            //test các trạng thái của mario : thường, lửa, super.

            else if(input == ButtonAction.TEST0){
                if(mario.getMarioForm().isSuper() || mario.getMarioForm().isFire()){
                    BufferedImage[] leftFrames = imageLoader.getLeftFrames(MarioForm.SMALL);
                    BufferedImage[] rightFrames = imageLoader.getRightFrames(MarioForm.SMALL);

                    Animation animation = new Animation(leftFrames, rightFrames);
                    MarioForm newForm = new MarioForm(animation, false, false);
                    mario.setMarioForm(newForm);
                    mario.setDimension(48, 48);
                }
            }
            else if(input == ButtonAction.TEST1){
                if(!mario.getMarioForm().isSuper()) {
                    BufferedImage[] leftFrames = imageLoader.getLeftFrames(MarioForm.SUPER);
                    BufferedImage[] rightFrames = imageLoader.getRightFrames(MarioForm.SUPER);

                    Animation animation = new Animation(leftFrames, rightFrames);
                    MarioForm newForm = new MarioForm(animation, true, false);
                    mario.setMarioForm(newForm);
                    mario.setDimension(48, 96);
                }
            }
            else if(input == ButtonAction.TEST2){
                if(!mario.getMarioForm().isFire()) {
                    BufferedImage[] leftFrames = imageLoader.getLeftFrames(MarioForm.FIRE);
                    BufferedImage[] rightFrames = imageLoader.getRightFrames(MarioForm.FIRE);

                    Animation animation = new Animation(leftFrames, rightFrames);
                    MarioForm newForm = new MarioForm(animation, false, true);
                    mario.setMarioForm(newForm);
                    mario.setDimension(48, 96);
                }
            }
        } else if (gameStatus == GameStatus.PAUSED) {
            if (input == ButtonAction.PAUSE_RESUME) {
                pauseGame();
            }
            else if(input == ButtonAction.GO_UP){
                selectOptionPause(true);
            }
            else if(input== ButtonAction.GO_DOWN){
                selectOptionPause(false);
            }
            else if(input== ButtonAction.SELECT){
                if(pauselection == PauseSelection.NEW_GAME){
                    resetCamera();
                    createMap(pathMap);
                }
                else if(pauselection == PauseSelection.RESUME){
                    setGameStatus(GameStatus.RUNNING);
                }
                else if(pauselection == PauseSelection.QUIT){
                    reset();
                }
            }
        } else if(gameStatus == GameStatus.GAME_OVER && input == ButtonAction.GO_TO_START_SCREEN){
            reset();
            soundManager.restartBackground();
        } else if(gameStatus == GameStatus.MISSION_PASSED && input == ButtonAction.GO_TO_START_SCREEN){
            reset();
            soundManager.restartBackground();
        }

        if(input == ButtonAction.GO_TO_START_SCREEN){
            setGameStatus(GameStatus.START_SCREEN);
//            soundManager.restartBackground();
        }
    }

    private void selectOption(boolean selectUp) {
        startScreenSelection = startScreenSelection.select(selectUp);
    }

    private void startGame() {
        if (gameStatus != GameStatus.GAME_OVER) {
            setGameStatus(GameStatus.MAP_SELECTION);
        }
    }

    private void pauseGame() {
        if (gameStatus == GameStatus.RUNNING) {
            setGameStatus(GameStatus.PAUSED);
            soundManager.pauseBackground();
        } else if (gameStatus == GameStatus.PAUSED) {
            setGameStatus(GameStatus.RUNNING);
            soundManager.resumeBackground();
        }
    }

    public void shakeCamera(){
        camera.shakeCamera();
    }

    private boolean isGameOver() {
        if(gameStatus == GameStatus.RUNNING)
            return mapManager.isGameOver();
        return false;
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    // lựa chọn chế độ trong màn hình chính
    public StartScreenSelection getStartScreenSelection() {
        return startScreenSelection;
    }

    //lựa chọn chế độ khi pause game
    public PauseSelection getPauseSelection(){
        return pauselection;
    }
    private void selectOptionPause(boolean selectUp) {
        pauselection=pauselection.select(selectUp);
    }

    public void setGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }

    public int getScore() {
        return mapManager.getScore();
    }

    public int getRemainingLives() {
        return mapManager.getRemainingLives();
    }

    public int getCoins() {
        return mapManager.getCoins();
    }

    public int getSelectedMap() {
        return selectedMap;
    }

    public void drawMap(Graphics2D g2) {
        mapManager.drawMap(g2);
    }

    public Point getCameraLocation() {
        return new Point((int)camera.getX(), (int)camera.getY());
    }

    private int passMission(){
        return mapManager.passMission();
    }
    public void SavePointFile() {
        try (FileWriter pw = new FileWriter(new File("high_score.txt"), true)) {
            int point = getScore();
            pw.write(String.valueOf(point)); // Ghi điểm số dưới dạng chuỗi
            pw.write("\n");
        } catch (IOException e) {
            System.err.println("Không thể lưu điểm vào file: " + e.getMessage());
        }
    }

    public void playCoin() {
        soundManager.playCoin();
    }

    public void playOneUp() {
        soundManager.playOneUp();
    }

    public void playSuperMushroom() {
        soundManager.playSuperMushroom();
    }

    public void playMarioDies() {
        soundManager.playMarioDies();
    }

    public void playJump() {
        soundManager.playJump();
    }

    public void playFireFlower() {
        soundManager.playFireFlower();
    }

    public void playFireball() {
        soundManager.playFireball();
    }

    public void playStomp() {
        soundManager.playStomp();
    }

    public MapManager getMapManager() {
        return mapManager;
    }

    public static void main(String... args) {
        new GameEngine();
    }

    public int getRemainingTime() {
        return mapManager.getRemainingTime();
    }
}
