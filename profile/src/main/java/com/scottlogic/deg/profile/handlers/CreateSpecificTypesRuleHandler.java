package com.scottlogic.deg.profile.handlers;

import com.scottlogic.deg.common.commands.CommandHandler;
import com.scottlogic.deg.common.commands.CommandResult;
import com.scottlogic.deg.common.validators.Validator;
import com.scottlogic.deg.generator.profile.Rule;
import com.scottlogic.deg.profile.commands.CreateSpecificTypesRule;

public class CreateSpecificTypesRuleHandler extends CommandHandler<CreateSpecificTypesRule, Rule>
{
    public CreateSpecificTypesRuleHandler(Validator<CreateSpecificTypesRule> validator)
    {
        super(validator);
    }

    @Override
    protected CommandResult<Rule> handleCommand(CreateSpecificTypesRule command)
    {
        return null;
    }
}
