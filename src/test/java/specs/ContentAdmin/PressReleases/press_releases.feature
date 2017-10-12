Feature: Create a press release

  Background:
    Given that I am logged into Admin site (have valid session cookie)

  Scenario: Submit a new press release
    When I click “Add a Press Release”
    And fill in necessary information (date, time, headline, comment)
    And click “Save And Submit”
    Then that press release is listed in the “Press Release Page”
    And the displayed status of that listing is “For Approval”

  Scenario: Publish a press release
    Given that there exists a press release with the status “For Approval”
    And I a, on the Press Release List page
    When I select the press release’s checkbox
    And I click “Publish”
    Then the press release’s status become “Live” or “Live - Publish Pending”

  Scenario: Verify the presence of a recently published press release on the public site
    Given that I have just published a new press release
    When I navigate to the public site’s press release page
    Then that press release is displayed (allow for delay of up to 2 minutes for that press release to appear)