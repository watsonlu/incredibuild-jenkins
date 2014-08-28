package org.jenkinsci.plugins.incredibuild.Commands;

import hudson.util.ArgumentListBuilder;

/**
 * Created by lwatson on 8/28/2014.
 */
public class BuildConsoleCommand extends AbstractCommand {
    private String configuration;
    private String project;
    private String path;

    public BuildConsoleCommand(String configuration, String project, String path) {
        this.configuration = configuration;
        this.project = project;
        this.path = path;
    }


    public ArgumentListBuilder getArguments() {
        ArgumentListBuilder argumentListBuilder = new ArgumentListBuilder();
        argumentListBuilder.addTokenized(BuildConsoleName);
        argumentListBuilder.add(path);
        if (configuration != null || !configuration.equals("")) {
            argumentListBuilder.add("/Cfg=\"" + configuration + "\"");
        }
        if (project != null || !project.equals("")) {
            argumentListBuilder.add("/Prj=\"" + project + "\"");
        }
        return argumentListBuilder;
    }
}
