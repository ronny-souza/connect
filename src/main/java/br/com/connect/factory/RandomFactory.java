package br.com.connect.factory;

import java.util.Random;

public class RandomFactory {

    private final Random random;

    public static RandomFactory instance() {
        return new RandomFactory();
    }

    private RandomFactory() {
        this.random = new Random();
    }

    public String code() {
        int code = this.random.nextInt(900000) + 100000;
        return String.valueOf(code);
    }
}
