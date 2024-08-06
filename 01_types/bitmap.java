package org.example;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Pipeline;
import java.util.stream.IntStream;

public class Main {
    public static void main(String[] args) {
        try(var jedisPool = new JedisPool("127.0.0.1", 6379)) {
            try(var jedis = jedisPool.getResource()) {
                jedis.setbit("request-somepage-20240807", 100, true);
                jedis.setbit("request-somepage-20240807", 200, true);
                jedis.setbit("request-somepage-20240807", 300, true);

                System.out.println(jedis.getbit("request-somepage-20240807", 100));
                System.out.println(jedis.getbit("request-somepage-20240807", 50));

                System.out.println(jedis.bitcount("request-somepage-20240807"));

                // bitmap vs set
                // Memory usage: 16456 vs 4248776
                // 단순히 숫자 집계 목적이면, bit를 사용하도록 하자!
                Pipeline pipelined = jedis.pipelined();
                IntStream.rangeClosed(0, 100000).forEach(i -> {
                    pipelined.sadd("request-somepage-set-20240807", String.valueOf(i), "1");
                    pipelined.setbit("request-somepage-bit-20240807", i, true);

                    if (i == 1000) {
                        pipelined.sync();
                    }
                });
                pipelined.sync();

//                // if no pipeline (엄청 오래 걸림)
//                IntStream.rangeClosed(0, 100000).forEach(i -> {
//                    jedis.sadd("request-somepage-set-20240807", String.valueOf(i), "1");
//                    jedis.setbit("request-somepage-bit-20240807", i, true);
//                });

            }
        }
    }
}