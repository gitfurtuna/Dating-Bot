package com.datingbot.telegram;

public class UserInfo {
    public String name;
    public String sex;
    public String age;
    public String city;
    public String occupation;
    public String hobby;
    public String handsome;
    public String wealth;
    public String annoys;
    public String goals;

    private String fieldToString(String str, String description) {
        if (str != null && !str.isEmpty())
            return description + ": " + str + "\n";
        else
            return "";
    }

    @Override
    public String toString() {
        String result = "";

        result += fieldToString(name, "Имя");
        result += fieldToString(sex, "Пол");
        result += fieldToString(age, "Возраст");
        result += fieldToString(city, "Город");
        result += fieldToString(occupation, "Профессия");
        result += fieldToString(hobby, "Хобби");
        result += fieldToString(handsome, "Красота, привлекательность в баллах (максимум 10 баллов)");
        result += fieldToString(wealth, "Доход, богатство");
        result += fieldToString(annoys, "В людях раздражает");
        result += fieldToString(goals, "Цели знакомства");

        return result;
    }
}
