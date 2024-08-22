## SpringBoot에서 Publish 동작 실습

<br>

### 클래스
- RedisConfig
- MessageListenService

<br>

### 구조
RedisConfig의 Adapter가 Listener를 관리하고, 
Listener는 Channel과 연결된다.  
따라서 Listener에 토픽을 전달하면, 채널을 통하여 구독자가 수신가능하다.

<br>

### 설명
#### 1. RedisConfig
두개의 Bean을 설정한다.
- messageListenerAdapter()
  - return new MessageListenerAdapter(new MessageService())
- redisMessageListenerContainer()
  - RedisConnectionFactory와 MessageListenerAdapter를 파라미터로 받는다.
  - RedisMessageListenerContainer를 생성해서, container에 factory와 listener를 등록한다.
  - 이때, listener에는 토픽을 다음과 같이 등록해준다. users:unregister라는 토픽을 등록해보자.
    ``` java
    container.addMessageListener(listener, ChannelTopic.of("users:unregister));
    ```

#### 2. MessageListenService
Listener를 구현하는 서비스 클래스다.  
RedisConfig에서 구독할 토픽을 Listener에 등록했었던 것을 기억하자. Listener가 해당 토픽 발행건을 수신할 수 있다.  
redis의 MessageListener의 구현체는 onMessage를 재정의한다. onMessage는 Message 객체를 인자로 받고, 얘는 채널정보를 가지고 있다. 

``` java
@Slf4j
@Service
public class MessageListenService implements MessageListener {

    @Override
    public void onMessage(Message message, byte[] pattern) {
        log.info("Received {} channel: {}", new String(message.getChannel()), new String(message.getBody()));
    }
}
```
redis cli에서 PUBLISH users:unregister 200 명령을 통해 발행해보자.  

springboot에서 수신한 결과는 다음과 같다.
```
Received users:unregister channel: 200
```

RedisTemplate을 통해서도 발행해보았다.
``` java
@RestController
@RequiredArgsConstructor
public class PublishController {
    private final RedisTemplate<String, String> redisTemplate;

    @PostMapping("events/users/deregister")
    void publishUserDeregisterEvent() {
        redisTemplate.convertAndSend("users:unregister", "500");    // convertAndSend
    }
}
```