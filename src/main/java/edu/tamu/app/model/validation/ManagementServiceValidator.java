package edu.tamu.app.model.validation;

import edu.tamu.weaver.validation.model.InputValidationType;
import edu.tamu.weaver.validation.validators.BaseModelValidator;
import edu.tamu.weaver.validation.validators.InputValidator;

public class ManagementServiceValidator extends BaseModelValidator {

    public ManagementServiceValidator() {
        String nameProperty = "name";
        this.addInputValidator(new InputValidator(InputValidationType.required, "A Management service requires a name", nameProperty, true));

        String typeProperty = "type";
        this.addInputValidator(new InputValidator(InputValidationType.required, "A Management service requires a type", typeProperty, true));
    }
}
