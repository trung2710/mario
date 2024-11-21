package manager;
//Lớp này quản lý vị trí của camera trong trò chơi và cung cấp chức năng để di chuyển và lắc camera.
public class Camera {
    //x , y là tọa độ của camera trong không gian 2d
    private double x, y;
    //biến này dùng để đếm số khung hình còn lại khi camera đang lắc.
    private int frameNumber;
    ////biến này cho biết liệu camera có đang lắc hay không.
    private boolean shaking;

    public Camera(){
        this.x = 0;
        this.y = 0;
        this.frameNumber = 25;
        this.shaking = false;
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
    //kích hoạt chế độ lắc của camera, và camera sẽ lắc trong 60 khung hình.
    public void shakeCamera() {
        shaking = true;
        frameNumber = 60;
    }

    public void moveCam(double xAmount, double yAmount){
        //camera sẽ lắc di chuyển qua lại trục Ox với biên độ 4 đơn vị
        //hướng di chuyển mỗi khung hình tùy thuộc vào camera chẵn hay lẽ
        //Khung hình (frame) được đề cập trong đoạn mã Camera không phải là khung hình cụ thể của một đối tượng mà
        // là khái niệm khung hình trong trò chơi. Đây là đơn vị thời gian trong vòng lặp game loop, nơi các cập nhật
        // và render được thực hiện.
        if(shaking && frameNumber > 0){
            int direction = (frameNumber%2 == 0)? 1 : -1;
            x = x + 4 * direction;
            //sau mỗi khung hình thì frameNumber còn lại giảm đi 1.
            frameNumber--;
        }
        //nếu camera không lắc thì nó sẽ di chuyển theo các giá trị tham số đã được truyền vào hàm.
        else{
            x = x + xAmount;
            y = y + yAmount;
        }
        //dừng lắc camera khi frameNumber nhỏ hơn 0.
        if(frameNumber < 0)
            shaking = false;

        // Giới hạn camera không đi ra ngoài vùng bản đồ
        if (x < 0) x = 0; // Không cho phép di chuyển ra ngoài biên trái
        if (y < 0) y = 0; // Không cho phép di chuyển ra ngoài biên trên
    }
}
