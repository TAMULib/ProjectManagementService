package edu.tamu.app.model.validation;

import edu.tamu.weaver.validation.model.InputValidationType;
import edu.tamu.weaver.validation.validators.BaseModelValidator;
import edu.tamu.weaver.validation.validators.InputValidator;

public class StatusValidator extends BaseModelValidator {

    public StatusValidator() {
        String identifierProperty = "identifier";
        this.addInputValidator(new InputValidator(InputValidationType.required, "Identifier is required!", identifierProperty, true));
    }
}
