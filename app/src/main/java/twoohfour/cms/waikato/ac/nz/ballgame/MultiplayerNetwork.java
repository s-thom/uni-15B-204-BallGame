package twoohfour.cms.waikato.ac.nz.ballgame;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by timhampton on 6/10/15.
 * Code extended from COMP202 Networking.
 */
public class MultiplayerNetwork {

    public static int port = 40202;
    public static String ip = "239.0.202.1";

    private MulticastSocket ms;
    private InetAddress inetAd;

    private List<MultiplayerEventListener> _listeners = new ArrayList<MultiplayerEventListener>();


    public MultiplayerNetwork() {
        try {
            ms = new MulticastSocket(port);
            inetAd = InetAddress.getByName(ip);

            ms.joinGroup(inetAd);

            System.out.println("Listening on port:  " + port);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        Runnable listen = new Runnable() {
            public void run() {
                // Check for messages and output them
                while (true) {
                    try {
                        byte[] buffer = new byte[1000];
                        DatagramPacket recv = new DatagramPacket(buffer, buffer.length);
                        ms.receive(recv);
                        String message = new String(buffer, "UTF-8");
                        System.out.println(recv.getAddress() + " : " + message);

                        int statusCode = Integer.parseInt(message.split("\\s+")[0]);
                        String params = message.split("\\s(.*)")[0];

                        for ( MultiplayerEventListener mel : _listeners ) {
                            mel.message(statusCode, params, recv.getAddress());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        for ( MultiplayerEventListener mel : _listeners ) {
                            mel.onNetworkError(e, "Error in attempting to retrive code.");
                        }
                    }
                }


            }
        };

        new Thread(listen).start();



    }


    public void registerListener(MultiplayerEventListener m) {
        _listeners.add(m);
    }

    public void sendCode(int code, String vars) {

        // Format it for data sending:
        // Format should be 3 digit code, plus any parameters (e.g a 300 hello)

        String output = String.valueOf(code) + " " + vars;

        DatagramPacket out = new DatagramPacket(output.getBytes(), output.length(), inetAd, port);
        try {
            ms.send(out);

        } catch (IOException e) {
            e.printStackTrace();
            for ( MultiplayerEventListener mel : _listeners ) {
                mel.onNetworkError(e, "Error in attempting to send code.");
            }
        }



    }





}
