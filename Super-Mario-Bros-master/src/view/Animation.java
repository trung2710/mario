package view;

import java.awt.image.BufferedImage;
//Lớp Animation là một lớp được thiết kế để quản lý các hoạt hình trong game. Nó giúp tạo ra hiệu ứng chuyển động mượt mà
//cho các nhân vật và vật thể bằng cách thay đổi hình ảnh của chúng theo thời gian.
//Khung hình (frame): Mỗi hình ảnh tĩnh trong một chuỗi hoạt hình được gọi là một khung hình.
// Nghĩ đơn giản, mỗi khung hình là một bức ảnh chụp một tư thế khác nhau của Mario trong quá trình chạy.
//Chuỗi hoạt hình (animation sequence): Là một tập hợp các khung hình được sắp xếp theo một trình tự cụ thể
// để tạo ra ảo giác về chuyển động khi chúng được hiển thị liên tục và nhanh chóng.
public class Animation {
    // index: Một biến nguyên lưu trữ chỉ số của khung hình hiện tại trong chuỗi hoạt hình.
    //count: Một biến nguyên được sử dụng để đếm số khung hình đã được hiển thị kể từ lần hiển thị khung hình trước đó.
    private int index = 0, count = 0;
    //Hai mảng chứa các hình ảnh cho các hoạt hình hướng trái và phải.
    private BufferedImage[] leftFrames, rightFrames;
    private BufferedImage currentFrame;

    public Animation(BufferedImage[] leftFrames, BufferedImage[] rightFrames){
        this.leftFrames = leftFrames;
        this.rightFrames = rightFrames;

        currentFrame = rightFrames[1];
    }

    public BufferedImage animate(int speed, boolean toRight){
        count++;
        BufferedImage[] frames = toRight ? rightFrames : leftFrames;

        //Kiểm tra thời gian: Nếu count vượt quá speed, tức là đã đến lúc chuyển sang khung hình tiếp theo.
        //speed: Đây là một giá trị cố định, đại diện cho tốc độ của hoạt hình. Giá trị này càng nhỏ, hoạt hình diễn ra càng nhanh.
        //speed = 10: Điều này có nghĩa là sau 10 lần cập nhật, chúng ta sẽ chuyển sang khung hình tiếp theo.
        //Tốc độ khung hình (FPS): Giả sử chương trình của bạn chạy ở tốc độ 60 FPS, nghĩa là nó cập nhật màn hình 60 lần mỗi giây.
        //Với speed = 10 và FPS = 60, thời gian để chuyển sang khung hình tiếp theo là 10 * (1/60) giây = 1/6 giây.
        //speed là một tham số điều chỉnh tốc độ của hoạt hình, chứ không phải thời gian hiển thị một khung hình cụ thể.
        if(count > speed){
            nextFrame(frames);
            count = 0;
        }

        return currentFrame;
    }
    //hàm chuyển đến khung hình tiếp theo trong 1 chuỗi hoạt hinh
    private void nextFrame(BufferedImage[] frames) {
        //Hàm kiểm tra xem chỉ số index hiện tại cộng với 3 có vượt quá số lượng khung hình trong chuỗi hay không.
        //Nếu vượt quá, điều này có nghĩa là ta đã đến cuối chuỗi và cần quay lại đầu để bắt đầu lại chuỗi hoạt hình.
        if(index + 3 > frames.length)
            index = 0;
        // Lý do cộng thêm 2 vào index là để tạo ra một khoảng cách nhất định giữa các khung hình được hiển thị,
        // giúp cho hoạt hình trông tự nhiên hơn.
        currentFrame = frames[index+2];
        index++;
    }

    public BufferedImage[] getLeftFrames() {
        return leftFrames;
    }

    public BufferedImage[] getRightFrames() {
        return rightFrames;
    }

}
