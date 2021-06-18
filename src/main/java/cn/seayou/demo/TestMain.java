package cn.seayou.demo;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CyclicBarrier;

public class TestMain {
    volatile String name;
    public static void main(String[] args) throws InstantiationException, IllegalAccessException {
        ArrayList<String> list = new ArrayList<>();

        HashMap<String, String> hashMap = new HashMap<>();

        ThreadLocal<Connection> threadLocal = new ThreadLocal<>();

        CyclicBarrier barrier = new CyclicBarrier(2);

        Class clazz = TestMain.class;
        TestMain  estMain = (TestMain) clazz.newInstance();
    }
}
