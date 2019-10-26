package jp.co.jyl.bustime.exception;

/**
 * Created by jiang on 2015/05/16.
 * サーバに接続して、SocketTimeoutExceptionが発生する
 *
 */
public class ServerBusyException extends Exception {

    public ServerBusyException(Throwable cause){
        super(cause);
    }
}
