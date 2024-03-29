package com.athul.hazelcastserver1.config;

import com.hazelcast.config.*;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemErrRule;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Hazelcast.class})
public class ServerConfigurationTest
{

    @Rule
    public final SystemOutRule systemOutRule = new SystemOutRule().muteForSuccessfulTests().enableLog();

    @Rule
    public final SystemErrRule systemErrRule = new SystemErrRule().muteForSuccessfulTests().enableLog();

    @Mock
    private Config config;

    @InjectMocks
    private ServerConfiguration testObj;

    @Before
    public void setUp() throws Exception
    {
        PowerMockito.mockStatic(Hazelcast.class);

    }

    @Test
    public void hazelcastInstance()
    {
        when(config.getInstanceName()).thenReturn("somename");
        PowerMockito.when(Hazelcast.getOrCreateHazelcastInstance(config)).thenReturn(mock(HazelcastInstance.class));


        assertThat(testObj.hazelcastInstance(config), instanceOf(HazelcastInstance.class));


        PowerMockito.verifyStatic(Hazelcast.class);
        Hazelcast.getOrCreateHazelcastInstance(ArgumentMatchers.any(Config.class));

    }

    @Test
    public void hazelcastConfig()
    {
        Config result = testObj.hazelcastConfig();
        assertThat(result.getInstanceName(), equalTo("hazelcast-datastore-instance"));

        assertThat(result.getProperties(),
                allOf(hasEntry("hazelcast.logging.type", "slf4j")));


    }

    @Test
    public void testNetworkConfig()
    {
        Config result = testObj.hazelcastConfig();

        assertThat(result.getNetworkConfig(), instanceOf(NetworkConfig.class));

    }

    @Test
    public void testJoin()
    {
        JoinConfig result = testObj.hazelcastConfig().getNetworkConfig().getJoin();

        assertThat(result.getKubernetesConfig().isEnabled(), equalTo(true));

        assertThat(result.getMulticastConfig().isEnabled(), equalTo(false));

    }


    @Test
    public void testGroupConfig()
    {
        GroupConfig result = testObj.hazelcastConfig().getGroupConfig();

        assertThat(result.getName(), equalTo("hazelcast-server"));
    }

    @Test
    public void testMapConfig()
    {
        Map <String, MapConfig> result = testObj.hazelcastConfig().getMapConfigs();

        MapConfig mapConfig = result.get("test-map");

        assertThat(result.size(), equalTo(1));
        assertThat(mapConfig.getName(), equalTo("test-map"));
        assertThat(mapConfig.getEvictionPolicy(), equalTo(EvictionPolicy.LRU));
        assertThat(mapConfig.getTimeToLiveSeconds(), equalTo(28_000));
        assertThat(mapConfig.getMaxSizeConfig().getSize(), equalTo(200));
        assertThat(mapConfig.getMaxSizeConfig().getMaxSizePolicy(), equalTo(MaxSizeConfig.MaxSizePolicy.FREE_HEAP_SIZE));
    }

    @Test
    public void testQueueConfig()
    {
        Map <String, QueueConfig> result = testObj.hazelcastConfig().getQueueConfigs();

        assertThat(result.size(), equalTo(1));

        QueueConfig queueConfig = result.get("test-queue");
        assertThat(queueConfig.getName(), equalTo("test-queue"));
        assertThat(queueConfig.getMaxSize(), equalTo(200));
    }
}