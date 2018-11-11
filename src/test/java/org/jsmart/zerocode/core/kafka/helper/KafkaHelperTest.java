package org.jsmart.zerocode.core.kafka.helper;

import org.jsmart.zerocode.core.kafka.consume.ConsumerLocalConfigs;
import org.jsmart.zerocode.core.kafka.consume.ConsumerLocalConfigsWrap;
import org.jsmart.zerocode.core.kafka.receive.ConsumerCommonConfigs;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.jsmart.zerocode.core.kafka.helper.KafkaHelper.deriveEffectiveConfigs;

public class KafkaHelperTest {

    ConsumerCommonConfigs consumerCommon;
    ConsumerLocalConfigs consumerLocal;

    //ObjectMapper objectMapper = (new ObjectMapperProvider()).get();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void test_effectiveConfigsIsLocal() throws Exception{

        consumerCommon = new ConsumerCommonConfigs(true, false, "aTestFile", "JSON", true);
        consumerLocal = new ConsumerLocalConfigs("sTestLocalFile", "RAW", true, false, false);

        ConsumerLocalConfigs consumerEffectiveConfigs = deriveEffectiveConfigs(consumerLocal, consumerCommon);

        assertThat(consumerEffectiveConfigs.getFileDumpTo(), is("sTestLocalFile"));
        assertThat(consumerEffectiveConfigs.getFileDumpType(), is("RAW"));
        assertThat(consumerEffectiveConfigs.getCommitAsync(), is(true));
        assertThat(consumerEffectiveConfigs.getCommitSync(), is(false));
        assertThat(consumerEffectiveConfigs.getShowRecordsAsResponse(), is(false));
    }

    @Test
    public void test_effectiveConfigsIsCentrall() throws Exception{

        consumerCommon = new ConsumerCommonConfigs(true, false, "aTestFile", "JSON", true);
        consumerLocal = null;

        ConsumerLocalConfigs consumerEffectiveConfigs = deriveEffectiveConfigs(consumerLocal, consumerCommon);

        assertThat(consumerEffectiveConfigs.getFileDumpTo(), is("aTestFile"));
        assertThat(consumerEffectiveConfigs.getFileDumpType(), is("JSON"));
        assertThat(consumerEffectiveConfigs.getCommitAsync(), is(false));
        assertThat(consumerEffectiveConfigs.getCommitSync(), is(true));
        assertThat(consumerEffectiveConfigs.getShowRecordsAsResponse(), is(true));
    }

    @Test
    public void test_syncAsyncTrueCommon() throws Exception{
        consumerCommon = new ConsumerCommonConfigs(true, true, "aTestFile", "JSON", true);

        expectedException.expectMessage("Both commitSync and commitAsync can not be true");
        ConsumerLocalConfigs consumerEffectiveConfigs = deriveEffectiveConfigs(null, consumerCommon);
    }

    @Test
    public void test_syncAsyncTrueLocal() throws Exception{
        consumerCommon = new ConsumerCommonConfigs(true, false, "aTestFile", "JSON", true);
        consumerLocal = new ConsumerLocalConfigs("sTestLocalFile", "RAW", true, true, false);
        ConsumerLocalConfigsWrap localConfigsWrap = new ConsumerLocalConfigsWrap(consumerLocal);

        expectedException.expectMessage("Both commitSync and commitAsync can not be true");
        ConsumerLocalConfigs consumerEffectiveConfigs = deriveEffectiveConfigs(consumerLocal, consumerCommon);
    }

}