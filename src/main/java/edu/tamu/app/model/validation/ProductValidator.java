package edu.tamu.app.model.validation;

import edu.tamu.weaver.validation.model.InputValidationType;
import edu.tamu.weaver.validation.validators.BaseModelValidator;
import edu.tamu.weaver.validation.validators.InputValidator;

public class ProductValidator extends BaseModelValidator {

    private static final String URL_REGEX = "^(https?|ftp|file|wss?)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]+[-a-zA-Z0-9+&@#/%=~_|?]";

    public ProductValidator() {
        String nameProperty = "name";
        this.addInputValidator(new InputValidator(InputValidationType.required, "A Product requires a name", nameProperty, true));

        String devUrlProperty = "devUrl";
        this.addInputValidator(new InputValidator(InputValidationType.pattern, "The development URL must be a valid URL", devUrlProperty, URL_REGEX));

        String preUrlProperty = "preUrl";
        this.addInputValidator(new InputValidator(InputValidationType.pattern, "The pre-production URL must be a valid URL", preUrlProperty, URL_REGEX));

        String productionUrlProperty = "productionUrl";
        this.addInputValidator(new InputValidator(InputValidationType.pattern, "The production URL must be a valid URL", productionUrlProperty, URL_REGEX));

        String wikiUrlProperty = "wikiUrl";
        this.addInputValidator(new InputValidator(InputValidationType.pattern, "The wiki URL must be a valid URL", wikiUrlProperty, URL_REGEX));
    }
}
