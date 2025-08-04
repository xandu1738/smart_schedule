package com.servicecops.project.models.jpahelpers.enums;

/**
 * App domains are a way of categorizing your users.
 * This will become more handy while categorizing permissions
 *
 * Please feel free to change the default domains here to anything you want.
 * After Changing, remember to re-run the project for these changes to be reflected in your database
 */
public enum AppDomains {
    BACK_OFFICE, // Users in this domain are more of administrators
    INSTITUTION
}
