package com.javarush.telegram;

public class UserInfo {
    public String name; //Ім'я
    public String sex; //Стать
    public String age; //Вік
    public String city; //Місто
    public String occupation; //Професія
    public String hobby; //Хобі
    public String handsome; //Краса, привабливість
    public String wealth; //Дохід, багатство
    public String annoys; //Мене дратує у людях
    public String goals; //Цілі знайомства

    private String fieldToString(String str, String description) {
        if (str != null && !str.isEmpty())
            return description + ": " + str + "\n";
        else
            return "";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getHobby() {
        return hobby;
    }

    public void setHobby(String hobby) {
        this.hobby = hobby;
    }

    public String getHandsome() {
        return handsome;
    }

    public void setHandsome(String handsome) {
        this.handsome = handsome;
    }

    public String getWealth() {
        return wealth;
    }

    public void setWealth(String wealth) {
        this.wealth = wealth;
    }

    public String getAnnoys() {
        return annoys;
    }

    public void setAnnoys(String annoys) {
        this.annoys = annoys;
    }

    public String getGoals() {
        return goals;
    }

    public void setGoals(String goals) {
        this.goals = goals;
    }

    @Override
    public String toString() {
        String result = "";

        result += fieldToString(name, "Ім'я");
        result += fieldToString(sex, "Стать");
        result += fieldToString(age, "Вік");
        result += fieldToString(city, "Місто");
        result += fieldToString(occupation, "Професія");
        result += fieldToString(hobby, "Хобі");
        result += fieldToString(handsome, "Краса, привабливість у балах (максимум 10 балів)");
        result += fieldToString(wealth, "Доход, богатство");
        result += fieldToString(annoys, "В людях дратує");
        result += fieldToString(goals, "Цілі знайомства");

        return result;
    }
}
