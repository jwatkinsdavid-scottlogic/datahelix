package com.scottlogic.deg.generator.fieldspecs.relations;

import com.scottlogic.deg.common.profile.Field;
import com.scottlogic.deg.generator.fieldspecs.FieldSpec;
import com.scottlogic.deg.generator.restrictions.DateTimeRestrictions;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.Period;
import java.time.ZoneOffset;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAmount;

import static org.junit.jupiter.api.Assertions.*;

class EqualToOffsetDateRelationTest {

    @Test
    public void reduceToRelatedFieldSpec_comparingTwoFields_givesEquivalentFieldSpec() {
        Field first = new Field("first");
        Field second = new Field("second");

        TemporalAdjuster adjuster = t -> t.plus(Period.ofDays(1));
        int days = 3;

        FieldSpecRelations relation = new EqualToOffsetDateRelation(first, second, adjuster, 3);

        OffsetDateTime exactTime = OffsetDateTime.of(
            2005,
            3,
            4,
            5,
            6,
            7,
            0,
            ZoneOffset.UTC);

        FieldSpec initialSpec = specEqualToTime(exactTime);

        FieldSpec expectedSpec = specEqualToTime(exactTime.plusDays(days));

        FieldSpec newSpec = relation.reduceToRelatedFieldSpec(initialSpec);

        assertEquals(expectedSpec, newSpec);
    }

    private static FieldSpec specEqualToTime(OffsetDateTime time) {
        DateTimeRestrictions.DateTimeLimit limit = new DateTimeRestrictions.DateTimeLimit(time, true);

        DateTimeRestrictions restrictions = new DateTimeRestrictions();
        restrictions.min = limit;
        restrictions.max = limit;
        return FieldSpec.Empty.withDateTimeRestrictions(restrictions);
    }

}