package com.github.castorm.kafka.connect.http;

/*-
 * #%L
 * kafka-connect-http
 * %%
 * Copyright (C) 2020 CastorM
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.github.castorm.kafka.connect.http.client.okhttp.OkHttpClient;
import com.github.castorm.kafka.connect.http.client.spi.HttpClient;
import com.github.castorm.kafka.connect.http.model.HttpRecord;
import com.github.castorm.kafka.connect.http.model.HttpRequest;
import com.github.castorm.kafka.connect.http.model.HttpResponse;
import com.github.castorm.kafka.connect.http.model.Offset;
import com.github.castorm.kafka.connect.http.record.SchemedSourceRecordMapper;
import com.github.castorm.kafka.connect.http.record.spi.SourceRecordMapper;
import com.github.castorm.kafka.connect.http.request.spi.HttpRequestFactory;
import com.github.castorm.kafka.connect.http.request.template.TemplateHttpRequestFactory;
import com.github.castorm.kafka.connect.http.response.OffsetTimestampRecordFilterFactory;
import com.github.castorm.kafka.connect.http.response.PassthroughRecordFilterFactory;
import com.github.castorm.kafka.connect.http.response.StatusCodeFilterResponseParser;
import com.github.castorm.kafka.connect.http.response.spi.HttpResponseParser;
import com.github.castorm.kafka.connect.throttle.AdaptableIntervalThrottler;
import com.github.castorm.kafka.connect.throttle.FixedIntervalThrottler;
import com.google.common.collect.ImmutableMap;
import org.apache.kafka.connect.source.SourceRecord;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.castorm.kafka.connect.http.HttpSourceConnectorConfigTest.Fixture.config;
import static com.github.castorm.kafka.connect.http.HttpSourceConnectorConfigTest.Fixture.configWithout;
import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;

class HttpSourceConnectorConfigTest {

    @Test
    void whenNoThrottler_thenDefault() {
        assertThat(configWithout("http.throttler").getThrottler()).isInstanceOf(AdaptableIntervalThrottler.class);
    }

    @Test
    void whenThrottler_thenInitialized() {
        assertThat(config("http.throttler", FixedIntervalThrottler.class.getName()).getThrottler()).isInstanceOf(FixedIntervalThrottler.class);
    }

    @Test
    void whenNoClient_thenDefault() {
        assertThat(configWithout("http.client").getClient()).isInstanceOf(OkHttpClient.class);
    }

    @Test
    void whenClient_thenInitialized() {
        assertThat(config("http.client", TestHttpClient.class.getName()).getClient()).isInstanceOf(TestHttpClient.class);
    }

    @Test
    void whenNoRequestFactory_thenDefault() {
        assertThat(configWithout("http.request.factory").getRequestFactory()).isInstanceOf(TemplateHttpRequestFactory.class);
    }

    @Test
    void whenRequestFactory_thenInitialized() {
        assertThat(config("http.request.factory", TestRequestFactory.class.getName()).getRequestFactory()).isInstanceOf(TestRequestFactory.class);
    }

    @Test
    void whenNoResponseParser_thenDefault() {
        assertThat(configWithout("http.response.parser").getResponseParser()).isInstanceOf(StatusCodeFilterResponseParser.class);
    }

    @Test
    void whenResponseParser_thenInitialized() {
        assertThat(config("http.response.parser", TestResponseParser.class.getName()).getResponseParser()).isInstanceOf(TestResponseParser.class);
    }

    @Test
    void whenNoResponseFilterFactory_thenDefault() {
        assertThat(configWithout("http.record.filter.factory").getRecordFilterFactory()).isInstanceOf(PassthroughRecordFilterFactory.class);
    }

    @Test
    void whenResponseFilterFactory_thenInitialized() {
        assertThat(config("http.record.filter.factory", OffsetTimestampRecordFilterFactory.class.getName()).getRecordFilterFactory()).isInstanceOf(OffsetTimestampRecordFilterFactory.class);
    }

    @Test
    void whenNoRecordMapper_thenDefault() {
        assertThat(configWithout("http.record.mapper").getRecordMapper()).isInstanceOf(SchemedSourceRecordMapper.class);
    }

    @Test
    void whenRecordMapper_thenInitialized() {
        assertThat(config("http.record.mapper", TestRecordMapper.class.getName()).getRecordMapper()).isInstanceOf(TestRecordMapper.class);
    }

    @Test
    void whenNoInitialOffset_thenDefault() {
        assertThat(configWithout("http.offset.initial").getInitialOffset()).isEqualTo(emptyMap());
    }

    @Test
    void whenInitialOffset_thenInitialized() {
        assertThat(config("http.offset.initial", "k=v").getInitialOffset()).isEqualTo(ImmutableMap.of("k", "v"));
    }

    public static class TestHttpClient implements HttpClient {
        public HttpResponse execute(HttpRequest request) { return null; }
    }

    public static class TestRequestFactory implements HttpRequestFactory {
        public HttpRequest createRequest(Offset offset) { return null; }
    }

    public static class TestResponseParser implements HttpResponseParser {
        public List<HttpRecord> parse(HttpResponse response) { return null; }
    }

    public static class TestRecordMapper implements SourceRecordMapper {
        public SourceRecord map(HttpRecord record) { return null; }
    }

    interface Fixture {
        static Map<String, String> defaultMap() {
            return new HashMap<String, String>() {{
                put("kafka.topic", "topic");
                put("http.request.url", "foo");
                put("http.response.json.record.offset.value.pointer", "/baz");
            }};
        }

        static HttpSourceConnectorConfig config(String key, String value) {
            Map<String, String> customMap = defaultMap();
            customMap.put(key, value);
            return new HttpSourceConnectorConfig(customMap);
        }

        static HttpSourceConnectorConfig configWithout(String key) {
            Map<String, String> customMap = defaultMap();
            customMap.remove(key);
            return new HttpSourceConnectorConfig(customMap);
        }
    }
}
