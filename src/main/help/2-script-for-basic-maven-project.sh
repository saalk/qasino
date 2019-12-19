#!/bin/bash
# run this script with bash <scriptname>.sh
# guide to: Java Maven AngularJS Seed Project on Web Dev Zone
# - https://dzone.com/articles/java-maven-angularjs-seed
# guide to: Hello World Restful Web service Using Spring
# - https://dzone.com/articles/hello-world-restful-web
#
#
# 1: Archetype is a Maven project templating toolkit - to create a new project based on an archetype 
#    - use mvn archetype:generate
#	 - use eg. -DarchetypeArtifactId=mamaven-archetype-webapp to generate a sample Maven Webapp project
#    - example: mvn archetype:generate -DgroupId=nl.deknik -DartifactId=CardGamesAPI -DarchetypeArtifactId=maven-archetype-webapp -DinteractiveMode=false
#
echo "1: make archetype for webapp"
mvn archetype:generate -DgroupId=nl.deknik -DartifactId=CardGamesAPI -DarchetypeArtifactId=maven-archetype-webapp -DinteractiveMode=false
#
# 2: Add a java folder structure to the project  
#    - mkdir for structure
#    - touch to create empty files
#	 - remove unnecessary files
echo "2: make java folder structure and initial js files"
mkdir -p src/main/java
mkdir -p src/test/java
mkdir -p src/test/javascript/unit
mkdir -p src/test/javascript/e2e
mkdir -p src/test/resources
rm -f ./src/main/webapp/WEB-INF/web.xml
rm -f ./src/main/webapp/index.jsp
mkdir -p ./src/main/webapp/css
touch ./src/main/webapp/css/specific.css
mkdir -p ./src/main/webapp/js
touch ./src/main/webapp/js/app.js
touch ./src/main/webapp/js/controllers.js
touch ./src/main/webapp/js/routes.js
touch ./src/main/webapp/js/services.js
touch ./src/main/webapp/js/filters.js
touch ./src/main/webapp/js/services.js
mkdir -p ./src/main/webapp/vendor
mkdir -p ./src/main/webapp/partials
mkdir -p ./src/main/webapp/img
touch .bowerrc
#
# 3: Initialize package.json to let npm know the name of your package and dependencies 
#    - npm init - to create a package.json for npm to handle the project's dependencies 
#    - cat package.json - to show the just created file
#    - tweak the package.json - add dev-dependencies and scripts for karma*, protractor
#
echo "3: add package.json at root of node.js project keeps track of other peoples packages you use"
echo "   use npm init for interactive creating a package.json file (see: npm help json)"
npm init
#
# 4: Tell bower to download packages not to bower-components but to a vendor folder
#    - tweak the bowerrc file
#    - add the directory vendor
#    - use bower to install angular and bootstrap dependencies
#    - bootstrap is a html, css and JS framework for developing mobile-first web sites
#
echo "4: install bower via npm for other peoples packages you use"
echo "   a bower.json as a manifest for packages installed"
bower install angular
bower install angular-route
bower install angular-animate
bower install angular-mocks
bower install angular-loader
bower install bootstrap
#
# 5: Initialize bower.json to let bower know the name of your package and dependencies 
#    - do bower init via CMD not via Bash!!
#    - npm via bower will fill the directory vendor automated after npm install!
#
echo "4: add bower.json to add currently installed components to  "
echo "   a bower.json as a manifest for packages installed"
bower init
also do: npm install -g http-server ?
#
# 6: Test the setup of bower; npm via bower will fill a removed vendor directory automatically after npm install!
#
rm -rf ./src/main/webapp/vendor
npm install
#
# 7: Add Karma as the testrunner for javascript
#    - create the ./src/test/javascript/karma.conf.js containing a link to angular-mocks.js and correct plugins
#    - this will run the unit tests by using the command npm test
#
# 8: Use protractor as the end-2-end tester.
#    - create the ./src/test/javascript/protractor.conf.js mentioning jasmine as framework
#    - this will run the integration test by using the command npm run protractor
#
# 9: update the pom
#    - add dependency for jackson jaxb annotations, glassfish
#
# 10: Add an index.html and the js files and the jasmine and e2e tests
#    - start a terminal window in the root of the project and start the test server
#    - start a terminal window in the root of the project and start the test server
#
# 11: make a java project with | Annotation | Meaning                                             |
#+------------+-----------------------------------------------------+
#| @Component | generic stereotype for any Spring-managed component |
#| @Repository| stereotype for persistence layer                    |
#| @service   | stereotype for service layer                        |
#| @Controller| stereotype for presentation layer (spring-mvc)      |
#
http://crunchify.com/simplest-spring-mvc-hello-world-example-tutorial-spring-model-view-controller-tips/
- Open pom.xml file and add below jar dependencies to project. sping-context/webmvc/web
- what is jstl
- Create new Spring Configuration Bean file: /WebContent/WEB-INF/crunchify-servlet.xml
- In the above crunchify-servlet.xml  configuration file, we have defined a tag <context:component-scan> . This will allow Spring to load all the components from package com.crunchify.controller  and all its child packages.
- Create new file web.xml. Map Spring MVC in /WebContent/WEB-INF/web.xml file.