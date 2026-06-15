package enums;

public enum SecurityQuestions {
    QUESTION1(1 ,"What is your favorite color?"),
    QUESTION2(2 ,"What is your favorite animal?");
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
