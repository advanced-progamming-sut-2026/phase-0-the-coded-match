package PvZ2.APproject.enums;

public enum SecurityQuestions {
    QUESTION1(1 ,"What is your favorite color?"),
    QUESTION2(2 ,"What is your favorite animal?"),
    QUESTION3(3, "What year were you born?");
    private final int num;
    private final String text;

    SecurityQuestions(int num, String text) {
        this.num = num;
        this.text = text;
    }

    public int getNum() {
        return num;
    }

    public String getText() {
        return text;
    }
}
