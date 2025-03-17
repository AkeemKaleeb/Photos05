# Photos05
In order to compile run the following commands:
```
javac --module-path "./lib/javafx-sdk-21/lib" --add-modules javafx.controls,javafx.fxml -d out src/main/java/*.java src/main/java/view/*.java src/main/java/controller/*.java src/main/java/model/*.java

java --module-path "./lib/javafx-sdk-21/lib" --add-modules javafx.controls,javafx.fxml -cp "out;src/main/resources" Photos
```

Or, with Maven installed:
```
mvn clean javafx:run
```

Or, with the launch.json file in VSCode