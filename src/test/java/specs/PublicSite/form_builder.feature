Feature: I can fill out a FormBuilder form

  Background: FormBuilder Test Page is open
    Given that I am on the ChicagoTest home page
    When I click on "Investors"
      And I click on "FormBuilder Test Page" in the sidebar
    Then the FormBuilder Test Page opens
      And the form appears
    Given SendGrid is enabled

  Scenario: I can successfully submit a form
    Given I am logged into test@q4websystems.com email account
    When I fill in all fields
      And I click "Submit"
    Then the form is replaced with the message "The form has been submitted successfully!"
      And test@q4websystems.com receives an email from info@q4websystems.com
      And the First Name is what I entered in the form
      And Email and Comments are blank

  Scenario: An invalid form is rejected
    When I fill in the form with invalid text in at least one required field (eg. first name = </div>)
      And I click submit
    Then the message "_____ contains invalid texts." appears
      And the rest of the form remains unchanged

  Scenario: An incomplete form is rejected
    When I fill in the form without completing at least one required field
      And I click submit
    Then the unfilled required fields are marked with the word "Required!"
      And the rest of the form remains unchanged