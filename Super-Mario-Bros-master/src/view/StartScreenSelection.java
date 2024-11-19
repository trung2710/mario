package view;

import java.awt.*;

//lớp này được sử dụng để định nghĩa các lựa chọn trên màn hình khởi động của trò chơi.
//mỗi lựa chọn đưuocj đại diện bởi 1 giá trị enum và 1 số dòng tương ứng.
public enum StartScreenSelection {
    //START_GAME : là các hằng số enum.
    //Lựa chọn bắt đầu trò chơi.
    START_GAME(0),
    //xem trợ giúp
    VIEW_HELP(1),
    //xem về trò chơi, about
    VIEW_ABOUT(2),
    //xem top điểm cao
    HIGH_SCORE(3),
    //thoat game
    QUIT(4);
    //lưu trữ số dòng tương ứng với mỗi lựa chọn.
    private final int lineNumber;
    //hàm khởi tạo với số dòng tương ứng.
    StartScreenSelection(int lineNumber){ this.lineNumber = lineNumber; }

    public StartScreenSelection getSelection(int number){
        if(number == 0)
            return START_GAME;
        else if(number == 1)
            return VIEW_HELP;
        else if(number == 2)
            return VIEW_ABOUT;
        else if (number == 3 ){
            return HIGH_SCORE;
        }
        else if(number == 4){
            return QUIT;
        }
        else return null;
    }
    //lựa chọn 3 lựa chọn ở trên.
    public StartScreenSelection select(boolean toUp){
        int selection;
        //nếu là lựa chọn(3 chế độ ở màn hình khi bắt đầu vào game) bằng 3 thì quay trở lại 0, nếu lưạ chọn là -1 thì quay lại 2.
        if(lineNumber > -1 && lineNumber < 5){
            selection = lineNumber - (toUp ? 1 : -1);
            if(selection == -1)
                selection = 4;
            else if(selection == 5)
                selection = 0;
            return getSelection(selection);
        }

        return null;
    }


    public int getLineNumber() {
        return lineNumber;
    }
}
