package com.athul.hazelcastserver1.config;

import com.hazelcast.config.*;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Slf4j
public class ServerConfiguration
{

    @Bean("hzServerInstance")
    public HazelcastInstance hazelcastInstance(@Autowired Config hazelcastConfig)
    {
        log.info("Hazelcast instance {}", hazelcastConfig.getInstanceName());
        return Hazelcast.getOrCreateHazelcastInstance(hazelcastConfig);
    }

    @Bean
    public Config hazelcastConfig()
    {
        Config config = new Config();
        config.setInstanceName("hazelcast-datastore-instance");
        config.setProperty("hazelcast.logging.type", "slf4j");
        config.setNetworkConfig(
                new NetworkConfig().setJoin(createJoin()));

        config.setGroupConfig(new GroupConfig("hazelcast-server"));
        config.setMapConfigs(createMapConfig());
        config.setQueueConfigs(createQueueConfig());
        return config;
    }


    private JoinConfig createJoin()
    {
        JoinConfig joinConfig = new JoinConfig();
        joinConfig.getMulticastConfig().setEnabled(false);
        joinConfig.getKubernetesConfig().setEnabled(true);
        return joinConfig;
    }

    private Map <String, MapConfig> createMapConfig()
    {
        Map <String, MapConfig> mapConfigs = new HashMap <>();

        mapConfigs.put("test-map", new MapConfig().setName("test-map").setMaxSizeConfig(
                new MaxSizeConfig(200, MaxSizeConfig.MaxSizePolicy.FREE_HEAP_SIZE)).setEvictionPolicy(
                EvictionPolicy.LRU).setTimeToLiveSeconds(28000));

        return mapConfigs;

    }

    private Map <String, QueueConfig> createQueueConfig()
    {

        Map <String, QueueConfig> qConfigs = new HashMap <>();
        qConfigs.put("test-queue", new QueueConfig().setName("test-queue").setMaxSize(200));
        return qConfigs;
    }

}
