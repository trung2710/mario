package view;

//Khi bạn khai báo một enum như PauseSelection, các hằng số (NEW_GAME, RESUME, QUIT)
// được khởi tạo một lần khi lớp enum được tải.
//Mỗi hằng số được khởi tạo với giá trị của thuộc tính lineNumber thông qua hàm khởi tạo PauseSelection(int lineNumber).
//NEW_GAME có lineNumber = 0.
//RESUME có lineNumber = 1.
//QUIT có lineNumber = 2.
public enum PauseSelection {
    // Các hằng số enum
    NEW_GAME(0),
    RESUME(1),
    QUIT(2);

    // Thuộc tính
    private final int lineNumber;

    // Hàm khởi tạo
    PauseSelection(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    // Trả về lựa chọn tương ứng với số
    public PauseSelection getSelection(int number) {
        if (number == 0) return NEW_GAME;
        if (number == 1) return RESUME;
        if (number == 2) return QUIT;

        return null;
    }

    // Lựa chọn di chuyển lên hoặc xuống
    public PauseSelection select(boolean toUp) {
        int selection;
        if(lineNumber>-1 && lineNumber<3){
            selection=lineNumber-(toUp ? 1 : -1);
            if(selection==-1){
                selection=2;
            }
            else if(selection==3){
                selection=0;
            }
            return getSelection(selection);
        }
        return null;
    }

    public int getLineNumber() {
        return lineNumber;
    }
}