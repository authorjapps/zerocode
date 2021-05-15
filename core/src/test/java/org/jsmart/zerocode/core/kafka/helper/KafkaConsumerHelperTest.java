package org.jsmart.zerocode.core.kafka.helper;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Iterators;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.header.internals.RecordHeaders;
import org.jsmart.zerocode.core.kafka.KafkaConstants;
import org.jsmart.zerocode.core.kafka.consume.ConsumerLocalConfigs;
import org.jsmart.zerocode.core.kafka.consume.ConsumerLocalConfigsWrap;
import org.jsmart.zerocode.core.kafka.receive.ConsumerCommonConfigs;
import org.jsmart.zerocode.core.kafka.receive.message.ConsumerJsonRecord;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.jsmart.zerocode.core.kafka.helper.KafkaConsumerHelper.deriveEffectiveConfigs;
import static org.jsmart.zerocode.core.kafka.helper.KafkaConsumerHelper.initialPollWaitingForConsumerGroupJoin;
import static org.mockito.Matchers.any;

public class KafkaConsumerHelperTest {

    ConsumerCommonConfigs consumerCommon;
    ConsumerLocalConfigs consumerLocal;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void test_syncAsyncTrueCommon() throws Exception {
        consumerCommon = new ConsumerCommonConfigs(true, true, "aTestFile", "JSON", true, 3, 50L, "");

        expectedException.expectMessage("Both commitSync and commitAsync can not be true");
        ConsumerLocalConfigs consumerEffectiveConfigs = deriveEffectiveConfigs(null, consumerCommon);
    }

    @Test
    public void test_syncAsyncTrueLocal() throws Exception {
        consumerCommon = new ConsumerCommonConfigs(true, false, "aTestFile", "JSON", true, 3, 50L, "");
        consumerLocal = new ConsumerLocalConfigs("RAW", "sTestLocalFile", true, true, false, 3, 50L, "1,0,test-topic");
        ConsumerLocalConfigsWrap localConfigsWrap = new ConsumerLocalConfigsWrap(consumerLocal);

        expectedException.expectMessage("Both commitSync and commitAsync can not be true");
        ConsumerLocalConfigs consumerEffectiveConfigs = deriveEffectiveConfigs(consumerLocal, consumerCommon);
    }

    @Test
    public void test_effectiveConfigsIsLocal() throws Exception {

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
    public void test_effectiveConfigsIsCentral() throws Exception {

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
    public void test_effectiveCommitAsync_true() throws Exception {

        consumerCommon = new ConsumerCommonConfigs(true, null, "aTestFile", "JSON", true, 3, 50L, "");
        consumerLocal = new ConsumerLocalConfigs("RAW", "sTestLocalFile", true, false, false, 3, 50L, "1,0,test-topic");

        ConsumerLocalConfigs consumerEffectiveConfigs = deriveEffectiveConfigs(consumerLocal, consumerCommon);

        assertThat(consumerEffectiveConfigs.getCommitAsync(), is(true));
        assertThat(consumerEffectiveConfigs.getCommitSync(), is(false));
    }

    @Test
    public void test_effectiveCommitSync_true() throws Exception {

        consumerCommon = new ConsumerCommonConfigs(null, true, "aTestFile", "JSON", true, 3, 50L, "");
        consumerLocal = new ConsumerLocalConfigs("RAW", "sTestLocalFile", null, true, false, 3, 50L, "1,0,test-topic");

        ConsumerLocalConfigs consumerEffectiveConfigs = deriveEffectiveConfigs(consumerLocal, consumerCommon);

        assertThat(consumerEffectiveConfigs.getCommitSync(), is(true));
        assertThat(consumerEffectiveConfigs.getCommitAsync(), nullValue());
    }

    @Test
    public void test_effectiveCommitSyncFromCommon_true() throws Exception {

        consumerCommon = new ConsumerCommonConfigs(true, false, "aTestFile", "JSON", true, 3, 50L, "");
        consumerLocal = new ConsumerLocalConfigs("RAW", "sTestLocalFile", null, null, false, 3, 50L, "1,0,test-topic");

        ConsumerLocalConfigs consumerEffectiveConfigs = deriveEffectiveConfigs(consumerLocal, consumerCommon);

        assertThat(consumerEffectiveConfigs.getCommitSync(), is(true));
        assertThat(consumerEffectiveConfigs.getCommitAsync(), is(false));
    }

    @Test
    public void test_effectiveCommitAsyncFromCommon_true() throws Exception {

        consumerCommon = new ConsumerCommonConfigs(null, true, "aTestFile", "JSON", true, 3, 50L, "");
        consumerLocal = new ConsumerLocalConfigs("RAW", "sTestLocalFile", true, false, false, 3, 150L, "1,0,test-topic");

        ConsumerLocalConfigs consumerEffectiveConfigs = deriveEffectiveConfigs(consumerLocal, consumerCommon);

        assertThat(consumerEffectiveConfigs.getCommitAsync(), is(true));
        assertThat(consumerEffectiveConfigs.getCommitSync(), is(false));
    }

    @Test
    public void should_read_json_with_headers_in_record() throws IOException {
        // given
        ConsumerRecord consumerRecord = Mockito.mock(ConsumerRecord.class);
        Mockito.when(consumerRecord.key()).thenReturn("\"key\"");
        Mockito.when(consumerRecord.value()).thenReturn("\"value\"");
        Mockito.when(consumerRecord.headers())
                .thenReturn(new RecordHeaders().add("headerKey", "headerValue".getBytes()));

        // when
        List<ConsumerJsonRecord> consumerJsonRecords = new ArrayList<>();
        KafkaConsumerHelper.readJson(consumerJsonRecords, Iterators.forArray(consumerRecord),null);

        // then
        Assert.assertEquals(1, consumerJsonRecords.size());
        ConsumerJsonRecord consumerJsonRecord = consumerJsonRecords.get(0);
        Assert.assertTrue(consumerJsonRecord.getKey() instanceof JsonNode);
        Assert.assertTrue(consumerJsonRecord.getValue() instanceof JsonNode);
        Assert.assertEquals("\"key\"", consumerJsonRecord.getKey().toString());
        Assert.assertEquals("\"value\"", consumerJsonRecord.getValue().toString());
        Assert.assertEquals(Collections.singletonMap("headerKey", "headerValue"), consumerJsonRecord.getHeaders());
    }

    @Test
    public void test_firstPoll_exits_early_on_assignment() {

        // given
        consumerCommon = new ConsumerCommonConfigs(true, false, "aTestFile", "JSON", true, 3, 1000L, "");
        consumerLocal = null;
        ConsumerLocalConfigs consumerEffectiveConfigs = deriveEffectiveConfigs(consumerLocal, consumerCommon);
        Consumer consumer = Mockito.mock(Consumer.class);
        HashSet<TopicPartition> partitions = new HashSet<>();
        partitions.add(new TopicPartition("test.topic", 0));
        Mockito.when(consumer.assignment()).thenReturn(partitions);

        // when
        ConsumerRecords records = initialPollWaitingForConsumerGroupJoin(consumer, consumerEffectiveConfigs);

        // then
        assertThat(records.isEmpty(), is(true));
    }

    @Test
    public void test_firstPoll_exits_on_receiving_records() {

        // given
        consumerCommon = new ConsumerCommonConfigs(true, false, "aTestFile", "JSON", true, 3, 5000L, "");
        consumerLocal = new ConsumerLocalConfigs("RAW", "sTestLocalFile", true, false, false, 3, 50L, "1,0,test-topic");
        ConsumerLocalConfigs consumerEffectiveConfigs = deriveEffectiveConfigs(consumerLocal, consumerCommon);
        Consumer consumer = Mockito.mock(Consumer.class);
        Mockito.when(consumer.assignment()).thenReturn(new HashSet<TopicPartition>());

        ConsumerRecords consumerRecords = Mockito.mock(ConsumerRecords.class);
        Mockito.when(consumer.poll(any(Duration.class))).thenReturn(consumerRecords);

        Mockito.when(consumerRecords.isEmpty()).thenReturn(false);

        // when
        ConsumerRecords records = initialPollWaitingForConsumerGroupJoin(consumer, consumerEffectiveConfigs);

        // then
        assertThat(records, equalTo(consumerRecords));
    }


    @Test
    public void test_firstPoll_throws_after_timeout() throws Exception {
        
        // given
        consumerCommon = new ConsumerCommonConfigs(true, false, "aTestFile", "JSON", true, 3, null, "");
        consumerLocal = new ConsumerLocalConfigs("RAW", "sTestLocalFile", true, false, false, 3, 50L, "1,0,test-topic");
        ConsumerLocalConfigs consumerEffectiveConfigs = deriveEffectiveConfigs(consumerLocal, consumerCommon);
        
        Consumer consumer = Mockito.mock(Consumer.class);
        Mockito.when(consumer.assignment()).thenReturn(new HashSet<TopicPartition>());

        ConsumerRecords consumerRecords = Mockito.mock(ConsumerRecords.class);
        Mockito.when(consumer.poll(any(Duration.class))).thenReturn(consumerRecords);

        Mockito.when(consumerRecords.isEmpty()).thenReturn(true);

        // should throw
        expectedException.expectMessage("Kafka Consumer unable to join in time");

        // when
        ConsumerRecords records = initialPollWaitingForConsumerGroupJoin(consumer, consumerEffectiveConfigs);
    }
}
