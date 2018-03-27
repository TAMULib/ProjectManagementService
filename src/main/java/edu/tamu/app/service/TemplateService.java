package edu.tamu.app.service;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

import edu.tamu.app.model.request.FeatureRequest;

@Service
public class TemplateService {

    @Autowired
    private SpringTemplateEngine templateEngine;

    public String craftVersionOneXmlRequestBody(FeatureRequest request) {
        return templateEngine.process("request", createContext("request", request));
    }

    private Context createContext(String modelName, Object model) {
        Context ctx = new Context(Locale.getDefault());
        ctx.setVariable(modelName, model);
        return ctx;
    }

}
