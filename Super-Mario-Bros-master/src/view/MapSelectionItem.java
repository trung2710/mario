package view;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
//Lớp MapSelectionItem được sử dụng để lưu trữ thông tin của một lựa chọn bản đồ trong giao diện chọn bản đồ.
public class MapSelectionItem {
    // biến lưu trữ hình ảnh thu nhỏ của bản đồ.
    private BufferedImage image;
    //ten bản đồ.
    private String name;
    //lưu trữ vị trí hiển thị của lựa chọn bản đồ (kiểu Point).: tọa độ x, y.
    private Point location;
    //biến dimension lưu trữ kích thước của tên bản đồ khi vẽ (kiểu Dimension).: chiều rộng, chiều cao.
    private Dimension dimension;

    public MapSelectionItem(String map, Point location){
        this.location = location;
        this.name = map;
        //Sử dụng ImageLoader để tải ảnh bản đồ dựa trên đường dẫn được ghép với tên bản đồ (/maps/ + map).
        //Gán ảnh được tải cho biến image.
        //Khởi tạo một đối tượng Dimension rỗng cho biến dimension (sẽ được cập nhật sau).
        ImageLoader loader = new ImageLoader();
        this.image = loader.loadImage("/maps/" + map);

        this.dimension = new Dimension();
    }


    public String getName() {
        return name;
    }

    public Point getLocation() {
        return location;
    }

    public Dimension getDimension() {
        return dimension;
    }

    public void setDimension(Dimension dimension) {
        this.dimension = dimension;
    }

    public void setLocation(Point location) {
        this.location = location;
    }
}
