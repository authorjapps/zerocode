package org.jsmart.zerocode.core.kafka.helper;

import org.jsmart.zerocode.core.kafka.consume.ConsumerLocalConfigs;
import org.jsmart.zerocode.core.kafka.consume.ConsumerLocalConfigsWrap;
import org.jsmart.zerocode.core.kafka.receive.ConsumerCommonConfigs;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.jsmart.zerocode.core.kafka.helper.KafkaConsumerHelper.deriveEffectiveConfigs;

public class KafkaConsumerHelperTest {

    ConsumerCommonConfigs consumerCommon;
    ConsumerLocalConfigs consumerLocal;

    //ObjectMapper objectMapper = (new ObjectMapperProvider()).get();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void test_syncAsyncTrueCommon() throws Exception{
        consumerCommon = new ConsumerCommonConfigs(true, true, "aTestFile", "JSON", true, 3, 50L, "");

        expectedException.expectMessage("Both commitSync and commitAsync can not be true");
        ConsumerLocalConfigs consumerEffectiveConfigs = deriveEffectiveConfigs(null, consumerCommon);
    }

    @Test
    public void test_syncAsyncTrueLocal() throws Exception{
        consumerCommon = new ConsumerCommonConfigs(true, false, "aTestFile", "JSON", true, 3, 50L, "");
        consumerLocal = new ConsumerLocalConfigs("RAW", "sTestLocalFile", true, true, false, 3, 50L, "1,0,test-topic");
        ConsumerLocalConfigsWrap localConfigsWrap = new ConsumerLocalConfigsWrap(consumerLocal);

        expectedException.expectMessage("Both commitSync and commitAsync can not be true");
        ConsumerLocalConfigs consumerEffectiveConfigs = deriveEffectiveConfigs(consumerLocal, consumerCommon);
    }

    @Test
    public void test_effectiveConfigsIsLocal() throws Exception{

        consumerCommon = new ConsumerCommonConfigs(true, false, "aTestFile", "JSON", true, 3, 50L, "");
        consumerLocal = new ConsumerLocalConfigs("RAW", "sTestLocalFile", true, false, false, 3, 150L, "1,0,test-topic");

        ConsumerLocalConfigs consumerEffectiveConfigs = deriveEffectiveConfigs(consumerLocal, consumerCommon);

        assertThat(consumerEffectiveConfigs.getFileDumpTo(), is("sTestLocalFile"));
        assertThat(consumerEffectiveConfigs.getCommitAsync(), is(true));
        assertThat(consumerEffectiveConfigs.getCommitSync(), is(false));
        assertThat(consumerEffectiveConfigs.getShowRecordsConsumed(), is(false));
        assertThat(consumerEffectiveConfigs.getMaxNoOfRetryPollsOrTimeouts(), is(3));
        assertThat(consumerEffectiveConfigs.getPollingTime(), is(consumerLocal.getPollingTime()));
    }

    @Test
    public void test_effectiveConfigsIsCentrall() throws Exception{

        consumerCommon = new ConsumerCommonConfigs(true, false, "aTestFile", "JSON", true, 3, 50L, "");
        consumerLocal = null;

        ConsumerLocalConfigs consumerEffectiveConfigs = deriveEffectiveConfigs(consumerLocal, consumerCommon);

        assertThat(consumerEffectiveConfigs.getFileDumpTo(), is("aTestFile"));
        assertThat(consumerEffectiveConfigs.getCommitAsync(), is(false));
        assertThat(consumerEffectiveConfigs.getCommitSync(), is(true));
        assertThat(consumerEffectiveConfigs.getShowRecordsConsumed(), is(true));
        assertThat(consumerEffectiveConfigs.getPollingTime(), is(consumerCommon.getPollingTime()));
    }

    @Test
    public void test_effectiveCommitAsync_true() throws Exception{

        consumerCommon = new ConsumerCommonConfigs(true, null, "aTestFile", "JSON", true, 3, 50L, "");
        consumerLocal = new ConsumerLocalConfigs("RAW", "sTestLocalFile",  true, false, false, 3, 50L, "1,0,test-topic");

        ConsumerLocalConfigs consumerEffectiveConfigs = deriveEffectiveConfigs(consumerLocal, consumerCommon);

        assertThat(consumerEffectiveConfigs.getCommitAsync(), is(true));
        assertThat(consumerEffectiveConfigs.getCommitSync(), is(false));
    }

    @Test
    public void test_effectiveCommitSync_true() throws Exception{

        consumerCommon = new ConsumerCommonConfigs(null, true, "aTestFile", "JSON", true, 3, 50L, "");
        consumerLocal = new ConsumerLocalConfigs("RAW", "sTestLocalFile", null, true, false, 3, 50L, "1,0,test-topic");

        ConsumerLocalConfigs consumerEffectiveConfigs = deriveEffectiveConfigs(consumerLocal, consumerCommon);

        assertThat(consumerEffectiveConfigs.getCommitSync(), is(true));
        assertThat(consumerEffectiveConfigs.getCommitAsync(), nullValue());
    }

    @Test
    public void test_effectiveCommitSyncFromCommon_true() throws Exception{

        consumerCommon = new ConsumerCommonConfigs(true, false, "aTestFile", "JSON", true, 3, 50L, "");
        consumerLocal = new ConsumerLocalConfigs("RAW", "sTestLocalFile", null, null, false, 3, 50L, "1,0,test-topic");

        ConsumerLocalConfigs consumerEffectiveConfigs = deriveEffectiveConfigs(consumerLocal, consumerCommon);

        assertThat(consumerEffectiveConfigs.getCommitSync(), is(true));
        assertThat(consumerEffectiveConfigs.getCommitAsync(), is(false));
    }

    @Test
    public void test_effectiveCommitAsyncFromCommon_true() throws Exception{

        consumerCommon = new ConsumerCommonConfigs(null, true, "aTestFile", "JSON", true, 3,50L, "");
        consumerLocal = new ConsumerLocalConfigs("RAW", "sTestLocalFile", true, false, false, 3, 150L, "1,0,test-topic");

        ConsumerLocalConfigs consumerEffectiveConfigs = deriveEffectiveConfigs(consumerLocal, consumerCommon);

        assertThat(consumerEffectiveConfigs.getCommitAsync(), is(true));
        assertThat(consumerEffectiveConfigs.getCommitSync(), is(false));
    }
}