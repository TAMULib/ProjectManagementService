package edu.tamu.app.model.validation;

import edu.tamu.weaver.validation.model.InputValidationType;
import edu.tamu.weaver.validation.validators.BaseModelValidator;
import edu.tamu.weaver.validation.validators.InputValidator;

public class InternalRequestValidator extends BaseModelValidator {

    public InternalRequestValidator() {
        String titleProperty = "title";
        this.addInputValidator(new InputValidator(InputValidationType.required, "An Internal Request requires a title", titleProperty, true));

        String descriptionProperty = "description";
        this.addInputValidator(new InputValidator(InputValidationType.required, "An Internal Request requires a description", descriptionProperty, true));
    }
}
