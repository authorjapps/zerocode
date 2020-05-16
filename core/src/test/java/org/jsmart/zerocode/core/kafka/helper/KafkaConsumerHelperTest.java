package org.jsmart.zerocode.core.kafka.helper;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Iterators;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.hamcrest.CoreMatchers;
import org.jsmart.zerocode.core.kafka.consume.ConsumerLocalConfigs;
import org.jsmart.zerocode.core.kafka.consume.ConsumerLocalConfigsWrap;
import org.jsmart.zerocode.core.kafka.receive.ConsumerCommonConfigs;
import org.jsmart.zerocode.core.kafka.receive.message.ConsumerJsonRecord;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    @Test
    public void should_read_json_with_headers_in_record() throws IOException {
        // given
        ConsumerRecord consumerRecord = Mockito.mock(ConsumerRecord.class);
        Mockito.when(consumerRecord.key()).thenReturn("key");
        Mockito.when(consumerRecord.value()).thenReturn("\"value\"");
        Mockito.when(consumerRecord.headers())
                .thenReturn(new RecordHeaders().add("headerKey", "headerValue".getBytes()));

        // when
        List<ConsumerJsonRecord> consumerJsonRecords = new ArrayList<>();
        KafkaConsumerHelper.readJson(consumerJsonRecords, Iterators.forArray(consumerRecord));

        // then
        Assert.assertEquals(1, consumerJsonRecords.size());
        ConsumerJsonRecord consumerJsonRecord = consumerJsonRecords.get(0);
        Assert.assertEquals("key", consumerJsonRecord.getKey());
        Assert.assertEquals("\"value\"", consumerJsonRecord.getValue().toString());
        Assert.assertEquals(Collections.singletonMap("headerKey", "headerValue"), consumerJsonRecord.getHeaders());
    }
}