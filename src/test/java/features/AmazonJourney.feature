Feature: Amazon journey

  Scenario: Harry Potter purchase
    Given I start Chrome browser
    When I navigate to https://www.amazon.co.uk/
    And I search for Harry Potter and the Cursed Child in Books category
    And I go to details of Harry Potter and the Cursed Child - Parts One and Two: The Official Playscript of the Original West End Production
    And I add to basket
    And I edit basket
    Then I stop Chrome browser
