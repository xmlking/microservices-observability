package com.example

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.PropertySource
import org.springframework.context.annotation.PropertySources

@Configuration
@PropertySources(
        PropertySource(value = "classpath:shared_application.properties"),
        PropertySource(value = "classpath:shared_\${spring.profiles.active}_application.properties", ignoreResourceNotFound = true))
class SharedConfiguration
