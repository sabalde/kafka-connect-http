package com.github.castorm.kafka.connect.http.response.spi;

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

import com.github.castorm.kafka.connect.http.model.HttpRecord;
import com.github.castorm.kafka.connect.http.model.Offset;
import org.apache.kafka.common.Configurable;

import java.util.Map;
import java.util.function.Predicate;

public interface HttpRecordFilterFactory extends Configurable {

    Predicate<HttpRecord> create(Offset offset);

    default void configure(Map<String, ?> map) {
        // Do nothing
    }
}
