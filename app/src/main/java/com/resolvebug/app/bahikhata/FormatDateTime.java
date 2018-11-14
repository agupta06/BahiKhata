package com.resolvebug.app.bahikhata;

public class FormatDateTime {

    public String formatDate(String selectedDate) {
        String replaceString = selectedDate.replace(",", "");
        String[] splitting = replaceString.split(" ");
        String month = splitting[0];
        String currentDate = splitting[1];
        String currentYear = splitting[2];
        int currentMonth = 0;
        switch (month) {
            case "Janurary":
                currentMonth = 1;
                break;
            case "February":
                currentMonth = 2;
                break;
            case "March":
                currentMonth = 3;
                break;
            case "April":
                currentMonth = 4;
                break;
            case "May":
                currentMonth = 5;
                break;
            case "June":
                currentMonth = 6;
                break;
            case "July":
                currentMonth = 7;
                break;
            case "August":
                currentMonth = 8;
                break;
            case "September":
                currentMonth = 9;
                break;
            case "October":
                currentMonth = 10;
                break;
            case "November":
                currentMonth = 11;
                break;
            case "December":
                currentMonth = 12;
                break;
        }
        return currentDate + "." + currentMonth + "." + currentYear;
    }

    public String[] formatTime(String selectedTime) {
        String[] splittedTimeFormat = selectedTime.split(" ");
        String[] splittedTime = splittedTimeFormat[0].split(":");
        String currentHour = splittedTime[0];
        String currentMinute = splittedTime[1];
        String currentSecond = splittedTime[2];
        int selectedHour = 0;
        if (splittedTimeFormat[1].equals("PM")) {
            selectedHour = Integer.parseInt(currentHour) + 12;
        } else {
            selectedHour = Integer.parseInt(currentHour);
        }
        if(Integer.toString(selectedHour).length()==1){
            currentHour = "0" + Integer.toString(selectedHour);
        }
        String chosenTime = currentHour + ":" + currentMinute + ":" + currentSecond;
        String timeZone = splittedTimeFormat[2];
        String[] formattedTime = new String[2];
        formattedTime[0] = chosenTime;
        formattedTime[1] = timeZone;
        return formattedTime;
    }
}
