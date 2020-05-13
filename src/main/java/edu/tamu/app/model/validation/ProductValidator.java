package edu.tamu.app.model.validation;

import edu.tamu.weaver.validation.model.InputValidationType;
import edu.tamu.weaver.validation.validators.BaseModelValidator;
import edu.tamu.weaver.validation.validators.InputValidator;

public class ProductValidator extends BaseModelValidator {

    public ProductValidator() {
        String nameProperty = "name";
        this.addInputValidator(new InputValidator(InputValidationType.required, "A Product requires a name", nameProperty, true));

        String devUrlProperty = "devUrl";
        this.addInputValidator(new InputValidator(InputValidationType.pattern, "The development URL must be a valid URL", devUrlProperty, "^(https?|ftp|file|wss?)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]+[-a-zA-Z0-9+&@#/%=~_|?]"));

        String preUrlProperty = "preUrl";
        this.addInputValidator(new InputValidator(InputValidationType.pattern, "The pre-production URL must be a valid URL", preUrlProperty, "^(https?|ftp|file|wss?)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]+[-a-zA-Z0-9+&@#/%=~_|?]"));

        String productionUrlProperty = "productionUrl";
        this.addInputValidator(new InputValidator(InputValidationType.pattern, "The production URL must be a valid URL", productionUrlProperty, "^(https?|ftp|file|wss?)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]+[-a-zA-Z0-9+&@#/%=~_|?]"));

        String wikiUrlProperty = "wikiUrl";
        this.addInputValidator(new InputValidator(InputValidationType.pattern, "The wiki URL must be a valid URL", wikiUrlProperty, "^(https?|ftp|file|wss?)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]+[-a-zA-Z0-9+&@#/%=~_|?]"));
    }
}
