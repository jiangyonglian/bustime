package jp.co.jyl.bustime.repository;

/**
 * Created by jiang on 2015/04/19.
 */
public class RepositoryFactory {

    public static  RepositoryFactory i = new RepositoryFactory();

    private RepositoryFactory(){

    }

    public BusRepository getBusRepository(){
        return DBHelper.getInstance();
    }

}
