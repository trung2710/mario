package manager;

import model.GameObject;
import model.Map;
import model.brick.Brick;
import model.brick.OrdinaryBrick;
import model.enemy.Enemy;
import model.enemy.KoopaTroopa;
import model.hero.Fireball;
import model.hero.Mario;
import model.prize.BoostItem;
import model.prize.Coin;
import model.prize.Prize;
import view.ImageLoader;

import java.awt.*;
import java.util.ArrayList;
//Đoạn mã trên là một lớp MapManager có nhiệm vụ quản lý các chức năng liên quan đến bản đồ trong một trò chơi,
// bao gồm cập nhật vị trí của các đối tượng, kiểm tra va chạm, xử lý các vật phẩm, và điều khiển các hành động của
// nhân vật chính (Mario).
public class MapManager {

    public  Map map;

    public MapManager() {}

    public void updateLocations() {
        if (map == null)
            return;
        //cập nhật lại trang thái, vị trí của map.
        //ở đây là cập nhật lại vị trí và trạng thái của các đối tượng như mario, các enemy.
        map.updateLocations();
    }

    public void resetCurrentMap(GameEngine engine) {
        Mario mario = getMario();
        mario.setToRight(true);
        mario.resetLocation();
        engine.resetCamera();
        // đây là câu lệnh mà khi mario chết 1 mạng thì reset lại toàn bộ map, khôi phục lại các con quái và các phần thưởng đã ăn.
        //createMap(engine.getImageLoader(), map.getPath());
        map.setMario(mario);
    }

    public boolean createMap(ImageLoader loader, String path) {
        MapCreator mapCreator = new MapCreator(loader);
        map = mapCreator.createMap("/maps/" + path, 400);

        return map != null;
    }

    public void acquirePoints(int point) {
        map.getMario().acquirePoints(point);
    }

    public Mario getMario() {
        return map.getMario();
    }

    //ham tao ra lua ban ra khi mario o dang lua.
    public void fire(GameEngine engine) {
        Fireball fireball = getMario().fire();
        if (fireball != null) {
            map.addFireball(fireball);
            engine.playFireball();
        }
    }

    public boolean isGameOver() {
        return getMario().getRemainingLives() == 0 || map.isTimeOver();
    }

    public int getScore() {
        return getMario().getPoints();
    }

    public int getRemainingLives() {
        return getMario().getRemainingLives();
    }

    public int getCoins() {
        return getMario().getCoins();
    }

    //vẽ map đã được lựa chọn.
    public void drawMap(Graphics2D g2) {
        map.drawMap(g2);
    }

    //kiểm tra xem mario đã vượt qua mốc kết thúc trò chơi hay chưa.
    public int passMission() {
        if(getMario().getX() >= map.getEndPoint().getX() && !map.getEndPoint().isTouched()){
            map.getEndPoint().setTouched(true);
            int height = (int)getMario().getY();
            //điểm số của mario nhận được khi chạm vào cờ, kết thúc trò chơi
            // điểm số phụ thuộc vào chiều cao của mario khi chạm vào cờ.
            return height * 2;
        }
        else
            return -1;
    }
    //Kiểm tra xem Mario đã vượt qua điểm kết thúc (vượt qua 320 đơn vị) chưa. Nếu có, màn chơi kết thúc.
    public boolean endLevel(){
        return getMario().getX() >= map.getEndPoint().getX() + 320;
    }

    //Kiểm tra va chạm của Mario, kẻ thù, vật phẩm, và quả cầu lửa.
    public void checkCollisions(GameEngine engine) {
        if (map == null) {
            return;
        }

        checkBottomCollisions(engine);
        checkTopCollisions(engine);
        checkMarioHorizontalCollision(engine);
        checkEnemyCollisions();
        checkPrizeCollision(engine);
        checkPrizeContact(engine);
        checkFireballContact();
    }
    //Kiểm tra va chạm ở phía dưới của Mario, xem liệu Mario có va chạm với các viên gạch (bricks) hoặc kẻ thù (enemies).
    // Cập nhật vị trí và hành động của Mario khi có va chạm.
    private void checkBottomCollisions(GameEngine engine) {
        Mario mario = getMario();
        ArrayList<Brick> bricks = map.getAllBricks();
        ArrayList<Enemy> enemies = map.getEnemies();
        ArrayList<GameObject> toBeRemoved = new ArrayList<>();

        Rectangle marioBottomBounds = mario.getBottomBounds();

        if (!mario.isJumping())
            mario.setFalling(true);

        //khi mario nhảy lên mà vượt quá giới hạn cả khung hình ở trên thì set vận tốc bằng 0.
        // không cho mario khi nhảy lên thì vượt ra khỏi khng hình.
        if(mario.isJumping()){
            if(mario.getY()<=0){
                mario.setY(1);
                mario.setVelY(0);
                mario.setFalling(true);
                mario.setJumping(false);
            }
        }


        for (Brick brick : bricks) {
            //lấy vùng giới hạn phía trên của viên gạch
            Rectangle brickTopBounds = brick.getTopBounds();
            //kiểm tra xem nếu vùng giới hạn phía dưới của mario giao với vùng  giới hạn phía trên của viên gạch.
            //hàm intersects là hàm kiểm tra va chạm.
            if (marioBottomBounds.intersects(brickTopBounds)) {
                // đặt vị trí của mario ngay phía trên viên gạch sao cho mario không xuyên qua viên gạch.
                //Nếu không cộng thêm 1, đôi khi Mario có thể bị coi là vẫn đang va chạm với viên gạch do sai số nhỏ trong tính toán.
                mario.setY(brick.getY() - mario.getDimension().height + 1);
                //đặt lại trạng thái cho mario thành không rơi
                mario.setFalling(false);
                //đặt vector theo trục y thành 0
                mario.setVelY(0);
            }
        }
        //xử lí va chạm với quái.
        for (Enemy enemy : enemies) {
            //lấy vùng giới hạn phía trên của enemy
            Rectangle enemyTopBounds = enemy.getTopBounds();
            // kiểm tra xem nếu vùng dưới của mario có va chạm với vùng trên của kẻ thù hay không.
            if (marioBottomBounds.intersects(enemyTopBounds)) {
                //nếu va chạm thì cộng thêm 100 điểm.
                mario.acquirePoints(100);
                // cho quái vào danh sách cần xóa
                toBeRemoved.add(enemy);
                //phát ra âm thanh tiêu diệt kẻ thù.
                engine.playStomp();
            }
        }
        // xử lí ra chạm với đường biên.
        if (mario.getY() + mario.getDimension().height >= map.getBottomBorder()) {
            //set lại tọa độ cho mairo
            mario.setY(map.getBottomBorder() - mario.getDimension().height);
            //đặt lại trạng thái rơi bằng false
            mario.setFalling(false);
            //đặt vận tốc theo phương y bằng 0
            mario.setVelY(0);
        }
        // thêm các đối tượng cần xóa vào hàm xóa đối tượng.
        removeObjects(toBeRemoved);
    }
    //Kiểm tra va chạm phía trên của Mario, như khi Mario nhảy vào viên gạch (brick).
    private void checkTopCollisions(GameEngine engine) {
        Mario mario = getMario();
        ArrayList<Brick> bricks = map.getAllBricks();
        // lấy vùng giới hạn phía trên của mario
        Rectangle marioTopBounds = mario.getTopBounds();
        for (Brick brick : bricks) {
            // lấy vùng giới hạn phía dưới của gạch
            Rectangle brickBottomBounds = brick.getBottomBounds();
            //nếu mà xảy ra va chạm
            if (marioTopBounds.intersects(brickBottomBounds)) {

                mario.setVelY(0);
                //cập nhật vị trí cho mario đặt ngay bên dưới viên gạch
                mario.setY(brick.getY() + brick.getDimension().height);
                //kiểm tra xem viên gạch có phânf thưởng hay không.
                // nếu là gạch vỡ được thì sẽ gọi hiêuj ứng vỡ cả gạch
                // nếu là surpriseBirk thì sẽ xuất hiện prize, và biến viên gạch đó thành hình ảnh
                // 1 viên gạch trống.
                Prize prize = brick.reveal(engine);
                //nếu mà có phần thưởng, thêm phàn thưởng vào danh sách phần thưởng trên bản đồ.
                if(prize != null)
                    map.addRevealedPrize(prize);
            }
        }
    }
    // Kiểm tra va chạm theo chiều ngang của Mario, xem liệu Mario có va chạm với
    // các viên gạch hoặc kẻ thù ở phía trái hay phải không.
    private void checkMarioHorizontalCollision(GameEngine engine){
        Mario mario = getMario();
        ArrayList<Brick> bricks = map.getAllBricks();
        ArrayList<Enemy> enemies = map.getEnemies();
        ArrayList<GameObject> toBeRemoved = new ArrayList<>();
        //biến true/false kiểm tra xem mario đã đie chưa.
        // chết kiểu trừ mạng.
        boolean marioDies = false;
        // xác định hướng di chuyển của mario
        boolean toRight = mario.getToRight();
        //lấy vùng giới hạn của mario tùy theo hướng di chuyển.
        Rectangle marioBounds = toRight ? mario.getRightBounds() : mario.getLeftBounds();
        //kiểm tra va chạm với gạch.
        for (Brick brick : bricks) {
            //lấy vùng giới hạn của gạch tùy theo hướng di chuyển của mario(ngược lại với mario).
            Rectangle brickBounds = !toRight ? brick.getRightBounds() : brick.getLeftBounds();
            //nếu mario va chạm với gạch
            if (marioBounds.intersects(brickBounds)) {
                //cho vận tốc ngang của mairo bằng 0
                mario.setVelX(0);
                //set lại vị trí của mario theo hướng di chuyển.
                //2 trường hợp này đều đặt mario sát viên gạch.
                if(toRight)
                    //tọa độ x của mario sẽ là tọa độ x của viên gạch trừ đi chiều rộng của nhận vật mario.
                    mario.setX(brick.getX() - mario.getDimension().width);
                else
                    //nếu mario di chuyển sang bên trái thì tọa độ của mario
                    // bằng tọa độ của viên gạch cộng với chiều rộng của viên gạch
                    mario.setX(brick.getX() + brick.getDimension().width);
            }
        }
        //xử lí va chạm với kẻ thù.
        for(Enemy enemy : enemies){
            Rectangle enemyBounds = !toRight ? enemy.getRightBounds() : enemy.getLeftBounds();
            //nếu mairo va chạm với quái
            // nếu mairo ở trạng thái thường thì--> died
            //nếu mario ở trạng thái khác--> trở về trạng thái thường.
            if (marioBounds.intersects(enemyBounds)) {
                marioDies = mario.onTouchEnemy(engine);
                //nếu mario chết thì thêm mario vào mảng các đồi tượng cần xóa trong map.
                toBeRemoved.add(enemy);
            }
        }
        // gọi hàm xóa các đối tượng trong mảng.
        removeObjects(toBeRemoved);

        //giới hạn mario trong vùng camera
        //kiểm tra xem nếu mario đi ra ngoài vùng của camera và đang di chuyển về bên trái
        if (mario.getX() <= engine.getCameraLocation().getX() && mario.getVelX() < 0) {
            //đặt vận tốc của camera bằng 0 để dừng lại.
            mario.setVelX(0);
            //đặt lại tọa độ x của mairo bằng vị trí x của camera
            mario.setX(engine.getCameraLocation().getX());
        }
        //nếu mario chết thì đặt lại map và vị trí của mario
        if(marioDies) {
            map.updateTime(100);
            resetCurrentMap(engine);
        }
    }
    //xử lí và kiểm tra va chạm của các đối tượng kẻ thù với gach và các giới hạn chyển động.
    private void checkEnemyCollisions() {
        ArrayList<Brick> bricks = map.getAllBricks();
        ArrayList<Enemy> enemies = map.getEnemies();
        Mario mario=getMario();

        for (int i=0;i<enemies.size();i++) {
            Enemy enemy=enemies.get(i);
            // biến kiểm tra xem kẻ thù có ở đứng ở trên gạch hay không.
            boolean standsOnBrick = false;

            //test trường hợp enemy bị thu hút bởi mario như kiểu di chuyển vào lại gần mario
            // Khoảng cách giữa Mario và enemy
            double distanceToMario = Math.abs(mario.getX() - enemy.getX());

            // Kiểm tra xem có chướng ngại vật giữa Mario và kẻ thù không
            boolean hasObstacleBetween = isObstacleBetweenMarioAndEnemy(mario, enemy, bricks);
            // Kiểm tra Mario có đứng trên gạch không
            boolean marioStandingOnBrick = isMarioStandingOnBrick(mario, bricks);


            // Nếu khoảng cách nhỏ hơn hoặc bằng 300 và không có chướng ngại vật, kẻ thù sẽ bị thu hút và đổi hướng
            // chỉ xét khi mario đứng ngang hàng với enemy
            if (distanceToMario <= 500 && !hasObstacleBetween && !marioStandingOnBrick && mario.getY()==enemy.getY()) {
                if (mario.getX() > enemy.getX() && enemy.getVelX() < 0) {
                    enemy.setVelX(-enemy.getVelX());
                } else if (mario.getX() < enemy.getX() && enemy.getVelX() > 0) {
                    enemy.setVelX(-enemy.getVelX());
                }

            }
            for (Brick brick : bricks) {
                //lấy vùng giới hạn bên trái kẻ thù
                Rectangle enemyBounds = enemy.getLeftBounds();
                //lấy vùng giới hạn bên phải gạch
                Rectangle brickBounds = brick.getRightBounds();
                //lấy vùng giới hạn đáy của kẻ thù
                Rectangle enemyBottomBounds = enemy.getBottomBounds();
                //lấy vùng giới hạn bên trên của gạch
                Rectangle brickTopBounds = brick.getTopBounds();

                //kiểm tra xem nếu quái di chuyển sang bên phải.
                if (enemy.getVelX() > 0) {
                    //lấy vùng giới hạn bên phải của quái.
                    enemyBounds = enemy.getRightBounds();
                    //lấy vùng giới hạn bên trái của gạch.
                    brickBounds = brick.getLeftBounds();
                }
                // nế là con vịt, vì đầu nó dài nên đôi khi chạm vào bottom cả gạch nên phải đổi chiều .
                if(enemy instanceof KoopaTroopa){
                    if(enemy.getTopBounds().intersects(brick.getBottomBounds())){
                        enemy.setVelX(-enemy.getVelX());
                    }
                }
                // nếu quái mà va chạm với gạch
                if (enemyBounds.intersects(brickBounds)) {
                    //thì quái sẽ di chuyển theo hướng ngược lại sang bên trái
                    //với cùng vận tốc ban đầu. (ngược hướng ban đầu)
                    enemy.setVelX(-enemy.getVelX());
                }
                //kiểm tra va chạm theo hướng dọc
                // nếu vùng dưới của quái va chạm với vùng trên của gạch
                if (enemyBottomBounds.intersects(brickTopBounds)){
                    //ngừng rơi
                    enemy.setFalling(false);
                    // vector theo phương y bằng 0
                    enemy.setVelY(0);
                    // cập nhật lại vị trí y của quái.
                    enemy.setY(brick.getY()-enemy.getDimension().height);
                    //quái đang đưngs trên viên gạch.
                    standsOnBrick = true;
                }
            }
            //nếu quái vượt qua ranh giới đáy của bản đồ, (đang ở dưới sàn)
            if(enemy.getY() + enemy.getDimension().height > map.getBottomBorder()){
                enemy.setFalling(false);
                enemy.setVelY(0);
                enemy.setY(map.getBottomBorder()-enemy.getDimension().height);
            }
            //nếu quái không đứng trên gạch và vị trí y của quái bé hơn vị trí y của nền
            //-> đang rơi
            if (!standsOnBrick && enemy.getY() < map.getBottomBorder()){
                enemy.setFalling(true);
            }
            // Kiểm tra va chạm giữa các quái
            //cho 2 con quái va chạm với nhau thì đẩy nhau
//            for(int j=i+1;j<enemies.size();j++) {
//                Enemy e2=enemies.get(j);
//                if (e2!=enemy && enemy.getBounds().intersects(e2.getBounds())) {
//                    // Đổi chiều di chuyển của cả hai con quái
//                    enemy.setVelX(-enemy.getVelX());
//                    e2.setVelX(-e2.getVelX());
//                }
//            }


        }
    }
    // Phương thức kiểm tra xem có chướng ngại vật nào giữa Mario và enemy hay không
    private boolean isObstacleBetweenMarioAndEnemy(Mario mario, Enemy enemy, ArrayList<Brick> bricks) {
        // Lấy tọa độ bắt đầu và kết thúc giữa Mario và enemy
        int marioX = (int)mario.getX();
        int enemyX = (int)enemy.getX();

        // Đảm bảo rằng ta luôn kiểm tra từ trái sang phải
        int left = Math.min(marioX, enemyX);
        int right = Math.max(marioX, enemyX);

        // Kiểm tra các gạch nằm giữa Mario và enemy
        for (Brick brick : bricks) {
            // Nếu gạch nằm trong phạm vi giữa Mario và enemy và nó cản trở đường đi
            if (brick.getX() > left && brick.getX() < right) {
                // Nếu gạch có chiều cao đủ để chắn giữa Mario và enemy (khoảng cách theo trục Y)
                if (brick.getY() < Math.max(mario.getY(), enemy.getY())) {
                    return true; // Có chướng ngại vật
                }
            }
        }

        return false; // Không có chướng ngại vật
    }
    //kiem tra xem mario co dung tren 1 vien gach nao khong.
    private boolean isMarioStandingOnBrick(Mario mario, ArrayList<Brick> bricks) {
        Rectangle marioBounds = mario.getBounds();
        for (Brick brick : bricks) {
            // Kiểm tra xem Mario có đứng trên gạch hay không
            if (marioBounds.intersects(brick.getBounds())) {
                // Nếu Mario đang trên gạch, trả về true
                return true;
            }
        }
        return false;
    }

    private void checkPrizeCollision(GameEngine engine) {
        ArrayList<Prize> prizes = map.getRevealedPrizes();
        ArrayList<Brick> bricks = map.getAllBricks();

        for (Prize prize : prizes) {
            if (prize instanceof BoostItem) {
                //kiểm tra xem phần thưởng hiện tại có phải BóostItem(loại phần thưởng có rơi/di chuyển)

                BoostItem boost = (BoostItem) prize;
                Rectangle prizeBottomBounds = boost.getBottomBounds();
                Rectangle prizeRightBounds = boost.getRightBounds();
                Rectangle prizeLeftBounds = boost.getLeftBounds();
                //đặt trạng thái rơi.
                boost.setFalling(true);

                //neu nam di qua gioi han ben trai thi doi chieu chuyen dong
                if(((BoostItem) prize).getVelX()<0 && ((BoostItem) prize).getX()<=engine.getCameraLocation().getX()){
                    ((BoostItem) prize).setVelX(-((BoostItem) prize).getVelX());
                }
                //xử lí va chạm boostIntem và gạch.
                for (Brick brick : bricks) {
                    Rectangle brickBounds;
                    //nếu boostitem đang rơi
                    // kiểm tra va chạm theo chiều dọc
                    if (boost.isFalling()) {
                        //lấy vùng giới hạn bên trên của gạch
                        brickBounds = brick.getTopBounds();
                        //nếu đáy của boostitem va chạm với đỉnh viên gạch-> dừng rơi
                        //vector theo phương y bằng 0
                        if (brickBounds.intersects(prizeBottomBounds)) {
                            boost.setFalling(false);
                            boost.setVelY(0);
                            // thiết lập lại vi trí y cho boostitem: ở trên viên gạch.
                            boost.setY(brick.getY() - boost.getDimension().height + 1);
                            // nếu boostitem chưa di chuyển ngang thì vận tốc di chuyển ngang bằng 2
                            if (boost.getVelX() == 0)
                                boost.setVelX(2);
                        }
                    }

                    //kiểm tra va chạm theo chiều ngang
                    //nếu va chạm với gạch thì đảo chiều chuyển động.
                    if (boost.getVelX() > 0) {
                        brickBounds = brick.getLeftBounds();
                        //nếu va chạm với với gạch thì đảo chiều chuyển động.
                        if (brickBounds.intersects(prizeRightBounds)) {
                            boost.setVelX(-boost.getVelX());
                        }
                    } else if (boost.getVelX() < 0) {
                        brickBounds = brick.getRightBounds();

                        if (brickBounds.intersects(prizeLeftBounds)) {
                            boost.setVelX(-boost.getVelX());
                        }
                    }
                }

                //xử lí va chạm với đáy của bản đồ(mặt nền)
                //set vị trí y của boostitem bằng với mặt nền, boostitem di chuyển trên nền
                if (boost.getY() + boost.getDimension().height > map.getBottomBorder()) {
                    boost.setFalling(false);
                    boost.setVelY(0);
                    boost.setY(map.getBottomBorder() - boost.getDimension().height);
                    if (boost.getVelX() == 0)
                        boost.setVelX(2);
                }


            }
        }
    }


    // kiểm tra sự va chạm của mario và Prize
    private void checkPrizeContact(GameEngine engine) {
        ArrayList<Prize> prizes = map.getRevealedPrizes();
        ArrayList<GameObject> toBeRemoved = new ArrayList<>();

        //nếu vùng giới hạn của mario va chạm với vùng giới hạn của prize
        Rectangle marioBounds = getMario().getBounds();
        for(Prize prize : prizes){
            Rectangle prizeBounds = prize.getBounds();
            //nếu va chạm thif thực hiện các thay đổi đối với mario : ăn xu, tăng mạng, biến thành bất thử hay , firemario, supermario
            // hàm onTouch : sẽ thực hiện các thay đổi này.
            //  thêm các prize vào danh sách xóa của map
            if (prizeBounds.intersects(marioBounds)) {
                prize.onTouch(getMario(), engine);
                toBeRemoved.add((GameObject) prize);
            }
            //nếu không va chạm thì là ăn xu.
            else if(prize instanceof Coin){
                prize.onTouch(getMario(), engine);
            }
        }

        removeObjects(toBeRemoved);
    }

    //kiểm tra sự tương tác của quả cầu  lửa.
    // nếu quả cầu  lửa va chạm với quái: tăng điểm cho mario, quái vào quả cầu lửa đều biến mất
    // nếu quả cầu lửa va chạm với tường thì quả câù lửa biến mất.
    private void checkFireballContact() {
        ArrayList<Fireball> fireballs = map.getFireballs();
        ArrayList<Enemy> enemies = map.getEnemies();
        ArrayList<Brick> bricks = map.getAllBricks();
        ArrayList<GameObject> toBeRemoved = new ArrayList<>();

        for(Fireball fireball : fireballs){
            Rectangle fireballBounds = fireball.getBounds();

            for(Enemy enemy : enemies){
                Rectangle enemyBounds = enemy.getBounds();
                if (fireballBounds.intersects(enemyBounds)) {
                    acquirePoints(100);
                    toBeRemoved.add(enemy);
                    toBeRemoved.add(fireball);
                }
            }

            for(Brick brick : bricks){
                Rectangle brickBounds = brick.getBounds();
                if (fireballBounds.intersects(brickBounds)) {
                    toBeRemoved.add(fireball);
                }
            }
        }

        removeObjects(toBeRemoved);
    }
    //xóa các đối tượng ra khỏi bản đồ.
    private void removeObjects(ArrayList<GameObject> list){
        if(list == null)
            return;

        for(GameObject object : list){
            if(object instanceof Fireball){
                map.removeFireball((Fireball)object);
            }
            else if(object instanceof Enemy){
                map.removeEnemy((Enemy)object);
            }
            else if(object instanceof Coin || object instanceof BoostItem){
                map.removePrize((Prize)object);
            }
        }
    }
    //thêm các viên gạch đã bị phá vỡ vào danh sách.
    public void addRevealedBrick(OrdinaryBrick ordinaryBrick) {
        map.addRevealedBrick(ordinaryBrick);
    }

    // hàm cập nhật thời gian còn lại.
    public void updateTime(){
        if(map != null)
            map.updateTime(1);
    }
    //lấy ra thời gian còn lại
    public int getRemainingTime() {
        return (int)map.getRemainingTime();
    }
}
