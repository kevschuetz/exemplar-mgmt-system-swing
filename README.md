# Exemplar Management System (Team 4)

Repository for the project of creating an Exemplar Management Tool in the course of the PR Software Engineering in the summer term 2021 led by Prof. Luca Berardinelli.

## Structure of the repository 

The root folder and the pom.xml therein represents the maven project for the desktop application (Exemplar Management System).

The /database folder represents an independent Spring-Boot application that acts as a proxy between the desktop application and the database.
[Spring-Boot project](https://github.com/jku-win-se/teaching-2021.prse-exemplar-team4/tree/main/database)

For further information regarding the project feel free to look at the [project documentation](https://github.com/jku-win-se/teaching-2021.prse-exemplar-team4/blob/main/Documents/Release%204/Project%20Documentation.pdf)

Other documents and documentation files can be found under /Documents
[Documentation](https://github.com/jku-win-se/teaching-2021.prse-exemplar-team4/tree/main/Documents)
Subfolders for every release contain release-specific documents.

## **Installation**

In order to install the system and run it locally follow these steps.
* Clone the repository
$ git clone https://github.com/jku-win-se/teaching-2021.prse-exemplar-team4

* Execute the executable jar of the frontend by navigating to the /out/artifacts/application_jar/ folder and executing the jar with the following command
$ java -jar application.jar

It is recommended to have at least JDK 15 installed, although it may work with lower versions.




