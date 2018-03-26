package edu.tamu.app.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import edu.tamu.weaver.email.config.WeaverEmailConfig;

@Configuration
@Profile("!test")
public class AppEmailConfig extends WeaverEmailConfig {

}
