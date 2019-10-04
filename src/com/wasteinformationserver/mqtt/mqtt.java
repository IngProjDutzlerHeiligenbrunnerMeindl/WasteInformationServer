package com.wasteinformationserver.mqtt;

import com.wasteinformationserver.db.jdcb;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class mqtt {

    private ArrayList<String> mylist=new ArrayList<>();
    private int index=0;

    public mqtt(){

    }

    public void notifymessage(){

        mqttreceiver mr=new mqttreceiver();

        mr.addMessageReceivedListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println(e.getActionCommand());
            }
        });
    }

    public void getDatabasedata() throws SQLException {
        jdcb database=new jdcb("placeuser","eaL956R6yFItQVBl","wasteinformation");
        ResultSet result = database.executeQuery("SELECT*FROM place");

        while(result.next())
        {
            String temp= String.valueOf(result.getStatement());
            filllist(temp);
        }



        //transmitmessagetoESP();
    }

    private void transmitmessagetoESP()
    {

        mqtttransmitter mt=new mqtttransmitter();
    }

    private void filllist(String temp)
    {
        mylist.add(index,temp);
    }

}
