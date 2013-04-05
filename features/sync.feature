Feature: Newsfeed Sync

  Scenario: As a user I can cancel a sync when it is in progress
    Given I press view with id "actionbar_refresh"
    Then I wait for a second
    When I press view with id "actionbar_refresh"
    Then I wait for 3 seconds
