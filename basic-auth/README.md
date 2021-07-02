
## Disable spring security auto config
By default, the Authentication gets enabled for the Application. Also, content negotiation is used to determine if basic or formLogin should be used.
```java
@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
```

or
```properties
spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration
```

Add `ManagementWebSecurityAutoConfiguration.class` in to exclusive list when you use actuator
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```