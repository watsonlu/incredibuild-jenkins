package org.jenkinsci.plugins.incredibuild.Commands;

import hudson.util.ArgumentListBuilder;

/**
 * Created by lwatson on 8/28/2014.
 */
public class BuildConsoleCommand extends AbstractCommand {
    private String configuration;
    private String project;
    private String path;
    private String buildConsolePath;
    private Boolean wait;
    private Boolean rebuild;

    public BuildConsoleCommand(String configuration, String project, String path, String buildConsolePath, Boolean wait, Boolean rebuild) {
        this.configuration = configuration;
        this.project = project;
        this.path = path;
        this.buildConsolePath = buildConsolePath;
        this.wait = wait;
        this.rebuild = rebuild;
    }


    public ArgumentListBuilder getArguments() {

        ArgumentListBuilder argumentListBuilder = new ArgumentListBuilder();
        argumentListBuilder.addTokenized(buildConsolePath);
        argumentListBuilder.add(path);
        if (!configuration.equals("")) {
            argumentListBuilder.add("/Cfg=\"" + configuration + "\"");
        }
        if (!project.equals("")) {
            argumentListBuilder.add("/Prj=\"" + project + "\"");
        }
        if (wait) {
            argumentListBuilder.add("/Wait");
        }
        if (rebuild) {
            argumentListBuilder.add("/Rebuild");
        }
        return argumentListBuilder;
    }
}
