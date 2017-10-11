package xyz.donot.roselinx.ui.util;


import twitter4j.StatusListener;
import twitter4j.TwitterStream;

public class StreamCreateUtil {
  public static void addStatusListener(TwitterStream stream, StatusListener listener){
    stream.addListener(listener);
  }
}
