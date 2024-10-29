package com.example.demo;

import io.lettuce.core.RedisAsyncCommandsImpl;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.codec.StringCodec;
import io.lettuce.core.output.StatusOutput;
import io.lettuce.core.protocol.CommandArgs;
import io.lettuce.core.protocol.CommandKeyword;
import io.lettuce.core.protocol.CommandType;
import io.lettuce.core.protocol.ProtocolKeyword;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamInfo;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.stereotype.Component;

import java.util.Iterator;

@Component
@RequiredArgsConstructor
public class ConsumerFactory {
    private final RedisTemplate<String, Object> redisTemplate;

    private final StreamMessageListenerContainer container;
    private final StreamListener listener;

    @PostConstruct
    private void createConsumers() {
        for(int i=1; i<=3; i++) {
            String streamKey = "consumer" + i;
            String groupName = "consumer" + i;
            String consumeKey = "consumer" + i;
            createStreamConsumerGroup(streamKey, groupName);

            StreamOffset streamOffset = StreamOffset.create(streamKey, ReadOffset.lastConsumed());
            Consumer consumer = Consumer.from(groupName, consumeKey);
            container.receiveAutoAck(consumer, streamOffset, listener);
            System.out.println(":::::" + streamKey + " created");
        }
        container.start();

    }

    public void createStreamConsumerGroup(String streamKey, String consumerGroupName) {
        if (Boolean.FALSE.equals(redisTemplate.hasKey(streamKey))) {
            RedisAsyncCommandsImpl commands =
                    (RedisAsyncCommandsImpl) redisTemplate
                            .getConnectionFactory()
                            .getConnection()
                            .getNativeConnection();
            CommandArgs args =
                    (new CommandArgs((RedisCodec) StringCodec.UTF8))
                            .add(CommandKeyword.CREATE)
                            .add(streamKey)
                            .add(consumerGroupName)
                            .add("0")
                            .add("MKSTREAM");
            commands.dispatch((ProtocolKeyword) CommandType.XGROUP, new StatusOutput((RedisCodec) StringCodec.UTF8), args);
        } else {
            if (!isStreamConsumerGroupExist(streamKey, consumerGroupName)) {
                redisTemplate
                        .opsForStream()
                        .createGroup(streamKey, ReadOffset.from("0"), consumerGroupName);
            }
        }
    }

    public boolean isStreamConsumerGroupExist(String streamKey, String consumerGroupName) {
        Iterator<StreamInfo.XInfoGroup> iterator = redisTemplate.opsForStream().groups(streamKey).stream().iterator();
        while (iterator.hasNext()) {
            StreamInfo.XInfoGroup xInfoGroup = iterator.next();
            if (xInfoGroup.groupName().equals(consumerGroupName))
                return true;
        }
        return false;
    }
}