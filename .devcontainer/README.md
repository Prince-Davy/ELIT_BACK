# Steps

Install remote containers extension to your vscode if not already done.

Ask for the .env file to your colleagues and put it at the root of the project (make sure it is named .env and not env !!)

ctrl+maj+p -> Remote-Containers: Reopen in container

Wait for image to be pulled/built, and for containers to launch.

If you see a popup asking to reload the window because of extensions, do it.

Done! Java Test Runner extension allows unit tests to be ran without worrying about context initialisation, and when you want to compile/launch the project:
```bash
mvn spring-boot:run
```
DB is already launched in the background. App is accessible through localhost:8102

# Details

If you want to develop in a devcontainer, make sure you have the required extension installed in your vscode (remote containers). Then, simply press ctrl+maj+P and search for the command: 'Remote-Containers: Reopen in container'.

VSCode will create a container in which you will be able to work and launch unit tests without worrying about the env (provided you got the .env files from your colleagues and put it at the root of the project). It will be a bit long the first time because images need to be pulled and containers built, but it will go faster the next times you open this project. You should not have to worry about installing maven, java, etc. Several other useful vscode extensions are added, like LiveShare, Language Support for Java, etc.

Other useful commands offered by Remote-Containers are 'Rebuild and Reopen in container', 'Rebuild without cache', and 'Reopen folder locally' if you ever want to stop work in the container.

/!\ /!\ IMPORTANT /!\ /!\ If  for some reason you want to be able to launch the project using compose with app.dev.yml from the devcontainer, it is possible but you have to modify the '.devcontainer/devcontainer.json' file. Uncomment the "remoteEnv" property and modify it accordingly, then rebuild. Absolute path is the output of command 'pwd' used in project repository, on your **LOCAL** machine (not in devcontainer !!). Leave the final '/' in your path, it is important so that app.dev.yml can still be used locally.
