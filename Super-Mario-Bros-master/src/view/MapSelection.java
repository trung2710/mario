package view;

import java.awt.*;
import java.util.ArrayList;
//Lớp MapSelection được sử dụng để quản lý việc lựa chọn bản đồ trong game.
public class MapSelection {
    //Biến maps là một danh sách lưu trữ tên các file bản đồ (kiểu String).
    private ArrayList<String> maps = new ArrayList<>();
    //Biến mapSelectionItems là một mảng lưu trữ các đối tượng MapSelectionItem
    private MapSelectionItem[]  mapSelectionItems;

    public MapSelection(){
        getMaps();
        this.mapSelectionItems = createItems(this.maps);
    }
    //g: Một đối tượng Graphics được sử dụng để vẽ các thành phần lên màn hình.
    //vẽ màn hình chọn map.
    public void draw(Graphics g){
        //vẽ 1 hình chữ nhật màu đen để xóa toàn bộ nội dung trên màn hình., tạo phông nền màu đen.
        g.setColor(Color.BLACK);
        g.fillRect(0,0, 1280, 720);
        //kiểm tra xem mảng lựa chọn bản đồ có rỗng không.
        if(mapSelectionItems == null){
            System.out.println(1);
            return;
        }
        //vẽ tiêu đề
        String title = "Select a Map";
        //tính toán vị trí ngang để hiển thị tiêu đề giữa màn hình.
        //lấy ra độ dài của tiêu đề với phông chữ hiện tại.
        //lấy ra điểm trái trên cùng của tiêu đề.
        int x_location = (1280 - g.getFontMetrics().stringWidth(title))/2;
        //chọn màu của chữ và bắt đầu vẽ.
        g.setColor(Color.YELLOW);
        g.drawString(title, x_location, 150);


        //Vẽ các lựa chọn bản đồ.
        for(MapSelectionItem item : mapSelectionItems){
            //set màu cho chữ là màu trắng.
            g.setColor(Color.WHITE);
            //tính toán chiều dài và rộng của tên bản đồ với font chữ hiện tại.
            int width = g.getFontMetrics().stringWidth(item.getName().split("[.]")[0]);
            int height = g.getFontMetrics().getHeight();
            //lưu kích thước hiển thị của tên bản đồ.
            item.setDimension( new Dimension(width, height));

            item.setLocation( new Point((1280-width)/2, item.getLocation().y));
            //vẽ lựa chọn bản đồ lên màn hình.
            g.drawString(item.getName().split("[.]")[0], item.getLocation().x, item.getLocation().y);
        }
    }

    private void getMaps(){
        //TODO: read from file
        maps.add("Map 1.png");
        maps.add("Map 2.png");
        maps.add("Map 3.png");
    }

    private MapSelectionItem[] createItems(ArrayList<String> maps){
        if(maps == null)
            return null;

        int defaultGridSize = 100;
        MapSelectionItem[] items = new MapSelectionItem[maps.size()];
        for (int i = 0; i < items.length; i++) {
            Point location = new Point(0, (i+1)*defaultGridSize+200);
            items[i] = new MapSelectionItem(maps.get(i), location);
        }

        return items;
    }

    public String selectMap(Point mouseLocation) {
        for(MapSelectionItem item : mapSelectionItems) {
            Dimension dimension = item.getDimension();
            Point location = item.getLocation();
            //kiểm tra xem con tror chuột có năm ftrong vùng lưauj chọn bản đồ không.
            //Kiểm tra xem tọa độ x của vị trí chuột có nằm trong khoảng từ vị trí x của lựa chọn bản đồ đến vị trí x + chiều rộng của tên bản đồ.
            //Kiểm tra xem tọa độ y của vị trí chuột có nằm trong khoảng từ vị trí y của lựa chọn bản đồ đến vị trí y - chiều cao của tên bản đồ.
            boolean inX = location.x <= mouseLocation.x && location.x + dimension.width >= mouseLocation.x;
            boolean inY = location.y >= mouseLocation.y && location.y - dimension.height <= mouseLocation.y;
            //đúng thì trả về tên bản đồ.
            if(inX && inY){
                return item.getName();
            }
        }
        return null;
    }

    public String selectMap(int index){
        if(index < mapSelectionItems.length && index > -1)
            return mapSelectionItems[index].getName();
        return null;
    }

    public int changeSelectedMap(int index, boolean up) {
        if(up){
            if(index <= 0)
                return mapSelectionItems.length - 1;
            else
                return index - 1;
        }
        else{
            if(index >= mapSelectionItems.length - 1)
                return 0;
            else
                return index + 1;
        }
    }
}
