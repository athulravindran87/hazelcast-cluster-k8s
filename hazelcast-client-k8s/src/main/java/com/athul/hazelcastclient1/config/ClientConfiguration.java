package com.athul.hazelcastclient1.config;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.client.config.ClientConnectionStrategyConfig;
import com.hazelcast.client.config.ClientNetworkConfig;
import com.hazelcast.config.DiscoveryConfig;
import com.hazelcast.config.DiscoveryStrategyConfig;
import com.hazelcast.config.GroupConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.kubernetes.HazelcastKubernetesDiscoveryStrategyFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ComponentScan(basePackages = "com.athul.common.client", lazyInit = true)
@Slf4j
public class ClientConfiguration
{

    @Bean("dsHzInstance")
    public HazelcastInstance hazelcastInstance(@Autowired @Qualifier("clientConfig") ClientConfig config) {
        return HazelcastClient.newHazelcastClient(config);
    }

    @Bean("clientConfig")
    public ClientConfig hazelcastConfig() {

        return new ClientConfig()
                .setProperty("hazelcast.logging.type", "slf4j")
                .setGroupConfig(new GroupConfig("hazelcast-server"))
                .setNetworkConfig(getClientNetworkConfig())
                .setConnectionStrategyConfig(clientConnectionStrategyConfig());

    }

    private ClientNetworkConfig getClientNetworkConfig() {
        ClientNetworkConfig networkConfig = new ClientNetworkConfig()
                .setRedoOperation(true)
                .setSmartRouting(true)
                .setConnectionAttemptLimit(100_000)
                .setConnectionAttemptPeriod(10_000);
        networkConfig.getKubernetesConfig().setEnabled(true);
     //   networkConfig.setDiscoveryConfig(createDiscoveryConfig());
        return networkConfig;
    }

    private DiscoveryConfig createDiscoveryConfig() {
        DiscoveryConfig discoveryConfig = new DiscoveryConfig();
        Map<String, Comparable> properties = new HashMap<>();
        discoveryConfig.addDiscoveryStrategyConfig(
                new DiscoveryStrategyConfig
                (new HazelcastKubernetesDiscoveryStrategyFactory(), properties)
        );

        return discoveryConfig;
    }

    private ClientConnectionStrategyConfig clientConnectionStrategyConfig() {
        return new ClientConnectionStrategyConfig().setAsyncStart(true).setReconnectMode(ClientConnectionStrategyConfig.ReconnectMode.ASYNC);
    }

}
