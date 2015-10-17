package twoohfour.cms.waikato.ac.nz.ballgame;

import java.net.InetAddress;


/**
 * Created by timhampton on 6/10/15.
 */
public interface MultiplayerEventListener {

    // On message
    void message(int statusCode, String event, InetAddress from);

    // If we get a network error.
    void onNetworkError(Exception e, String text);
}
