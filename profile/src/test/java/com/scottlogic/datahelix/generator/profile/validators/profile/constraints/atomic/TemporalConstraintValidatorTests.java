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
package com.scottlogic.datahelix.generator.profile.validators.profile.constraints.atomic;

import com.scottlogic.datahelix.generator.common.profile.StandardSpecificFieldType;
import com.scottlogic.datahelix.generator.common.validators.ValidationResult;
import com.scottlogic.datahelix.generator.profile.creation.FieldDTOBuilder;
import com.scottlogic.datahelix.generator.profile.dtos.FieldDTO;
import com.scottlogic.datahelix.generator.profile.dtos.constraints.atomic.temporal.AfterConstraintDTO;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TemporalConstraintValidatorTests {

    private final List<FieldDTO> fields = Arrays.asList
        (
            FieldDTOBuilder.create("datetime", StandardSpecificFieldType.DATETIME.toSpecificFieldType()),
            FieldDTOBuilder.create("time", StandardSpecificFieldType.TIME.toSpecificFieldType())
        );

    @Test
    public void validateTemporalConstraint_withValidDatetime_succeeds()
    {
        // Arrange
        AfterConstraintDTO dto = new AfterConstraintDTO();
        dto.field = "datetime";
        dto.value = "0001-01-01T00:00:00.000Z";

        // Act
        ValidationResult validationResult = new TemporalConstraintValidator(fields).validate(dto);

        // Assert
        assertTrue(validationResult.isSuccess);
    }

    @Test
    public void validateTemporalConstraint_withValidTime_succeeds()
    {
        // Arrange
        AfterConstraintDTO dto = new AfterConstraintDTO();
        dto.field = "time";
        dto.value = "00:00:00";

        // Act
        ValidationResult validationResult = new TemporalConstraintValidator(fields).validate(dto);

        // Assert
        assertTrue(validationResult.isSuccess);
    }

    @Test
    public void validateTemporalConstraint_withNullField_fails()
    {
        // Arrange
        AfterConstraintDTO dto = new AfterConstraintDTO();
        dto.field = null;
        dto.value = "0001-01-01T00:00:00.000Z";

        // Act
        ValidationResult validationResult = new TemporalConstraintValidator(fields).validate(dto);

        // Assert
        assertFalse(validationResult.isSuccess);
    }

    @Test
    public void validateTemporalConstraint_withEmptyField_fails()
    {
        // Arrange
        AfterConstraintDTO dto = new AfterConstraintDTO();
        dto.field = "";
        dto.value = "0001-01-01T00:00:00.000Z";

        // Act
        ValidationResult validationResult = new TemporalConstraintValidator(fields).validate(dto);

        // Assert
        assertFalse(validationResult.isSuccess);
    }

    @Test
    public void validateTemporalConstraint_withUndefinedField_fails()
    {
        // Arrange
        AfterConstraintDTO dto = new AfterConstraintDTO();
        dto.field = "unknown";
        dto.value = "0001-01-01T00:00:00.000Z";

        // Act
        ValidationResult validationResult = new TemporalConstraintValidator(fields).validate(dto);

        // Assert
        assertFalse(validationResult.isSuccess);
    }

    @Test
    public void validateTemporalConstraint_withUnspecifiedDatetime_fails()
    {
        // Arrange
        AfterConstraintDTO dto = new AfterConstraintDTO();
        dto.field = "datetime";
        dto.value = "";

        // Act
        ValidationResult validationResult = new TemporalConstraintValidator(fields).validate(dto);

        // Assert
        assertFalse(validationResult.isSuccess);
    }

    @Test
    public void validateTemporalConstraint_withNullDatetime_fails()
    {
        // Arrange
        AfterConstraintDTO dto = new AfterConstraintDTO();
        dto.field = "datetime";
        dto.value = null;

        // Act
        ValidationResult validationResult = new TemporalConstraintValidator(fields).validate(dto);

        // Assert
        assertFalse(validationResult.isSuccess);
    }

    @Test
    public void validateTemporalConstraint_withUnspecifiedTime_fails()
    {
        // Arrange
        AfterConstraintDTO dto = new AfterConstraintDTO();
        dto.field = "time";
        dto.value = "";

        // Act
        ValidationResult validationResult = new TemporalConstraintValidator(fields).validate(dto);

        // Assert
        assertFalse(validationResult.isSuccess);
    }

    @Test
    public void validateTemporalConstraint_withNullTime_fails()
    {
        // Arrange
        AfterConstraintDTO dto = new AfterConstraintDTO();
        dto.field = "time";
        dto.value = null;

        // Act
        ValidationResult validationResult = new TemporalConstraintValidator(fields).validate(dto);

        // Assert
        assertFalse(validationResult.isSuccess);
    }

    @Test
    public void validateTemporalConstraint_withInvalidDatetime_fails()
    {
        // Arrange
        AfterConstraintDTO dto = new AfterConstraintDTO();
        dto.field = "datetime";
        dto.value = "invalid datetime";

        // Act
        ValidationResult validationResult = new TemporalConstraintValidator(fields).validate(dto);

        // Assert
        assertFalse(validationResult.isSuccess);
    }

    @Test
    public void validateTemporalConstraint_withInvalidTime_fails()
    {
        // Arrange
        AfterConstraintDTO dto = new AfterConstraintDTO();
        dto.field = "time";
        dto.value = "invalid time";

        // Act
        ValidationResult validationResult = new TemporalConstraintValidator(fields).validate(dto);

        // Assert
        assertFalse(validationResult.isSuccess);
    }

    @Test
    public void validateTemporalConstraint_withDatetimeBeforeMin_fails()
    {
        // Arrange
        AfterConstraintDTO dto = new AfterConstraintDTO();
        dto.field = "datetime";
        dto.value = "0000-01-01T00:00:00.000Z";

        // Act
        ValidationResult validationResult = new TemporalConstraintValidator(fields).validate(dto);

        // Assert
        assertFalse(validationResult.isSuccess);
    }

    @Test
    public void validateTemporalConstraint_withDatetimeAfterMax_fails()
    {
        // Arrange
        AfterConstraintDTO dto = new AfterConstraintDTO();
        dto.field = "datetime";
        dto.value = "10000-01-01T00:00:00.000Z";

        // Act
        ValidationResult validationResult = new TemporalConstraintValidator(fields).validate(dto);

        // Assert
        assertFalse(validationResult.isSuccess);
    }
}
