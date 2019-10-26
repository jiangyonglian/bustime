package jp.co.jyl.bustime.exception;

/**
 * Created by jiang on 2015/05/16.
 * サーバに接続して、SocketTimeoutExceptionが発生する
 *
 */
public class NoInternetException extends Exception {
    public NoInternetException(Throwable cause){
        super(cause);
    }

}
