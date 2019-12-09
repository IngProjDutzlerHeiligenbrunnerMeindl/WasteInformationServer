package com.wasteinformationserver;

import com.wasteinformationserver.basicutils.Log;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Scanner;

public class Dateget {
    private int index = 0;
    ArrayList<String> list = new ArrayList<>();
    ArrayList<String> listnew = new ArrayList<>();
    public String nextDate;


    public void getdata() {

        GregorianCalendar now = new GregorianCalendar();
        DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
        String datum = df.format(now.getTime());

        URL url = null;
        try {
            url = new URL("https://www.steyr.at/system/web/kalender.aspx?vdatum=" + datum + "&bdatum=19.10.2019&typ=&typid=0&typids=225781950&detailonr=0&menuonr=225781812");
            Scanner scanner = new Scanner(new InputStreamReader(url.openStream()));

            int n = 0;
            while (scanner.hasNext()) {
                String temp = scanner.next();
                addList(temp);
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Filter();
    }

    private void addList(String temp) {
        list.add(index, temp);
    }

    public void printList() {
        for (int n = 0; n < list.size(); n++) {
            Log.debug(list.get(n));
        }
    }

    public void printListnew() {
        for (int n = 0; n < listnew.size(); n++) {
            Log.debug(listnew.get(n));
        }
    }

    private void Filter() {
        String temp = "href=\"/system/web/kalender.aspx?detailonr=225781954-6&amp;menuonr=225781812\">Hausabfall";
        int counter = 0;

        for (int n = 0; n < list.size(); n++) {
            if (list.get(n).equals(temp)) {
                counter++;

                if (counter == 4) {

                    int zaehler = 0;

                    for (int v = n; v < list.size(); v++) {
                        listnew.add(zaehler, list.get(v));
                        zaehler++;
                    }

                  /*  String string = "004-034556";
                    String[] parts = string.split("-");
                    String part1 = parts[0]; // 004
                    String part2 = parts[1]; // 034556*/

                    splitter();

                }
            }
        }
    }

    private void splitter() {
        String temp = "</ul><h2>";

        for (int n = 0; n < listnew.size(); n++) {

            if (listnew.get(n).equals(temp)) {

            }
        }
    }

}
