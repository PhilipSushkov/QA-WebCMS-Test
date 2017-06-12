Feature: Test filling out job application on the website

  Background: Job Application page is open
    Given I go to the public page
    And I click on "Layouts"
    And I click on "Job Application 4.3" from the side nav
    Then Job Application page is displayed

  Scenario: Check for correct email formatting
    Given I write down incorrect email address
    And Email address does not have correct formatting "something@something.whatever"
    When Submit application button is pressed
    Then Application should not be submitted
    And Warning for incorrect formatting should show up

  Scenario: Check if incorrect formatting for error is gone after fixing it
    Given I complete the test for correct email formatting
    And I input an email with correct formatting
    When Submit application button is pressed
    Then Application should not be submitted
    And Warning for incorrect formatting should show NOT up

  Scenario Outline: Check if a required field is missing
    Given I fill out the form
    And I leave out one of the required fields called <field name>
    Then Application should not be submitted
    And I should see a warning to correct the error for <field name>
    Examples:
      | field name |

  Scenario: Check if I can submit a job application form
    Given I am logged into test@q4websystems.com account
    When I fill out all the required fields for the page
    And I click "Submit Application"
    Then I should see success message
    And I should see an email for job application
    And Content of the application should match the fields that were put in

  Scenario: Check if I can upload a file on the page
    Given I am on the job application page
    And I click on choose file
    And I select a file
    Then "No file chosen" should be replaced with name of the file

  Scenario: Check if uploaded file is submitted on the form
    Given I filled out the form
    And I have uploaded a file
    And I have submitted the form
    And I am logged into the test account
    And I click on the email which has the application
    Then I should see the attached file on the email