/*
 * Copyright 2019 Scott Logic Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.scottlogic.datahelix.generator.core.fieldspecs.relations;

import com.scottlogic.datahelix.generator.common.profile.*;
import com.scottlogic.datahelix.generator.common.util.defaults.DateTimeDefaults;
import com.scottlogic.datahelix.generator.core.fieldspecs.FieldSpec;
import com.scottlogic.datahelix.generator.core.fieldspecs.FieldSpecFactory;
import com.scottlogic.datahelix.generator.core.generation.databags.DataBagValue;
import com.scottlogic.datahelix.generator.core.restrictions.linear.LinearRestrictions;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

import static com.scottlogic.datahelix.generator.common.util.Defaults.ISO_MIN_DATE;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BeforeRelationTest {

    private final Field a = new Field("a", StandardSpecificFieldType.DATETIME.toSpecificFieldType(), false, "", false, false, null);
    private final Field b = new Field("b", StandardSpecificFieldType.DATETIME.toSpecificFieldType(), false, "", false, false, null);

    @Test
    public void testReduceToFieldSpec_withNotNull_reducesToSpec() {
        DateTimeGranularity offsetGranularity = DateTimeGranularity.create("MILLIS");
        FieldSpecRelation beforeDateRelations = new BeforeRelation(a, b, true, DateTimeDefaults.get(), offsetGranularity, 0);
        OffsetDateTime value = OffsetDateTime.of(2000,
            1,
            1,
            0,
            0,
            0,
            0,
            ZoneOffset.UTC);
        DataBagValue generatedValue = new DataBagValue(value);

        FieldSpec result = beforeDateRelations.createModifierFromOtherValue(generatedValue);

        FieldSpec expected = FieldSpecFactory.fromRestriction(new LinearRestrictions<>(ISO_MIN_DATE, value, new DateTimeGranularity(ChronoUnit.MILLIS)));
        assertEquals(expected, result);
    }

    @Test
    public void testReduceToFieldSpec_withNotNullExclusive_reducesToSpec() {
        DateTimeGranularity offsetGranularity = DateTimeGranularity.create("MILLIS");
        FieldSpecRelation beforeDateRelations = new BeforeRelation(a, b, false, DateTimeDefaults.get(), offsetGranularity, 0);
        OffsetDateTime value = OffsetDateTime.of(2000,
            1,
            1,
            0,
            0,
            0,
            0,
            ZoneOffset.UTC);
        DataBagValue generatedValue = new DataBagValue(value);

        FieldSpec result = beforeDateRelations.createModifierFromOtherValue(generatedValue);

        Granularity<OffsetDateTime> granularity = new DateTimeGranularity(ChronoUnit.MILLIS);
        FieldSpec expected = FieldSpecFactory.fromRestriction(new LinearRestrictions<>(ISO_MIN_DATE, granularity.getPrevious(value), new DateTimeGranularity(ChronoUnit.MILLIS)));
        assertEquals(expected, result);
    }

    @Test
    public void testReduceToFieldSpec_withNull_reducesToSpec() {
        DateTimeGranularity offsetGranularity = DateTimeGranularity.create("MILLIS");
        FieldSpecRelation beforeDateRelations = new BeforeRelation(a, b, true, DateTimeDefaults.get(), offsetGranularity, 0);
        OffsetDateTime value = null;
        DataBagValue generatedValue = new DataBagValue(value);

        FieldSpec result = beforeDateRelations.createModifierFromOtherValue(generatedValue);

        FieldSpec expected = FieldSpecFactory.fromType(FieldType.DATETIME);
        assertEquals(expected, result);
    }

}