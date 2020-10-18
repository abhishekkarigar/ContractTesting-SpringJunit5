package de.kreuzwerker.cdc.userservice;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import au.com.dius.pact.provider.junit.Provider;
import au.com.dius.pact.provider.junit.State;
import au.com.dius.pact.provider.junit.loader.PactBroker;
import au.com.dius.pact.provider.junit.loader.PactBrokerAuth;
import au.com.dius.pact.provider.junit.target.Target;
import au.com.dius.pact.provider.junit.target.TestTarget;
import au.com.dius.pact.provider.spring.SpringRestPactRunner;
import au.com.dius.pact.provider.spring.target.SpringBootHttpTarget;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.annotation.PostConstruct;

@RunWith(SpringRestPactRunner.class)
@Provider("user-service")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,properties = {"pact.verifier.publishResults=true"})
//pact_broker is the service name in docker-compose
//@PactBroker(host = "pact_broker", tags = "${pactbroker.tags:prod}")
@Ignore
@PactBroker(host = "192.168.99.114",tags = "latest",port = "80",authentication = @PactBrokerAuth(username = "pactbroker",password = "pactbroker"),consumers = "messaging-app")
public class GenericStateWithParameterContractTest {

    @TestTarget
    public final Target target = new SpringBootHttpTarget();

    @MockBean
    private UserService userService;

    @State("default")
    public void toDefaultState(Map<String, Object> params) {
        final boolean userExists = (boolean) params.get("userExists");
        if (userExists) {
            when(userService.findUser(any())).thenReturn(User.builder()
                .id("1")
                .legacyId(UUID.randomUUID().toString())
                .name("Beth")
                .role(UserRole.ADMIN)
                .lastLogin(new Date())
                .friend(Friend.builder().id("2").name("Ronald Smith").build())
                .friend(Friend.builder().id("3").name("Matt Spencer").build())
                .build());
        } else {
            when(userService.findUser(any())).thenThrow(NotFoundException.class);
        }
    }


}

